package com.github.randomdwi.polygonclipping.drawing;

import com.github.randomdwi.polygonclipping.BooleanOperation;
import com.github.randomdwi.polygonclipping.BooleanOperationTest;
import com.github.randomdwi.polygonclipping.Polygon;
import com.github.randomdwi.polygonclipping.geometry.BoundingBox;
import com.github.randomdwi.polygonclipping.geometry.Contour;
import org.junit.Ignore;
import org.junit.Test;

import java.awt.*;
import java.io.File;
import java.io.IOException;

@Ignore
public class DrawSamplesTest {

    @Test
    public void testCreatePolygon() throws IOException {

        double[][] points = {{2.5,7.5}, {5.0, 5.0}, {7.5, 7.5}, {5.0, 10.0}};
        Polygon p = Polygon.from(points);

        PolygonDraw draw = new PolygonDraw(200, 200, p.boundingBox());
        draw.drawPolygon(p, Color.YELLOW);
        draw.save(new File("images/create_polygon.png"));
    }

    @Test
    public void testCreatePolygonWithHoles() throws IOException {

        double[][] outerContour = {{0.0,0.0}, {10.0, 0.0}, {10.0, 10.0}, {0.0, 10.0}};
        double[][] hole1 = {{0.5,0.5}, {4.0, 0.5}, {2.0, 4.0}};
        double[][] hole2 = {{2.5,7.5}, {5.0, 5.0}, {7.5, 7.5}, {5.0, 10.0}};

        Polygon p = Polygon.from(Contour.from(outerContour), Contour.from(hole1), Contour.from(hole2));

        PolygonDraw draw = new PolygonDraw(200, 200, p.boundingBox());
        draw.drawPolygon(p, Color.BLUE);
        draw.save(new File("images/create_polygon_with_holes.png"));
    }

    @Test
    public void drawSample1() throws IOException {

        Polygon subj = new Polygon(BooleanOperationTest.class.getResourceAsStream("/polygons/samples/rectangle1"));
        Polygon clip = new Polygon(BooleanOperationTest.class.getResourceAsStream("/polygons/samples/triangle2"));

        Polygon result = BooleanOperation.INTERSECTION(subj, clip);

        BoundingBox bb = new BoundingBox().combine(subj.boundingBox()).combine(clip.boundingBox()).combine(result.boundingBox());

        PolygonDraw draw = new PolygonDraw(200, 200, bb);
        draw.drawPolygon(subj, Color.BLUE);
        draw.drawPolygon(clip, Color.GREEN);
        draw.save(new File("images/sample_1.png"));

        draw = new PolygonDraw(200, 200, bb);
        draw.setAlpha(0.2f);
        draw.drawPolygon(subj, Color.BLUE);
        draw.drawPolygon(clip, Color.GREEN);

        draw.setAlpha(0.6f);
        draw.setStrokeWidth(2);
        draw.drawPolygon(result, Color.RED);
        draw.save(new File("images/sample_1_intersection.png"));

        draw = new PolygonDraw(200, 200, bb);
        draw.setAlpha(0.2f);
        draw.drawPolygon(subj, Color.BLUE);
        draw.drawPolygon(clip, Color.GREEN);

        draw.setAlpha(0.6f);
        draw.setStrokeWidth(2);
        draw.drawPolygon(BooleanOperation.DIFFERENCE(subj, clip), Color.RED);
        draw.save(new File("images/sample_1_difference.png"));

        draw = new PolygonDraw(200, 200, bb);
        draw.setAlpha(0.2f);
        draw.drawPolygon(subj, Color.BLUE);
        draw.drawPolygon(clip, Color.GREEN);

        draw.setAlpha(0.6f);
        draw.setStrokeWidth(2);
        draw.drawPolygon(BooleanOperation.XOR(subj, clip), Color.RED);
        draw.save(new File("images/sample_1_xor.png"));

        draw = new PolygonDraw(200, 200, bb);
        draw.setAlpha(0.2f);
        draw.drawPolygon(subj, Color.BLUE);
        draw.drawPolygon(clip, Color.GREEN);

        draw.setAlpha(0.6f);
        draw.setStrokeWidth(2);
        draw.drawPolygon(BooleanOperation.UNION(subj, clip), Color.RED);
        draw.save(new File("images/sample_1_union.png"));
    }
}
