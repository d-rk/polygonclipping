package com.github.randomdwi.polygonclipping.sweepline;

import com.github.randomdwi.polygonclipping.Polygon;
import com.github.randomdwi.polygonclipping.drawing.PolygonDraw;
import com.github.randomdwi.polygonclipping.geometry.BoundingBox;
import com.github.randomdwi.polygonclipping.segment.SegmentComparator;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class DrawingSweepLineStatus extends SweepLineStatus {

    private final Polygon subject;
    private final Polygon clipping;
    private final String imageFolder;
    private int imageIdx = 0;

    private java.util.List<SweepEvent> resultEvents = new ArrayList<>();

    /**
     * Instantiates a new Sweep line status.
     *
     * @param segmentComparator the segment comparator
     * @param subject           subject polygon
     * @param clipping          clipping polygon
     * @param imageFolder       folder for debug images
     */
    public DrawingSweepLineStatus(SegmentComparator segmentComparator, Polygon subject, Polygon clipping, String imageFolder) {
        super(segmentComparator);
        this.subject = subject;
        this.clipping = clipping;
        this.imageFolder = imageFolder;
        initImageFolder();
    }

    private void initImageFolder() {

        File folder = new File(imageFolder);

        if (!folder.exists()) {
            folder.mkdirs();
        }

        File[] files = folder.listFiles(file -> file.getName().endsWith(".png"));

        if (files != null) {
            Arrays.stream(files).forEach(File::delete);
        }
    }

    @Override
    public void addEvent(SweepEvent event) {
        super.addEvent(event);
        drawStatusLines(event, true);
    }

    @Override
    public void removeEvent(SweepEvent event) {
        super.removeEvent(event);

        if (event.inResult) {
            resultEvents.add(event);
        } else if (event.otherEvent.inResult) {
            resultEvents.add(event.otherEvent);
        }

        drawStatusLines(event, false);
    }

    private void drawStatusLines(SweepEvent event, boolean eventAdded) {
        BoundingBox boundingBox = new BoundingBox().combine(subject.boundingBox()).combine(clipping.boundingBox());
        PolygonDraw draw = new PolygonDraw(1000, 1000, boundingBox);

        draw.drawPolygon(subject, Color.DARK_GRAY);
        draw.drawPolygon(clipping, Color.DARK_GRAY);

        // draw all lines in status lines
        SL.iterator().forEachRemaining(e -> drawStatusLine(draw, e, Color.WHITE, 3));
        resultEvents.stream().filter(e -> e.inResult).forEach(e -> drawStatusLine(draw, e, Color.GREEN, 3));

        if (eventAdded) {
            drawStatusLine(draw, event, new Color(0, 0, 255), 6);

            SweepEvent previousEvent = getPreviousEvent(event);
            drawStatusLine(draw, previousEvent, new Color(90, 50, 110), 5);

            SweepEvent nextEvent = getNextEvent(event);
            drawStatusLine(draw, nextEvent, new Color(150, 150, 255), 4);
        } else {
            if (!resultEvents.contains(event)) {
                drawStatusLine(draw, event, Color.RED, 6);
            }
        }

        try {
            draw.save(new File(imageFolder, String.format("status_%03d.png", imageIdx++)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void drawStatusLine(PolygonDraw draw, SweepEvent event, Color color, int pointWidth) {
        if (event != null) {
            draw.setStrokeWidth(2);
            draw.drawLine(event.point, event.otherEvent.point, color, 0.4);
            draw.setStrokeWidth(pointWidth);
            draw.drawPoint(event.point, color, 1.0);
        }
    }
}
