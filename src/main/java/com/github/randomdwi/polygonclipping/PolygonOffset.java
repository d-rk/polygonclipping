package com.github.randomdwi.polygonclipping;

import com.github.randomdwi.polygonclipping.drawing.PolygonDraw;
import com.github.randomdwi.polygonclipping.geometry.Contour;
import com.github.randomdwi.polygonclipping.geometry.Point;
import com.github.randomdwi.polygonclipping.segment.Segment;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PolygonOffset {

    private static final double TWO_PI = Math.PI * 2;

    //An odd number so that one arc vertex will be exactly arcRadius from center.
    private int arcSegmentCount = 1;

    Contour createArcContour(Point center, double radius, Point arcStart, Point arcEnd) {

        double startAngle = Math.atan2(arcStart.y - center.y, arcStart.x - center.x);
        double endAngle = Math.atan2(arcEnd.y - center.y, arcEnd.x - center.x);

        if (startAngle < 0.0) {
            startAngle += TWO_PI;
        }

        if (endAngle < 0.0) {
            endAngle += TWO_PI;
        }

        double angle, segmentAngle;

        if (startAngle > endAngle) {
            angle = startAngle - endAngle;
        } else {
            angle = startAngle + TWO_PI - endAngle;
        }

        if (radius < 0.0) {
            // padding
            segmentAngle = -angle / arcSegmentCount;
        } else {
            // margin
            segmentAngle = (TWO_PI - angle) / arcSegmentCount;
        }

        Contour contour = new Contour();
        contour.add(center);

        for (int i = 0; i <= arcSegmentCount; i++) {
            angle = startAngle + segmentAngle * i;
            double x = center.x + Math.cos(angle) * Math.abs(radius);
            double y = center.y + Math.sin(angle) * Math.abs(radius);
            contour.add(new Point(x, y));
        }

        return contour;
    }

    Contour createOffsetRectangle(Segment segment, double offset) {

        Contour contour = new Contour();
        contour.add(segment.pBegin);
        contour.add(segment.pEnd);
        contour.add(segment.pEnd.plus(segment.getOutwardsNormal().multiply(offset)));
        contour.add(segment.pBegin.plus(segment.getOutwardsNormal().multiply(offset)));

        return contour;
    }

    public Polygon createOffsetPolygon(Contour contour, double offset) throws IOException {

        List<Boolean> pointConvexity = getConvexity(contour.getPoints());

        List<Contour> offsetContours = new ArrayList<>();

        for (int i = 0; i < contour.pointCount(); i++) {
            int nextI = (i + 1) % contour.pointCount();
            Segment segment = contour.segment(i);

            Contour offsetRectangle = createOffsetRectangle(segment, offset);
            offsetContours.add(offsetRectangle);

            if (pointConvexity.get(nextI) == (offset > 0)) {
                // add arc
                Segment nextSegment = contour.segment(nextI);
                Point endPoint = segment.pEnd.plus(segment.getOutwardsNormal().multiply(offset));
                Point nextPoint = nextSegment.pBegin.plus(nextSegment.getOutwardsNormal().multiply(offset));

                Contour arcContour = createArcContour(segment.pEnd, offset, endPoint, nextPoint);
                offsetContours.add(arcContour);
            }
        }

        return union(contour, offsetContours);
    }

    private Polygon union(Contour contour, List<Contour> offsetContours) {

        Polygon polygon = Polygon.from(contour);
        int i = 0;
        for (Contour offsetContour : offsetContours) {
            PolygonDraw.savePolygonImage(200, 200, polygon, "subject" + i + ".png");
            PolygonDraw.savePolygonImage(200, 200, Polygon.from(offsetContour), "clipping" + i + ".png");

            try {
                polygon = BooleanOperation.UNION(polygon, Polygon.from(offsetContour));
            } catch (Exception ex) {
                System.out.println("ill");
                try {
                    polygon.serialize(new FileOutputStream("subject.pol"));
                    Polygon.from(offsetContour).serialize(new FileOutputStream("clipping.pol"));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                throw ex;
            }


            PolygonDraw.savePolygonImage(200, 200, polygon, "result" + i++ + ".png");
        }

        return polygon;
    }

    private List<Boolean> getConvexity(List<Point> points) {

        List<Boolean> convexity = new ArrayList<>(points.size());

        int n = points.size();

        for (int i = 0; i < n; i++) {

            Point point = points.get(i);
            Point nextPoint = points.get((i + 1) % n);
            Point prevPoint = points.get((i + n - 1) % n);

            Point vectorPrev = prevPoint.minus(point);
            Point vectorNext = nextPoint.minus(point);

            // cross product
            double cross = vectorPrev.x * vectorNext.y - vectorNext.x * vectorPrev.y;
            double dot = vectorPrev.x * vectorNext.x + vectorPrev.y * vectorNext.y;

            // |v_p x v_next| = |v_p| * |v_next| * sin(alpha)
            // alpha <= 180° : sin(alpha) >= 0 => cross >= 0
            // alpha >  180° : sin(alpha) <  0 => cross <  0
            convexity.add(dot >= 0);
        }
        return convexity;
    }
}