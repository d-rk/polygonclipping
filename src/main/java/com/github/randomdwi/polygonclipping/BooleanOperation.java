package com.github.randomdwi.polygonclipping;

import com.github.randomdwi.polygonclipping.enums.EdgeType;
import com.github.randomdwi.polygonclipping.enums.PolygonType;
import com.github.randomdwi.polygonclipping.geometry.BoundingBox;
import com.github.randomdwi.polygonclipping.geometry.Contour;
import com.github.randomdwi.polygonclipping.geometry.Intersection;
import com.github.randomdwi.polygonclipping.geometry.Point;
import com.github.randomdwi.polygonclipping.segment.Segment;
import com.github.randomdwi.polygonclipping.segment.SegmentComparator;
import com.github.randomdwi.polygonclipping.sweepline.SweepEvent;
import com.github.randomdwi.polygonclipping.sweepline.SweepEventComparator;
import com.github.randomdwi.polygonclipping.sweepline.SweepLine;

import java.util.*;

import static com.github.randomdwi.polygonclipping.BooleanOperation.Type.*;

public class BooleanOperation {

    enum Type {
        INTERSECTION,
        UNION,
        DIFFERENCE,
        XOR
    }

    private Polygon subject;
    private Polygon clipping;
    private Polygon result;
    private Type operation;

    private SweepEventComparator sweepEventComparator = new SweepEventComparator(false); // to compare events
    private SweepLine sweepLine = new SweepLine(new SegmentComparator(false));
    private Deque<SweepEvent> sortedEvents = new LinkedList<>();

    /**
     * Compute the intersection of subject with clipping polygon.
     *
     * @param subject subject polygon
     * @param clipping clipping polygon
     * @return result of the operation
     */
    public static Polygon INTERSECTION(Polygon subject, Polygon clipping) {
        return new BooleanOperation(subject, clipping, INTERSECTION).execute();
    }

    /**
     * Compute the union of subject with clipping polygon.
     *
     * @param subject subject polygon
     * @param clipping clipping polygon
     * @return result of the operation
     */
    public static Polygon UNION(Polygon subject, Polygon clipping) {
        return new BooleanOperation(subject, clipping, UNION).execute();
    }

    /**
     * Compute the difference of subject with clipping polygon.
     *
     * @param subject subject polygon
     * @param clipping clipping polygon
     * @return result of the operation
     */
    public static Polygon DIFFERENCE(Polygon subject, Polygon clipping) {
        return new BooleanOperation(subject, clipping, DIFFERENCE).execute();
    }

    /**
     * Compute exclusive or of subject with clipping polygon.
     *
     * @param subject subject polygon
     * @param clipping clipping polygon
     * @return result of the operation
     */
    public static Polygon XOR(Polygon subject, Polygon clipping) {
        return new BooleanOperation(subject, clipping, XOR).execute();
    }

    private BooleanOperation(Polygon subject, Polygon clip, BooleanOperation.Type operation) {
        this.subject = subject.copy();
        this.clipping = clip.copy();
        this.operation = operation;
        this.result = new Polygon();
    }

    private Polygon execute() {

        BoundingBox subjectBB = subject.boundingBox();     // for optimizations 1 and 2
        BoundingBox clippingBB = clipping.boundingBox();   // for optimizations 1 and 2
        double MINMAXX = Math.min(subjectBB.xMax, clippingBB.xMax); // for optimization 2

        if (trivialOperation(subjectBB, clippingBB)) {
            // trivial cases can be quickly resolved without sweeping the plane
            return result;
        }

        for (int i = 0; i < subject.contourCount(); i++) {
            for (int j = 0; j < subject.contour(i).pointCount(); j++) {
                processSegment(subject.contour(i).segment(j), PolygonType.SUBJECT);
            }
        }

        for (int i = 0; i < clipping.contourCount(); i++) {
            for (int j = 0; j < clipping.contour(i).pointCount(); j++) {
                processSegment(clipping.contour(i).segment(j), PolygonType.CLIPPING);
            }
        }

        while (!sweepLine.eventQueue.isEmpty()) {
            SweepEvent se = sweepLine.eventQueue.poll();
            // optimization 2
            if ((INTERSECTION.equals(operation) && se.point.x > MINMAXX) ||
                    (DIFFERENCE.equals(operation) && se.point.x > subjectBB.xMax)) {
                connectEdges();
                return result;
            }
            sortedEvents.add(se);

            if (se.left) { // the line segment must be inserted into sl

                sweepLine.statusLine.addEvent(se);

                SweepEvent prev = sweepLine.statusLine.getPreviousEvent(se);
                SweepEvent next = sweepLine.statusLine.getNextEvent(se);

                computeFields(se, prev);
                // Process a possible intersection between "se" and its next neighbor in sl
                if (next != null) {
                    if (possibleIntersection(se, next) == 2) {
                        computeFields(se, prev);
                        computeFields(next, se);
                    }
                }
                // Process a possible intersection between "se" and its previous neighbor in sl
                if (prev != null) {
                    if (possibleIntersection(prev, se) == 2) {
                        SweepEvent prevPrev = sweepLine.statusLine.getPreviousEvent(prev);
                        computeFields(prev, prevPrev);
                        computeFields(se, prev);
                    }
                }
            } else {
                // the line segment must be removed from sl
                se = se.otherEvent; // we work with the left event

                SweepEvent prev = sweepLine.statusLine.getPreviousEvent(se);
                SweepEvent next = sweepLine.statusLine.getNextEvent(se);

                // delete line segment associated to "se" from sl
                sweepLine.statusLine.removeEvent(se);

                if (prev != null && next != null) {
                    //check for intersection between the neighbors of "se" in sl
                    possibleIntersection(prev, next);
                }
            }
        }
        connectEdges();
        return result;
    }

