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
     */
    public void combine(BoundingBox other) {
        xMin = Math.min(xMin, other.xMin);
        yMin = Math.min(yMin, other.yMin);
        xMax = Math.max(xMax, other.xMax);
        yMax = Math.max(yMax, other.yMax);
    }
}
