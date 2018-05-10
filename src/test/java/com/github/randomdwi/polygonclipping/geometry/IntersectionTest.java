package com.github.randomdwi.polygonclipping.geometry;

import com.github.randomdwi.polygonclipping.segment.Segment;
import org.junit.Test;

import static com.github.randomdwi.polygonclipping.geometry.Intersection.Type.OVERLAPPING;
import static org.assertj.core.api.Assertions.assertThat;

public class IntersectionTest {

    @Test
    public void testRoundingError() {

        Segment segment1 = new Segment(new Point(10, 0), new Point(10, 5));
        Segment segment2 = new Segment(new Point(9.999999999999998, 0), new Point(10, 5));

        Intersection intersection = new Intersection(segment1, segment1);
        assertThat(intersection.type).isEqualTo(OVERLAPPING);
        assertThat(intersection.point).isEqualTo(segment1.pBegin);
        assertThat(intersection.pi1).isEqualTo(segment1.pEnd);

        Intersection intersection2 = new Intersection(segment1, segment2);
        assertThat(intersection2.type).isEqualTo(OVERLAPPING);
        assertThat(intersection.point).isEqualTo(segment1.pBegin);
        assertThat(intersection.pi1).isEqualTo(segment1.pEnd);

    }
}