    private boolean trivialOperation(BoundingBox subjectBB, BoundingBox clippingBB) {

        // Test 1 for trivial result case (at least one of the polygons is empty)
        if (subject.isEmpty() || clipping.isEmpty()) {
            if (DIFFERENCE.equals(operation)) {
                result = subject;
            }
            if (UNION.equals(operation) || XOR.equals(operation)) {
                result = subject.isEmpty() ? clipping : subject;
            }
            return true;
        }
        // Test 2 for trivial result case (the bounding boxes do not overlap)
        if (subjectBB.xMin > clippingBB.xMax || clippingBB.xMin > subjectBB.xMax ||
                subjectBB.yMin > clippingBB.yMax || clippingBB.yMin > subjectBB.yMax) {
            if (DIFFERENCE.equals(operation)) {
                result = subject;
            }
            if (UNION.equals(operation) || XOR.equals(operation)) {
                result = subject;
                result.join(clipping);
            }
            return true;
        }
        return false;
    }

    /**
     * Compute the events associated to segment s, and insert them into pq and eq
     */
    private void processSegment(Segment s, PolygonType pt) {
//        // if the two edge endpoints are equal the segment is dicarded
//        if (s.degenerate ()) {
//            // This can be done as preprocessing to avoid "polygons" with less than 3 edges */
//            return;
//        }
        SweepEvent e1 = new SweepEvent(s.pBegin, true, null, pt);
        SweepEvent e2 = new SweepEvent(s.pEnd, true, e1, pt);
        e1.otherEvent = e2;

        if (s.min().equals(s.pBegin)) {
            e2.left = false;
        } else {
            e1.left = false;
        }
        sweepLine.eventQueue.add(e1);
        sweepLine.eventQueue.add(e2);
    }

