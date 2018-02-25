package com.github.randomdwi.polygonclipping.drawing;

import com.github.randomdwi.polygonclipping.BooleanOperation;
import com.github.randomdwi.polygonclipping.BooleanOperationTest;
import com.github.randomdwi.polygonclipping.Polygon;
import com.github.randomdwi.polygonclipping.PolygonTest;
import com.github.randomdwi.polygonclipping.geometry.BoundingBox;
import org.junit.Test;

import java.awt.*;
import java.io.File;
import java.io.IOException;

public class PolygonDrawTest {

    @Test
    public void testDrawContour2() throws IOException {

        Polygon p = new Polygon(PolygonTest.class.getResourceAsStream("/polygons/samples/touchingpoint"));

        PolygonDraw draw = new PolygonDraw(500, 500, p.boundingBox());

        draw.drawPolygon(p, Color.BLUE);
        draw.save(new File("test.png"));
    }

    @Test
    public void testDrawContour() throws IOException {

        Polygon p = new Polygon(PolygonTest.class.getResourceAsStream("/polygons/samples/polygonwithholes"));

        PolygonDraw draw = new PolygonDraw(500, 500, p.boundingBox());

        draw.drawPolygon(p, Color.BLUE);

        draw.save(new File("test.png"));
    }

    @Test
    public void drawIntersection() throws IOException {


        Polygon subj = new Polygon(BooleanOperationTest.class.getResourceAsStream("/polygons/samples/rectangle1"));
        Polygon clip = new Polygon(BooleanOperationTest.class.getResourceAsStream("/polygons/samples/triangle2"));

        Polygon result = BooleanOperation.XOR(subj, clip);

        BoundingBox bb = new BoundingBox().combine(subj.boundingBox()).combine(clip.boundingBox()).combine(result.boundingBox());

        PolygonDraw draw = new PolygonDraw(500, 500, bb);

        draw.drawPolygon(subj, Color.BLUE);
        draw.drawPolygon(clip, Color.GREEN);
        draw.drawPolygon(result, Color.RED);

        draw.save(new File("test.png"));
    }
}
