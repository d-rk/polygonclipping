package com.github.randomdwi.polygonclipping.geometry;

public class Triangle {

    private static final double EPSILON = 0.00000001;

    /**
     * Signed area of the triangle (p0, p1, p2).
     *
     * @param p0 point 0
     * @param p1 point 1
     * @param p2 point 2
     * @return signed area
     */
    public static double signedArea(Point p0, Point p1, Point p2) {
        return (p0.x - p2.x) * (p1.y - p2.y) - (p1.x - p2.x) * (p0.y - p2.y);
    }

    public static boolean areaCloseToZero(Point p0, Point p1, Point p2) {
        double area = signedArea(p0, p1, p2);
        return Math.abs(area) < EPSILON;
    }
}
