/*
 * #%L
 * anwiba spatial
 * %%
 * Copyright (C) 2007 - 2019 Andreas Bartels
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
// Copyright (c) 2006 by Andreas W. Bartels
package net.anwiba.spatial.geometry.impl;

import static net.anwiba.spatial.coordinate.junit.CoordinateSequenceAssert.assertEquals;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.anwiba.spatial.coordinate.Coordinate;
import net.anwiba.spatial.coordinate.CoordinateSequenceFactory;
import net.anwiba.spatial.coordinate.ICoordinate;
import net.anwiba.spatial.coordinate.ICoordinateSequence;
import net.anwiba.spatial.coordinate.ICoordinateSequenceFactory;
import net.anwiba.spatial.coordinatereferencesystem.coordinatesystem.ITestCoordinateReferenceSystem;
import net.anwiba.spatial.geometry.GeometryType;
import net.anwiba.spatial.geometry.IGeometryFactory;
import net.anwiba.spatial.geometry.ILineString;
import net.anwiba.spatial.geometry.ILinearRing;
import net.anwiba.spatial.geometry.IMultiLineString;
import net.anwiba.spatial.geometry.IMultiPoint;
import net.anwiba.spatial.geometry.IPoint;
import net.anwiba.spatial.geometry.internal.GeometryFactory;
import net.anwiba.spatial.geometry.utilities.GeometryUtilities;

public class GeometryFactoryTest {

  IGeometryFactory geometryFactory;
  private final ICoordinateSequenceFactory coordinateSequenceFactory = new CoordinateSequenceFactory();

  @BeforeEach
  public void setUp() throws Exception {
    this.geometryFactory = GeometryUtilities.getDefaultGeometryFactory();
  }

  @Test
  public void testPoint() throws Exception {
    final Coordinate expectedCoordinate = new Coordinate(5, 5);
    final ICoordinateSequence expectedCoordinateSequence = this.coordinateSequenceFactory.create(expectedCoordinate);
    final IPoint point = this.geometryFactory.createPoint(expectedCoordinate);
    assertEquals(expectedCoordinateSequence, point.getCoordinateSequence());
    assertEquals(1, point.getNumberOfCoordinates());
    assertEquals(expectedCoordinate, point.getCoordinateN(0));
    assertEquals(0, point.getDimension());
    assertEquals(GeometryType.POINT, point.getGeometryType());
    assertEquals(this.geometryFactory.getCoordinateReferenceSystem(), point.getCoordinateReferenceSystem());
  }

  @Test
  public void testMultiPoint() throws Exception {
    final Coordinate coordinate00 = new Coordinate(5, 5);
    final Coordinate coordinate01 = new Coordinate(15, 8);
    final ICoordinateSequence expectedCoordinateSequence =
        this.coordinateSequenceFactory.create(new Coordinate[] { coordinate00, coordinate01 });
    final IMultiPoint point = this.geometryFactory.createMultiPoint(expectedCoordinateSequence);
    assertEquals(2, point.getNumberOfGeometries());
    assertEquals(expectedCoordinateSequence, point.getCoordinateSequence());
    assertEquals(2, point.getNumberOfCoordinates());
    assertEquals(coordinate00, point.getCoordinateN(0));
    assertEquals(coordinate01, point.getCoordinateN(1));
    assertEquals(2, point.getDimension());
    assertEquals(GeometryType.MULTIPOINT, point.getGeometryType());
    assertEquals(this.geometryFactory.getCoordinateReferenceSystem(), point.getCoordinateReferenceSystem());
  }

  @Test
  public void testLineString() throws Exception {
    final ICoordinate coordinate00 = new Coordinate(5, 5);
    final ICoordinate coordinate01 = new Coordinate(15, 8);
    final ICoordinateSequence expectedCoordinateSequence =
        this.coordinateSequenceFactory.create(new ICoordinate[] { coordinate00, coordinate01 });
    final ILineString lineString = this.geometryFactory.createLineString(expectedCoordinateSequence);
    assertEquals(expectedCoordinateSequence, lineString.getCoordinateSequence());
    assertEquals(2, lineString.getNumberOfCoordinates());
    assertEquals(coordinate00, lineString.getCoordinateN(0));
    assertEquals(coordinate01, lineString.getCoordinateN(1));
    assertEquals(1, lineString.getDimension());
    assertEquals(GeometryType.LINESTRING, lineString.getGeometryType());
    assertEquals(this.geometryFactory.getCoordinateReferenceSystem(), lineString.getCoordinateReferenceSystem());
  }

  @Test
  public void testLinearRing() throws Exception {
    final double[] xs = new double[] { 50, 50, 100, 50 };
    final double[] ys = new double[] { 50, 150, 100, 50 };
    final ICoordinateSequence expectedCoordinateSequence = this.coordinateSequenceFactory.create(xs, ys);
    final ILinearRing linearRing = this.geometryFactory.createLinearRing(expectedCoordinateSequence);
    assertEquals(expectedCoordinateSequence, linearRing.getCoordinateSequence());
    assertEquals(4, linearRing.getNumberOfCoordinates());
    assertEquals(new Coordinate(xs[0], ys[0]), linearRing.getCoordinateN(0));
    assertEquals(new Coordinate(xs[1], ys[1]), linearRing.getCoordinateN(1));
    assertEquals(new Coordinate(xs[2], ys[2]), linearRing.getCoordinateN(2));
    assertEquals(new Coordinate(xs[3], ys[3]), linearRing.getCoordinateN(3));
    assertEquals(1, linearRing.getDimension());
    assertEquals(GeometryType.LINEARRING, linearRing.getGeometryType());
    assertEquals(this.geometryFactory.getCoordinateReferenceSystem(), linearRing.getCoordinateReferenceSystem());
  }

  @Test
  public void testMultiLineString() throws Exception {
    final double[][] xs = new double[][] { { 50, 50, 150, 150 }, { 75, 75, 125, 125 } };
    final double[][] ys = new double[][] { { 50, 150, 150, 50 }, { 75, 125, 125, 75 } };
    final IMultiLineString multiLineString = this.geometryFactory.createMultiLineString(xs, ys);
    assertEquals(2, multiLineString.getNumberOfGeometries());
    assertEquals(8, multiLineString.getNumberOfCoordinates());
    assertEquals(new Coordinate(xs[0][0], ys[0][0]), multiLineString.getCoordinateN(0));
    assertEquals(new Coordinate(xs[0][1], ys[0][1]), multiLineString.getCoordinateN(1));
    assertEquals(new Coordinate(xs[0][2], ys[0][2]), multiLineString.getCoordinateN(2));
    assertEquals(new Coordinate(xs[0][3], ys[0][3]), multiLineString.getCoordinateN(3));
    assertEquals(new Coordinate(xs[1][0], ys[1][0]), multiLineString.getCoordinateN(4));
    assertEquals(new Coordinate(xs[1][1], ys[1][1]), multiLineString.getCoordinateN(5));
    assertEquals(new Coordinate(xs[1][2], ys[1][2]), multiLineString.getCoordinateN(6));
    assertEquals(new Coordinate(xs[1][3], ys[1][3]), multiLineString.getCoordinateN(7));
    assertEquals(2, multiLineString.getDimension());
    assertEquals(2, multiLineString.getCoordinateDimension());
    assertEquals(GeometryType.MULTILINESTRING, multiLineString.getGeometryType());
    assertEquals(this.geometryFactory.getCoordinateReferenceSystem(), multiLineString.getCoordinateReferenceSystem());
  }

  @Test
  public void testMultiLineStringZ() throws Exception {
    final double[][] xs = new double[][] { { 50, 50, 150, 150 }, { 75, 75, 125, 125 } };
    final double[][] ys = new double[][] { { 50, 150, 150, 50 }, { 75, 125, 125, 75 } };
    final double[][] zs = new double[][] { { 30, 33, 31, 45 }, { 20, 22, 26, 28 } };
    final IMultiLineString multiLineString = this.geometryFactory.createMultiLineString(xs, ys, zs);
    assertEquals(2, multiLineString.getNumberOfGeometries());
    assertEquals(8, multiLineString.getNumberOfCoordinates());
    assertEquals(new Coordinate(xs[0][0], ys[0][0], zs[0][0], false), multiLineString.getCoordinateN(0));
    assertEquals(new Coordinate(xs[0][1], ys[0][1], zs[0][1], false), multiLineString.getCoordinateN(1));
    assertEquals(new Coordinate(xs[0][2], ys[0][2], zs[0][2], false), multiLineString.getCoordinateN(2));
    assertEquals(new Coordinate(xs[0][3], ys[0][3], zs[0][3], false), multiLineString.getCoordinateN(3));
    assertEquals(new Coordinate(xs[1][0], ys[1][0], zs[1][0], false), multiLineString.getCoordinateN(4));
    assertEquals(new Coordinate(xs[1][1], ys[1][1], zs[1][1], false), multiLineString.getCoordinateN(5));
    assertEquals(new Coordinate(xs[1][2], ys[1][2], zs[1][2], false), multiLineString.getCoordinateN(6));
    assertEquals(new Coordinate(xs[1][3], ys[1][3], zs[1][3], false), multiLineString.getCoordinateN(7));
    assertEquals(2, multiLineString.getDimension());
    assertEquals(3, multiLineString.getCoordinateDimension());
    assertEquals(GeometryType.MULTILINESTRING, multiLineString.getGeometryType());
    assertEquals(this.geometryFactory.getCoordinateReferenceSystem(), multiLineString.getCoordinateReferenceSystem());
  }

  @Test
  public void testGetTargetCoordianteReferenceSystem() {
    IGeometryFactory factory = new GeometryFactory(ITestCoordinateReferenceSystem.GG_WGS_84);
    assertThat(factory.getCoordinateReferenceSystem(), equalTo(ITestCoordinateReferenceSystem.GG_WGS_84));
    factory = new GeometryFactory(ITestCoordinateReferenceSystem.GG_WGS_84);
    assertThat(factory.getCoordinateReferenceSystem(), equalTo(ITestCoordinateReferenceSystem.GG_WGS_84));
  }

}
