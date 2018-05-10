package com.github.randomdwi.polygonclipping;

import com.github.randomdwi.polygonclipping.drawing.PolygonDraw;
import com.github.randomdwi.polygonclipping.geometry.Contour;
import com.github.randomdwi.polygonclipping.geometry.Point;
import com.github.randomdwi.polygonclipping.segment.Segment;
import org.junit.Test;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class PolygonOffsetTest {

    @Test
    public void testCreateArcSegment() throws IOException {

        Point center = new Point(5.0, 5.0);
        Point start = new Point(5.0, 10.0);
        Point end = new Point(10.0, 5.0);
        double radius = 10.0;

        PolygonOffset offset = new PolygonOffset();
        Contour arcContour = offset.createArcContour(center, radius, start, end);

        PolygonDraw draw = new PolygonDraw(200, 200, arcContour.boundingBox());
        draw.drawPolygon(Polygon.from(arcContour), Color.YELLOW);
        draw.save(new File("test.png"));
    }

    @Test
    public void testCreateOffsetRectangle() throws IOException {

        Point start = new Point(5.0, 10.0);
        Point end = new Point(10.0, 5.0);
        Segment segment = new Segment(start, end);

        double offset = 10.0;

        Contour offsetRectangle = new PolygonOffset().createOffsetRectangle(segment, offset);
        PolygonDraw draw = new PolygonDraw(200, 200, offsetRectangle.boundingBox());
        draw.drawPolygon(Polygon.from(offsetRectangle), Color.YELLOW);
        draw.save(new File("test.png"));
    }

    @Test
    public void testCreateContourOffset() throws IOException {

        double[][] points = {{5.0, 5.0}, {10.0, 5.0}, {5.0, 10.0}};
        Contour contour = Contour.from(points);

        Polygon polygon = Polygon.from(contour);

        Polygon offset = new PolygonOffset().createOffsetPolygon(contour, -1.0);

        PolygonDraw draw = new PolygonDraw(600, 600, offset.boundingBox().combine(polygon.boundingBox()));
        draw.setAlpha(0.3f);
        draw.setStrokeWidth(2);
        draw.drawPolygon(offset, Color.RED);
        draw.setAlpha(0.0f);
        draw.setStrokeWidth(1);
        draw.drawPolygon(polygon, Color.BLUE);
        draw.save(new File("test.png"));

        try {
            offset.serialize(new FileOutputStream("offset.pol"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testCreateOffsetPolygonWithHoles() throws IOException {

        Polygon polygon = new Polygon(PolygonOffsetTest.class.getResourceAsStream("/polygons/samples/polygonwithholes"));

        Polygon offset = new PolygonOffset().createOffsetPolygon(polygon, 0.02);

        PolygonDraw draw = new PolygonDraw(600, 600, offset.boundingBox().combine(polygon.boundingBox()));
        draw.setAlpha(0.3f);
        draw.setStrokeWidth(2);
        draw.drawPolygon(offset, Color.RED);
        draw.setAlpha(0.2f);
        draw.setStrokeWidth(1);
        draw.drawPolygon(polygon, Color.BLUE);
        draw.save(new File("test.png"));

        try {
            offset.serialize(new FileOutputStream("offset.pol"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
