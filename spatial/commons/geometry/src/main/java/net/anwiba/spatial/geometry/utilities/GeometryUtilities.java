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
// Copyright (c) 2009 by Andreas W. Bartels 
package net.anwiba.spatial.geometry.utilities;

import net.anwiba.spatial.coordinate.ICoordinate;
import net.anwiba.spatial.coordinate.ICoordinateSequence;
import net.anwiba.spatial.coordinate.IEnvelope;
import net.anwiba.spatial.coordinatereferencesystem.ICoordinateReferenceSystem;
import net.anwiba.spatial.coordinatereferencesystem.ICoordinateReferenceSystemConstants;
import net.anwiba.spatial.geometry.IBaseGeometry;
import net.anwiba.spatial.geometry.IGeometry;
import net.anwiba.spatial.geometry.IGeometryCollection;
import net.anwiba.spatial.geometry.IGeometryFactory;
import net.anwiba.spatial.geometry.IGeometryTypeVisitor;
import net.anwiba.spatial.geometry.ILinearRing;
import net.anwiba.spatial.geometry.IPolygon;
import net.anwiba.spatial.geometry.internal.GeometryFactory;
import net.anwiba.spatial.geometry.internal.Point;

public class GeometryUtilities {

  public static IGeometryFactory getDefaultGeometryFactory() {
    return DEFAULT_GEOMETRY_FACTORY;
  }

  public static IGeometryFactory getGeometryFactory(final ICoordinateReferenceSystem coordianteReferenceSystem) {
    return new GeometryFactory(coordianteReferenceSystem);
  }

  public static int getSrid(final IGeometry geometry) {
    return geometry.getCoordinateReferenceSystem().getSrid();
  }

  private final static IGeometryFactory DEFAULT_GEOMETRY_FACTORY = new GeometryFactory(
      ICoordinateReferenceSystemConstants.NULL_COORDIANTE_REFERENCE_SYSTEM);

  public static IBaseGeometry extract(final IGeometry geometry, final int index) {
    final IGeometryTypeVisitor<IBaseGeometry, RuntimeException> visitor =
        new IGeometryTypeVisitor<IBaseGeometry, RuntimeException>() {

          @Override
          public IBaseGeometry visitCollection() {
            return ((IGeometryCollection) geometry).getGeometryN(index);
          }

          @Override
          public IBaseGeometry visitLineString() {
            return createPoint(geometry.getCoordinateReferenceSystem(), geometry.getCoordinateN(index));
          }

          @Override
          public IBaseGeometry visitLinearRing() {
            return createPoint(geometry.getCoordinateReferenceSystem(), geometry.getCoordinateN(index));
          }

          @Override
          public IBaseGeometry visitMultiLineString() {
            return ((IGeometryCollection) geometry).getGeometryN(index);
          }

          @Override
          public IBaseGeometry visitMultiPoint() {
            return ((IGeometryCollection) geometry).getGeometryN(index);
          }

          @Override
          public IBaseGeometry visitMultiPolygon() {
            return ((IGeometryCollection) geometry).getGeometryN(index);
          }

          @Override
          public IBaseGeometry visitPoint() {
            return createPoint(geometry.getCoordinateReferenceSystem(), geometry.getCoordinateN(index));
          }

          private IBaseGeometry createPoint(
              final ICoordinateReferenceSystem coordinateReferenceSystem,
              final ICoordinate coordinate) {
            return new Point(coordinateReferenceSystem, coordinate);
          }

          @Override
          public IBaseGeometry visitPolygon() {
            if (index == 0) {
              return ((IPolygon) geometry).getOuterRing();
            }
            return ((IPolygon) geometry).getInnerRingN(index - 1);
          }

          @Override
          public IBaseGeometry visitUnknown() {
            throw new IllegalArgumentException();
          }
        };
    return geometry.getGeometryType().accept(visitor);
  }

  public static boolean isRectangle(final IPolygon polygon) {
    if (polygon.getNumberOfInnerRings() != 0) {
      return false;
    }
    final ILinearRing outerRing = polygon.getOuterRing();
    if (outerRing.getNumberOfCoordinates() != 5) {
      return false;
    }

    final ICoordinateSequence sequence = outerRing.getCoordinateSequence();

    final IEnvelope env = polygon.getEnvelope();
    for (int i = 0; i < 5; i++) {
      final double x = sequence.getXValue(i);
      if (!(x == env.getMinimum().getXValue() || x == env.getMaximum().getXValue())) {
        return false;
      }
      final double y = sequence.getYValue(i);
      if (!(y == env.getMinimum().getYValue() || y == env.getMaximum().getYValue())) {
        return false;
      }
    }

    double previousX = sequence.getXValue(0);
    double previousY = sequence.getYValue(0);
    for (int i = 1; i <= 4; i++) {
      final double x = sequence.getXValue(i);
      final double y = sequence.getYValue(i);
      final boolean isXChanged = x != previousX;
      final boolean isYChanged = y != previousY;
      if (isXChanged == isYChanged) {
        return false;
      }
      previousX = x;
      previousY = y;
    }
    return true;
  }

}
