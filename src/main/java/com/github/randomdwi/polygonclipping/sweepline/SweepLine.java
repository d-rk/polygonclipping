package com.github.randomdwi.polygonclipping.sweepline;

import com.github.randomdwi.polygonclipping.segment.SegmentComparator;

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
