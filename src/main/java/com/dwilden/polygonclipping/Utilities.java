package com.dwilden.polygonclipping;

public class Utilities {

    public static int findIntersection(double u0, double u1, double v0, double v1, double[] w) {
        if ((u1 < v0) || (u0 > v1))
            return 0;
        if (u1 > v0) {
            if (u0 < v1) {
                w[0] = (u0 < v0) ? v0 : u0;
                w[1] = (u1 > v1) ? v1 : u1;
                return 2;
            } else {
                // u0 == v1
                w[0] = u0;
                return 1;
            }
        } else {
            // u1 == v0
            w[0] = u1;
            return 1;
        }
    }

    public static IntersectionResult findIntersection(Segment seg0, Segment seg1) {

        IntersectionResult result = new IntersectionResult();

        Point p0 = seg0.pBegin;
        Point d0 = new Point(seg0.pEnd.x - p0.x, seg0.pEnd.y - p0.y);
        Point p1 = seg1.pBegin;
        Point d1 = new Point(seg1.pEnd.x - p1.x, seg1.pEnd.y - p1.y);
        double sqrEpsilon = 0.0000001; // it was 0.001 before
        Point E = new Point(p1.x - p0.x, p1.y - p0.y);
        double kross = d0.x * d1.y - d0.y * d1.x;
        double sqrKross = kross * kross;
        double sqrLen0 = d0.x * d0.x + d0.y * d0.y;
        double sqrLen1 = d1.x * d1.x + d1.y * d1.y;

        if (sqrKross > sqrEpsilon * sqrLen0 * sqrLen1) {
            // lines of the segments are not parallel
            double s = (E.x * d1.y - E.y * d1.x) / kross;
            if ((s < 0) || (s > 1)) {
                result.intersections = 0;
                return result;
            }
            double t = (E.x * d0.y - E.y * d0.x) / kross;
            if ((t < 0) || (t > 1)) {
                result.intersections = 0;
                return result;
            }
            // intersection of lines is a point an each segment
            result.pi0 = new Point(p0.x + s * d0.x, p0.y + s * d0.y);
            if (result.pi0.dist(seg0.pBegin) < 0.00000001) result.pi0 = seg0.pBegin;
            if (result.pi0.dist(seg0.pEnd) < 0.00000001) result.pi0 = seg0.pEnd;
            if (result.pi0.dist(seg1.pBegin) < 0.00000001) result.pi0 = seg1.pBegin;
            if (result.pi0.dist(seg1.pEnd) < 0.00000001) result.pi0 = seg1.pEnd;

            result.intersections = 1;
            return result;
        }

        // lines of the segments are parallel
        double sqrLenE = E.x * E.x + E.y * E.y;
        kross = E.x * d0.y - E.y * d0.x;
        sqrKross = kross * kross;
        if (sqrKross > sqrEpsilon * sqrLen0 * sqrLenE) {
            // lines of the segment are different
            result.intersections = 0;
            return result;
        }

        // Lines of the segments are the same. Need to test for overlap of segments.
        double s0 = (d0.x * E.x + d0.y * E.y) / sqrLen0;  // so = Dot (D0, E) * sqrLen0
        double s1 = s0 + (d0.x * d1.x + d0.y * d1.y) / sqrLen0;  // s1 = s0 + Dot (D0, D1) * sqrLen0
        double smin = Math.min(s0, s1);
        double smax = Math.max(s0, s1);
        double[] w = new double[2];
        int imax = findIntersection(0.0, 1.0, smin, smax, w);

        if (imax > 0) {
            result.pi0 = new Point(p0.x + w[0] * d0.x, p0.y + w[0] * d0.y);
            if (result.pi0.dist(seg0.pBegin) < 0.00000001) result.pi0 = seg0.pBegin;
            if (result.pi0.dist(seg0.pEnd) < 0.00000001) result.pi0 = seg0.pEnd;
            if (result.pi0.dist(seg1.pBegin) < 0.00000001) result.pi0 = seg1.pBegin;
            if (result.pi0.dist(seg1.pEnd) < 0.00000001) result.pi0 = seg1.pEnd;
            if (imax > 1) {
                result.pi1 = new Point(p0.x + w[1] * d0.x, p0.y + w[1] * d0.y);
            }
        }

        result.intersections = imax;
        return result;
    }

    /**
     * Signed area of the triangle (p0, p1, p2)
     */
    public static double signedArea(Point p0, Point p1, Point p2) {
        return (p0.x - p2.x) * (p1.y - p2.y) - (p1.x - p2.x) * (p0.y - p2.y);
    }

    /**
     * Signed area of the triangle ( (0,0), p1, p2)
     */
    public static double signedArea(Point p1, Point p2) {
        return -p2.x * (p1.y - p2.y) - -p2.y * (p1.x - p2.x);
    }

    /**
     * Sign of triangle (p1, p2, o)
     */
    public static int sign(Point p1, Point p2, Point o) {
        double det = (p1.x - o.x) * (p2.y - o.y) - (p2.x - o.x) * (p1.y - o.y);
        return (det < 0 ? -1 : (det > 0 ? +1 : 0));
    }

    public static boolean pointInTriangle(Segment s, Point o, Point p) {
        int x = sign(s.pBegin, s.pEnd, p);
        return ((x == sign(s.pEnd, o, p)) && (x == sign(o, s.pBegin, p)));
    }
}
