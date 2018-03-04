package com.github.randomdwi.polygonclipping;

import com.github.randomdwi.polygonclipping.geometry.Contour;
import com.github.randomdwi.polygonclipping.geometry.Point;
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

        Polygon result = BooleanOperation.INTERSECTION(subj, clip);

        assertThat(result).isNotNull();
        assertThat(result.contourCount()).isEqualTo(1);

        Contour contour = result.contour(0);
        assertThat(contour.getHoles()).isEmpty();
        assertThat(contour.pointCount()).isEqualTo(3);
        assertThat(contour.getPoint(0).x).isCloseTo(0.5, ALLOWED_OFFSET);
        assertThat(contour.getPoint(0).y).isCloseTo(0.0, ALLOWED_OFFSET);
        assertThat(contour.getPoint(1).x).isCloseTo(0.9083333, ALLOWED_OFFSET);
        assertThat(contour.getPoint(1).y).isCloseTo(0.0, ALLOWED_OFFSET);
        assertThat(contour.getPoint(2).x).isCloseTo(0.5, ALLOWED_OFFSET);
        assertThat(contour.getPoint(2).y).isCloseTo(0.49, ALLOWED_OFFSET);
    }

    @Test
    public void testDegenerated() throws IOException {

        Polygon subj = new Polygon(BooleanOperationTest.class.getResourceAsStream("/polygons/test/triangle1.pol"));
        Polygon clip = new Polygon(BooleanOperationTest.class.getResourceAsStream("/polygons/test/degenerated.pol"));

        Polygon result = BooleanOperation.UNION(subj, clip);

        assertThat(result).isNotNull();
        assertThat(result.contourCount()).isEqualTo(1);

        Contour contour = result.contour(0);
        assertThat(contour.getHoles()).isEmpty();
        assertThat(contour.pointCount()).isEqualTo(3);
        assertThat(contour.getPoint(0).x).isCloseTo(0.5, ALLOWED_OFFSET);
        assertThat(contour.getPoint(0).y).isCloseTo(0.0, ALLOWED_OFFSET);
        assertThat(contour.getPoint(1).x).isCloseTo(0.9083333, ALLOWED_OFFSET);
        assertThat(contour.getPoint(1).y).isCloseTo(0.0, ALLOWED_OFFSET);
        assertThat(contour.getPoint(2).x).isCloseTo(0.5, ALLOWED_OFFSET);
        assertThat(contour.getPoint(2).y).isCloseTo(0.49, ALLOWED_OFFSET);
    }

    @Test
    public void testPolygonsWithRoundingIssues() throws IOException {

        Polygon subj = new Polygon(BooleanOperationTest.class.getResourceAsStream("/polygons/test/triangle1.pol"));
        Polygon clip = new Polygon(BooleanOperationTest.class.getResourceAsStream("/polygons/test/fan1.pol"));

        Polygon result = BooleanOperation.UNION(subj, clip);

        assertThat(result).isNotNull();
        assertThat(result.contourCount()).isEqualTo(1);

        Contour contour = result.contour(0);
        assertThat(contour.getHoles()).isEmpty();
        assertThat(contour.pointCount()).isEqualTo(10);
        assertThat(contour.getPoint(0).x).isCloseTo(5.0, ALLOWED_OFFSET);
        assertThat(contour.getPoint(0).y).isCloseTo(0.0, ALLOWED_OFFSET);
        assertThat(contour.getPoint(1).x).isCloseTo(10.0, ALLOWED_OFFSET);
        assertThat(contour.getPoint(1).y).isCloseTo(0.0, ALLOWED_OFFSET);
        assertThat(contour.getPoint(2).x).isCloseTo(10.0, ALLOWED_OFFSET);
        assertThat(contour.getPoint(2).y).isCloseTo(5.0, ALLOWED_OFFSET);
        assertThat(contour.getPoint(3).x).isCloseTo(9.9999999999, ALLOWED_OFFSET);
        assertThat(contour.getPoint(3).y).isCloseTo(0.0, ALLOWED_OFFSET);
        assertThat(contour.getPoint(4).x).isCloseTo(12.269952498697734, ALLOWED_OFFSET);
        assertThat(contour.getPoint(4).y).isCloseTo(0.5449673790581597, ALLOWED_OFFSET);
        assertThat(contour.getPoint(5).x).isCloseTo(14.045084971874736, ALLOWED_OFFSET);
        assertThat(contour.getPoint(5).y).isCloseTo(2.061073738537633, ALLOWED_OFFSET);

        assertThat(contour.getPoint(6).x).isCloseTo(14.938441702975688, ALLOWED_OFFSET);
        assertThat(contour.getPoint(6).y).isCloseTo(4.217827674798844, ALLOWED_OFFSET);
        assertThat(contour.getPoint(7).x).isCloseTo(14.755282581475768, ALLOWED_OFFSET);
        assertThat(contour.getPoint(7).y).isCloseTo(6.545084971874736, ALLOWED_OFFSET);
        assertThat(contour.getPoint(8).x).isCloseTo(13.535533905932738, ALLOWED_OFFSET);
        assertThat(contour.getPoint(8).y).isCloseTo(8.535533905932738, ALLOWED_OFFSET);
        assertThat(contour.getPoint(9).x).isCloseTo(10.0, ALLOWED_OFFSET);
        assertThat(contour.getPoint(9).y).isCloseTo(5.0, ALLOWED_OFFSET);
    }


    @Test
    public void testPolygonsWithRoundingIssues2() throws IOException {

        Polygon subj = new Polygon(BooleanOperationTest.class.getResourceAsStream("/polygons/test/subject1.pol"));
        Polygon clip = new Polygon(BooleanOperationTest.class.getResourceAsStream("/polygons/test/clippingTriangle"));

        Polygon result = BooleanOperation.UNION(subj, clip);

        assertThat(result).isNotNull();
        assertThat(result.contourCount()).isEqualTo(1);

        Contour contour = result.contour(0);
        assertThat(contour.getHoles()).isEmpty();
        assertThat(contour.pointCount()).isEqualTo(4);
        assertThat(contour.getPoint(0).x).isCloseTo(0.0, ALLOWED_OFFSET);
        assertThat(contour.getPoint(0).y).isCloseTo(5.0, ALLOWED_OFFSET);
        assertThat(contour.getPoint(1).x).isCloseTo(5.0, ALLOWED_OFFSET);
        assertThat(contour.getPoint(1).y).isCloseTo(0.0, ALLOWED_OFFSET);
        assertThat(contour.getPoint(2).x).isCloseTo(15.0, ALLOWED_OFFSET);
        assertThat(contour.getPoint(2).y).isCloseTo(0.0, ALLOWED_OFFSET);
        assertThat(contour.getPoint(3).x).isCloseTo(0.0, ALLOWED_OFFSET);
        assertThat(contour.getPoint(3).y).isCloseTo(10.0, ALLOWED_OFFSET);

        // modify clipping polygon
        Contour clippingContour = clip.getContours().get(0);
        clippingContour.getPoints().set(1, new Point(0.0, 5.000000000000001));
        clippingContour.getPoints().set(2, new Point(4.999999999999999, 0.0));

        Polygon resultRounding = BooleanOperation.UNION(subj, clip);

        assertThat(resultRounding).isNotNull();
        assertThat(resultRounding.contourCount()).isEqualTo(1);

        contour = result.contour(0);
        assertThat(contour.getHoles()).isEmpty();
        assertThat(contour.pointCount()).isEqualTo(4);
        assertThat(contour.getPoint(0).x).isCloseTo(0.0, ALLOWED_OFFSET);
        assertThat(contour.getPoint(0).y).isCloseTo(5.0, ALLOWED_OFFSET);
        assertThat(contour.getPoint(1).x).isCloseTo(5.0, ALLOWED_OFFSET);
        assertThat(contour.getPoint(1).y).isCloseTo(0.0, ALLOWED_OFFSET);
        assertThat(contour.getPoint(2).x).isCloseTo(15.0, ALLOWED_OFFSET);
        assertThat(contour.getPoint(2).y).isCloseTo(0.0, ALLOWED_OFFSET);
        assertThat(contour.getPoint(3).x).isCloseTo(0.0, ALLOWED_OFFSET);
        assertThat(contour.getPoint(3).y).isCloseTo(10.0, ALLOWED_OFFSET);
    }
}
