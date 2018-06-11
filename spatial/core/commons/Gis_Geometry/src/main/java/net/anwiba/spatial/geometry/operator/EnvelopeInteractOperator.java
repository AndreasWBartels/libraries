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

import net.anwiba.spatial.coordinate.Envelope;
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

public class EnvelopeInteractOperator {

  private final IEnvelope envelope;

  public EnvelopeInteractOperator(final IEnvelope envelope) {
    this.envelope = envelope == null ? Envelope.NULL_ENVELOPE : envelope;
  }

  //  private boolean interact(final IEnvelope envelope, final IBaseGeometry geometry) {
  //    for (final ICoordinate coordinate : geometry.getCoordinateSequence().getCoordinates()) {
  //      if (envelope.interact(coordinate)) {
  //        return true;
  //      }
  //    }
  //    return false;
  //  }

  //  private boolean interact(final IEnvelope envelope, final IGeometry geometry) {
  //    final IEnvelope geometryEnvelope = geometry.getEnvelope();
  //    if (!envelope.interact(geometryEnvelope)) {
  //      return false;
  //    }
  //    if (envelope.contains(geometryEnvelope)) {
  //      return true;
  //    }
  //    if (geometry instanceof IGeometryCollection) {
  //      final IGeometryCollection collection = (IGeometryCollection) geometry;
  //      for (final IBaseGeometry baseGeometry : collection.geometries()) {
  //        if (interact(envelope, baseGeometry)) {
  //          return true;
  //        }
  //        return false;
  //      }
  //    }
  //    return interact(envelope, (IBaseGeometry) geometry);
  //  }

  public boolean covers(final IGeometry geometry) {
    if (geometry == null) {
      return false;
    }
    return this.envelope.contains(geometry.getEnvelope());
  }

  public boolean interact(final IGeometry geometry) {
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
        return interact(polygon) ? Boolean.TRUE : Boolean.FALSE;
      }

      @Override
      public Boolean visitPoint() throws RuntimeException {
        final IPoint point = (IPoint) geometry;
        return interact(point) ? Boolean.TRUE : Boolean.FALSE;
      }

      @Override
      public Boolean visitMultiPolygon() throws RuntimeException {
        final IMultiPolygon multiPolygon = (IMultiPolygon) geometry;
        return interact(multiPolygon) ? Boolean.TRUE : Boolean.FALSE;
      }

      @Override
      public Boolean visitMultiPoint() throws RuntimeException {
        final IMultiPoint multiPoint = (IMultiPoint) geometry;
        return interact(multiPoint) ? Boolean.TRUE : Boolean.FALSE;
      }

      @Override
      public Boolean visitMultiLineString() throws RuntimeException {
        final IMultiLineString multiLineString = (IMultiLineString) geometry;
        return interact(multiLineString) ? Boolean.TRUE : Boolean.FALSE;
      }

      @Override
      public Boolean visitLinearRing() throws RuntimeException {
        final ILinearRing linearRing = (ILinearRing) geometry;
        return interact(linearRing) ? Boolean.TRUE : Boolean.FALSE;
      }

      @Override
      public Boolean visitLineString() throws RuntimeException {
        final ILineString lineString = (ILineString) geometry;
        return interact(lineString) ? Boolean.TRUE : Boolean.FALSE;
      }

      @Override
      public Boolean visitCollection() throws RuntimeException {
        final IGeometryCollection collection = (IGeometryCollection) geometry;
        return interact(collection) ? Boolean.TRUE : Boolean.FALSE;
      }
    };
    return geometry.getGeometryType().accept(visitor).booleanValue();
  }

  private boolean interact(final IPoint point) {
    return this.envelope.interact(point.getCoordinateN(0));
  }

  private boolean interact(final IMultiPoint multiPoint) {
    final IEnvelope geometryEnvelop = multiPoint.getEnvelope();
    if (!geometryEnvelop.interact(this.envelope)) {
      return false;
    }
    if (this.envelope.contains(geometryEnvelop)) {
      return true;
    }
    for (final ICoordinate coordinate : multiPoint.getCoordinateSequence().getCoordinates()) {
      if (this.envelope.interact(coordinate)) {
        return true;
      }
    }
    return false;
  }

  private boolean interact(final ILineString lineString) {
    final IEnvelope geometryEnvelop = lineString.getEnvelope();
    if (!this.envelope.interact(geometryEnvelop)) {
      return false;
    }
    if (this.envelope.contains(geometryEnvelop)) {
      return true;
    }
    return isCrossing(lineString.getCoordinateSequence().getCoordinates());
  }

  private boolean interact(final IGeometryCollection collection) {
    final IEnvelope geometryEnvelop = collection.getEnvelope();
    if (!this.envelope.interact(geometryEnvelop)) {
      return false;
    }
    if (this.envelope.contains(geometryEnvelop)) {
      return true;
    }
    for (int i = 0; i < collection.getNumberOfGeometries(); i++) {
      if (interact(collection.getGeometryN(i))) {
        return true;
      }
    }
    return false;
  }

  private boolean interact(final IMultiLineString multiLineString) {
    final IEnvelope geometryEnvelop = multiLineString.getEnvelope();
    if (!this.envelope.interact(geometryEnvelop)) {
      return false;
    }
    if (this.envelope.contains(geometryEnvelop)) {
      return true;
    }
    for (int i = 0; i < multiLineString.getNumberOfGeometries(); i++) {
      if (interact(multiLineString.getGeometryN(i))) {
        return true;
      }
    }
    return false;
  }

  private boolean interact(final IPolygon polygon) {
    final IEnvelope geometryEnvelop = polygon.getEnvelope();
    if (!this.envelope.interact(geometryEnvelop)) {
      return false;
    }
    if (this.envelope.contains(geometryEnvelop)) {
      return true;
    }
    final ICoordinateSequence coordinateSequence = this.envelope.getCoordinateSequence();
    for (int i = 0; i < 4; i++) {
      if (new PointInteractOperator(coordinateSequence.getCoordinateN(i)).interact(polygon, 0.)) {
        return true;
      }
    }
    return isCrossing(polygon.getOuterRing().getCoordinateSequence().getCoordinates());
  }

  private boolean interact(final IMultiPolygon multiPolygon) {
    final IEnvelope geometryEnvelop = multiPolygon.getEnvelope();
    if (!this.envelope.interact(geometryEnvelop)) {
      return false;
    }
    if (this.envelope.contains(geometryEnvelop)) {
      return true;
    }
    for (int i = 0; i < multiPolygon.getNumberOfGeometries(); i++) {
      if (interact(multiPolygon.getGeometryN(i))) {
        return true;
      }
    }
    return false;
  }

  private boolean isCrossing(final Iterable<ICoordinate> coordinates) {
    ICoordinate previous = null;
    for (final ICoordinate next : coordinates) {
      if (previous == null) {
        if (this.envelope.interact(next)) {
          return true;
        }
        previous = next;
        continue;
      }
      if (this.envelope.interact(next)) {
        return true;
      }
      if (this.envelope.cross(previous, next)) {
        return true;
      }
      previous = next;
    }
    return false;
  }

  public boolean interact(final ICoordinate coordinate) {
    return this.envelope.interact(coordinate);
  }

  public boolean interact(@SuppressWarnings("hiding") final IEnvelope envelope) {
    if (envelope == null || Envelope.NULL_ENVELOPE.equals(envelope)) {
      return false;
    }
    return this.envelope.interact(envelope);
  }
}