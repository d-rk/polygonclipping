package com.dwilden.polygonclipping;

import org.junit.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class PolygonTest {

    @Test
    public void testComputeHoles() throws IOException {

        Polygon p = new Polygon(PolygonTest.class.getResourceAsStream("/polygons/samples/polygonwithholes"));

        assertThat(p.contourCount()).isEqualTo(3);

        p.computeHoles();

        assertThat(p.contourCount()).isEqualTo(3);
        assertThat(p.contour(0).getHoles()).containsExactly(1);
        assertThat(p.contour(1).getHoles()).containsExactly(2);
        assertThat(p.contour(2).getHoles()).isEmpty();
    }
}
