/*
 * #%L
 * anwiba spatial
 * %%
 * Copyright (C) 2007 - 2022 Andreas Bartels
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
package net.anwiba.spatial.geometry.converter;

import net.anwiba.commons.lang.functional.ConversionException;
import net.anwiba.spatial.coordinate.Coordinate;
import net.anwiba.spatial.coordinate.CoordinateSequenceFactory;
import net.anwiba.spatial.coordinate.ICoordinate;
import net.anwiba.spatial.coordinate.ICoordinateSequence;
import net.anwiba.spatial.geometry.IBaseGeometry;
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
import net.anwiba.spatial.geometry.internal.GeometryFactory;

import java.util.ArrayList;
import java.util.List;

public class GeometryToGeometryConverter implements IGeometryToGeometryConverter {

  private final CoordinateSequenceFactory coordinateSequenceFactory = new CoordinateSequenceFactory();
  private final int targetCoordinateDimesion;
  private final boolean isMeasured;
  private final IOrdinateValueCalculatorFactory ordinateValueCalculator;

  public GeometryToGeometryConverter(final int targetCoordinateDimesion,
      final boolean isMeasured) {
    this(targetCoordinateDimesion,
        isMeasured,
        coordinateReferenceSystem -> new IOrdinateValueCalculator() {

          double[] previous = null;
          double previousMeasuredValue = Double.NaN;

          @Override
          public double calculate(final int coordinateIndex,
              final int ordinateIndex,
              final boolean measured,
              final double[] values,
              final boolean valuesMeasured) {
            try {
              if (measured) {
                double measuredValue = Double.NaN;
                try {
                  if (isMeasured) {
                    measuredValue = values[values.length - 1];
                  }
                  return measuredValue;
                } finally {
                  this.previousMeasuredValue = measuredValue;
                }
              }
              if (isMeasured && ordinateIndex >= values.length - 2) {
                return 0;
              }
              return ordinateIndex >= values.length ? 0d : values[ordinateIndex];
            } finally {
              this.previous = values;
            }
          }
        });
  }

  public GeometryToGeometryConverter(final int targetCoordinateDimesion,
      final boolean isMeasured,
      final IOrdinateValueCalculatorFactory ordinateValueCalculator) {
    this.targetCoordinateDimesion = targetCoordinateDimesion;
    this.isMeasured = isMeasured;
    this.ordinateValueCalculator = ordinateValueCalculator;
  }

  @Override
  public IGeometry convert(final IGeometry geometry) throws ConversionException {
    return geometry.getGeometryType().accept(new IGeometryTypeVisitor<IGeometry, ConversionException>() {

      @Override
      public IPoint visitPoint() throws ConversionException {
        return toPoint((IPoint) geometry);
      }

      @Override
      public ILineString visitLineString() throws ConversionException {
        return toLineString((ILineString) geometry);
      }

      @Override
      public ILinearRing visitLinearRing() throws ConversionException {
        return toLinearRing((ILinearRing) geometry);
      }

      @Override
      public IPolygon visitPolygon() throws ConversionException {
        return toPolygon((IPolygon) geometry);
      }

      @Override
      public IMultiPoint visitMultiPoint() throws ConversionException {
        return toMultiPoint((IMultiPoint) geometry);
      }

      @Override
      public IMultiLineString visitMultiLineString() throws ConversionException {
        return toMultiLineString((IMultiLineString) geometry);
      }

      @Override
      public IMultiPolygon visitMultiPolygon() throws ConversionException {
        return toMultiPolygon((IMultiPolygon) geometry);
      }

      @Override
      public IGeometryCollection visitCollection() throws ConversionException {
        return toGeometryCollection((IGeometryCollection) geometry);
      }

      @Override
      public IGeometry visitUnknown() throws ConversionException {
        final UnsupportedOperationException cause = new UnsupportedOperationException("Unsupported geometry type");
        throw new ConversionException(cause.getMessage(), cause);
      }
    });
  }

  private IPoint toPoint(final IPoint point) {
    final ICoordinate coordinate =
        convert(this.ordinateValueCalculator.create(point.getCoordinateReferenceSystem()), point.getCoordinate());
    return new GeometryFactory(point.getCoordinateReferenceSystem())
        .createPoint(coordinate);
  }

  private IMultiPoint toMultiPoint(final IMultiPoint multiPoint) {
    List<IPoint> points = new ArrayList<>();
    for (IPoint point : multiPoint.geometries()) {
      points.add(toPoint(point));
    }
    return new GeometryFactory(multiPoint.getCoordinateReferenceSystem())
        .createMultiPoint(points);
  }

  protected ILineString toLineString(final ILineString lineString) {
    final ICoordinateSequence coordinateSequence = convert(
        this.ordinateValueCalculator.create(lineString.getCoordinateReferenceSystem()),
        lineString.getCoordinateSequence());
    return new GeometryFactory(lineString.getCoordinateReferenceSystem())
        .createLineString(coordinateSequence);
  }

  protected ILinearRing toLinearRing(final ILinearRing linearRing) {
    final ICoordinateSequence coordinateSequence = convert(
        this.ordinateValueCalculator.create(linearRing.getCoordinateReferenceSystem()),
        linearRing.getCoordinateSequence());
    return new GeometryFactory(linearRing.getCoordinateReferenceSystem())
        .createLinearRing(coordinateSequence);
  }

  protected IMultiLineString toMultiLineString(final IMultiLineString multiLineString) {
    List<ILineString> lineStrings = new ArrayList<>();
    for (ILineString lineString : multiLineString.geometries()) {
      if (lineString instanceof ILinearRing linearRing) {
        lineStrings.add(toLinearRing(linearRing));
        continue;
      }
      lineStrings.add(toLineString(lineString));
    }
    return new GeometryFactory(multiLineString.getCoordinateReferenceSystem())
        .createMultiLineString(lineStrings);
  }

  protected IPolygon toPolygon(final IPolygon polygon) {
    ILinearRing outerRing = toLinearRing(polygon.getOuterRing());
    List<ILinearRing> innerRings = new ArrayList<>(polygon.getNumberOfInnerRings());
    for (ILinearRing innerRing : polygon.getInnerRings()) {
      innerRings.add(toLinearRing(innerRing));
    }
    return new GeometryFactory(polygon.getCoordinateReferenceSystem())
        .createPolygon(outerRing, innerRings);
  }

  protected IMultiPolygon toMultiPolygon(final IMultiPolygon multiPolygon) {
    List<IPolygon> polygons = new ArrayList<>();
    for (IPolygon polygon : multiPolygon.geometries()) {
      polygons.add(toPolygon(polygon));
    }
    return new GeometryFactory(multiPolygon.getCoordinateReferenceSystem())
        .createMultiPolygon(polygons);
  }

  protected IGeometryCollection toGeometryCollection(final IGeometryCollection geometryCollection) {
    List<IBaseGeometry> baseGeometries = new ArrayList<>();
    for (IBaseGeometry baseGeometry : geometryCollection.geometries()) {
      if (baseGeometry instanceof IPoint point) {
        baseGeometries.add(toPoint(point));
        continue;
      }
      if (baseGeometry instanceof ILineString lineString) {
        baseGeometries.add(toLineString(lineString));
        continue;
      }
      if (baseGeometry instanceof ILinearRing linearRing) {
        baseGeometries.add(toLinearRing(linearRing));
        continue;
      }
      if (baseGeometry instanceof IPolygon polygon) {
        baseGeometries.add(toPolygon(polygon));
        continue;
      }
    }
    return new GeometryFactory(geometryCollection.getCoordinateReferenceSystem())
        .createCollection(baseGeometries);
  }

  private ICoordinateSequence convert(final IOrdinateValueCalculator calculator,
      final ICoordinateSequence coordinateSequence) {
    List<ICoordinate> coordinates = new ArrayList<>(coordinateSequence.getNumberOfCoordinates());
    int index = 0;
    for (ICoordinate coordinate : coordinateSequence.getCoordinates()) {
      coordinates.add(convert(calculator, index++, coordinate));
    }
    return this.coordinateSequenceFactory.create(coordinates);
  }

  private ICoordinate convert(final IOrdinateValueCalculator calculator, final ICoordinate coordinate) {
    return convert(calculator, 0, coordinate);
  }

  private ICoordinate convert(final IOrdinateValueCalculator calculator,
      final int coordinateIndex,
      final ICoordinate coordinate) {
    double[] values = coordinate.getValues();
    if (this.targetCoordinateDimesion == 2) {
      if (this.isMeasured) {
        return new Coordinate(
            calculator.calculate(coordinateIndex, 0, false, values, coordinate.isMeasured()),
            calculator.calculate(coordinateIndex, 1, false, values, coordinate.isMeasured()),
            calculator.calculate(coordinateIndex, 2, true, values, coordinate.isMeasured()),
            true);
      }
      return new Coordinate(
          calculator.calculate(coordinateIndex, 0, false, values, coordinate.isMeasured()),
          calculator.calculate(coordinateIndex, 1, false, values, coordinate.isMeasured()));
    }
    if (this.targetCoordinateDimesion == 3) {
      if (this.isMeasured) {
        return new Coordinate(
            calculator.calculate(coordinateIndex, 0, false, values, coordinate.isMeasured()),
            calculator.calculate(coordinateIndex, 1, false, values, coordinate.isMeasured()),
            calculator.calculate(coordinateIndex, 2, false, values, coordinate.isMeasured()),
            calculator.calculate(coordinateIndex, 3, true, values, coordinate.isMeasured()));
      }
      return new Coordinate(
          calculator.calculate(coordinateIndex, 0, false, values, coordinate.isMeasured()),
          calculator.calculate(coordinateIndex, 1, false, values, coordinate.isMeasured()),
          calculator.calculate(coordinateIndex, 2, false, values, coordinate.isMeasured()),
          false);
    }
    double[] result = new double[this.targetCoordinateDimesion + (this.isMeasured ? 1 : 0)];
    for (int i = 0; i < result.length; i++) {
      result[i] = calculator.calculate(coordinateIndex,
          i,
          this.isMeasured && i + 1 == result.length,
          values,
          coordinate.isMeasured());
    }
    return new Coordinate(result, this.isMeasured);
  }
}
