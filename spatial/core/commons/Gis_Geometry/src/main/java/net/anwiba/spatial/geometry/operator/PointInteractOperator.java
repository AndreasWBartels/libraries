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
 
package net.anwiba.spatial.geometry.operator;

import net.anwiba.spatial.coordinate.CoordinateUtilities;
import net.anwiba.spatial.coordinate.EnvelopeUtilities;
import net.anwiba.spatial.coordinate.ICoordinate;
import net.anwiba.spatial.coordinate.ICoordinateSequence;
import net.anwiba.spatial.coordinate.IEnvelope;
import net.anwiba.spatial.geometry.IGeometry;
import net.anwiba.spatial.geometry.IGeometryCollection;
import net.anwiba.spatial.geometry.IGeometryTypeVisitor;
import net.anwiba.spatial.geometry.ILineString;
import net.anwiba.spatial.geometry.ILinearRing;
import net.anwiba.spatial.geometry.IMultiLineString;
import net.anwiba.spatial.geometry.IMultiPoint;
import net.anwiba.spatial.geometry.IMultiPolygon;
import net.anwiba.spatial.geometry.IPoint;
import net.anwiba.spatial.geometry.IPolygon;

public class PointInteractOperator {

  private final ICoordinate coordinate;

  public PointInteractOperator(final IPoint point) {
    this(point.getCoordinateN(0));
  }

  public PointInteractOperator(final ICoordinate coordinate) {
    this.coordinate = coordinate;
  }

  public boolean interact(final IGeometry geometry, final double tolerance) {
    // TODO NOW null geometry
    if (geometry == null) {
      return false;
    }
    final IGeometryTypeVisitor<Boolean, RuntimeException> visitor = new IGeometryTypeVisitor<Boolean, RuntimeException>() {

      @Override
      public Boolean visitUnknown() throws RuntimeException {
        return Boolean.FALSE;
      }

      @Override
      public Boolean visitPolygon() throws RuntimeException {
        final IPolygon polygon = (IPolygon) geometry;
        return interact(polygon, tolerance) ? Boolean.TRUE : Boolean.FALSE;
      }

      @Override
      public Boolean visitPoint() throws RuntimeException {
        final IPoint point = (IPoint) geometry;
        return interact(point, tolerance) ? Boolean.TRUE : Boolean.FALSE;
      }

      @Override
      public Boolean visitMultiPolygon() throws RuntimeException {
        final IMultiPolygon polygones = (IMultiPolygon) geometry;
        return interact(polygones, tolerance) ? Boolean.TRUE : Boolean.FALSE;
      }

      @Override
      public Boolean visitMultiPoint() throws RuntimeException {
        final IMultiPoint points = (IMultiPoint) geometry;
        return interact(points, tolerance) ? Boolean.TRUE : Boolean.FALSE;
      }

      @Override
      public Boolean visitMultiLineString() throws RuntimeException {
        final IMultiLineString lineStrings = (IMultiLineString) geometry;
        return interact(lineStrings, tolerance) ? Boolean.TRUE : Boolean.FALSE;
      }

      @Override
      public Boolean visitLinearRing() throws RuntimeException {
        final ILinearRing linearRing = (ILinearRing) geometry;
        return interact(linearRing, tolerance) ? Boolean.TRUE : Boolean.FALSE;
      }

      @Override
      public Boolean visitLineString() throws RuntimeException {
        final ILineString lineString = (ILineString) geometry;
        return interact(lineString, tolerance) ? Boolean.TRUE : Boolean.FALSE;
      }

      @Override
      public Boolean visitCollection() throws RuntimeException {
        final IGeometryCollection geometryCollection = (IGeometryCollection) geometry;
        return interact(geometryCollection, tolerance) ? Boolean.TRUE : Boolean.FALSE;
      }
    };
    return geometry.getGeometryType().accept(visitor).booleanValue();
  }

  public boolean interact(final IGeometryCollection collection, final double tolerance) {
    for (final IGeometry geometry : collection.geometries()) {
      if (interact(geometry, tolerance)) {
        return true;
      }
    }
    return false;
  }

  public boolean interact(final IPoint point, final double tolerance) {
    final ICoordinate pointCoordinate = point.getCoordinateN(0);
    return CoordinateUtilities.interact(pointCoordinate, this.coordinate, tolerance);
  }

  public boolean interact(final IMultiPoint multiPoint, final double tolerance) {
    final IEnvelope envelope = EnvelopeUtilities.createEnvelope(multiPoint.getEnvelope(), tolerance);
    if (!envelope.interact(this.coordinate)) {
      return false;
    }
    for (int i = 0; i < multiPoint.getNumberOfGeometries(); i++) {
      if (interact(multiPoint.getGeometryN(i), tolerance)) {
        return true;
      }
    }
    return false;
  }

  public boolean interact(final ILineString line, final double tolerance) {
    final IEnvelope envelope = EnvelopeUtilities.createEnvelope(line.getEnvelope(), tolerance);
    if (!envelope.interact(this.coordinate)) {
      return false;
    }
    final ICoordinateSequence coordinateSequence = line.getCoordinateSequence();
    ICoordinate previous = null;
    for (final ICoordinate next : coordinateSequence.getCoordinates()) {
      if (previous == null) {
        if (CoordinateUtilities.interact(this.coordinate, next, tolerance)) {
          return true;
        }
        previous = next;
        continue;
      }
      if (CoordinateUtilities.isBetween(previous, next, this.coordinate, tolerance)) {
        return true;
      }
      previous = next;
    }
    return false;
  }

  public boolean interact(final IMultiLineString multiLineString, final double tolerance) {
    final IEnvelope envelope = EnvelopeUtilities.createEnvelope(multiLineString.getEnvelope(), tolerance);
    if (!envelope.interact(this.coordinate)) {
      return false;
    }
    for (int i = 0; i < multiLineString.getNumberOfGeometries(); i++) {
      if (interact(multiLineString.getGeometryN(i), tolerance)) {
        return true;
      }
    }
    return false;
  }

  public boolean interact(final IPolygon polygon, final double tolerance) {
    final IEnvelope envelope = EnvelopeUtilities.createEnvelope(polygon.getEnvelope(), tolerance);
    if (!envelope.interact(this.coordinate)) {
      return false;
    }
    if (CoordinateUtilities.isPointInRing(this.coordinate, polygon.getOuterRing().getCoordinateSequence())) {
      for (int i = 0; i < polygon.getNumberOfInnerRings(); i++) {
        final IGeometry innerRing = polygon.getInnerRingN(i);
        if (!innerRing.getEnvelope().interact(this.coordinate)) {
          continue;
        }
        if (CoordinateUtilities.isPointInRing(this.coordinate, innerRing.getCoordinateSequence())) {
          return false;
        }
      }
      return true;
    }
    if (interact(polygon.getOuterRing(), tolerance)) {
      return true;
    }
    return false;
  }

  public boolean interact(final IMultiPolygon polygon, final double tolerance) {
    final IEnvelope envelope = EnvelopeUtilities.createEnvelope(polygon.getEnvelope(), tolerance);
    if (!envelope.interact(this.coordinate)) {
      return false;
    }
    for (int i = 0; i < polygon.getNumberOfGeometries(); i++) {
      if (interact(polygon.getGeometryN(i), tolerance)) {
        return true;
      }
    }
    return false;
  }

}