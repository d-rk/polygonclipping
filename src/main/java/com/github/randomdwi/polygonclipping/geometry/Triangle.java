package com.github.randomdwi.polygonclipping.geometry;

public class Triangle {

    /**
     * Signed area of the triangle (p0, p1, p2)
     */
    public static double signedArea(Point p0, Point p1, Point p2) {
        return (p0.x - p2.x) * (p1.y - p2.y) - (p1.x - p2.x) * (p0.y - p2.y);
    }
}
