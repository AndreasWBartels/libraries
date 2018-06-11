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
 
package net.anwiba.spatial.geometry.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import net.anwiba.commons.logging.ILogger;
import net.anwiba.commons.logging.Logging;
import net.anwiba.spatial.coordinate.Coordinate;
import net.anwiba.spatial.coordinate.CoordinateSequence;
import net.anwiba.spatial.coordinate.CoordinateSequenceFactory;
import net.anwiba.spatial.coordinate.CoordinateSequenceUtilities;
import net.anwiba.spatial.coordinate.CoordinateUtilities;
import net.anwiba.spatial.coordinate.ICoordinate;
import net.anwiba.spatial.coordinate.ICoordinateSequence;
import net.anwiba.spatial.coordinate.ICoordinateSequenceFactory;
import net.anwiba.spatial.coordinate.IEnvelope;
import net.anwiba.spatial.coordinate.calculator.CoordinateSequenceOrientationCalculator;
import net.anwiba.spatial.coordinatereferencesystem.ICoordinateReferenceSystem;
import net.anwiba.spatial.geometry.IBaseGeometry;
import net.anwiba.spatial.geometry.IGeometryCollection;
import net.anwiba.spatial.geometry.IGeometryFactory;
import net.anwiba.spatial.geometry.ILineString;
import net.anwiba.spatial.geometry.ILinearRing;
import net.anwiba.spatial.geometry.IMultiLineString;
import net.anwiba.spatial.geometry.IMultiPoint;
import net.anwiba.spatial.geometry.IMultiPolygon;
import net.anwiba.spatial.geometry.IPoint;
import net.anwiba.spatial.geometry.IPolygon;
import net.anwiba.spatial.geometry.IRectangle;

public class GeometryFactory implements IGeometryFactory {

  private static final long serialVersionUID = -7130012312884204195L;
  private static ILogger logger = Logging.getLogger(GeometryFactory.class.getName());
  private final ICoordinateReferenceSystem coordianteReferenceSystem;
  private final ICoordinateSequenceFactory coordinateSequenceFactory = new CoordinateSequenceFactory();

  public GeometryFactory(final ICoordinateReferenceSystem coordianteReferenceSystem) {
    this.coordianteReferenceSystem = coordianteReferenceSystem;
  }

  @Override
  public IPoint createPoint(final double x, final double y) {
    return createPoint(new Coordinate(x, y));
  }

  @Override
  public IPoint createPoint(final ICoordinate coordinate) {
    return createPoint(coordinate, new CoordinateSequenceFactory().create(coordinate).getEnvelope());
  }

  @Override
  public IPoint createPoint(final ICoordinate coordinate, final IEnvelope envelope) {
    return new Point(getCoordinateReferenceSystem(), coordinate, envelope);
  }

  @Override
  public ILineString createLineString(final double[] xs, final double[] ys) {
    return new LineString(getCoordinateReferenceSystem(), this.coordinateSequenceFactory.create(xs, ys));
  }

  @Override
  public ILineString createLineString(final ICoordinateSequence coordinateSequence) {
    return new LineString(getCoordinateReferenceSystem(), coordinateSequence);
  }

  @Override
  public ILinearRing createLinearRing(final double[] xs, final double[] ys) {
    return createLinearRing(this.coordinateSequenceFactory.create(xs, ys));
  }

  @Override
  public ILinearRing createLinearRing(final ICoordinateSequence sequence) {
    if (!sequence.isClosed() && sequence.getNumberOfCoordinates() > 2) {
      logger.log(Level.WARNING, "Unclosed coordinate sequence for linear ring"); //$NON-NLS-1$
      return new LinearRing(
          getCoordinateReferenceSystem(),
          CoordinateSequenceUtilities.concat(sequence, sequence.getCoordinateN(0)),
          CoordinateSequenceOrientationCalculator.getOrientation(sequence));
    }
    return new LinearRing(
        getCoordinateReferenceSystem(),
        sequence,
        CoordinateSequenceOrientationCalculator.getOrientation(sequence));
  }

  @Override
  public IPolygon createPolygon(final double[] xs, final double[] ys) {
    return createPolygon(this.coordinateSequenceFactory.create(xs, ys), new CoordinateSequence[0]);
  }

  @Override
  public IPolygon createPolygon(final ICoordinateSequence coordinateSequence) {
    return createPolygon(coordinateSequence, new ICoordinateSequence[0]);
  }

  @Override
  public IPolygon createPolygon(final double[] xs, final double[] ys, final double[][] hxs, final double[][] hys) {
    final ICoordinateSequence[] coordinateSequences = new ICoordinateSequence[hxs.length];
    for (int i = 0; i < coordinateSequences.length; i++) {
      coordinateSequences[i] = this.coordinateSequenceFactory.create(hxs[i], hys[i]);
    }
    return createPolygon(this.coordinateSequenceFactory.create(xs, ys), coordinateSequences);
  }

