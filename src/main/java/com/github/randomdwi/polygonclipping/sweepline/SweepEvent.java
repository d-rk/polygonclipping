package com.github.randomdwi.polygonclipping.sweepline;

import com.github.randomdwi.polygonclipping.geometry.Point;
import com.github.randomdwi.polygonclipping.geometry.Triangle;
import com.github.randomdwi.polygonclipping.segment.Segment;
import com.github.randomdwi.polygonclipping.enums.EdgeType;
import com.github.randomdwi.polygonclipping.enums.PolygonType;

public class SweepEvent {

    public boolean left;              // is point the left endpoint of the edge (point, otherEvent->point)?
    public Point point;          // point associated with the event
    public SweepEvent otherEvent; // event associated to the other endpoint of the edge
    public PolygonType polygonType;        // Polygon to which the associated segment belongs to
    public int polygon;
    public EdgeType type;
    //The following fields are only used in "left" events
    /**
     * Does segment (point, otherEvent.p) represent an inside-outside transition in the polygon for a vertical ray from (p.x, -infinite)?
     */
    public boolean inOut;
    public boolean otherInOut; // inOut transition for the segment from the other polygon preceding this segment in sl

    public SweepEvent prevInResult; // previous segment in sl belonging to the result of the boolean operation
    public boolean inResult;
    public int pos;
    public boolean resultInOut;
    public int contourId;

    /**
     * Instantiates a new Sweep event.
     *
     * @param point       the point
     * @param left        the left
     * @param otherEvent  the other event
     * @param polygonType the polygon type
     */
    public SweepEvent(Point point, boolean left, SweepEvent otherEvent, PolygonType polygonType) {
        this(point, left, otherEvent, polygonType, EdgeType.NORMAL);
    }

    /**
     * Instantiates a new Sweep event.
     *
     * @param point       the point
     * @param left        the left
     * @param otherEvent  the other event
     * @param polygonType the polygon type
     * @param edgeType    the edge type
     */
    public SweepEvent(Point point, boolean left, SweepEvent otherEvent, PolygonType polygonType, EdgeType edgeType) {
        this.left = left;
        this.point = point;
        this.otherEvent = otherEvent;
        this.polygonType = polygonType;
        this.type = edgeType;
        this.prevInResult = null;
        this.inResult = false;
        this.inOut = false;
        this.otherInOut = false;
        this.contourId = -1;
    }

    /**
     * Instantiates a new Sweep event.
     *
     * @param point   the point
     * @param left    the left
     * @param polygon the polygon
     * @param inOut   the in out
     */
    public SweepEvent(Point point, boolean left, int polygon, boolean inOut) {
        this.left = left;
        this.point = point;
        this.polygon = polygon;
        this.inOut = inOut;
    }

    /**
     * Is the line segment (point, otherEvent.point) below point p
     * @param p other point
     *
     * @return is point below?
     */
    public boolean below(Point p) {
        return (left) ? Triangle.signedArea(point, otherEvent.point, p) > 0 :
                Triangle.signedArea(otherEvent.point, point, p) > 0;
    }

    /**
     * Is the line segment (point, otherEvent.point) above point p
     * @param p other point
     *
     * @return is point above?
     */
    public boolean above(Point p) {
        return !below(p);
    }

    /**
     * Is the line segment (point, otherEvent.point) a vertical line segment
     * @return is line segment vertical
     */
    public boolean vertical() {
        return point.x == otherEvent.point.x;
    }

    /**
     * Return the line segment associated to the SweepEvent
     * @return the segment
     */
    public Segment segment() {
        return new Segment(point, otherEvent.point);
    }

    @Override
    public String toString() {
        return "SweepEvent{" +
                "left=" + left +
                ", inOut=" + inOut +
                ", point=" + point +
                ", polygon=" + polygon +
                '}';
    }
}
