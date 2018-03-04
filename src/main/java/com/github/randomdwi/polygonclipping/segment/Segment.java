package com.github.randomdwi.polygonclipping.segment;

import com.github.randomdwi.polygonclipping.geometry.Point;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class Segment {

    private static final Point ORIGIN = new Point(0.0, 0.0);

    public Point pBegin;
    public Point pEnd;

    /**
     * Instantiates a new Segment.
     *
     * @param pBegin the p begin
     * @param pEnd   the p end
     */
    public Segment(Point pBegin, Point pEnd) {
        this.pBegin = pBegin;
        this.pEnd = pEnd;
    }

    /**
     * Change the segment orientation
     * @return the segment
     */
    public Segment changeOrientation() {
        Point tmp = pBegin;
        pBegin = pEnd;
        pEnd = tmp;
        return this;
    }

    /**
     * Return the point of the segment with lexicographically smallest coordinate
     * @return the point
     */
    public Point min() {
        return (pBegin.x < pEnd.x) || (pBegin.x == pEnd.x && pBegin.y < pEnd.y) ? pBegin : pEnd;
    }

    /**
     * Return the point of the segment with lexicographically largest coordinate
     * @return the point
     */
    public Point max() {
        return (pBegin.x > pEnd.x) || (pBegin.x == pEnd.x && pBegin.y > pEnd.y) ? pBegin : pEnd;
    }

    /**
     * Returns if the segment is degenerated.
     *
     * @return is segment degenerated?
     */
    public boolean degenerate() {
        return pBegin.equals(pEnd);
    }

    /**
     * Returns if the segment is vertical.
     *
     * @return is segment vertical?
     */
    public boolean isVertical() {
        return pBegin.x == pEnd.x;
    }

    /**
     * Get normal of segment pointing inwards.
     * @return inwards normal
     */
    public Point getInwardsNormal() {

        Point edge = pEnd.minus(pBegin);
        double edgeLength = edge.dist(ORIGIN);

        if (edgeLength == 0.0) {
            throw new IllegalStateException("degenerated segment");
        }

        return new Point(-edge.y / edgeLength, edge.x / edgeLength);
    }

    /**
     * Get normal of segment pointing outwards.
     * @return outwards normal
     */
    public Point getOutwardsNormal() {
        return getInwardsNormal().multiply(-1.0);
    }
}
