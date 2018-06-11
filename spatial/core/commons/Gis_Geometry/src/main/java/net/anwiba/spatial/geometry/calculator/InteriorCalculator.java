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
 
package net.anwiba.spatial.geometry.calculator;

import java.util.ArrayList;
import java.util.List;

import net.anwiba.spatial.coordinate.Coordinate;
import net.anwiba.spatial.coordinate.CoordinateSequenceFactory;
import net.anwiba.spatial.coordinate.CoordinateSequenceUtilities;
import net.anwiba.spatial.coordinate.ICoordinate;
import net.anwiba.spatial.coordinate.ICoordinateSequence;
import net.anwiba.spatial.coordinate.IEnvelope;
import net.anwiba.spatial.geometry.IBaseGeometry;
import net.anwiba.spatial.geometry.IGeometry;
import net.anwiba.spatial.geometry.IGeometryCollection;
import net.anwiba.spatial.geometry.IGeometryTypeVisitor;
import net.anwiba.spatial.geometry.ILineSegment;
import net.anwiba.spatial.geometry.ILineString;
import net.anwiba.spatial.geometry.ILinearRing;
import net.anwiba.spatial.geometry.IMultiLineString;
import net.anwiba.spatial.geometry.IMultiPoint;
import net.anwiba.spatial.geometry.IMultiPolygon;
import net.anwiba.spatial.geometry.IPoint;
import net.anwiba.spatial.geometry.IPolygon;
import net.anwiba.spatial.geometry.internal.LineSegment;
import net.anwiba.spatial.geometry.operator.LineSegmentIntersectOperator;
import net.anwiba.spatial.geometry.operator.LineStringInteractOperator;

public class InteriorCalculator {

  public static ICoordinate calculateInterior(final IGeometry geometry) {
    final IGeometryTypeVisitor<ICoordinate, RuntimeException> visitor = new IGeometryTypeVisitor<ICoordinate, RuntimeException>() {

      @Override
      public ICoordinate visitUnknown() throws RuntimeException {
        throw new UnsupportedOperationException();
      }

      @Override
      public ICoordinate visitPolygon() throws RuntimeException {
        final IPolygon polygone = (IPolygon) geometry;
        return calculateInterior(polygone);
      }

      @Override
      public ICoordinate visitPoint() throws RuntimeException {
        final IPoint point = (IPoint) geometry;
        return calculateInterior(point);
      }

      @Override
      public ICoordinate visitMultiPolygon() throws RuntimeException {
        final IMultiPolygon multiPolygon = (IMultiPolygon) geometry;
        return calculateInterior(multiPolygon);
      }

      @Override
      public ICoordinate visitMultiPoint() throws RuntimeException {
        final IMultiPoint multiPoint = (IMultiPoint) geometry;
        return calculateInterior(multiPoint);
      }

      @Override
      public ICoordinate visitMultiLineString() throws RuntimeException {
        final IMultiLineString lineString = (IMultiLineString) geometry;
        return calculateInterior(lineString);
      }

      @Override
      public ICoordinate visitLinearRing() throws RuntimeException {
        final ILineString lineString = (ILineString) geometry;
        return calculateInterior(lineString);
      }

      @Override
      public ICoordinate visitLineString() throws RuntimeException {
        final ILineString lineString = (ILineString) geometry;
        return calculateInterior(lineString);
      }

      @Override
      public ICoordinate visitCollection() throws RuntimeException {
        final IGeometryCollection collection = (IGeometryCollection) geometry;
        return calculateInterior(collection);
      }
    };
    return geometry.getGeometryType().accept(visitor);
  }

  public static ICoordinate calculateInterior(final IMultiLineString geometry) {
    ICoordinateSequence sequence = new CoordinateSequenceFactory().createEmptyCoordinateSequence(2, false);
    final Iterable<ILineString> geometries = geometry.geometries();
    for (final ILineString lineString : geometries) {
      sequence = CoordinateSequenceUtilities.concat(sequence, getBisectorIntersections(lineString));
    }
    return CoordinateSequenceUtilities.findNearestNeighbor(geometry.getEnvelope().getCenterCoordinate(), sequence);
  }

  public static ICoordinate calculateInterior(final ILineString geometry) {
    final ICoordinateSequence sequence = getBisectorIntersections(geometry);
    if (sequence.isEmpty()) {
      return null;
    }
    final LineStringInteractOperator lineStringInteractOperator = new LineStringInteractOperator(geometry);
    final List<ICoordinate> coordinates = new ArrayList<>();
    for (final ICoordinate coordinate : sequence.getCoordinates()) {
      if (!lineStringInteractOperator.touch(coordinate)) {
        continue;
      }
      coordinates.add(coordinate);
    }
    if (coordinates.isEmpty()) {
      return null;
    }
    return CoordinateSequenceUtilities.findNearestNeighbor(
        geometry.getEnvelope().getCenterCoordinate(),
        new CoordinateSequenceFactory().create(coordinates));
  }

