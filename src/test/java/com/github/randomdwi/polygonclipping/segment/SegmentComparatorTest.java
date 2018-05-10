package com.github.randomdwi.polygonclipping.segment;

import com.github.randomdwi.polygonclipping.enums.PolygonType;
import com.github.randomdwi.polygonclipping.geometry.Point;
import com.github.randomdwi.polygonclipping.sweepline.SweepEvent;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * The type Segment comparator test.
 */
public class SegmentComparatorTest {

    /**
     * Test equality.
     */
    @Test
    public void testEquality() {

        Point p1 = new Point(0, 1);
        Point p2 = new Point(0, 1);

        SweepEvent event = new SweepEvent(p1, true, 0, false);
        event.otherEvent = new SweepEvent(p2, false, 0, true);

        SweepEvent eventCopy = new SweepEvent(p1.copy(), true, 0, false);
        eventCopy.otherEvent = new SweepEvent(p2.copy(), false, 0, true);

        SegmentComparator comparator = new SegmentComparator(true);
        assertThat(comparator.compare(event, event)).isEqualTo(0);
        assertThat(comparator.compare(event, eventCopy)).isEqualTo(0);

        SegmentComparator comparator2 = new SegmentComparator(false);
        assertThat(comparator2.compare(event, event)).isEqualTo(0);
        assertThat(comparator2.compare(event, eventCopy)).isEqualTo(0);
    }

    @Test
    public void testRoundingErrors() {

        // se
        SweepEvent otherEvent1 = new SweepEvent(new Point(10, 5), false, null, PolygonType.CLIPPING);
        SweepEvent event1 = new SweepEvent(new Point(10, 0), true, otherEvent1, PolygonType.CLIPPING);
        otherEvent1.otherEvent = event1;

        // prev
        SweepEvent otherEvent2 = new SweepEvent(new Point(12.269952498697734, 0.5449673790581597), false, null, PolygonType.CLIPPING);
        SweepEvent event2 = new SweepEvent(new Point(10, 0), true, otherEvent2, PolygonType.CLIPPING);
        otherEvent2.otherEvent = event2;

        // next
        SweepEvent otherEvent3 = new SweepEvent(new Point(10, 5), false, null, PolygonType.SUBJECT);
        SweepEvent event3 = new SweepEvent(new Point(10, 0), true, otherEvent1, PolygonType.SUBJECT);
        otherEvent3.otherEvent = event3;


        SegmentComparator segmentComparator = new SegmentComparator(false);

        SortedSet<SweepEvent> SL = new TreeSet<>(segmentComparator);
        SL.add(event1);
        SL.add(event2);
        SL.add(event3);

        List<SweepEvent> eventList = new ArrayList<>(SL);

        assertThat(eventList.get(0) == event2).isTrue();
        assertThat(eventList.get(1) == event3).isTrue();
        assertThat(eventList.get(2) == event1).isTrue();

        //now add a rounding errors
        event1.point = new Point(9.999999999999998, 0.0);
        event2.point = new Point(9.999999999999998, 0.0);

        SL = new TreeSet<>(segmentComparator);
        SL.add(event1);
        SL.add(event2);
        SL.add(event3);

        eventList = new ArrayList<>(SL);

        assertThat(eventList.get(0) == event2).isTrue();
        assertThat(eventList.get(1) == event3).isTrue();
        assertThat(eventList.get(2) == event1).isTrue();
    }
}
