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
 
package net.anwiba.spatial.geometry;

import java.io.Serializable;
import java.util.List;

import net.anwiba.spatial.coordinate.ICoordinate;
import net.anwiba.spatial.coordinate.ICoordinateSequence;
import net.anwiba.spatial.coordinate.ICoordinateSequenceFactory;
import net.anwiba.spatial.coordinate.IEnvelope;
import net.anwiba.spatial.coordinatereferencesystem.ICoordinateReferenceSystem;

public interface IGeometryFactory extends Serializable {

  public ICoordinateReferenceSystem getCoordinateReferenceSystem();

  public IPoint createPoint(final ICoordinate coordinate);

  public IPoint createPoint(final ICoordinate coordinate, IEnvelope envelope);

  public IPoint createPoint(double x, double y);

  public ILineString createLineString(final double[] xs, final double[] ys);

  public ILineString createLineString(final ICoordinateSequence coordinateSequence);

  public ILinearRing createLinearRing(final double[] xs, final double[] ys);

  public ILinearRing createLinearRing(final ICoordinateSequence sequence);

  public IPolygon createPolygon(final double[] xs, final double[] ys);

  public IPolygon createPolygon(final ICoordinateSequence coordinateSequence);

  public IPolygon createPolygon(final double[] xs, final double[] ys, final double[][] hxs, final double[][] hys);

  public IPolygon createPolygon(final ICoordinateSequence sequence, final ICoordinateSequence[] sequences);

  public IPolygon createPolygon(final ILinearRing outerRing, final ILinearRing[] innerRings);

  public IPolygon createPolygon(final ILinearRing outerRing, final List<ILinearRing> innerRings);

  public IMultiPoint createMultiPoint(final double[] xs, final double[] ys);

  public IMultiPoint createMultiPoint(final IPoint point);

  public IMultiPoint createMultiPoint(final IPoint[] points);

  public IMultiPoint createMultiPoint(final ICoordinateSequence coordinateSequence);

  public IMultiLineString createMultiLineString(final double[][] xs, final double[][] ys);

  public IMultiLineString createMultiLineString(final double[][] xs, final double[][] ys, final double[][] zs);

  public IMultiLineString createMultiLineString(
      final double[][] xs,
      final double[][] ys,
      final double[][] zs,
      final boolean isMesured);

  public IMultiLineString createMultiLineString(
      final double[][] xs,
      final double[][] ys,
      final double[][] zs,
      final double[][] ms);

  public IMultiLineString createMultiLineString(final ICoordinateSequence[] coordinateSequences);

  public IMultiLineString createMultiLineString(ILineString geometry);

  public IMultiLineString createMultiLineString(final ILineString[] lineStrings);

  public IMultiPolygon createMultiPolygon(final double[][] xs, final double[][] ys);

  public IMultiPolygon createMultiPolygon(
      final double[][] xs,
      final double[][] ys,
      final double[][][] hxs,
      final double[][][] hys);

  public IMultiPolygon createMultiPolygon(
      final ICoordinateSequence[] outerRingSequences,
      final ICoordinateSequence[][] innerRingSequences);

  public IMultiPolygon createMultiPolygon(IPolygon polygon);

  public IMultiPolygon createMultiPolygon(final IPolygon[] polygons);

  public IMultiPolygon createMultiPolygon(List<IPolygon> polygons);

  public IGeometryCollection createCollection(final IBaseGeometry[] geometries);

  public ICoordinateSequenceFactory getCoordinateSequenceFactory();

  public IPolygon createPolygon(IEnvelope envelope);

  public IRectangle createRectangle(IEnvelope envelope);

}