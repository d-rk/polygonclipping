package com.dwilden.polygonclipping;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Contour {

    /**
     * Set of points conforming the external contour
     */
    private List<Point> points = new ArrayList<>();

    /**
     * Holes of the contour. They are stored as the indexes of the holes in a polygon class
     */
    private List<Integer> holes = new ArrayList<>();

    private boolean external; // is the contour an external contour? (i.e., is it not a hole?)
    private Boolean precomputedCC;

    public BoundingBox boundingBox() {
        BoundingBox boundingBox = new BoundingBox();
        points.forEach(p -> boundingBox.union(p.boundingBox()));
        return boundingBox;
    }

    public boolean clockwise() {
        return !counterclockwise();
    }

    public boolean counterclockwise() {
        if (precomputedCC != null) {
            return precomputedCC;
        }

        double area = 0.0;
        int n = points.size();

        // https://en.wikipedia.org/wiki/Shoelace_formula
        for (int i=0; i < n; i++) {
            int i_next = (i + 1) % n;
            area += (points.get(i).y + points.get(i_next).y) * ((points.get(i).x - points.get(i_next).x));
        }

        precomputedCC = (area >= 0.0);
        return precomputedCC;
    }

    public int nvertices() {
        return points.size();
    }

    public int nedges() {
        return points.size();
    }

    public void move(double x, double y) {
        points.forEach(p -> {
            p.x += x;
            p.y += y;
        });
    }

    public Segment segment (int p) {
        int lastPointIdx = points.size() - 1;

        if (p == lastPointIdx) {
            return new Segment(points.get(lastPointIdx), points.get(0));
        } else {
            return new Segment(points.get(p), points.get(p + 1));
        }
    }

    public void setClockwise() {
        if (counterclockwise()) changeOrientation();
    }

    public void setCounterClockwise() {
        if (clockwise()) changeOrientation();
    }

    public void changeOrientation() {
        Collections.reverse(points);
        precomputedCC = precomputedCC != null ? !precomputedCC : null;
    }

    public void add(Point p) {
        points.add(p);
    }

    public void remove(int i) {
        points.remove(i);
    }

    public void clear() {
        points.clear();
        holes.clear();
    }

    public void clearHoles() {
        holes.clear();
    }

    public Point getPoint(int p) {
        return points.get(p);
    }

    public Point back() {
        return points.get(points.size() - 1);
    }

    public void addHole(int ind) {
        holes.add(ind);
    }

    public int nholes() {
        return holes.size();
    }

    public int getHole(int p) {
        return holes.get(p);
    }

    public boolean isExternal() {
        return external;
    }

    public void setExternal(boolean e) {
        external = e;
    }

    public void serialize(PrintWriter writer) {
        writer.println(points.size());
        points.forEach(p -> writer.println(String.format("\t%s %s", Double.toString(p.x), Double.toString(p.y))));
    }

    public List<Integer> getHoles() {
        return holes;
    }

    public void setHoles(List<Integer> holes) {
        this.holes = holes;
    }

    public Contour copy() {
        Contour copy = new Contour();
        copy.points = points.stream().map(Point::copy).collect(Collectors.toList());
        copy.holes.addAll(holes);
        copy.external = external;
        copy.precomputedCC = precomputedCC;
        return copy;
    }
}
