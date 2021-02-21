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
package net.anwiba.spatial.geometry.calculator;

import net.anwiba.spatial.coordinate.CoordinateSequenceUtilities;
import net.anwiba.spatial.coordinate.CoordinateUtilities;
import net.anwiba.spatial.coordinate.Envelope;
import net.anwiba.spatial.coordinate.ICoordinate;
import net.anwiba.spatial.coordinate.IEnvelope;
import net.anwiba.spatial.geometry.GeometryType;
import net.anwiba.spatial.geometry.IGeometry;
import net.anwiba.spatial.geometry.IGeometryCollection;
import net.anwiba.spatial.geometry.IPolygon;

public class GeometryCalculator {

  public static IEnvelope createEnvelope(final IGeometry[] geometries) {
    IEnvelope envelope = Envelope.NULL_ENVELOPE;
    for (final IGeometry geometry : geometries) {
      envelope = envelope.concat(geometry.getEnvelope());
    }
    return envelope;
  }

  public static double calculateLength(final IGeometry geometry) {
    if (geometry == null) {
      return 0;
    }
    if (geometry.isCollection()) {
      final IGeometryCollection collection = (IGeometryCollection) geometry;
      double sum = 0;
      for (int i = 0; i < collection.getNumberOfGeometries(); i++) {
        sum += calculateLength(collection.getGeometryN(i));
      }
      return sum;
    }
    if (geometry.getGeometryType() == GeometryType.POLYGON) {
      final IPolygon polygon = (IPolygon) geometry;
      double sum = CoordinateUtilities.calculateLength(polygon.getOuterRing().getCoordinateSequence());
      for (int i = 0; i < polygon.getNumberOfInnerRings(); i++) {
        sum += CoordinateUtilities.calculateLength(polygon.getInnerRingN(i).getCoordinateSequence());
      }
      return sum;
    }
    return CoordinateUtilities.calculateLength(geometry.getCoordinateSequence());
  }

  public static ICoordinate calculateInterior(final IGeometry geometry) {
    return InteriorCalculator.calculateInterior(geometry);
  }

  public static ICoordinate calculateCentroid(final IGeometry geometry) {
    return CoordinateSequenceUtilities.calculateCentroid(geometry.getCoordinateSequence());
  }

  public static double calculateArea(final IGeometry geometry) {
    if (geometry == null) {
      return 0;
    }
    if (geometry.isCollection()) {
      final IGeometryCollection collection = (IGeometryCollection) geometry;
      double sum = 0;
      for (int i = 0; i < collection.getNumberOfGeometries(); i++) {
        sum += calculateArea(collection.getGeometryN(i));
      }
      return sum;
    }
    if (geometry.getGeometryType() == GeometryType.POLYGON) {
      final IPolygon polygon = (IPolygon) geometry;
      double area = CoordinateUtilities.calculateArea(polygon.getOuterRing().getCoordinateSequence());
      for (int i = 0; i < polygon.getNumberOfInnerRings(); i++) {
        area -= CoordinateUtilities.calculateArea(polygon.getInnerRingN(i).getCoordinateSequence());
      }
      return area;
    }
    return 0;
  }
}
