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
// Copyright (c) 2006 by Andreas W. Bartels 
package net.anwiba.spatial.geometry.internal;

import static net.anwiba.commons.ensure.Conditions.*;
import static net.anwiba.commons.ensure.Ensure.*;

import java.util.Objects;

import net.anwiba.spatial.coordinate.ICoordinateSequence;
import net.anwiba.spatial.coordinate.Orientation;
import net.anwiba.spatial.coordinatereferencesystem.ICoordinateReferenceSystem;
import net.anwiba.spatial.geometry.GeometryType;
import net.anwiba.spatial.geometry.ILinearRing;

public class LinearRing extends LineString implements ILinearRing {

  private static final long serialVersionUID = -6728378856953023848L;
  private final Orientation orientation;

  LinearRing(
    final ICoordinateReferenceSystem coordinateReferenceSystem,
    final ICoordinateSequence coordinateSequence,
    final Orientation orientation) {
    super(coordinateReferenceSystem, coordinateSequence);
    ensureThat(orientation, notNull());
    this.orientation = orientation;
  }

  @Override
  protected void ensure(final ICoordinateSequence sequence) {
    super.ensure(sequence);
    final int coordinateCount = sequence.getNumberOfCoordinates();
    if (coordinateCount < 4) {
      throw new IllegalArgumentException("Linearring needs more than two different coordinates"); //$NON-NLS-1$
    }
    if (!sequence.isClosed()) {
      throw new IllegalArgumentException("first and last coordinare must be equal for Linearrings"); //$NON-NLS-1$
    }
  }

  @Override
  public GeometryType getGeometryType() {
    return GeometryType.LINEARRING;
  }

  @Override
  public Orientation getOrientation() {
    return this.orientation;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + Objects.hash(orientation);
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (!super.equals(obj))
      return false;
    if (getClass() != obj.getClass())
      return false;
    LinearRing other = (LinearRing) obj;
    return this.orientation == other.orientation;
  }
}
