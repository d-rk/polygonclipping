package com.dwilden.polygonclipping.geometry;

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
     */
    public double dist(Point p) {
        double dx = x - p.x;
        double dy = y - p.y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    public BoundingBox boundingBox() {
        return new BoundingBox(x, y, x, y);
    }

    public Point copy() {
        return new Point(x, y);
    }
}
