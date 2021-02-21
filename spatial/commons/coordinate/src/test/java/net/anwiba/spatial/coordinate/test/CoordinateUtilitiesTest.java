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
// Copyright (c) 2007 by Andreas W. Bartels
package net.anwiba.spatial.coordinate.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import net.anwiba.spatial.coordinate.Coordinate;
import net.anwiba.spatial.coordinate.CoordinateUtilities;
import net.anwiba.spatial.coordinate.ICoordinate;

public class CoordinateUtilitiesTest {

  @Test
  public void testCalculateAngle() throws Exception {
    assertEquals(0, CoordinateUtilities.calculateAngle(new Coordinate(0, 0), new Coordinate(0, 10)), 0);
    assertEquals(Math.PI, CoordinateUtilities.calculateAngle(new Coordinate(0, 0), new Coordinate(0, -10)), 0);
    assertEquals(Math.PI / 2, CoordinateUtilities.calculateAngle(new Coordinate(0, 0), new Coordinate(10, 0)), 0);
    assertEquals(Math.PI / 2 * 3, CoordinateUtilities.calculateAngle(new Coordinate(0, 0), new Coordinate(-10, 0)), 0);
    assertEquals(Math.PI / 4, CoordinateUtilities.calculateAngle(new Coordinate(0, 0), new Coordinate(10, 10)), 0);
    assertEquals(Math.PI / 4 * 3, CoordinateUtilities.calculateAngle(new Coordinate(0, 0), new Coordinate(10, -10)), 0);
    assertEquals(Math.PI / 4 * 5,
        CoordinateUtilities.calculateAngle(new Coordinate(0, 0), new Coordinate(-10, -10)),
        0);
    assertEquals(Math.PI / 4 * 7, CoordinateUtilities.calculateAngle(new Coordinate(0, 0), new Coordinate(-10, 10)), 0);
  }

  @Test
  public void testCalculateLength() throws Exception {
    assertEquals(0, CoordinateUtilities.calculateDistance(new Coordinate(0, 0, 0, 0), new Coordinate(0, 0, 10, 0)), 0);
    assertEquals(5, CoordinateUtilities.calculateDistance(new Coordinate(0, 0), new Coordinate(3, 4)), 0);
    assertEquals(
        3,
        CoordinateUtilities.calculateDistance(new Coordinate(0, 0, 0, false), new Coordinate(3, 0, 4, false)),
        0.0000000000001);
  }

  @Test
  public void testCalculatePolarCoordinate() throws Exception {
    ICoordinate coordinate = CoordinateUtilities.calculatePolarCoordinate(new Coordinate(0, 0), 0, 10);
    assertEquals(0, coordinate.getXValue(), 0.0000000000001);
    assertEquals(10, coordinate.getYValue(), 0.0000000000001);
    coordinate = CoordinateUtilities.calculatePolarCoordinate(new Coordinate(0, 0), Math.PI, 10);
    assertEquals(0, coordinate.getXValue(), 0.0000000000001);
    assertEquals(-10, coordinate.getYValue(), 0.0000000000001);
    coordinate = CoordinateUtilities.calculatePolarCoordinate(new Coordinate(0, 0), Math.PI / 2, 10);
    assertEquals(10, coordinate.getXValue(), 0.0000000000001);
    assertEquals(0, coordinate.getYValue(), 0.0000000000001);
    coordinate = CoordinateUtilities.calculatePolarCoordinate(new Coordinate(0, 0), Math.PI / 2 * 3, 10);
    assertEquals(-10, coordinate.getXValue(), 0.0000000000001);
    assertEquals(0, coordinate.getYValue(), 0.0000000000001);
    final double distance = CoordinateUtilities.calculateDistance(new Coordinate(0, 0, 0, 0), new Coordinate(10, 10));
    coordinate = CoordinateUtilities.calculatePolarCoordinate(new Coordinate(0, 0), Math.PI / 4, distance);
    assertEquals(10, coordinate.getXValue(), 0.0000000000001);
    assertEquals(10, coordinate.getYValue(), 0.0000000000001);
    coordinate = CoordinateUtilities.calculatePolarCoordinate(new Coordinate(0, 0), Math.PI / 4 * 3, distance);
    assertEquals(10, coordinate.getXValue(), 0.0000000000001);
    assertEquals(-10, coordinate.getYValue(), 0.0000000000001);
    coordinate = CoordinateUtilities.calculatePolarCoordinate(new Coordinate(0, 0), Math.PI / 4 * 5, distance);
    assertEquals(-10, coordinate.getXValue(), 0.0000000000001);
    assertEquals(-10, coordinate.getYValue(), 0.0000000000001);
    coordinate = CoordinateUtilities.calculatePolarCoordinate(new Coordinate(0, 0), Math.PI / 4 * 7, distance);
    assertEquals(-10, coordinate.getXValue(), 0.0000000000001);
    assertEquals(10, coordinate.getYValue(), 0.0000000000001);
  }

  @Test
  public void testCalculateBaseCoordinate() throws Exception {
    final ICoordinate expected = new Coordinate(0, 0);
    final ICoordinate c0 = new Coordinate(0, -10, 0, false);
    final ICoordinate c1 = new Coordinate(0, 10, 0, false);
    final ICoordinate c2 = new Coordinate(10, 0, 0, false);
    assertEquals(expected, CoordinateUtilities.calculateBasePoint(c0, c1, c2));
  }

  @Test
  public void testIsBetween() throws Exception {
    final ICoordinate c0 = new Coordinate(0, 0, 0, false);
    final ICoordinate c1 = new Coordinate(0, 10, 0, false);
    final ICoordinate c2 = new Coordinate(0, 5, 0, false);
    assertTrue(CoordinateUtilities.isInterior(c0, c1, c2));
  }

  @Test
  public void testCrossCoordinate() throws Exception {
    ICoordinate expected = new Coordinate(5, 5);
    ICoordinate c0 = new Coordinate(0, 0, 0, false);
    ICoordinate c1 = new Coordinate(10, 10, 0, false);
    ICoordinate c2 = new Coordinate(0, 10, 0, false);
    ICoordinate c3 = new Coordinate(10, 0, 0, false);
    ICoordinate result = CoordinateUtilities.calculateIntersection(c0, c1, c2, c3);
    assertEquals(expected, result);
    expected = new Coordinate(5, 0);
    c0 = new Coordinate(0, 0, 0, false);
    c1 = new Coordinate(10, 0, 0, false);
    c2 = new Coordinate(5, 5, 0, false);
    c3 = new Coordinate(5, -5, 0, false);
    result = CoordinateUtilities.calculateIntersection(c0, c1, c2, c3);
    assertEquals(expected, result);
    expected = new Coordinate(0, 5);
    c0 = new Coordinate(0, 0, 0, false);
    c1 = new Coordinate(0, 10, 0, false);
    c2 = new Coordinate(5, 5, 0, false);
    c3 = new Coordinate(-5, 5, 0, false);
    result = CoordinateUtilities.calculateIntersection(c0, c1, c2, c3);
    assertEquals(expected, result);
  }
}
