package com.dwilden.polygonclipping.sweepline;

import com.dwilden.polygonclipping.segment.SegmentComparator;

import java.util.PriorityQueue;

public class SweepLine {

    // event queue (sorted events to be processed)
    public PriorityQueue<SweepEvent> eventQueue;

    public SweepLineStatus statusLine;

    public SweepLine(SegmentComparator segmentComparator) {
        this.eventQueue = new PriorityQueue<>(new SweepEventComparator(true));
        this.statusLine = new SweepLineStatus(segmentComparator);
    }
}
