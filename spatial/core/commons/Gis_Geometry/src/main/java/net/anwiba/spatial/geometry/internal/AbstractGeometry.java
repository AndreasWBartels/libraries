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

import net.anwiba.spatial.coordinate.ICoordinate;
import net.anwiba.spatial.coordinate.ICoordinateSequence;
import net.anwiba.spatial.coordinate.IEnvelope;
import net.anwiba.spatial.coordinatereferencesystem.ICoordinateReferenceSystem;
import net.anwiba.spatial.geometry.GeometryType;
import net.anwiba.spatial.geometry.IGeometry;

public abstract class AbstractGeometry implements IGeometry {

  private static final long serialVersionUID = -106165025117947056L;
  private final IEnvelope envelope;
  private final ICoordinateReferenceSystem coordinateReferenceSystem;
  private final int dimension;

  AbstractGeometry(
      final ICoordinateReferenceSystem coordinateReferenceSystem,
      final int dimension,
      final IEnvelope envelope) {
    this.coordinateReferenceSystem = coordinateReferenceSystem;
    this.dimension = dimension;
    this.envelope = envelope;
  }

  protected void ensure(final ICoordinateSequence sequence) {
    if (sequence.getDimension() < getDimension()) {
      throw new IllegalArgumentException(
          "Coordinate dimension (" + sequence.getDimension() + ") is to small for a " + getGeometryType().name()); //$NON-NLS-1$ //$NON-NLS-2$
    }
  }

  @Override
  public final ICoordinate getCoordinateN(final int index) {
    return getCoordinateSequence().getCoordinateN(index);
  }

  @Override
  public abstract ICoordinateSequence getCoordinateSequence();

  @Override
  public final int getDimension() {
    return this.dimension;
  }

  @Override
  public IEnvelope getEnvelope() {
    return this.envelope;
  }

  @Override
  public ICoordinateReferenceSystem getCoordinateReferenceSystem() {
    return this.coordinateReferenceSystem;
  }

  @Override
  public abstract GeometryType getGeometryType();

  @Override
  public final int getNumberOfCoordinates() {
    return getCoordinateSequence().getNumberOfCoordinates();
  }

  @Override
  public boolean isMeasured() {
    return getCoordinateSequence().isMeasured();
  }

  @Override
  public final boolean isCollection() {
    return getGeometryType().isCollection();
  }
}