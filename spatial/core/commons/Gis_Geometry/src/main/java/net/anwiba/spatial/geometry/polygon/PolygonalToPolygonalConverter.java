/*
 * #%L
 * *
 * %%
 * Copyright (C) 2007 - 2017 Andreas W. Bartels
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
package net.anwiba.spatial.geometry.polygon;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import net.anwiba.commons.lang.functional.ConversionException;
import net.anwiba.spatial.coordinate.CoordinateSequenceUtilities;
import net.anwiba.spatial.coordinate.ICoordinateSequence;
import net.anwiba.spatial.coordinate.Orientation;
import net.anwiba.spatial.coordinate.calculator.CoordinateSequenceOrientationCalculator;
import net.anwiba.spatial.geometry.IGeometryFactory;
import net.anwiba.spatial.geometry.IGeometryFactoryProvider;
import net.anwiba.spatial.geometry.ILinearRing;
import net.anwiba.spatial.geometry.IMultiPolygon;
import net.anwiba.spatial.geometry.IPolygon;
import net.anwiba.spatial.geometry.IPolygonal;

public class PolygonalToPolygonalConverter {

  private final Orientation interiorOrientation;
  private final Orientation exteriorOrientation;
  private final IGeometryFactoryProvider geometryFactoryProvider;

  public PolygonalToPolygonalConverter(
      final IGeometryFactoryProvider geometryFactoryProvider,
      final IPolygonConfiguration configuration) {
    this(geometryFactoryProvider, configuration.getExteriorOrientation(), configuration.getInteriorOrientation());
  }

  public PolygonalToPolygonalConverter(
      final IGeometryFactoryProvider geometryFactoryProvider,
      final Orientation exteriorOrientation,
      final Orientation interiorOrientation) {
    this.geometryFactoryProvider = geometryFactoryProvider;
    this.exteriorOrientation = exteriorOrientation;
    this.interiorOrientation = interiorOrientation;
  }

  public IPolygonal convert(final IPolygonal polygonal) throws ConversionException {
    if (polygonal == null) {
      return null;
    }
    final IGeometryFactory geometryFactory = this.geometryFactoryProvider
        .getGeometryFactory(polygonal.getCoordinateReferenceSystem());
    if (polygonal instanceof IPolygon) {
      return convert(geometryFactory, (IPolygon) polygonal);
    }
    if (polygonal instanceof IMultiPolygon) {
      return convert(geometryFactory, (IMultiPolygon) polygonal);
    }
    throw new ConversionException("Unsupported implementation " + polygonal.getClass().getSimpleName()); //$NON-NLS-1$
  }

  private IMultiPolygon convert(final IGeometryFactory geometryFactory, final IMultiPolygon multiPolygon) {
    final List<IPolygon> polygons = new ArrayList<>(multiPolygon.getNumberOfGeometries());
    for (final IPolygon polygon : multiPolygon.geometries()) {
      polygons.add(convert(geometryFactory, polygon));
    }
    return geometryFactory.createMultiPolygon(polygons);
  }

  private IPolygon convert(final IGeometryFactory geometryFactory, final IPolygon polygon) {
    final ILinearRing outerRing = convert(geometryFactory, this.exteriorOrientation, polygon.getOuterRing());
    final List<ILinearRing> innerRings = new ArrayList<>(polygon.getNumberOfInnerRings());
    for (final ILinearRing innerRing : polygon.getInnerRings()) {
      innerRings.add(convert(geometryFactory, this.interiorOrientation, innerRing));
    }
    return geometryFactory.createPolygon(outerRing, innerRings);
  }

  private ILinearRing convert(
      final IGeometryFactory geometryFactory,
      final Orientation orientation,
      final ILinearRing ring) {
    final ICoordinateSequence coordinateSequence = ring.getCoordinateSequence();
    if (Objects.equals(orientation, CoordinateSequenceOrientationCalculator.getOrientation(coordinateSequence))) {
      return ring;
    }
    return geometryFactory.createLinearRing(CoordinateSequenceUtilities.reverse(coordinateSequence));
  }
}
