/*
 * #%L
 * anwiba commons core
 * %%
 * Copyright (C) 2007 - 2018 Andreas W. Bartels
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */
 
package net.anwiba.spatial.geometry;

import net.anwiba.spatial.coordinate.Coordinate;
import net.anwiba.spatial.coordinate.CoordinateSequenceFactory;
import net.anwiba.spatial.coordinate.CoordinateSequenceUtilities;
import net.anwiba.spatial.coordinate.ICoordinateSequenceFactory;
import net.anwiba.spatial.geometry.GeometryType;
import net.anwiba.spatial.geometry.IBaseGeometry;
import net.anwiba.spatial.geometry.IGeometry;
import net.anwiba.spatial.geometry.IGeometryCollection;
import net.anwiba.spatial.geometry.IGeometryFactory;
import net.anwiba.spatial.geometry.IGeometryTypeVisitor;
import net.anwiba.spatial.geometry.ILineString;
import net.anwiba.spatial.geometry.ILinearRing;
import net.anwiba.spatial.geometry.IMultiLineString;
import net.anwiba.spatial.geometry.IMultiPoint;
import net.anwiba.spatial.geometry.IMultiPolygon;
import net.anwiba.spatial.geometry.IPoint;
import net.anwiba.spatial.geometry.IPolygon;
import net.anwiba.spatial.geometry.utilities.GeometryUtilities;

public class TestGeometryFactory {

  private static final ICoordinateSequenceFactory coordinateSequenceFactory = new CoordinateSequenceFactory();
  private static IGeometryFactory geometryFactory = GeometryUtilities.getDefaultGeometryFactory();

  public static IGeometryFactory getGeometryFactory() {
    return geometryFactory;
  }

  public static IGeometry create(final GeometryType geometryType) {
    return geometryType.accept(new IGeometryTypeVisitor<IGeometry, RuntimeException>() {

      @Override
      public IGeometry visitCollection() throws RuntimeException {
        return createGeometryCollection();
      }

      @Override
      public IGeometry visitLineString() throws RuntimeException {
        return createLineString();
      }

      @Override
      public IGeometry visitLinearRing() throws RuntimeException {
        return createLinearRing();
      }

      @Override
      public IGeometry visitMultiLineString() throws RuntimeException {
        return createMultiLineString();
      }

      @Override
      public IGeometry visitMultiPoint() throws RuntimeException {
        return createMultiPoint();
      }

      @Override
      public IGeometry visitMultiPolygon() throws RuntimeException {
        return createMultiPolygonWithHoles();
      }

      @Override
      public IGeometry visitPoint() throws RuntimeException {
        return createPoint();
      }

      @Override
      public IGeometry visitPolygon() throws RuntimeException {
        return createPolygon();
      }

      @Override
      public IGeometry visitUnknown() throws RuntimeException {
        throw new UnsupportedOperationException();
      }
    });
  }

  public static IPoint createPoint() {
    return geometryFactory.createPoint(new Coordinate(5, 5));
  }

  public static IPoint createPointZ() {
    return geometryFactory.createPoint(new Coordinate(5, 5, 5, false));
  }

  public static IPoint createPointZM() {
    return geometryFactory.createPoint(new Coordinate(5, 5, 5, 5));
  }

  public static IPoint createPointM() {
    return geometryFactory.createPoint(new Coordinate(5, 5, 5, true));
  }

  public static IMultiPoint createOnePointMultiPoint() {
    final Coordinate coordinate = new Coordinate(5, 5);
    return geometryFactory.createMultiPoint(coordinateSequenceFactory.create(new Coordinate[]{ coordinate }));
  }

  public static IMultiPoint createMultiPoint() {
    final Coordinate coordinate00 = new Coordinate(5, 5);
    final Coordinate coordinate01 = new Coordinate(15, 8);
    return geometryFactory.createMultiPoint(coordinateSequenceFactory.create(new Coordinate[]{
        coordinate00,
        coordinate01 }));
  }

  public static IMultiPoint createMultiPointZ() {
    final Coordinate coordinate00 = new Coordinate(5, 5, 5, false);
    final Coordinate coordinate01 = new Coordinate(15, 8, 5, false);
    return geometryFactory.createMultiPoint(coordinateSequenceFactory.create(new Coordinate[]{
        coordinate00,
        coordinate01 }));
  }

  public static IMultiPoint createMultiPointM() {
    final Coordinate coordinate00 = new Coordinate(5, 5, 5, true);
    final Coordinate coordinate01 = new Coordinate(15, 8, 5, true);
    return geometryFactory.createMultiPoint(coordinateSequenceFactory.create(new Coordinate[]{
        coordinate00,
        coordinate01 }));
  }

  public static IMultiPoint createMultiPointZM() {
    final Coordinate coordinate00 = new Coordinate(5, 5, 5, 5);
    final Coordinate coordinate01 = new Coordinate(15, 8, 5, 5);
    return geometryFactory.createMultiPoint(coordinateSequenceFactory.create(new Coordinate[]{
        coordinate00,
        coordinate01 }));
  }

