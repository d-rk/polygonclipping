package com.dwilden.polygonclipping;

import lombok.Data;

@Data
public class IntersectionResult {

    public Point pi0;
    public Point pi1;
    public int intersections;
}
