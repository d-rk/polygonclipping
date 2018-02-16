package com.dwilden.polygonclipping;

import com.dwilden.polygonclipping.enums.BooleanOperationType;

import java.io.FileOutputStream;
import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {

        Polygon subj = new Polygon(Main.class.getResourceAsStream("/polygons/samples/rectangle1"));
        Polygon clip = new Polygon(Main.class.getResourceAsStream("/polygons/samples/triangle2"));

        BooleanOperation boi = new BooleanOperation(subj, clip, BooleanOperationType.INTERSECTION);
        Polygon result = boi.execute();

        FileOutputStream outputStream = new FileOutputStream("out");
        result.serialize(outputStream);
        outputStream.close();

//        Polygon p = new Polygon(Main.class.getResourceAsStream("/polygons/samples/polygonwithholes"));
//
//
//        p.computeHoles();
//
//        FileOutputStream outputStream = new FileOutputStream("out");
//        p.serialize(outputStream);
//        outputStream.close();

    }
}
