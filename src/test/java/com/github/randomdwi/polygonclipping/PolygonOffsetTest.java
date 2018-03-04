package com.github.randomdwi.polygonclipping;

import com.github.randomdwi.polygonclipping.drawing.PolygonDraw;
import com.github.randomdwi.polygonclipping.geometry.Contour;
import com.github.randomdwi.polygonclipping.geometry.Point;
import com.github.randomdwi.polygonclipping.segment.Segment;
import org.junit.Test;

import java.awt.*;
import java.io.File;
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

        Polygon offset = new PolygonOffset().createOffsetPolygon(contour, 5.0);

        PolygonDraw draw = new PolygonDraw(200, 200, offset.boundingBox());
        draw.drawPolygon(offset, Color.YELLOW);
        draw.setAlpha(0.0f);
        draw.drawPolygon(Polygon.from(contour), Color.BLUE);
        draw.save(new File("test.png"));
    }
}