    /**
     * Process a possible intersection between the edges associated to the left events le1 and le2
     */
    private int possibleIntersection(SweepEvent le1, SweepEvent le2) {

        // you can uncomment these two lines if self-intersecting polygons are not allowed
//        if (le1.polygonType.equals(le2.polygonType)) {
//            // self intersection
//            return 0;
//        }

        Intersection intersections = new Intersection(le1.segment(), le2.segment());

        if (Intersection.Type.NO_INTERSECTION.equals(intersections.type)) {
            // no intersection
            return 0;
        }

        if ((Intersection.Type.POINT.equals(intersections.type)) && ((le1.point.isCloseTo(le2.point)) || (le1.otherEvent.point.isCloseTo(le2.otherEvent.point)))) {
            // the line segments intersect at an endpoint of both line segments
            return 0;
        }

        if (Intersection.Type.OVERLAPPING.equals(intersections.type) && le1.polygonType.equals(le2.polygonType)) {
            throw new IllegalStateException("edges of the same polygon overlap");
        }

        // The line segments associated to le1 and le2 intersect
        if (Intersection.Type.POINT.equals(intersections.type)) {
            if (!le1.point.isCloseTo(intersections.point) && !le1.otherEvent.point.isCloseTo(intersections.point)) {
                // if the intersection point is not an endpoint of le1.segment ()
                divideSegment(le1, intersections.point);
            }
            if (!le2.point.isCloseTo(intersections.point) && !le2.otherEvent.point.isCloseTo(intersections.point)) {
                // if the intersection point is not an endpoint of le2.segment ()
                divideSegment(le2, intersections.point);
            }
            return 1;
        }
        // The line segments associated to le1 and le2 overlap
        List<SweepEvent> sortedEvents = new ArrayList<>();

        if (le1.point.isCloseTo(le2.point)) {
            sortedEvents.add(null);
        } else if (sweepEventComparator.compare(le1, le2) < 0) {
            sortedEvents.add(le2);
            sortedEvents.add(le1);
        } else {
            sortedEvents.add(le1);
            sortedEvents.add(le2);
        }
        if (le1.otherEvent.point.isCloseTo(le2.otherEvent.point)) {
            sortedEvents.add(null);
        } else if (sweepEventComparator.compare(le1.otherEvent, le2.otherEvent) < 0) {
            sortedEvents.add(le2.otherEvent);
            sortedEvents.add(le1.otherEvent);
        } else {
            sortedEvents.add(le1.otherEvent);
            sortedEvents.add(le2.otherEvent);
        }

        if ((sortedEvents.size() == 2) || (sortedEvents.size() == 3 && sortedEvents.get(2) != null)) {
            // both line segments are equal or share the left endpoint
            le1.type = EdgeType.NON_CONTRIBUTING;
            le2.type = (le1.inOut == le2.inOut) ? EdgeType.SAME_TRANSITION : EdgeType.DIFFERENT_TRANSITION;
            if (sortedEvents.size() == 3) {
                divideSegment(sortedEvents.get(2).otherEvent, sortedEvents.get(1).point);
            }
            return 2;
        }
        if (sortedEvents.size() == 3) { // the line segments share the right endpoint
            divideSegment(sortedEvents.get(0), sortedEvents.get(1).point);
            return 3;
        }
        if (sortedEvents.get(0) != sortedEvents.get(3).otherEvent) {
            // no line segment includes totally the other one
            divideSegment(sortedEvents.get(0), sortedEvents.get(1).point);
            divideSegment(sortedEvents.get(1), sortedEvents.get(2).point);
            return 3;
        }
        // one line segment includes the other one
        divideSegment(sortedEvents.get(0), sortedEvents.get(1).point);
        divideSegment(sortedEvents.get(3).otherEvent, sortedEvents.get(2).point);
        return 3;
    }

    /**
     * Divide the segment associated to left event le, updating pq and (implicitly) the status line
     */
    private void divideSegment(SweepEvent le, Point p) {

        // "Right event" of the "left line segment" resulting from dividing le->segment ()
        SweepEvent r = new SweepEvent(p, false, le, le.polygonType);
        // "Left event" of the "right line segment" resulting from dividing le->segment ()
        SweepEvent l = new SweepEvent(p, true, le.otherEvent, le.polygonType);
        if (sweepEventComparator.compare(l, le.otherEvent) < 0) { // avoid a rounding error. The left event would be processed after the right event
            le.otherEvent.left = true;
            l.left = false;
        }
        le.otherEvent.otherEvent = l;
        le.otherEvent = r;
        sweepLine.eventQueue.add(l);
        sweepLine.eventQueue.add(r);
    }

    /**
     * return if the left event le belongs to the result of the boolean operation
     */
    private boolean inResult(SweepEvent le) {
        switch (le.type) {
            case NORMAL:
                switch (operation) {
                    case INTERSECTION:
                        return !le.otherInOut;
                    case UNION:
                        return le.otherInOut;
                    case DIFFERENCE:
                        return (PolygonType.SUBJECT.equals(le.polygonType) && le.otherInOut) || (PolygonType.CLIPPING.equals(le.polygonType) && !le.otherInOut);
                    case XOR:
                        return true;
                }
            case SAME_TRANSITION:
                return operation == INTERSECTION || operation == UNION;
            case DIFFERENT_TRANSITION:
                return operation == DIFFERENCE;
            case NON_CONTRIBUTING:
                return false;
        }
        throw new IllegalStateException("unexpected event type");
    }

    /**
     * compute several fields of left event le
     */
    private void computeFields(SweepEvent le, SweepEvent prev) {
        // compute inOut and otherInOut fields
        if (prev == null) {
            le.inOut = false;
            le.otherInOut = true;
        } else if (le.polygonType.equals(prev.polygonType)) {
            // previous line segment in sl belongs to the same polygon that "se" belongs to
            le.inOut = !prev.inOut;
            le.otherInOut = prev.otherInOut;
        } else {
            // previous line segment in sl belongs to a different polygon that "se" belongs to
            le.inOut = !prev.otherInOut;
            le.otherInOut = prev.vertical() != prev.inOut;
        }
        // compute prevInResult field
        if (prev != null) {
            le.prevInResult = (!inResult(prev) || prev.vertical()) ? prev.prevInResult : prev;
        }
        // check if the line segment belongs to the Boolean operation
        le.inResult = inResult(le);
    }

