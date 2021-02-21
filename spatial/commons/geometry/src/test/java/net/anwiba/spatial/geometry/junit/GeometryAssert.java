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
package net.anwiba.spatial.geometry.junit;

import static org.junit.jupiter.api.Assertions.fail;

import net.anwiba.commons.lang.exception.UnreachableCodeReachedException;
import net.anwiba.spatial.coordinate.CoordinateSequenceUtilities;
import net.anwiba.spatial.coordinate.junit.CoordinateSequenceAssert;
import net.anwiba.spatial.geometry.IBaseGeometry;
import net.anwiba.spatial.geometry.IGeometry;
import net.anwiba.spatial.geometry.IGeometryCollection;
import net.anwiba.spatial.geometry.IPolygon;
import net.anwiba.spatial.geometry.internal.LinearRing;

public class GeometryAssert {

  public static void assertEquals(final IGeometry expected, final IGeometry actual) {
    assertEquals("", expected, actual); //$NON-NLS-1$
  }

  public static void assertEquals(final String message, final IGeometry expected, final IGeometry actual) {
    if (expected == null ? actual == null : expected == actual) {
      return;
    }
    if (expected == null) {
      fail(message + "expected geometry was null"); //$NON-NLS-1$
      return;
    }
    if (actual == null) {
      fail(message + "actual geometry was null"); //$NON-NLS-1$
      return;
    }
    if (!expected.getGeometryType().equals(actual.getGeometryType())) {
      fail(message
          + "geometryType differed, expected.geometryType=" + expected.getGeometryType() + " actual.geometryType=" //$NON-NLS-1$ //$NON-NLS-2$
          + actual.getGeometryType());
      return;
    }
    if (expected.isCollection()) {
      final IGeometryCollection expectedCollection = (IGeometryCollection) expected;
      final IGeometryCollection actualCollection = (IGeometryCollection) actual;
      assertGeometryCollectionsEquals(message, expectedCollection, actualCollection);
      return;
    }
    final IBaseGeometry expectedBaseGeometry = (IBaseGeometry) expected;
    final IBaseGeometry actualBaseGeometry = (IBaseGeometry) actual;
    assertBasicGeometriesEquals(message, expectedBaseGeometry, actualBaseGeometry);
  }

  private static void assertGeometryCollectionsEquals(
      final String message,
      final IGeometryCollection expected,
      final IGeometryCollection actual) {
    if (expected.getNumberOfGeometries() != actual.getNumberOfGeometries()) {
      fail(message
          + "number of base geometries differed, expected.numberOfGeometries=" + expected.getNumberOfGeometries() //$NON-NLS-1$
          + " actual.numberOfGeometries=" + actual.getNumberOfGeometries()); //$NON-NLS-1$
      return;
    }
    switch (expected.getGeometryType()) {
      case MULTIPOINT:
      case MULTILINESTRING:
      case MULTIPOLYGON: {
        for (int i = 0; i < expected.getNumberOfGeometries(); i++) {
          final IBaseGeometry expectedBaseGeometry = expected.getGeometryN(i);
          final IBaseGeometry actualBaseGeometry = actual.getGeometryN(i);
          assertBasicGeometriesEquals(message + "base geometry " + i, expectedBaseGeometry, actualBaseGeometry); //$NON-NLS-1$
        }
        return;
      }
      case COLLECTION:
      case UNKNOWN: {
        for (int i = 0; i < expected.getNumberOfGeometries(); i++) {
          if (!expected.getGeometryType().equals(actual.getGeometryType())) {
            fail(message
                + "base geometry " + i + ", geometryType differed, expected.geometryType=" + expected.getGeometryType() //$NON-NLS-1$ //$NON-NLS-2$
                + " actual.geometryType=" //$NON-NLS-1$
                + actual.getGeometryType());
            return;
          }
          final IBaseGeometry expectedBaseGeometry = expected.getGeometryN(i);
          final IBaseGeometry actualBaseGeometry = actual.getGeometryN(i);
          assertBasicGeometriesEquals(message + "base geometry " + i + ", ", expectedBaseGeometry, actualBaseGeometry); //$NON-NLS-1$ //$NON-NLS-2$
        }
        return;
      }
      default:
        throw new UnreachableCodeReachedException();
    }
  }

  private static void assertBasicGeometriesEquals(
      final String message,
      final IBaseGeometry expected,
      final IBaseGeometry actual) {
    switch (expected.getGeometryType()) {
      case POINT:
      case LINESTRING:
        CoordinateSequenceAssert
            .assertEquals(message, expected.getCoordinateSequence(), actual.getCoordinateSequence());
        return;
      case LINEARRING: {
        final LinearRing expectedLinearRing = (LinearRing) expected;
        final LinearRing actualLinearRing = (LinearRing) actual;
        CoordinateSequenceAssert.assertEquals(
            message,
            expected.getCoordinateSequence(),
            actualLinearRing.getOrientation().equals(expectedLinearRing.getOrientation()) ? actual
                .getCoordinateSequence() : CoordinateSequenceUtilities.reverse(actual.getCoordinateSequence()));
        return;
      }
      case POLYGON: {
        final IPolygon expectedPolygon = (IPolygon) expected;
        final IPolygon actualPolygon = (IPolygon) actual;
        assertEquals(expectedPolygon.getOuterRing(), actualPolygon.getOuterRing());
        if (expectedPolygon.getNumberOfInnerRings() != actualPolygon.getNumberOfInnerRings()) {
          fail(message
              + ", number off inner rings differed, expected.number=" + expectedPolygon.getNumberOfInnerRings() //$NON-NLS-1$
              + " actual.number=" //$NON-NLS-1$
              + actualPolygon.getNumberOfInnerRings());
        }
        for (int i = 0; i < expectedPolygon.getNumberOfInnerRings(); i++) {
          assertEquals("innerring" + i + ", ", expectedPolygon.getInnerRingN(i), actualPolygon.getInnerRingN(i)); //$NON-NLS-1$//$NON-NLS-2$
        }
        return;
      }
      default:
        throw new UnreachableCodeReachedException();
    }
  }
}
