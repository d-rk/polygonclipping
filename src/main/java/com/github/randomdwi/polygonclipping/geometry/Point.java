package com.github.randomdwi.polygonclipping.geometry;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@AllArgsConstructor
@ToString
public class Point {

    private static final double EPSILON = 0.00000001;

    public double x;
    public double y;

    /**
     * Get squared distance to other point
     *
     * @param p the p
     * @return the double
     */
    public double sqrDist(Point p) {
        double dx = x - p.x;
        double dy = y - p.y;
        return dx * dx + dy * dy;
    }

    /**
     * Get distance to other point
     *
     * @param p the p
     * @return the double
     */
    public double dist(Point p) {
        return Math.sqrt(sqrDist(p));
    }

    /**
     * Get bounding box of point.
     *
     * @return bounding box
     */
    public BoundingBox boundingBox() {
        return new BoundingBox(x, y, x, y);
    }

    /**
     * Check if this point is close to given point.
     *
     * @param point comparison point
     * @return squared distance < EPSILON
     */
    public boolean isCloseTo(Point point) {
        return sqrDist(point) < EPSILON;
    }

    /**
     * Create a copy of this point.
     *
     * @return point copy
     */
    public Point copy() {
        return new Point(x, y);
    }

    /**
     * Substract a point.
     * @param point point to substract
     * @return result
     */
    public Point minus(Point point) {
        return new Point(x - point.x, y - point.y);
    }

    /**
     * Add a point
     * @param point point to add
     * @return result
     */
    public Point plus(Point point) {
        return new Point(x + point.x, y + point.y);
    }

    /**
     * Multiply to a point
     * @param factor factor
     * @return result
     */
    public Point multiply(double factor) {
        return new Point(x * factor, y * factor);
    }

    public static boolean isCloseTo(double coordinateA, double coordinateB) {
        double diff = coordinateA - coordinateB;
        return (diff * diff) < EPSILON;
    }
}
