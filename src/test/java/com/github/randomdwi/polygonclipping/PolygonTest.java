package com.github.randomdwi.polygonclipping;

import org.assertj.core.api.Assertions;
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
        Assertions.assertThat(p.contour(0).getHoles()).containsExactly(1);
        Assertions.assertThat(p.contour(1).getHoles()).containsExactly(2);
        Assertions.assertThat(p.contour(2).getHoles()).isEmpty();
    }
}
