package com.dwilden.polygonclipping;

import com.dwilden.polygonclipping.enums.BooleanOperationType;
import org.assertj.core.data.Offset;
import org.junit.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.offset;

public class BooleanOperationTest {

    private static final Offset<Double> ALLOWED_OFFSET = offset(0.000001);

    @Test
    public void testIntersectionRectWithTriangle() throws IOException {

        Polygon subj = new Polygon(BooleanOperationTest.class.getResourceAsStream("/polygons/samples/rectangle1"));
        Polygon clip = new Polygon(BooleanOperationTest.class.getResourceAsStream("/polygons/samples/triangle2"));

        Polygon result = new BooleanOperation(subj, clip, BooleanOperationType.INTERSECTION).execute();

        assertThat(result).isNotNull();
        assertThat(result.ncontours()).isEqualTo(1);

        Contour contour = result.contour(0);
        assertThat(contour.getHoles()).isEmpty();
        assertThat(contour.nvertices()).isEqualTo(3);
        assertThat(contour.getPoint(0).x).isCloseTo(0.5, ALLOWED_OFFSET);
        assertThat(contour.getPoint(0).y).isCloseTo(0.0, ALLOWED_OFFSET);
        assertThat(contour.getPoint(1).x).isCloseTo(0.9083333, ALLOWED_OFFSET);
        assertThat(contour.getPoint(1).y).isCloseTo(0.0, ALLOWED_OFFSET);
        assertThat(contour.getPoint(2).x).isCloseTo(0.5, ALLOWED_OFFSET);
        assertThat(contour.getPoint(2).y).isCloseTo(0.49, ALLOWED_OFFSET);
    }
}
