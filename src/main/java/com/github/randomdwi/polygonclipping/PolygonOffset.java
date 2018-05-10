package com.github.randomdwi.polygonclipping;

import com.github.randomdwi.polygonclipping.drawing.PolygonDraw;
import com.github.randomdwi.polygonclipping.geometry.Contour;
import com.github.randomdwi.polygonclipping.geometry.Point;
import com.github.randomdwi.polygonclipping.segment.Segment;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;

public class PolygonOffset {

    private static final double TWO_PI = Math.PI * 2;

    //An odd number so that one arc vertex will be exactly arcRadius from center.
    private int arcSegmentCount = 5;

    /**
     * Create offset polygon polygon.
     *
     * @param polygon the polygon
     * @param offset  the offset
     * @return offset polygon
     */
    public static Polygon createOffsetPolygon(Polygon polygon, double offset) {
        return new PolygonOffset().create(polygon, offset);
    }

    Contour createArcContour(Point center, double radius, Point arcStart, Point arcEnd) {

        double startAngle = Math.atan2(arcStart.y - center.y, arcStart.x - center.x);
        double endAngle = Math.atan2(arcEnd.y - center.y, arcEnd.x - center.x);

        if (startAngle < 0.0) {
            startAngle += TWO_PI;
        }

        if (endAngle < 0.0) {
            endAngle += TWO_PI;
        }

        double angle, segmentAngle;

        if (startAngle > endAngle) {
            angle = startAngle - endAngle;
        } else {
            angle = startAngle + TWO_PI - endAngle;
        }

        if (radius < 0.0) {
            // padding
            segmentAngle = -angle / arcSegmentCount;
        } else {
            // margin
            segmentAngle = (TWO_PI - angle) / arcSegmentCount;
        }

        Contour contour = new Contour();
        contour.add(center);

        for (int i = 0; i <= arcSegmentCount; i++) {
            angle = startAngle + segmentAngle * i;
            double x = center.x + Math.cos(angle) * Math.abs(radius);
            double y = center.y + Math.sin(angle) * Math.abs(radius);
            contour.add(new Point(x, y));
        }

        return contour;
    }

    Contour createOffsetRectangle(Segment segment, double offset) {

        Contour contour = new Contour();
        contour.add(segment.pBegin);
        contour.add(segment.pEnd);
        contour.add(segment.pEnd.plus(segment.getOutwardsNormal().multiply(offset)));
        contour.add(segment.pBegin.plus(segment.getOutwardsNormal().multiply(offset)));

        return contour;
    }

    /**
     * Create offset polygon polygon.
     *
     * @param polygon the polygon
     * @param offset  the offset
     * @return offset polygon
     */
    public Polygon create(Polygon polygon, double offset) {

        if (Point.isCloseTo(offset, 0.0)) {
            return polygon;
        }

        Set<Contour> processed = new HashSet<>();

        final Polygon[] offsetPolygon = {polygon};

        polygon.getOrderedContours().stream()
                .filter(contour -> !processed.contains(contour))
                .forEach(contour -> {

            if (contour.isHole()) {
                throw new IllegalStateException("should not be a hole");
            }

            Polygon offsetContour = createOffsetRecursive(polygon, offset, processed, contour, false);

            offsetPolygon[0] = BooleanOperation.UNION(offsetPolygon[0], offsetContour);
        });

        return offsetPolygon[0];
    }

    private Polygon createOffsetRecursive(Polygon polygon, double offset, Set<Contour> processed,
                                          Contour contour, boolean isHole) {

        Polygon offsetContour = create(contour, offset, isHole);
        processed.add(contour);

        final Polygon[] offsetPolygon = {offsetContour};

        contour.getHoles().stream()
                .map(polygon::contour)
                .forEach(holeContour -> {
                    Polygon offsetHole = createOffsetRecursive(polygon, offset, processed, holeContour, !isHole);

                    offsetPolygon[0] = BooleanOperation.DIFFERENCE(offsetPolygon[0], offsetHole);
                });
        return offsetPolygon[0];
    }

    public Polygon create(Contour contour, double offset) {

        if (Point.isCloseTo(offset, 0.0)) {
            return Polygon.from(contour);
        }

        return create(contour, offset, false);
    }

    private Polygon create(Contour contour, double offset, boolean isHole) {

        List<Boolean> pointConvexity = getConvexity(contour.getPoints());

        List<Contour> offsetContours = new ArrayList<>();

        for (int i = 0; i < contour.pointCount(); i++) {
            int nextI = (i + 1) % contour.pointCount();
            Segment segment = contour.segment(i);

            Contour offsetRectangle = createOffsetRectangle(segment, offset);
            offsetContours.add(offsetRectangle);

            if (pointConvexity.get(nextI) == (offset > 0)) {
                // add arc
                Segment nextSegment = contour.segment(nextI);
                Point endPoint = segment.pEnd.plus(segment.getOutwardsNormal().multiply(offset));
                Point nextPoint = nextSegment.pBegin.plus(nextSegment.getOutwardsNormal().multiply(offset));

                Contour arcContour = createArcContour(segment.pEnd, offset, endPoint, nextPoint);
                offsetContours.add(arcContour);
            }
        }

        boolean isOuterOffsetContour = (offset > 0);

        if ((isOuterOffsetContour && !isHole) || (!isOuterOffsetContour && isHole)) {
            return apply(contour, BooleanOperation::UNION, offsetContours);
        } else {
            return apply(contour, BooleanOperation::DIFFERENCE, offsetContours);
        }
    }

    private Polygon apply(Contour contour, BiFunction<Polygon, Polygon, Polygon> operation, List<Contour> offsetContours) {

        PolygonDraw.drawPolygonImage(200, 200, new Polygon(offsetContours), "offsetContours.png");

        Polygon polygon = Polygon.from(contour.copy());
        int i = 0;
        for (Contour offsetContour : offsetContours) {
            PolygonDraw.drawPolygonImage(200, 200, polygon, "subject" + i + ".png");
            PolygonDraw.drawPolygonImage(200, 200, Polygon.from(offsetContour), "clipping" + i + ".png");

            Polygon tmp;

            try {
                tmp = operation.apply(polygon, Polygon.from(offsetContour));
            } catch (Exception ex) {
                System.out.println("ill");
                try {
                    polygon.serialize(new FileOutputStream("subject.pol"));
                    Polygon.from(offsetContour).serialize(new FileOutputStream("clipping.pol"));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                throw ex;
            }

            polygon = tmp;

            PolygonDraw.drawPolygonImage(200, 200, polygon, "result" + i++ + ".png");
        }

        return polygon;
    }

    private List<Boolean> getConvexity(List<Point> points) {

        List<Boolean> convexity = new ArrayList<>(points.size());

        int n = points.size();

        for (int i = 0; i < n; i++) {

            Point point = points.get(i);
            Point nextPoint = points.get((i + 1) % n);
            Point prevPoint = points.get((i + n - 1) % n);

            Point vectorPrev = prevPoint.minus(point);
            Point vectorNext = nextPoint.minus(point);

            // cross product
            double cross = vectorPrev.x * vectorNext.y - vectorNext.x * vectorPrev.y;
            double dot = vectorPrev.x * vectorNext.x + vectorPrev.y * vectorNext.y;

            // |v_p x v_next| = |v_p| * |v_next| * sin(alpha)
            // alpha <= 180° : sin(alpha) >= 0 => cross >= 0
            // alpha >  180° : sin(alpha) <  0 => cross <  0
            convexity.add(dot >= 0);
        }
        return convexity;
    }
}