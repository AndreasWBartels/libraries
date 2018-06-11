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

import java.util.Arrays;

import net.anwiba.spatial.coordinate.CoordinateSequenceUtilities;
import net.anwiba.spatial.coordinate.ICoordinateSequence;
import net.anwiba.spatial.coordinatereferencesystem.ICoordinateReferenceSystem;
import net.anwiba.spatial.geometry.GeometryType;
import net.anwiba.spatial.geometry.ILinearRing;
import net.anwiba.spatial.geometry.IPolygon;

public class Polygon extends AbstractGeometry implements IPolygon {

  private static final long serialVersionUID = 5918310084074735053L;

  private final ILinearRing[] innerRings;
  private final ILinearRing outerRing;

  Polygon(final ICoordinateReferenceSystem coordinateReferenceSystem, final ILinearRing outerRing) {
    this(coordinateReferenceSystem, outerRing, new LinearRing[0]);
  }

  Polygon(
      final ICoordinateReferenceSystem coordinateReferenceSystem,
      final ILinearRing outerRing,
      final ILinearRing[] innerRings) {
    super(coordinateReferenceSystem, 2, outerRing.getEnvelope());
    this.outerRing = outerRing;
    this.innerRings = innerRings;
  }

  @Override
  public int getCoordinateDimension() {
    return this.outerRing.getCoordinateDimension();
  }

  @Override
  public ICoordinateSequence getCoordinateSequence() {
    ICoordinateSequence sequence = this.outerRing.getCoordinateSequence();
    for (final ILinearRing innerRing : this.innerRings) {
      sequence = CoordinateSequenceUtilities.concat(sequence, innerRing.getCoordinateSequence());
    }
    return sequence;
  }

  @Override
  public GeometryType getGeometryType() {
    return GeometryType.POLYGON;
  }

  @Override
  public ILinearRing getInnerRingN(final int index) {
    return this.innerRings[index];
  }

  @Override
  public int getNumberOfInnerRings() {
    return this.innerRings.length;
  }

  @Override
  public ILinearRing getOuterRing() {
    return this.outerRing;
  }

  @Override
  public Iterable<ILinearRing> getInnerRings() {
    return Arrays.asList(this.innerRings);
  }
}