  @Override
  public IPolygon createPolygon(final ICoordinateSequence sequence, final ICoordinateSequence[] sequences) {
    final ILinearRing outerRing = this.createLinearRing(sequence);
    final List<ILinearRing> innerRings = new ArrayList<>();
    for (final ICoordinateSequence coordinateSequence : sequences) {
      innerRings.add(this.createLinearRing(coordinateSequence));
    }
    return createPolygon(outerRing, innerRings.toArray(new ILinearRing[innerRings.size()]));
  }

  @Override
  public IPolygon createPolygon(final ILinearRing outerRing, final List<ILinearRing> innerRings) {
    return createPolygon(outerRing, innerRings.stream().toArray(ILinearRing[]::new));
  }

  @Override
  public Polygon createPolygon(final ILinearRing outerRing, final ILinearRing[] innerRings) {
    if (innerRings == null) {
      return new Polygon(getCoordinateReferenceSystem(), outerRing);
    }
    return new Polygon(getCoordinateReferenceSystem(), outerRing, innerRings);
  }

  // private ILinearRing[] checkInnerRings(final ILinearRing[] innerRings) {
  // final List<ILinearRing> list = new ArrayList<ILinearRing>();
  // for (final ILinearRing linearRing : innerRings) {
  // list.add(checkInnerRing(linearRing));
  // }
  // return list.toArray(new ILinearRing[list.size()]);
  // }

  // private ILinearRing checkInnerRing(final ILinearRing innerRing) {
  // if (CoordinateSequenceOrientationCalculator.isOrientationPositive(innerRing.getCoordinateSequence())) {
  // return innerRing;
  // }
  // return createLinearRing(CoordinateSequenceUtilities.reverse(innerRing.getCoordinateSequence()));
  // }
  //
  // private ILinearRing checkOuterRing(final ILinearRing outerRing) {
  // if (!CoordinateSequenceOrientationCalculator.isOrientationPositive(outerRing.getCoordinateSequence())) {
  // return outerRing;
  // }
  // return createLinearRing(CoordinateSequenceUtilities.reverse(outerRing.getCoordinateSequence()));
  // }

  @Override
  public IMultiPoint createMultiPoint(final double[] xs, final double[] ys) {
    return createMultiPoint(this.coordinateSequenceFactory.create(xs, ys));
  }

  @Override
  public IMultiPoint createMultiPoint(final IPoint point) {
    return new MultiPoint(getCoordinateReferenceSystem(), new IPoint[]{ point });
  }

  @Override
  public IMultiPoint createMultiPoint(final IPoint[] points) {
    return new MultiPoint(getCoordinateReferenceSystem(), points);
  }

  @Override
  public IMultiPoint createMultiPoint(final ICoordinateSequence coordinateSequence) {
    final ICoordinate[] coordinates = CoordinateUtilities.getCoordinates(coordinateSequence);
    final List<IPoint> points = new ArrayList<>();
    for (final ICoordinate coordinate : coordinates) {
      points.add(createPoint(coordinate));
    }
    return new MultiPoint(getCoordinateReferenceSystem(), points.toArray(new IPoint[points.size()]));
  }

  @Override
  public IMultiLineString createMultiLineString(final double[][] xs, final double[][] ys) {
    final ICoordinateSequence[] coordinateSequences = new ICoordinateSequence[xs.length];
    for (int i = 0; i < coordinateSequences.length; i++) {
      coordinateSequences[i] = this.coordinateSequenceFactory.create(xs[i], ys[i]);
    }
    return createMultiLineString(coordinateSequences);
  }

  @Override
  public IMultiLineString createMultiLineString(final double[][] xs, final double[][] ys, final double[][] zs) {
    return createMultiLineString(xs, ys, zs, false);
  }

  @Override
  public IMultiLineString createMultiLineString(
      final double[][] xs,
      final double[][] ys,
      final double[][] zs,
      final boolean isMesured) {
    final ICoordinateSequence[] coordinateSequences = new ICoordinateSequence[xs.length];
    for (int i = 0; i < coordinateSequences.length; i++) {
      coordinateSequences[i] = this.coordinateSequenceFactory.create(xs[i], ys[i], zs[i], isMesured);
    }
    return createMultiLineString(coordinateSequences);
  }

  @Override
  public IMultiLineString createMultiLineString(
      final double[][] xs,
      final double[][] ys,
      final double[][] zs,
      final double[][] ms) {
    final ICoordinateSequence[] coordinateSequences = new ICoordinateSequence[xs.length];
    for (int i = 0; i < coordinateSequences.length; i++) {
      coordinateSequences[i] = this.coordinateSequenceFactory.create(xs[i], ys[i], zs[i], ms[i]);
    }
    return createMultiLineString(coordinateSequences);
  }

  @Override
  public IMultiLineString createMultiLineString(final ICoordinateSequence[] coordinateSequences) {
    final List<ILineString> lineStrings = new ArrayList<>();
    for (final ICoordinateSequence sequence : coordinateSequences) {
      lineStrings.add(this.createLineString(sequence));
    }
    return createMultiLineString(lineStrings.toArray(new ILineString[lineStrings.size()]));
  }

