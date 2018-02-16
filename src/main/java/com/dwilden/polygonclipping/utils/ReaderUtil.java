package com.dwilden.polygonclipping.utils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ReaderUtil {

    public static int toInt(String line) {
        return Integer.valueOf(line.trim());
    }

    public static List<Double> toDoubleList(String line) {
        String[] parts = line.trim().split(" ");
        return Arrays.stream(parts).map(Double::valueOf).collect(Collectors.toList());
    }

    public static List<Integer> toIntegerList(String line) {
        String[] parts = line.trim().split(" ");
        return Arrays.stream(parts).map(Integer::valueOf).collect(Collectors.toList());
    }
}