    // connect the solution edges to build the result polygon
    private void connectEdges() {
        // copy the events in the result polygon to resultEvents array
        List<SweepEvent> resultEvents = new ArrayList<>(sortedEvents.size());

        for (SweepEvent event : sortedEvents) {
            if ((event.left && event.inResult) || (!event.left && event.otherEvent.inResult)) {
                resultEvents.add(event);
            }
        }

        //TODO refactor sweepEventComparator
        SweepEventComparator sec2 = new SweepEventComparator(true);                    // to compare events

        // Due to overlapping edges the resultEvents array can be not wholly sorted
        boolean sorted = false;
        while (!sorted) {
            sorted = true;

            for (int i = 0; i < resultEvents.size() - 1; ++i) {
                SweepEvent event = resultEvents.get(i);
                SweepEvent nextEvent = resultEvents.get(i + 1);

                if (sec2.compare(event, nextEvent) >= 0) {
                    // swap
                    resultEvents.set(i, nextEvent);
                    resultEvents.set(i + 1, event);
                    sorted = false;
                }
            }
        }

        for (int i = 0; i < resultEvents.size(); ++i) {
            SweepEvent event = resultEvents.get(i);

            if (event.left) {
                event.pos = i;
            } else {
                event.pos = event.otherEvent.pos;
                event.otherEvent.pos = i;
            }
        }

        Set<Integer> processed = new HashSet<>(resultEvents.size());
        List<Integer> depth = new ArrayList<>();
        List<Integer> holeOf = new ArrayList<>();

        for (int i = 0; i < resultEvents.size(); i++) {

            SweepEvent event = resultEvents.get(i);

            if (processed.contains(i)) {
                continue;
            }

            Contour contour = new Contour();
            result.addContour(contour);

            int contourId = result.contourCount() - 1;
            depth.add(0);
            holeOf.add(-1);

            if (event.prevInResult != null) {
                int lowerContourId = event.prevInResult.contourId;
                if (!event.prevInResult.resultInOut) {
                    result.contour(lowerContourId).addHole(contourId);
                    holeOf.set(contourId, lowerContourId);
                    depth.set(contourId, depth.get(lowerContourId) + 1);
                    contour.setIsHole(true);
                } else if (result.contour(lowerContourId).isHole()) {
                    result.contour(holeOf.get(lowerContourId)).addHole(contourId);
                    holeOf.set(contourId, holeOf.get(lowerContourId));
                    depth.set(contourId, depth.get(lowerContourId));
                    contour.setIsHole(true);
                }
            }

            int pos = i;
            Point initial = event.point;
            contour.add(initial);

            while (!resultEvents.get(pos).otherEvent.point.equals(initial)) {
                processed.add(pos);
                if (resultEvents.get(pos).left) {
                    resultEvents.get(pos).resultInOut = false;
                    resultEvents.get(pos).contourId = contourId;
                } else {
                    resultEvents.get(pos).otherEvent.resultInOut = true;
                    resultEvents.get(pos).otherEvent.contourId = contourId;
                }
                pos = resultEvents.get(pos).pos;
                processed.add(pos);
                contour.add(resultEvents.get(pos).point);
                pos = nextPos(pos, resultEvents, processed);
            }
            processed.add(resultEvents.get(pos).pos);
            processed.add(pos);
            resultEvents.get(pos).otherEvent.resultInOut = true;
            resultEvents.get(pos).otherEvent.contourId = contourId;
            if (depth.get(contourId) % 2 == 1) {
                contour.changeOrientation();
            }
        }
    }

    private int nextPos(int pos, List<SweepEvent> resultEvents, Set<Integer> processed) {
        int newPos = pos + 1;
        while (newPos < resultEvents.size() && resultEvents.get(newPos).point.equals(resultEvents.get(pos).point)) {
            if (!processed.contains(newPos)) {
                return newPos;
            } else {
                ++newPos;
            }
        }
        newPos = pos - 1;
        while (processed.contains(newPos)) {
            --newPos;
        }

        if (newPos == -1) {
            throw new IllegalStateException("cannot determine next position");
        }

        return newPos;
    }
}
