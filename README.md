# polygon clipping

[![CircleCI](https://circleci.com/gh/random-dwi/polygonclipping/tree/master.svg?style=svg)](https://circleci.com/gh/random-dwi/polygonclipping/tree/master)
[![Maven Central](https://img.shields.io/maven-central/v/com.github.random-dwi/polygon-clipping.svg)]()

## Description
A library for polygon clipping written in Java.
It implements the algorithm described in the paper `F. Martínez, A.J. Rueda, F.R. Feito. A new algorithm for computing Boolean operations on polygons. Computers & Geosciences, 35 (2009) `

## Examples

```java
Polygon subj = new Polygon("/polygons/samples/rectangle1");
Polygon clip = new Polygon("/polygons/samples/triangle2");

Polygon result = new BooleanOperation(subj, clip, BooleanOperation.Type.INTERSECTION).execute();
```