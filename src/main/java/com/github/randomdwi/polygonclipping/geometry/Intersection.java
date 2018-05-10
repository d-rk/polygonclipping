package com.github.randomdwi.polygonclipping.geometry;

import com.github.randomdwi.polygonclipping.segment.Segment;
import lombok.Data;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;

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

        Point pointA0 = segmentA.pBegin;
        Point vectorA = new Point(segmentA.pEnd.x - pointA0.x, segmentA.pEnd.y - pointA0.y);
        Point pointB0 = segmentB.pBegin;
        Point vectorB = new Point(segmentB.pEnd.x - pointB0.x, segmentB.pEnd.y - pointB0.y);

        Point vectorA0_B0 = new Point(pointB0.x - pointA0.x, pointB0.y - pointA0.y);
        double cross = vectorA.x * vectorB.y - vectorA.y * vectorB.x;
        double sqrCross = cross * cross;
        double sqrLenVectorA = vectorA.x * vectorA.x + vectorA.y * vectorA.y;
        double sqrLenVectorB = vectorB.x * vectorB.x + vectorB.y * vectorB.y;

        if (sqrCross > EPSILON * sqrLenVectorA * sqrLenVectorB) {
            // lines of the segments are not parallel
            double s = (vectorA0_B0.x * vectorB.y - vectorA0_B0.y * vectorB.x) / cross;
            if ((s < 0) || (s > 1)) {
                type = NO_INTERSECTION;
                return;
            }
            double t = (vectorA0_B0.x * vectorA.y - vectorA0_B0.y * vectorA.x) / cross;
            if ((t < 0) || (t > 1)) {
                type = NO_INTERSECTION;
                return;
            }
            // intersection of lines is a point on each segment
            point = new Point(pointA0.x + s * vectorA.x, pointA0.y + s * vectorA.y);
            snapPointToClosestPoint(segmentA.pBegin, segmentA.pEnd, segmentB.pBegin, segmentB.pEnd);

            type = POINT;
            return;
        }

        // lines of the segments are parallel
        double sqrLenVectorA0_B0 = vectorA0_B0.x * vectorA0_B0.x + vectorA0_B0.y * vectorA0_B0.y;
        double crossVectorA0_B0_vectorA = vectorA0_B0.x * vectorA.y - vectorA0_B0.y * vectorA.x;
        double sqrCross2 = crossVectorA0_B0_vectorA * crossVectorA0_B0_vectorA;
        double rectArea = sqrLenVectorA0_B0 * sqrLenVectorA;

        // sqrCross2 == area of parallelogram spanned between vectorA0_B0 and vectorA
        // rectArea == area of rectangle spanned by length of vectorA0_B0 and length of vectorA
        if (sqrCross2 > EPSILON * rectArea && rectArea > EPSILON) {
            // lines of the segment are different
            type = NO_INTERSECTION;
            return;
        }

        // Lines of the segments are the same. Need to test for overlap of segments.
        double s0 = (vectorA.x * vectorA0_B0.x + vectorA.y * vectorA0_B0.y) / sqrLenVectorA;  // so = Dot (D0, E) * sqrLen0
        double s1 = s0 + (vectorA.x * vectorB.x + vectorA.y * vectorB.y) / sqrLenVectorA;  // s1 = s0 + Dot (D0, D1) * sqrLen0
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
            point = new Point(pointA0.x + w0 * vectorA.x, pointA0.y + w0 * vectorA.y);
            snapPointToClosestPoint(segmentA.pBegin, segmentA.pEnd, segmentB.pBegin, segmentB.pEnd);
            if (OVERLAPPING.equals(type)) {
                pi1 = new Point(pointA0.x + w1 * vectorA.x, pointA0.y + w1 * vectorA.y);
            }
        }
    }

    private void snapPointToClosestPoint(Point... points) {

        Optional<Point> minimum = Arrays.stream(points)
                .min(Comparator.comparingDouble(p -> point.sqrDist(p)));

        if (minimum.isPresent() && point.isCloseTo(minimum.get())) {
            point = minimum.get();
        }
    }
}
