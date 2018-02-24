package com.github.randomdwi.polygonclipping.geometry;

import com.github.randomdwi.polygonclipping.segment.Segment;

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

    /**
     * Get bounding box of contour.
     *
     * @return bounding box
     */
    public BoundingBox boundingBox() {
        BoundingBox boundingBox = new BoundingBox();
        points.forEach(p -> boundingBox.combine(p.boundingBox()));
        return boundingBox;
    }

    /**
     * Are the points in clockwise order?
     *
     * @return clockwise
     */
    public boolean clockwise() {
        return !counterClockwise();
    }

    /**
     * Are the points in counter-clockwise order?
     *
     * @return counter-clockwise
     */
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

    /**
     * Get the number of points.
     *
     * @return point count
     */
    public int pointCount() {
        return points.size();
    }

    /**
     * Get the number of edges.
     *
     * @return edge count
     */
    public int edgeCount() {
        return points.size();
    }

    /**
     * Move the contour.
     *
     * @param dx distance in x direction
     * @param dy distance in y direction
     */
    public void move(double dx, double dy) {
        points.forEach(p -> {
            p.x += dx;
            p.y += dy;
        });
    }

    /**
     * Get a segment of the contour by index.
     *
     * @param index the index
     * @return the segment
     */
    public Segment segment(int index) {
        int lastPointIdx = points.size() - 1;

        if (index == lastPointIdx) {
            return new Segment(points.get(lastPointIdx), points.get(0));
        } else {
            return new Segment(points.get(index), points.get(index + 1));
        }
    }

    /**
     * Set the point order to clockwise.
     */
    public void setClockwise() {
        if (counterClockwise()) changeOrientation();
    }

    /**
     * Set the point order to counter-clockwise.
     */
    public void setCounterClockwise() {
        if (clockwise()) changeOrientation();
    }

    /**
     * Change point order of the points.
     */
    public void changeOrientation() {
        Collections.reverse(points);
        isCounterClockwise = isCounterClockwise != null ? !isCounterClockwise : null;
    }

    /**
     * Add point to contour.
     *
     * @param p the point
     */
    public void add(Point p) {
        points.add(p);
    }

    /**
     * Remove point at index from contour.
     *
     * @param index the index
     */
    public void remove(int index) {
        points.remove(index);
    }

    /**
     * Clear the contour.
     */
    public void clear() {
        points.clear();
        holes.clear();
    }

    /**
     * Clear holes of the contour.
     */
    public void clearHoles() {
        holes.clear();
    }

    /**
     * Get point in contour by index.
     *
     * @param index the index
     * @return the point
     */
    public Point getPoint(int index) {
        return points.get(index);
    }

    /**
     * Get last point in contour.
     *
     * @return the point
     */
    public Point lastPoint() {
        return points.get(points.size() - 1);
    }

    /**
     * Add hole to contour.
     *
     * @param index contour index of a hole
     */
    public void addHole(int index) {
        holes.add(index);
    }

    /**
     * Get number of holes in contour.
     *
     * @return hole count
     */
    public int holeCount() {
        return holes.size();
    }

    /**
     * Get the contour index of a hole with given index.
     *
     * @param index index of the hole
     * @return contour index in polygon
     */
    public int getHole(int index) {
        return holes.get(index);
    }

    /**
     * Is contour a hole.
     *
     * @return is it a hole?
     */
    public boolean isHole() {
        return isHole;
    }

    /**
     * Set contour to be a hole.
     *
     * @param isHole is it a hole
     */
    public void setIsHole(boolean isHole) {
        this.isHole = isHole;
    }

    /**
     * Serialize contour.
     *
     * @param writer writer to serialize with
     */
    public void serialize(PrintWriter writer) {
        writer.println(points.size());
        points.forEach(p -> writer.println(String.format("\t%s %s", Double.toString(p.x), Double.toString(p.y))));
    }

    /**
     * Get holes of contour.
     *
     * @return the holes
     */
    public List<Integer> getHoles() {
        return holes;
    }

    /**
     * Sets holes of the contour.
     *
     * @param holes the holes
     */
    public void setHoles(List<Integer> holes) {
        this.holes = holes;
    }

    /**
     * Create a copy of the contour.
     *
     * @return contour copy
     */
    public Contour copy() {
        Contour copy = new Contour();
        copy.points = points.stream().map(Point::copy).collect(Collectors.toList());
        copy.holes.addAll(holes);
        copy.isHole = isHole;
        copy.isCounterClockwise = isCounterClockwise;
        return copy;
    }
}
