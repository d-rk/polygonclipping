package com.github.randomdwi.polygonclipping.utils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ReaderUtil {

    /**
     * Convert input string to integer.
     *
     * @param line input string
     * @return integer value
     */
    public static int toInt(String line) {
        return Integer.valueOf(line.trim());
    }

    /**
     * Convert input string to double list.
     *
     * @param line input string
     * @return double list
     */
    public static List<Double> toDoubleList(String line) {
        String[] parts = line.trim().split(" ");
        return Arrays.stream(parts).map(Double::valueOf).collect(Collectors.toList());
    }

    /**
     * Convert input string to integer list.
     *
     * @param line input string
     * @return integer list
     */
    public static List<Integer> toIntegerList(String line) {
        String[] parts = line.trim().split(" ");
        return Arrays.stream(parts).map(Integer::valueOf).collect(Collectors.toList());
    }
}
