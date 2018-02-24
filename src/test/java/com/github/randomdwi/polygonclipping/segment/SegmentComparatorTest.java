package com.github.randomdwi.polygonclipping.segment;

import com.github.randomdwi.polygonclipping.geometry.Point;
import com.github.randomdwi.polygonclipping.sweepline.SweepEvent;
import org.junit.Test;

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
}
