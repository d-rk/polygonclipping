package com.github.randomdwi.polygonclipping.geometry;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@AllArgsConstructor
@ToString
public class Point {

    public double x;
    public double y;

    /**
     * Get distance to other point
     *
     * @param p the p
     * @return the double
     */
    public double dist(Point p) {
        double dx = x - p.x;
        double dy = y - p.y;
        return Math.sqrt(dx * dx + dy * dy);
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
     * Create a copy of this point.
     *
     * @return point copy
     */
    public Point copy() {
        return new Point(x, y);
    }
}
