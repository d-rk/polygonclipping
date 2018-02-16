package com.dwilden.polygonclipping;

import com.dwilden.polygonclipping.sweepline.SweepEvent;
import com.dwilden.polygonclipping.sweepline.SweepEventComp;

import java.util.Comparator;

import static com.dwilden.polygonclipping.Utilities.signedArea;

public class SegmentComp implements Comparator<SweepEvent> {

    boolean forHoleAlgorithm;

    public SegmentComp(boolean forHoleAlgorithm) {
        this.forHoleAlgorithm = forHoleAlgorithm;
    }

    // for sorting edges in the sweep line (sl)
    // le1 and le2 are the left events of line segments (le1.point, le1.otherEvent.point) and (le2.point, le2.otherEvent.point)
    @Override
    public int compare(SweepEvent le1, SweepEvent le2) {
        if (le1.equals(le2)) {
            return 0;
        }
        if (signedArea(le1.point, le1.otherEvent.point, le2.point) != 0 ||
                signedArea(le1.point, le1.otherEvent.point, le2.otherEvent.point) != 0) {
            // Segments are not collinear
            // If they share their left endpoint use the right endpoint to sort
            if (le1.point.equals(le2.point)) {
                return le1.below(le2.otherEvent.point) ? -1 : 1;
            }

            //TODO cleanup
            if (!forHoleAlgorithm) {
                // Different left endpoint: use the left endpoint to sort
                if (le1.point.x == le2.point.x) {
                    return le1.point.y < le2.point.y ? -1 : 1;
                }
                SweepEventComp comp = new SweepEventComp(false);
                // has the line segment associated to e1 been inserted into S after the line segment associated to e2 ?
                if (comp.compare(le1, le2) < 0) {
                    return le2.above(le1.point) ? -1 : 1;
                }
                // The line segment associated to e2 has been inserted into S after the line segment associated to e1
                return le1.below(le2.point) ? -1 : 1;
            } else {
                // Different points
                SweepEventComp comp = new SweepEventComp(true);
                // has the segment associated to e1 been sorted in evp before the segment associated to e2?
                if (comp.compare(le1, le2) < 0) {
                    return le1.below(le2.point) ? -1 : 1;
                }
                // The segment associated to e2 has been sorted in evp before the segment associated to e1
                return le2.above(le1.point) ? -1 : 1;
            }
        }
        // Segments are collinear
        if (le1.polygonType != null && !le1.polygonType.equals(le2.polygonType)) {
            return le1.polygonType.ordinal() < le2.polygonType.ordinal() ? -1 : 1;
        }

        // Just a consistent criterion is used
        if (le1.point == le2.point) {
            return 0;
        }
        SweepEventComp comp = new SweepEventComp(forHoleAlgorithm);
        return comp.compare(le1, le2);
    }
}
