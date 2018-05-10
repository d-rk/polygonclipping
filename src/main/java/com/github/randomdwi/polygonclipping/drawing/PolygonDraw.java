package com.github.randomdwi.polygonclipping.drawing;

import com.github.randomdwi.polygonclipping.geometry.BoundingBox;
import com.github.randomdwi.polygonclipping.geometry.Contour;
import com.github.randomdwi.polygonclipping.geometry.Point;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class PolygonDraw {

    private final int width;
    private final int height;
    private final int centerX;
    private final int centerY;
    private final double sX;
    private final double sY;
    private final double tX;
    private final double tY;
    private final BufferedImage image;
    private final Graphics2D graphics;
    private int strokeWidth = 1;
    private int paddingPixels = 20;
    private float paddingScale = 0.05f;
    private float alpha = 0.6f;

    /**
     * Create a new polygon draw instance
     * @param width width of the image
     * @param height height of the image
     * @param boundingBox boundingBox of everything that should be drawn
     */
    public PolygonDraw(int width, int height, BoundingBox boundingBox) {
        this.width = width;
        this.height = height;
        this.centerX = (int) Math.round(width * 0.5);
        this.centerY = (int) Math.round(height * 0.5);
        this.sX = width / boundingBox.getWidth();
        this.sY = height / boundingBox.getHeight();
        this.tX = centerX - boundingBox.getCenter().x * sX;
        this.tY = centerY - boundingBox.getCenter().y * sY;
        this.image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        this.graphics = image.createGraphics();
        setPadding(paddingPixels);
        drawGrid();
    }

    /**
     * Draw a polygon on the image
     * @param polygon polygon
     * @param color color to use
     */
    public void drawPolygon(com.github.randomdwi.polygonclipping.Polygon polygon, Color color) {

        Set<Contour> drawnContours = new HashSet<>();

        // sort contours so that external contour is drawn before its holes
        polygon.getOrderedContours().forEach(c -> {
            if (!drawnContours.contains(c)) {
                drawContourWithHoles(polygon, c, color, drawnContours);
            } else {
                drawShape(contourToAwtPolygon(c), color, false);
            }
        });
    }

    /**
     * Save image
     * @param outputFile output file
     * @throws IOException error writing image
     */
    public void save(File outputFile) throws IOException {
        String fileFormat = getFileFormat(outputFile);
        ImageIO.write(image, fileFormat, outputFile);
    }

    /**
     * Set stroke width to use when drawing polygons
     * @param strokeWidth stroke width
     */
    public void setStrokeWidth(int strokeWidth) {
        this.strokeWidth = strokeWidth;
    }

    /**
     * Set padding pixels for drawing on image
     * @param padding pixels to pad the border
     */
    public void setPadding(int padding) {

        if (padding >= width || padding >= height) {
            throw new IllegalArgumentException("padding cannot be greater than image dimensions");
        }

        this.paddingPixels = padding;
        this.paddingScale = 1.0f - padding / (float)width;
    }

    /**
     * Set alpha to use for drawing polygon fill
     * @param alpha alpha in range [0.0 - 1.0]
     */
    public void setAlpha(float alpha) {
        this.alpha = alpha;
    }

    private void drawGrid() {
        graphics.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{width*0.005f}, 0));
        graphics.setColor(Color.DARK_GRAY);

        graphics.drawLine(0, mapYToImage(0.0), width, mapYToImage(0.0));
        graphics.drawLine(mapXToImage(0.0), 0, mapXToImage(0.0), height);
    }

    private void drawShape(Shape shape, Color color, boolean filled) {

        graphics.setStroke(new BasicStroke(strokeWidth));

        if (filled) {
            Color darkerColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), Math.round(255 * alpha));
            graphics.setColor(darkerColor);
            graphics.fill(shape);
        }

        graphics.setColor(color);
        graphics.draw(shape);
    }

    @NotNull
    private Polygon contourToAwtPolygon(Contour contour) {
        int[] px = contour.getPoints().stream().mapToInt(p -> mapXToImage(p.x)).toArray();
        int[] py = contour.getPoints().stream().mapToInt(p -> mapYToImage(p.y)).toArray();

        return new Polygon(px, py, contour.pointCount());
    }

    private void drawContourWithHoles(com.github.randomdwi.polygonclipping.Polygon polygon, Contour contour, Color color, Set<Contour> drawnContours) {

        // draw the outer contour
        Polygon awtPolygon = contourToAwtPolygon(contour);
        Area area = new Area(awtPolygon);

        contour.getHoles().forEach(holeIdx -> {
            Contour hole = polygon.contour(holeIdx);

            Area awtHolePolygon = new Area(contourToAwtPolygon(hole));
            area.subtract(awtHolePolygon);
            drawnContours.add(hole);
        });

        drawShape(area, color, true);
        drawShape(awtPolygon, color, false);
        drawnContours.add(contour);
    }

    private int mapXToImage(double x) {
        double imageX = x * sX + tX;
        return (int) Math.round((imageX - centerX)* paddingScale + centerX);
    }

    private int mapYToImage(double y) {
        double imageY = y * sY + tY;
        return height - (int) Math.round((imageY - centerY)* paddingScale + centerY);
    }

    private String getFileFormat(File outputFile) {
        String[] filenameParts = outputFile.getName().toLowerCase().split("\\.");
        return filenameParts[filenameParts.length - 1];
    }

    public static void drawPolygonImage(int width, int height, com.github.randomdwi.polygonclipping.Polygon polygon, String filename) {
        PolygonDraw draw = new PolygonDraw(width, height, polygon.boundingBox());
        draw.drawPolygon(polygon, Color.BLUE);
        try {
            draw.save(new File(filename));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void drawContourImage(int width, int height, Contour contour, String filename) {
        PolygonDraw draw = new PolygonDraw(width, height, contour.boundingBox());
        draw.drawPolygon(com.github.randomdwi.polygonclipping.Polygon.from(contour), Color.BLUE);
        try {
            draw.save(new File(filename));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void drawLine(Point pointA, Point pointB, Color color, double alpha) {
        graphics.setStroke(new BasicStroke(strokeWidth));
        graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), (int)Math.round(255 * alpha)));
        graphics.drawLine(mapXToImage(pointA.x), mapYToImage(pointA.y), mapXToImage(pointB.x), mapYToImage(pointB.y));
    }

    public void drawPoint(Point point, Color color, double alpha) {
        drawLine(point, point, color, alpha);
    }
}
