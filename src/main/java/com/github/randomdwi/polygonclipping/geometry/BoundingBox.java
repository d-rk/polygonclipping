package com.github.randomdwi.polygonclipping.geometry;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class BoundingBox {
    public double xMin;
    public double yMin;
    public double xMax;
    public double yMax;

    /**
     * Combine bounding box with other bounding box.
     *
     * @param other other bounding box
     * @return returns self for chaining
     */
    public BoundingBox combine(BoundingBox other) {
        xMin = Math.min(xMin, other.xMin);
        yMin = Math.min(yMin, other.yMin);
        xMax = Math.max(xMax, other.xMax);
        yMax = Math.max(yMax, other.yMax);
        return this;
    }

    /**
     * Get center point of bounding box.
     * @return center point
     */
    public Point getCenter() {
        return new Point(xMin + getWidth() * 0.5, yMin + getHeight() * 0.5);
    }

    /**
     * Get width of the bounding box.
     * @return width
     */
    public double getWidth() {
        return (xMax - xMin);
    }

    /**
     * Get height of the bounding box.
     * @return height
     */
    public double getHeight() {
        return (yMax - yMin);
    }
}