  @Override
  public IMultiLineString createMultiLineString(final ILineString lineStrings) {
    return new MultiLineString(getCoordinateReferenceSystem(), new ILineString[]{ lineStrings });
  }

  @Override
  public IMultiLineString createMultiLineString(final ILineString[] lineStrings) {
    return new MultiLineString(getCoordinateReferenceSystem(), lineStrings);
  }

  @Override
  public IMultiPolygon createMultiPolygon(final double[][] xs, final double[][] ys) {
    final ICoordinateSequence[] coordinateSequences = new ICoordinateSequence[xs.length];
    for (int i = 0; i < coordinateSequences.length; i++) {
      coordinateSequences[i] = this.coordinateSequenceFactory.create(xs[i], ys[i]);
    }
    return createMultiPolygon(coordinateSequences);
  }

  private IMultiPolygon createMultiPolygon(final ICoordinateSequence[] outerRingSequences) {
    final List<IPolygon> polygons = new ArrayList<>();
    for (final ICoordinateSequence sequence : outerRingSequences) {
      polygons.add(this.createPolygon(sequence));
    }
    return new MultiPolygon(getCoordinateReferenceSystem(), polygons.toArray(new IPolygon[polygons.size()]));
  }

  @Override
  public IMultiPolygon createMultiPolygon(
      final double[][] xs,
      final double[][] ys,
      final double[][][] hxs,
      final double[][][] hys) {
    final ICoordinateSequence[] outerRingSequences = new ICoordinateSequence[xs.length];
    for (int i = 0; i < outerRingSequences.length; i++) {
      outerRingSequences[i] = this.coordinateSequenceFactory.create(xs[i], ys[i]);
    }
    final ICoordinateSequence[][] innerRingSequences = new ICoordinateSequence[hxs.length][];
    for (int i = 0; i < innerRingSequences.length; i++) {
      innerRingSequences[i] = new ICoordinateSequence[hxs[i].length];
      for (int j = 0; j < innerRingSequences[i].length; j++) {
        innerRingSequences[i][j] = this.coordinateSequenceFactory.create(hxs[i][j], hys[i][j]);
      }
    }
    return createMultiPolygon(outerRingSequences, innerRingSequences);
  }

  @Override
  public IMultiPolygon createMultiPolygon(
      final ICoordinateSequence[] outerRingSequences,
      final ICoordinateSequence[][] innerRingSequences) {

    final List<ILinearRing> outerRings = new ArrayList<>();
    final List<ILinearRing[]> innerRings = new ArrayList<>();
    for (int i = 0; i < outerRingSequences.length; i++) {
      outerRings.add(this.createLinearRing(outerRingSequences[i]));
      final List<ILinearRing> rings = new ArrayList<>();
      for (int j = 0; j < innerRingSequences[i].length; j++) {
        rings.add(this.createLinearRing(innerRingSequences[i][j]));
      }
      innerRings.add(rings.toArray(new LinearRing[rings.size()]));
    }
    return createMultiPolygon(
        outerRings.toArray(new LinearRing[outerRings.size()]),
        innerRings.toArray(new ILinearRing[innerRings.size()][]));
  }

  private IMultiPolygon createMultiPolygon(final ILinearRing[] outerRings, final ILinearRing[][] innerRings) {
    final List<IPolygon> polygons = new ArrayList<>();
    for (int i = 0; i < outerRings.length; i++) {
      polygons.add(this.createPolygon(outerRings[i], innerRings[i]));
    }
    return new MultiPolygon(getCoordinateReferenceSystem(), polygons.toArray(new IPolygon[polygons.size()]));
  }

  @Override
  public IMultiPolygon createMultiPolygon(final IPolygon polygon) {
    return new MultiPolygon(getCoordinateReferenceSystem(), new IPolygon[]{ polygon });
  }

  @Override
  public IMultiPolygon createMultiPolygon(final IPolygon[] polygons) {
    return new MultiPolygon(getCoordinateReferenceSystem(), polygons);
  }

  @Override
  public IMultiPolygon createMultiPolygon(final List<IPolygon> polygons) {
    return new MultiPolygon(getCoordinateReferenceSystem(), polygons.stream().toArray(IPolygon[]::new));
  }

  @Override
  public IGeometryCollection createCollection(final IBaseGeometry[] geometries) {
    return new GeometryCollection(getCoordinateReferenceSystem(), geometries);
  }

  @Override
  public IPolygon createPolygon(final IEnvelope envelope) {
    return createRectangle(envelope);
  }

  @Override
  public IRectangle createRectangle(final IEnvelope envelope) {
    return new Rectangle(this.coordianteReferenceSystem, envelope);
  }

  @Override
  public ICoordinateSequenceFactory getCoordinateSequenceFactory() {
    return this.coordinateSequenceFactory;
  }

  @Override
  public ICoordinateReferenceSystem getCoordinateReferenceSystem() {
    return this.coordianteReferenceSystem;
  }
}