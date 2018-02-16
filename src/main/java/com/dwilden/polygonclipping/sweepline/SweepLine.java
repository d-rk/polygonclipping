package com.dwilden.polygonclipping.sweepline;

import java.util.PriorityQueue;

public class SweepLine {

    // event queue (sorted events to be processed)
    public PriorityQueue<SweepEvent> eventQueue;

    public SweepLineStatus statusLine;

    public SweepLine(boolean forHoleAlgorithm) {
        this.eventQueue = new PriorityQueue<>(new SweepEventComp(true));
        this.statusLine = new SweepLineStatus(forHoleAlgorithm);
    }
}
