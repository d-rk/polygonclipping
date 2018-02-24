package com.github.randomdwi.polygonclipping.sweepline;

import com.github.randomdwi.polygonclipping.geometry.Triangle;

import java.util.Comparator;

public class SweepEventComparator implements Comparator<SweepEvent> {

    private final boolean reverse;

    /**
     * Instantiates a new Sweep event comparator.
     *
     * @param reverse the reverse
     */
    public SweepEventComparator(boolean reverse) {
        this.reverse = reverse;
    }

    // for sorting sweep events
    // Compare two sweep events
    // Return true means that e1 is placed at the event queue after e2, i.e,, e1 is processed by the algorithm after e2
    @Override
    public int compare(SweepEvent e1, SweepEvent e2) {

        int direction = reverse ? -1 : 1;

        if (e1.point.x != e2.point.x) {
            // Different x-coordinate
            // smaller x-coordinate comes first
            return (e1.point.x < e2.point.x) ? direction : -direction;
        }

        // Different points, but same x-coordinate. The event with lower y-coordinate is processed first
        if (e1.point.y != e2.point.y) {
            return (e1.point.y < e2.point.y) ? direction : -direction;
        }

        // Same point, but one is a left endpoint and the other a right endpoint. The right endpoint is processed first
        if (e1.left != e2.left) {
            return !e1.left ? direction : -direction;
        }

        // Same point, both events are left endpoints or both are right endpoints.
        if (Triangle.signedArea(e1.point, e1.otherEvent.point, e2.otherEvent.point) != 0) { // not collinear
            if (reverse) {
                return e1.below(e2.otherEvent.point) ? direction : -direction;
            } else {
                // the event associate to the bottom segment is processed first
                return e1.above(e2.otherEvent.point) ? direction : -direction;
            }
        }

        if (e1.polygonType != null && e2.polygonType != null) {
            return e1.polygonType.ordinal() > e2.polygonType.ordinal() ? direction : -direction;
        }

        return e1.polygon > e2.polygon ? direction : -direction;
    }
}
