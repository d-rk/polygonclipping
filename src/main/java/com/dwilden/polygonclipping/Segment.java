package com.dwilden.polygonclipping;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class Segment {

    public Point pBegin;
    public Point pEnd;

    public Segment(Point pBegin, Point pEnd) {
        this.pBegin = pBegin;
        this.pEnd = pEnd;
    }

    /** Change the segment orientation */
    public Segment changeOrientation() {
        Point tmp = pBegin;
        pBegin = pEnd;
        pEnd = tmp;
        return this;
    }

    /** Return the point of the segment with lexicographically smallest coordinate */
    public Point min() {
        return (pBegin.x < pEnd.x) || (pBegin.x == pEnd.x && pBegin.y < pEnd.y) ? pBegin : pEnd;
    }

    /** Return the point of the segment with lexicographically largest coordinate */
    public Point max() {
        return (pBegin.x > pEnd.x) || (pBegin.x == pEnd.x && pBegin.y > pEnd.y) ? pBegin : pEnd;
    }

    public boolean degenerate() {
        return pBegin.equals(pEnd);
    }

    public boolean isVertical() {
        return pBegin.x == pEnd.x;
    }
}
