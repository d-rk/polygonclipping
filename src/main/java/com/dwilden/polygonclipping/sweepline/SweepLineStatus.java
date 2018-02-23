package com.dwilden.polygonclipping.sweepline;

import com.dwilden.polygonclipping.segment.SegmentComparator;

import java.util.Iterator;
import java.util.TreeSet;

public class SweepLineStatus {

    private TreeSet<SweepEvent> SL;

    public SweepLineStatus(SegmentComparator segmentComparator) {
        this.SL = new TreeSet<>(segmentComparator);
    }

    public void addEvent(SweepEvent event) {
        if (!event.left) {
            throw new IllegalStateException("only left events can be added to SweepLineStatus");
        }
        SL.add(event);
    }

    public SweepEvent getPreviousEvent(SweepEvent event) {
        Iterator<SweepEvent> it = SL.headSet(event, false).descendingIterator();
        return it.hasNext() ? it.next() : null;
    }

    public SweepEvent getNextEvent(SweepEvent event) {
        Iterator<SweepEvent> it = SL.tailSet(event, false).iterator();
        return it.hasNext() ? it.next() : null;
    }

    public void removeEvent(SweepEvent event) {
        SL.remove(event);
    }
}
