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
package net.anwiba.spatial.geometry;

import java.io.Serializable;
import java.util.List;

import net.anwiba.spatial.coordinate.ICoordinate;
import net.anwiba.spatial.coordinate.ICoordinateSequence;
import net.anwiba.spatial.coordinate.ICoordinateSequenceFactory;
import net.anwiba.spatial.coordinate.IEnvelope;
import net.anwiba.spatial.coordinatereferencesystem.ICoordinateReferenceSystem;

public interface IGeometryFactory extends Serializable {

  ICoordinateReferenceSystem getCoordinateReferenceSystem();

  IPoint createPoint(final ICoordinate coordinate);

  IPoint createPoint(final ICoordinate coordinate, IEnvelope envelope);

  IPoint createPoint(Number x, Number y);

  IPoint createPoint(double x, double y);

  ILineString createLineString(final double[] xs, final double[] ys);

  ILineString createLineString(final ICoordinateSequence coordinateSequence);

  ILinearRing createLinearRing(final double[] xs, final double[] ys);

  ILinearRing createLinearRing(final ICoordinateSequence sequence);

  IPolygon createPolygon(final double[] xs, final double[] ys);

  IPolygon createPolygon(final ICoordinateSequence coordinateSequence);

  IPolygon createPolygon(final double[] xs, final double[] ys, final double[][] hxs, final double[][] hys);

  IPolygon createPolygon(final ICoordinateSequence sequence, final ICoordinateSequence[] sequences);

  IPolygon createPolygon(final ILinearRing outerRing, final ILinearRing[] innerRings);

  IPolygon createPolygon(final ILinearRing outerRing, final List<ILinearRing> innerRings);

  IMultiPoint createMultiPoint(final double[] xs, final double[] ys);

  IMultiPoint createMultiPoint(IPoint points);

  IMultiPoint createMultiPoint(final List<IPoint> points);

  IMultiPoint createMultiPoint(final IPoint[] points);

  IMultiPoint createMultiPoint(final ICoordinateSequence coordinateSequence);

  IMultiLineString createMultiLineString(final double[][] xs, final double[][] ys);

  IMultiLineString createMultiLineString(final double[][] xs, final double[][] ys, final double[][] zs);

  IMultiLineString createMultiLineString(
      final double[][] xs,
      final double[][] ys,
      final double[][] zs,
      final boolean isMesured);

  IMultiLineString createMultiLineString(
      final double[][] xs,
      final double[][] ys,
      final double[][] zs,
      final double[][] ms);

  IMultiLineString createMultiLineString(final ICoordinateSequence[] coordinateSequences);

  IMultiLineString createMultiLineString(ILineString geometry);

  IMultiLineString createMultiLineString(final ILineString[] lineStrings);

  IMultiLineString createMultiLineString(List<ILineString> lineStrings);

  IMultiPolygon createMultiPolygon(final double[][] xs, final double[][] ys);

  IMultiPolygon createMultiPolygon(
      final double[][] xs,
      final double[][] ys,
      final double[][][] hxs,
      final double[][][] hys);

  IMultiPolygon createMultiPolygon(
      final ICoordinateSequence[] outerRingSequences,
      final ICoordinateSequence[][] innerRingSequences);

  IMultiPolygon createMultiPolygon(IPolygon polygon);

  IMultiPolygon createMultiPolygon(final IPolygon[] polygons);

  IMultiPolygon createMultiPolygon(List<IPolygon> polygons);

  IGeometryCollection createCollection(final IBaseGeometry[] geometries);

  IGeometryCollection createCollection(List<IBaseGeometry> geometries);

  ICoordinateSequenceFactory getCoordinateSequenceFactory();

  IPolygon createPolygon(IEnvelope envelope);

  IRectangle createRectangle(IEnvelope envelope);

}
