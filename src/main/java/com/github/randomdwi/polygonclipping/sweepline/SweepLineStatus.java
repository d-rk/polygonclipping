package com.github.randomdwi.polygonclipping.sweepline;

import com.github.randomdwi.polygonclipping.segment.SegmentComparator;

import java.util.Iterator;
import java.util.TreeSet;

public class SweepLineStatus {

    private TreeSet<SweepEvent> SL;

    /**
     * Instantiates a new Sweep line status.
     *
     * @param segmentComparator the segment comparator
     */
    public SweepLineStatus(SegmentComparator segmentComparator) {
        this.SL = new TreeSet<>(segmentComparator);
    }

    /**
     * Add event.
     *
     * @param event the event
     */
    public void addEvent(SweepEvent event) {
        if (!event.left) {
            throw new IllegalStateException("only left events can be added to SweepLineStatus");
        }
        SL.add(event);
    }

    /**
     * Gets previous event.
     *
     * @param event the event
     * @return the previous event
     */
    public SweepEvent getPreviousEvent(SweepEvent event) {
        Iterator<SweepEvent> it = SL.headSet(event, false).descendingIterator();
        return it.hasNext() ? it.next() : null;
    }

    /**
     * Gets next event.
     *
     * @param event the event
     * @return the next event
     */
    public SweepEvent getNextEvent(SweepEvent event) {
        Iterator<SweepEvent> it = SL.tailSet(event, false).iterator();
        return it.hasNext() ? it.next() : null;
    }

    /**
     * Remove event.
     *
     * @param event the event
     */
    public void removeEvent(SweepEvent event) {
        SL.remove(event);
    }
}
