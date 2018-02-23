package com.dwilden.polygonclipping.geometry;

import com.dwilden.polygonclipping.segment.Segment;

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

    // is this contour a hole? (i.e. not an external contour)
    private boolean isHole = true;

    // is contour counterClockwise? (lazily initialized)
    private Boolean isCounterClockwise;

    public BoundingBox boundingBox() {
        BoundingBox boundingBox = new BoundingBox();
        points.forEach(p -> boundingBox.union(p.boundingBox()));
        return boundingBox;
    }

    public boolean clockwise() {
        return !counterClockwise();
    }

    public boolean counterClockwise() {
        if (isCounterClockwise != null) {
            return isCounterClockwise;
        }

        double area = 0.0;
        int n = points.size();

        // https://en.wikipedia.org/wiki/Shoelace_formula
        for (int i = 0; i < n; i++) {
            int i_next = (i + 1) % n;
            area += (points.get(i).y + points.get(i_next).y) * ((points.get(i).x - points.get(i_next).x));
        }

        isCounterClockwise = (area >= 0.0);
        return isCounterClockwise;
    }

    public int pointCount() {
        return points.size();
    }

    public int edgeCount() {
        return points.size();
    }

    public void move(double x, double y) {
        points.forEach(p -> {
            p.x += x;
            p.y += y;
        });
    }

    public Segment segment(int index) {
        int lastPointIdx = points.size() - 1;

        if (index == lastPointIdx) {
            return new Segment(points.get(lastPointIdx), points.get(0));
        } else {
            return new Segment(points.get(index), points.get(index + 1));
        }
    }

    public void setClockwise() {
        if (counterClockwise()) changeOrientation();
    }

    public void setCounterClockwise() {
        if (clockwise()) changeOrientation();
    }

    public void changeOrientation() {
        Collections.reverse(points);
        isCounterClockwise = isCounterClockwise != null ? !isCounterClockwise : null;
    }

    public void add(Point p) {
        points.add(p);
    }

    public void remove(int index) {
        points.remove(index);
    }

    public void clear() {
        points.clear();
        holes.clear();
    }

    public void clearHoles() {
        holes.clear();
    }

    public Point getPoint(int index) {
        return points.get(index);
    }

    public Point lastPoint() {
        return points.get(points.size() - 1);
    }

    public void addHole(int ind) {
        holes.add(ind);
    }

    public int holeCount() {
        return holes.size();
    }

    public int getHole(int index) {
        return holes.get(index);
    }

    public boolean isHole() {
        return isHole;
    }

    public void setIsHole(boolean isHole) {
        this.isHole = isHole;
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
        copy.isHole = isHole;
        copy.isCounterClockwise = isCounterClockwise;
        return copy;
    }
}
