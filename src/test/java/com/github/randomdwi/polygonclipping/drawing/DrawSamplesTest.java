package com.github.randomdwi.polygonclipping.drawing;

import com.github.randomdwi.polygonclipping.BooleanOperation;
import com.github.randomdwi.polygonclipping.BooleanOperationTest;
import com.github.randomdwi.polygonclipping.Polygon;
import com.github.randomdwi.polygonclipping.geometry.BoundingBox;
import org.junit.Ignore;
import org.junit.Test;

import java.awt.*;
import java.io.File;
import java.io.IOException;

@Ignore
public class DrawSamplesTest {

    @Test
    public void drawSample1() throws IOException {

        Polygon subj = new Polygon(BooleanOperationTest.class.getResourceAsStream("/polygons/samples/rectangle1"));
        Polygon clip = new Polygon(BooleanOperationTest.class.getResourceAsStream("/polygons/samples/triangle2"));

        Polygon result = new BooleanOperation(subj, clip, BooleanOperation.Type.INTERSECTION).execute();

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
        draw.drawPolygon(new BooleanOperation(subj, clip, BooleanOperation.Type.DIFFERENCE).execute(), Color.RED);
        draw.save(new File("images/sample_1_difference.png"));

        draw = new PolygonDraw(200, 200, bb);
        draw.setAlpha(0.2f);
        draw.drawPolygon(subj, Color.BLUE);
        draw.drawPolygon(clip, Color.GREEN);

        draw.setAlpha(0.6f);
        draw.setStrokeWidth(2);
        draw.drawPolygon(new BooleanOperation(subj, clip, BooleanOperation.Type.XOR).execute(), Color.RED);
        draw.save(new File("images/sample_1_xor.png"));

        draw = new PolygonDraw(200, 200, bb);
        draw.setAlpha(0.2f);
        draw.drawPolygon(subj, Color.BLUE);
        draw.drawPolygon(clip, Color.GREEN);

        draw.setAlpha(0.6f);
        draw.setStrokeWidth(2);
        draw.drawPolygon(new BooleanOperation(subj, clip, BooleanOperation.Type.UNION).execute(), Color.RED);
        draw.save(new File("images/sample_1_union.png"));
    }
}