  public static ILineString createLineString() {
    final Coordinate coordinate00 = new Coordinate(5, 5);
    final Coordinate coordinate01 = new Coordinate(15, 8);
    return geometryFactory.createLineString(coordinateSequenceFactory.create(new Coordinate[]{
        coordinate00,
        coordinate01 }));
  }

  public static ILineString createLineStringZ() {
    final Coordinate coordinate00 = new Coordinate(5, 5, 5, false);
    final Coordinate coordinate01 = new Coordinate(15, 8, 5, false);
    return geometryFactory.createLineString(coordinateSequenceFactory.create(new Coordinate[]{
        coordinate00,
        coordinate01 }));
  }

  public static ILineString createLineStringM() {
    final Coordinate coordinate00 = new Coordinate(5, 5, 5, true);
    final Coordinate coordinate01 = new Coordinate(15, 8, 5, true);
    return geometryFactory.createLineString(coordinateSequenceFactory.create(new Coordinate[]{
        coordinate00,
        coordinate01 }));
  }

  public static ILineString createLineStringZM() {
    final Coordinate coordinate00 = new Coordinate(5, 5, 5, 5);
    final Coordinate coordinate01 = new Coordinate(15, 8, 5, 5);
    return geometryFactory.createLineString(coordinateSequenceFactory.create(new Coordinate[]{
        coordinate00,
        coordinate01 }));
  }

  public static ILinearRing createMirroredLinearRing() {
    final double[] xs = new double[]{ 50, 50, 100, 50 };
    final double[] ys = new double[]{ 50, 150, 100, 50 };
    return geometryFactory.createLinearRing(coordinateSequenceFactory.create(xs, ys));
  }

  public static ILinearRing createLinearRing() {
    final double[] xs = new double[]{ 50, 150, 100, 50 };
    final double[] ys = new double[]{ 50, 50, 100, 50 };
    return geometryFactory.createLinearRing(coordinateSequenceFactory.create(xs, ys));
  }

  public static ILinearRing createRevertedHoleLinearRing() {
    final double[] xs = new double[]{ 75, 125, 125, 75, 75 };
    final double[] ys = new double[]{ 75, 75, 125, 125, 75 };
    return geometryFactory.createLinearRing(CoordinateSequenceUtilities.reverse(coordinateSequenceFactory
        .create(xs, ys)));
  }

  public static ILinearRing createHoleLinearRing() {
    final double[] xs = new double[]{ 75, 125, 125, 75, 75 };
    final double[] ys = new double[]{ 75, 75, 125, 125, 75 };
    return geometryFactory.createLinearRing(coordinateSequenceFactory.create(xs, ys));
  }

  public static ILinearRing createShellLinearRing() {
    final double[] xs = new double[]{ 50, 50, 150, 150, 50 };
    final double[] ys = new double[]{ 50, 150, 150, 50, 50 };
    return geometryFactory.createLinearRing(coordinateSequenceFactory.create(xs, ys));
  }

  public static ILinearRing createNeighbourShellLinearRing() {
    final double[] xs = new double[]{ 175, 175, 200, 200, 175 };
    final double[] ys = new double[]{ 50, 150, 150, 50, 50 };
    return geometryFactory.createLinearRing(coordinateSequenceFactory.create(xs, ys));
  }

  public static ILinearRing createLinearRingZ() {
    final double[] xs = new double[]{ 50, 50, 100, 50 };
    final double[] ys = new double[]{ 50, 150, 100, 50 };
    final double[] zs = new double[]{ 5, 5, 5, 5 };
    return geometryFactory.createLinearRing(coordinateSequenceFactory.create(xs, ys, zs, false));
  }

  public static ILinearRing createLinearRingM() {
    final double[] xs = new double[]{ 50, 50, 100, 50 };
    final double[] ys = new double[]{ 50, 150, 100, 50 };
    final double[] zs = new double[]{ 5, 5, 5, 5 };
    return geometryFactory.createLinearRing(coordinateSequenceFactory.create(xs, ys, zs, true));
  }

  public static ILinearRing createLinearRingZM() {
    final double[] xs = new double[]{ 50, 50, 100, 50 };
    final double[] ys = new double[]{ 50, 150, 100, 50 };
    final double[] zs = new double[]{ 5, 5, 5, 5 };
    final double[] ms = new double[]{ 5, 5, 5, 5 };
    return geometryFactory.createLinearRing(coordinateSequenceFactory.create(xs, ys, zs, ms));
  }

  public static IMultiLineString createMultiLineString() {
    final double[][] xs = new double[][]{ { 50, 50, 150, 150 }, { 75, 75, 125, 125 } };
    final double[][] ys = new double[][]{ { 50, 150, 150, 50 }, { 75, 125, 125, 75 } };
    return geometryFactory.createMultiLineString(xs, ys);
  }

