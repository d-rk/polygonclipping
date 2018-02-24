package com.github.randomdwi.polygonclipping.geometry;

import com.github.randomdwi.polygonclipping.segment.Segment;
import lombok.Data;

import static com.github.randomdwi.polygonclipping.geometry.Intersection.Type.*;

@Data
public class Intersection {

    private static final double EPSILON = 0.00000001;

    public enum Type {
        NO_INTERSECTION,
        POINT,
        OVERLAPPING
    }

    public Point point;
    public Point pi1;
    public Type type;


    /**
     * Determine intersection between two segments.
     *
     * @param segmentA the segment a
     * @param segmentB the segment b
     */
    public Intersection(Segment segmentA, Segment segmentB) {

        Point p0 = segmentA.pBegin;
        Point d0 = new Point(segmentA.pEnd.x - p0.x, segmentA.pEnd.y - p0.y);
        Point p1 = segmentB.pBegin;
        Point d1 = new Point(segmentB.pEnd.x - p1.x, segmentB.pEnd.y - p1.y);

        Point E = new Point(p1.x - p0.x, p1.y - p0.y);
        double cross = d0.x * d1.y - d0.y * d1.x;
        double sqrCross = cross * cross;
        double sqrLen0 = d0.x * d0.x + d0.y * d0.y;
        double sqrLen1 = d1.x * d1.x + d1.y * d1.y;

        if (sqrCross > EPSILON * sqrLen0 * sqrLen1) {
            // lines of the segments are not parallel
            double s = (E.x * d1.y - E.y * d1.x) / cross;
            if ((s < 0) || (s > 1)) {
                type = NO_INTERSECTION;
                return;
            }
            double t = (E.x * d0.y - E.y * d0.x) / cross;
            if ((t < 0) || (t > 1)) {
                type = NO_INTERSECTION;
                return;
            }
            // intersection of lines is a point on each segment
            point = new Point(p0.x + s * d0.x, p0.y + s * d0.y);
            snapPointToSegment(segmentA);
            snapPointToSegment(segmentB);


            type = POINT;
            return;
        }

        // lines of the segments are parallel
        double sqrLenE = E.x * E.x + E.y * E.y;
        cross = E.x * d0.y - E.y * d0.x;
        sqrCross = cross * cross;
        if (sqrCross > EPSILON * sqrLen0 * sqrLenE) {
            // lines of the segment are different
            type = NO_INTERSECTION;
            return;
        }

        // Lines of the segments are the same. Need to test for overlap of segments.
        double s0 = (d0.x * E.x + d0.y * E.y) / sqrLen0;  // so = Dot (D0, E) * sqrLen0
        double s1 = s0 + (d0.x * d1.x + d0.y * d1.y) / sqrLen0;  // s1 = s0 + Dot (D0, D1) * sqrLen0
        double sMin = Math.min(s0, s1);
        double sMax = Math.max(s0, s1);
        double w0;
        double w1 = 0.0;

        if ((1.0 < sMin) || (0.0 > sMax)) {
            type = NO_INTERSECTION;
        }
        if (1.0 > sMin) {
            if (0.0 < sMax) {
                w0 = (0.0 < sMin) ? sMin : 0.0;
                w1 = (1.0 > sMax) ? sMax : 1.0;
                type = OVERLAPPING;
            } else {
                // 0.0 == v1
                w0 = 0.0;
                type = POINT;
            }
        } else {
            // 1.0 == v0
            w0 = 1.0;
            type = POINT;
        }

        if (!NO_INTERSECTION.equals(type)) {
            point = new Point(p0.x + w0 * d0.x, p0.y + w0 * d0.y);
            snapPointToSegment(segmentA);
            snapPointToSegment(segmentB);
            if (OVERLAPPING.equals(type)) {
                pi1 = new Point(p0.x + w1 * d0.x, p0.y + w1 * d0.y);
            }
        }
    }

    private void snapPointToSegment(Segment segment) {
        if (point.dist(segment.pBegin) < EPSILON) {
            point = segment.pBegin;
        }
        if (point.dist(segment.pEnd) < EPSILON) {
            point = segment.pEnd;
        }
    }
}