  public static ICoordinate calculateInterior(final IPoint geometry) {
    return geometry.getCoordinateN(0);
  }

  public static ICoordinate calculateInterior(final IMultiPoint geometry) {
    final ICoordinateSequence coordinateSequence = geometry.getCoordinateSequence();
    return CoordinateSequenceUtilities
        .findNearestNeighbor(geometry.getEnvelope().getCenterCoordinate(), coordinateSequence);
  }

  public static ICoordinate calculateInterior(final IPolygon geometry) {
    final ICoordinateSequence sequence = getBisectorIntersections(geometry);
    return CoordinateSequenceUtilities.findNearestNeighbor(geometry.getEnvelope().getCenterCoordinate(), sequence);
  }

  public static ICoordinate calculateInterior(final IMultiPolygon geometry) {
    final Iterable<IPolygon> geometries = geometry.geometries();
    final List<ICoordinate> coordinates = new ArrayList<>();
    for (final IPolygon polygon : geometries) {
      final ICoordinate calculateInterior = calculateInterior(polygon);
      if (calculateInterior == null) {
        continue;
      }
      coordinates.add(calculateInterior);
    }
    return CoordinateSequenceUtilities.findNearestNeighbor(
        geometry.getEnvelope().getCenterCoordinate(),
        new CoordinateSequenceFactory().create(coordinates));
  }

  public static ICoordinate calculateInterior(final IGeometryCollection geometry) {
    @SuppressWarnings("unchecked")
    final Iterable<IBaseGeometry> geometries = (Iterable<IBaseGeometry>) geometry.geometries();
    final List<ICoordinate> coordinates = new ArrayList<>();
    for (final IBaseGeometry baseGeometry : geometries) {
      coordinates.add(calculateInterior(baseGeometry));
    }
    final ICoordinateSequence coordinateSequence = geometry.getCoordinateSequence();
    final ICoordinate centroid = CoordinateSequenceUtilities.calculateCentroid(coordinateSequence);
    return CoordinateSequenceUtilities
        .findNearestNeighbor(centroid, new CoordinateSequenceFactory().create(coordinates));
  }

  private static ICoordinateSequence getBisectorIntersections(final IPolygon geometry) {
    final IEnvelope envelope = geometry.getEnvelope();
    if (envelope.getHeight() == 0) {
      return new CoordinateSequenceFactory().create(envelope.getCenterCoordinate());
    }
    final ILineSegment bisector = getBisector(envelope);
    final ICoordinateSequence[] sequences = getCoordinateSequence(geometry);
    return new LineSegmentIntersectOperator(bisector).calculate(sequences);
  }

  private static ICoordinateSequence[] getCoordinateSequence(final IPolygon geometry) {
    final ArrayList<ICoordinateSequence> sequences = new ArrayList<>();
    final Iterable<ILinearRing> innerRings = geometry.getInnerRings();
    sequences.add(geometry.getOuterRing().getCoordinateSequence());
    for (final ILinearRing ring : innerRings) {
      sequences.add(ring.getCoordinateSequence());
    }
    return sequences.toArray(new ICoordinateSequence[sequences.size()]);
  }

  public static ICoordinateSequence getBisectorIntersections(final IGeometry geometry) {
    final IEnvelope envelope = geometry.getEnvelope();
    if (envelope.getHeight() == 0) {
      return new CoordinateSequenceFactory().create(envelope.getCenterCoordinate());
    }
    final ILineSegment bisector = getBisector(envelope);
    return new LineSegmentIntersectOperator(bisector).calculate(geometry.getCoordinateSequence());
  }

  private static ILineSegment getBisector(final IEnvelope envelope) {
    if (envelope.getWidth() > envelope.getHeight()) {
      final ICoordinate cc = envelope.getCenterCoordinate();
      final ICoordinate c0 = new Coordinate(cc.getXValue(), cc.getYValue() + envelope.getHeight());
      final ICoordinate c1 = new Coordinate(cc.getXValue(), cc.getYValue() - envelope.getHeight());
      return new LineSegment(c0, c1);
    }
    final ICoordinate cc = envelope.getCenterCoordinate();
    final ICoordinate c0 = new Coordinate(cc.getXValue() - envelope.getWidth(), cc.getYValue());
    final ICoordinate c1 = new Coordinate(cc.getXValue() + envelope.getWidth(), cc.getYValue());
    return new LineSegment(c0, c1);
  }
}