package com.github.randomdwi.polygonclipping;


import com.github.randomdwi.polygonclipping.geometry.BoundingBox;
import com.github.randomdwi.polygonclipping.geometry.Contour;
import com.github.randomdwi.polygonclipping.geometry.Point;
import com.github.randomdwi.polygonclipping.segment.Segment;
import com.github.randomdwi.polygonclipping.segment.SegmentComparator;
import com.github.randomdwi.polygonclipping.sweepline.SweepEvent;
import com.github.randomdwi.polygonclipping.sweepline.SweepLine;
import com.github.randomdwi.polygonclipping.utils.ReaderUtil;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class Polygon {

    private List<Contour> contours = new ArrayList<>();

    /**
     * Instantiates an empty Polygon.
     */
    public Polygon() {
    }

    /**
     * Instantiates an empty Polygon.
     */
    public Polygon(List<Contour> contours) {
        this.contours = contours;
        computeHoles();
    }

    /**
     * Instantiates a new Polygon.
     *
     * @param file file defining the polygon
     * @throws IOException error reading input file
     */
    public Polygon(File file) throws IOException {
        new Polygon(new FileInputStream(file));
    }

    /**
     * Instantiates a new Polygon.
     *
     * @param inputStream input stream defining the polygon
     * @throws IOException error reading input stream
     */
    public Polygon(InputStream inputStream) throws IOException {

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

            // read the contours
            int contourCount = ReaderUtil.toInt(reader.readLine());

            for (int i = 0; i < contourCount; i++) {
                Contour contour = new Contour();
                contours.add(contour);

                int pointCount = ReaderUtil.toInt(reader.readLine());

                for (int j = 0; j < pointCount; j++) {
                    List<Double> coords = ReaderUtil.toDoubleList(reader.readLine());
                    double px = coords.get(0);
                    double py = coords.get(1);

                    if (j > 0 && px == contour.lastPoint().x && py == contour.lastPoint().y) {
                        continue;
                    }
                    if (j == pointCount - 1 && px == contour.getPoint(0).x && py == contour.getPoint(0).y) {
                        continue;
                    }

                    contour.add(new Point(px, py));
                }
                if (contour.pointCount() < 3) {
                    // not a valid contour, remove it
                    removeLastContour();
                }
            }

            // read holes information
            while (reader.ready()) {
                String[] parts = reader.readLine().split(":");

                int contourId = ReaderUtil.toInt(parts[0]);
                List<Integer> holes = ReaderUtil.toIntegerList(parts[1]);

                holes.forEach(hole -> {
                    contour(contourId).addHole(hole);
                    contour(hole).setIsHole(true);
                });
            }
        }

        inputStream.close();
    }

    /**
     * Get contour by index.
     *
     * @param index the index
     * @return the contour
     */
    public Contour contour(int index) {
        return contours.get(index);
    }

    /**
     * Returns the number of contours in the polygon.
     *
     * @return number of contours
     */
    public int contourCount() {
        return contours.size();
    }

    /**
     * Returns whether the polygon is empty.
     *
     * @return no contours in polygon
     */
    public boolean isEmpty() {
        return contours.isEmpty();
    }

    /**
     * Get number of points in polygon.
     *
     * @return point count
     */
    public int pointCount() {
        return contours.stream().mapToInt(Contour::pointCount).sum();
    }

    /**
     * Get boundingBox of the polygon.
     *
     * @return the bounding box
     */
    public BoundingBox boundingBox() {
        BoundingBox boundingBox = new BoundingBox();
        contours.forEach(c -> boundingBox.combine(c.boundingBox()));
        return boundingBox;
    }

    /**
     * Move the polygon.
     *
     * @param dx distance in x direction
     * @param dy distance in y direction
     */
    public void move(double dx, double dy) {
        contours.forEach(c -> c.move(dx, dy));
    }

    /**
     * Join current polygon with the given polygon.
     *
     * @param polygon the polygon to join with
     */
    public void join(Polygon polygon) {

        Polygon polygonCopy = polygon.copy();

        for (Contour contour : polygonCopy.contours) {
            addContour(contour);

            // shift the hole indices
            contour.setHoles(contour.getHoles().stream().map(h -> h + contourCount()).collect(Collectors.toList()));
        }
    }

    /**
     * Create a copy of this polygon.
     *
     * @return polygon copy
     */
    public Polygon copy() {
        Polygon copy = new Polygon();
        copy.contours = contours.stream().map(Contour::copy).collect(Collectors.toList());
        return copy;
    }

    /**
     * Get a list of all contours
     *
     * @return contours
     */
    public List<Contour> getContours() {
        return contours;
    }

    /**
     * Add contour to polygon.
     *
     * @param contour the contour
     */
    public void addContour(Contour contour) {
        contours.add(contour);
    }

    /**
     * Get last contour of polygon.
     *
     * @return the contour
     */
    public Contour lastContour() {
        return contours.get(contours.size() - 1);
    }

    /**
     * Remove last contour.
     */
    public void removeLastContour() {
        contours.remove(contours.size() - 1);
    }

    /**
     * Remove contour by index
     *
     * @param index the index
     */
    public void removeContour(int index) {
        contours.remove(index);
    }

    /**
     * Clear contours of polygon.
     */
    public void clear() {
        contours.clear();
    }

    /**
     * Compute which of the contours are holes in other contours.
     */
    public void computeHoles() {

        contours.forEach(c -> c.getHoles().clear());

        if (contours.size() < 2) {
            if (contours.size() == 1) {
                contour(0).setCounterClockwise();
            }
            return;
        }

        SweepLine sweepLine = new SweepLine(new SegmentComparator(true));

        for (int i = 0; i < contours.size(); i++) {
            contour(i).setCounterClockwise();
            for (int j = 0; j < contour(i).edgeCount(); j++) {
                Segment s = contour(i).segment(j);
                if (s.isVertical()) { // vertical segments are not processed
                    continue;
                }

                SweepEvent sBegin;
                SweepEvent sEnd;

                if (s.pBegin.x < s.pEnd.x) {
                    sBegin = new SweepEvent(s.pBegin, true, i, false);
                    sEnd = new SweepEvent(s.pEnd, false, i, true);
                } else {
                    sBegin = new SweepEvent(s.pBegin, false, i, true);
                    sEnd = new SweepEvent(s.pEnd, true, i, true);
                }

                sBegin.otherEvent = sEnd;
                sEnd.otherEvent = sBegin;

                sweepLine.eventQueue.add(sBegin);
                sweepLine.eventQueue.add(sEnd);
            }
        }

        Set<Integer> processedPolygons = new HashSet<>(contours.size());
        Map<Integer, Integer> holeMap = new HashMap<>(contours.size());

        while (!sweepLine.eventQueue.isEmpty() && processedPolygons.size() < contours.size()) {

            SweepEvent e = sweepLine.eventQueue.poll();

            if (e.left) {
                // the segment must be inserted into S
                sweepLine.statusLine.addEvent(e);

                if (!processedPolygons.contains(e.polygon)) {
                    processedPolygons.add(e.polygon);

                    SweepEvent prev = sweepLine.statusLine.getPreviousEvent(e);

                    if (prev == null) {
                        contour(e.polygon).setCounterClockwise();
                    } else {
                        if (!prev.inOut) {
                            addHole(holeMap, e.polygon, prev.polygon);
                        } else if (holeMap.containsKey(prev.polygon)) {
                            addHole(holeMap, e.polygon, holeMap.get(prev.polygon));
                        } else {
                            contour(e.polygon).setCounterClockwise();
                        }
                    }
                }
            } else {
                // the segment must be removed from S
                sweepLine.statusLine.removeEvent(e.otherEvent);
            }
        }
    }

    private void addHole(Map<Integer, Integer> holeMap, int holeContourIndex, int contourWithHoleIndex) {
        holeMap.put(holeContourIndex, contourWithHoleIndex);
        contour(holeContourIndex).setIsHole(true);
        contour(contourWithHoleIndex).addHole(holeContourIndex);

        if (contour(contourWithHoleIndex).counterClockwise()) {
            contour(holeContourIndex).setClockwise();
        } else {
            contour(holeContourIndex).setCounterClockwise();
        }
    }

    /**
     * Serialize the polygon.
     *
     * @param outputStream output to serialize to.
     */
    public void serialize(OutputStream outputStream) {
        try (PrintWriter writer = new PrintWriter(outputStream)) {
            // write contours
            writer.println(contours.size());
            contours.forEach(c -> c.serialize(writer));

            // write holes of every contour
            for (int i = 0; i < contours.size(); i++) {
                Contour c = contours.get(i);

                if (c.holeCount() > 0) {
                    String holes = c.getHoles().stream().map(String::valueOf).collect(Collectors.joining(" "));
                    writer.println(i + ": " + holes);
                }
            }
        }
    }

    /**
     * Create polygon from given contours.
     * Contours can be external or holes inside of other contours.
     *
     * @param contours contours
     * @return polygon
     */
    public static Polygon from(Contour... contours) {
        List<Contour> contourList = Arrays.stream(contours).collect(Collectors.toList());
        return new Polygon(contourList);
    }

    /**
     * Create polygon from points.
     * Polygon will contain one contour with the given points.
     *
     * @param points points
     * @return polygon
     */
    public static Polygon from(double[][] points) {
        return Polygon.from(Contour.from(points));
    }
}