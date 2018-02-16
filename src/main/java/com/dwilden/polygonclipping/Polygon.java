package com.dwilden.polygonclipping;


import com.dwilden.polygonclipping.sweepline.SweepEvent;
import com.dwilden.polygonclipping.sweepline.SweepEventComp;
import com.dwilden.polygonclipping.sweepline.SweepLineStatus;
import com.dwilden.polygonclipping.utils.ReaderUtil;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class Polygon {
    private List<Contour> contours = new ArrayList<>();

    public Polygon() {
    }

    public Polygon(File file) throws IOException {
        new Polygon(new FileInputStream(file));
    }

    public Polygon(InputStream inputStream) throws IOException {

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

            // read the contours
            int ncontours = ReaderUtil.toInt(reader.readLine());

            for (int i = 0; i < ncontours; i++) {
                Contour contour = new Contour();
                contours.add(contour);

                int npoints = ReaderUtil.toInt(reader.readLine());

                for (int j = 0; j < npoints; j++) {
                    List<Double> coords = ReaderUtil.toDoubleList(reader.readLine());
                    double px = coords.get(0);
                    double py = coords.get(1);

                    if (j > 0 && px == contour.back().x && py == contour.back().y) {
                        continue;
                    }
                    if (j == npoints - 1 && px == contour.getPoint(0).x && py == contour.getPoint(0).y) {
                        continue;
                    }

                    contour.add(new Point(px, py));
                }
                if (contour.nvertices() < 3) {
                    // not a valid contour, remove it
                    popBack();
                }
            }

            // read holes information
            while (reader.ready()) {
                String[] parts = reader.readLine().split(":");

                int contourId = ReaderUtil.toInt(parts[0]);
                List<Integer> holes = ReaderUtil.toIntegerList(parts[1]);

                holes.forEach(h -> {
                    contour(contourId).addHole(h);
                    contour(h).setExternal(false);
                });
            }
        }

        inputStream.close();
    }

    /**
     * Get the p-th contour
     */
    public Contour contour(int p) {
        return contours.get(p);
    }

    /**
     * Number of contours
     */
    public int ncontours() {
        return contours.size();
    }

    public boolean isEmpty() {
        return contours.isEmpty();
    }

    /**
     * Number of vertices
     */
    public int nvertices() {
        int nv = 0;
        for (int i = 0; i < contours.size(); i++)
            nv += contours.get(i).nvertices();
        return nv;
    }

    public BoundingBox boundingBox() {
        BoundingBox boundingBox = new BoundingBox();
        contours.forEach(c -> boundingBox.union(c.boundingBox()));
        return boundingBox;
    }

    public void move(double x, double y) {
        for (Contour contour : contours) {
            contour.move(x, y);
        }
    }

    public void join(Polygon polygon) {

        Polygon polygonCopy = polygon.copy();

        for (Contour contour : polygonCopy.contours) {
            add(contour);

            // shift the hole indices
            contour.setHoles(contour.getHoles().stream().map(h -> h + ncontours()).collect(Collectors.toList()));
        }
    }

    public Polygon copy() {
        Polygon copy = new Polygon();
        copy.contours = contours.stream().map(Contour::copy).collect(Collectors.toList());
        return copy;
    }

    public void add(Contour contour) {
        contours.add(contour);
    }

    public Contour back() {
        return contours.get(contours.size() - 1);
    }

    public void popBack() {
        contours.remove(contours.size() - 1);
    }

    public void remove(int i) {
        contours.remove(i);
    }

    public void clear() {
        contours.clear();
    }

    public void computeHoles() {

        contours.forEach(c -> c.getHoles().clear());

        if (contours.size() < 2) {
            if (contours.size() == 1) {
                contour(0).setCounterClockwise();
            }
            return;
        }

        List<SweepEvent> sweepEvents = new ArrayList<>(nvertices() * 2);

        for (int i = 0; i < contours.size(); i++) {
            contour(i).setCounterClockwise();
            for (int j = 0; j < contour(i).nedges(); j++) {
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

                sweepEvents.add(sBegin);
                sweepEvents.add(sEnd);
            }
        }

        sweepEvents.sort(new SweepEventComp(true));

        SweepLineStatus SL = new SweepLineStatus(true);
        Set<Integer> processedPolygons = new HashSet<>(contours.size());
        List<Integer> holeOf = new ArrayList<>(contours.size());

        for (int i=0; i < contours.size(); i++) {
            holeOf.add(-1);
        }

        for (int i = 0; i<sweepEvents.size () && processedPolygons.size() < contours.size(); i++){
            SweepEvent e = sweepEvents.get(i);

            if (e.left) { // the segment must be inserted into S
                SL.addEvent(e);

                if (!processedPolygons.contains(e.polygon)) {
                    processedPolygons.add(e.polygon);

                    SweepEvent prev = SL.getPreviousEvent(e);

                    if (prev == null) {
                        contour(e.polygon).setCounterClockwise();
                    } else {
                        if (!prev.inOut){
                            holeOf.set(e.polygon, prev.polygon);
                            contour(e.polygon).setExternal(false);
                            contour(prev.polygon).addHole(e.polygon);
                            if (contour(prev.polygon).counterclockwise()) {
                                contour(e.polygon).setClockwise();
                            } else {
                                contour(e.polygon).setCounterClockwise();
                            }
                        } else if (holeOf.get(prev.polygon) !=-1){
                            holeOf.set(e.polygon, holeOf.get(prev.polygon));
                            contour(e.polygon).setExternal(false);
                            contour(holeOf.get(e.polygon)).addHole(e.polygon);
                            if (contour(holeOf.get(e.polygon)).counterclockwise()) {
                                contour(e.polygon).setClockwise();
                            } else {
                                contour(e.polygon).setCounterClockwise();
                            }
                        } else{
                            contour(e.polygon).setCounterClockwise();
                        }
                    }
                }
            } else {
                // the segment must be removed from S
                SL.removeEvent(e.otherEvent);
            }
        }

        System.out.println();
    }

    private Iterator<SweepEvent> getDescendingIteratorToSegment(TreeSet<SweepEvent> SL, SweepEvent e) {
        return SL.headSet(e, true).descendingIterator();
    }

    public void serialize(OutputStream outputStream) {
        try (PrintWriter writer = new PrintWriter(outputStream)) {
            // write contours
            writer.println(contours.size());
            contours.forEach(c -> c.serialize(writer));

            // write holes of every contour
            for (int i = 0; i < contours.size(); i++) {
                Contour c = contours.get(i);

                if (c.nholes() > 0) {
                    String holes = c.getHoles().stream().map(String::valueOf).collect(Collectors.joining(" "));
                    writer.println(i + ": " + holes);
                }
            }
        }
    }
}