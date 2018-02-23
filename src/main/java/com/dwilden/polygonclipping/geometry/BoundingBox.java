package com.dwilden.polygonclipping.geometry;

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

    public void union(BoundingBox other) {
        xMin = Math.min(xMin, other.xMin);
        yMin = Math.min(yMin, other.yMin);
        xMax = Math.max(xMax, other.xMax);
        yMax = Math.max(yMax, other.yMax);
    }
}