  public static IMultiLineString createMultiLineStringZ() {
    final double[][] xs = new double[][]{ { 50, 50, 150, 150 }, { 75, 75, 125, 125 } };
    final double[][] ys = new double[][]{ { 50, 150, 150, 50 }, { 75, 125, 125, 75 } };
    final double[][] zs = new double[][]{ { 30, 33, 31, 45 }, { 20, 22, 26, 28 } };
    return geometryFactory.createMultiLineString(xs, ys, zs);
  }

  public static IMultiLineString createMultiLineStringM() {
    final double[][] xs = new double[][]{ { 50, 50, 150, 150 }, { 75, 75, 125, 125 } };
    final double[][] ys = new double[][]{ { 50, 150, 150, 50 }, { 75, 125, 125, 75 } };
    final double[][] zs = new double[][]{ { 5, 5, 5, 5 }, { 5, 5, 5, 5 } };
    return geometryFactory.createMultiLineString(xs, ys, zs, true);
  }

  public static IMultiLineString createMultiLineStringZM() {
    final double[][] xs = new double[][]{ { 50, 50, 150, 150 }, { 75, 75, 125, 125 } };
    final double[][] ys = new double[][]{ { 50, 150, 150, 50 }, { 75, 125, 125, 75 } };
    final double[][] zs = new double[][]{ { 5, 5, 5, 5 }, { 5, 5, 5, 5 } };
    final double[][] ms = new double[][]{ { 5, 5, 5, 5 }, { 5, 5, 5, 5 } };
    return geometryFactory.createMultiLineString(xs, ys, zs, ms);
  }

  public static IPolygon createPolygon() {
    final double[] xs = new double[]{ 50, 50, 150, 150, 50 };
    final double[] ys = new double[]{ 50, 150, 150, 50, 50 };
    return geometryFactory.createPolygon(xs, ys);
  }

  public static IPolygon createPolygonZ() {
    final double[] xs = new double[]{ 50, 50, 150, 150, 50 };
    final double[] ys = new double[]{ 50, 150, 150, 50, 50 };
    final double[] zs = new double[]{ 5, 5, 5, 5, 5 };
    return geometryFactory.createPolygon(coordinateSequenceFactory.create(xs, ys, zs, false));
  }

  public static IPolygon createPolygonM() {
    final double[] xs = new double[]{ 50, 50, 150, 150, 50 };
    final double[] ys = new double[]{ 50, 150, 150, 50, 50 };
    final double[] zs = new double[]{ 5, 5, 5, 5, 5 };
    return geometryFactory.createPolygon(coordinateSequenceFactory.create(xs, ys, zs, true));
  }

  public static IPolygon createPolygonZM() {
    final double[] xs = new double[]{ 50, 50, 150, 150, 50 };
    final double[] ys = new double[]{ 50, 150, 150, 50, 50 };
    final double[] zs = new double[]{ 5, 5, 5, 5, 5 };
    final double[] ms = new double[]{ 5, 5, 5, 5, 5 };
    return geometryFactory.createPolygon(coordinateSequenceFactory.create(xs, ys, zs, ms));
  }

  public static IPolygon createPolygonWithHoles() {
    final double[] xs = new double[]{ 50, 50, 150, 150, 50 };
    final double[] ys = new double[]{ 50, 150, 150, 50, 50 };
    final double[][] hxs = new double[][]{ { 75, 125, 125, 75, 75 } };
    final double[][] hys = new double[][]{ { 75, 75, 125, 125, 75 } };
    return geometryFactory.createPolygon(xs, ys, hxs, hys);
  }

  public static IMultiPolygon createMultiPolygon() {
    final double[][] xs = new double[][]{ { 50, 50, 150, 150, 50 }, { 175, 175, 200, 200, 175 } };
    final double[][] ys = new double[][]{ { 50, 150, 150, 50, 50 }, { 50, 150, 150, 50, 50 } };
    return geometryFactory.createMultiPolygon(xs, ys);
  }

  public static IMultiPolygon createMultiPolygonWithHoles() {
    final double[][] xs = new double[][]{ { 50, 50, 150, 150, 50 }, { 175, 175, 200, 200, 175 } };
    final double[][] ys = new double[][]{ { 50, 150, 150, 50, 50 }, { 50, 150, 150, 50, 50 } };
    final double[][][] hxs = new double[][][]{ { { 75, 125, 125, 75, 75 } }, {} };
    final double[][][] hys = new double[][][]{ { { 75, 75, 125, 125, 75 } }, {} };
    return geometryFactory.createMultiPolygon(xs, ys, hxs, hys);
  }

  public static IGeometryCollection createGeometryCollection() {
    final IBaseGeometry[] geometries = new IBaseGeometry[]{ createPoint(), createLineString(), createPolygonWithHoles() };
    return geometryFactory.createCollection(geometries);
  }
}
