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

import net.anwiba.spatial.coordinate.ICoordinateSequence;
import net.anwiba.spatial.coordinatereferencesystem.ICoordinateReferenceSystem;
import net.anwiba.spatial.geometry.GeometryType;
import net.anwiba.spatial.geometry.ILineString;

public class LineString extends AbstractGeometry implements ILineString {

  private static final long serialVersionUID = -8726599134630191264L;
  final ICoordinateSequence coordinateSequence;

  LineString(final ICoordinateReferenceSystem coordinateReferenceSystem, final ICoordinateSequence coordinateSequence) {
    super(coordinateReferenceSystem, 1, coordinateSequence.getEnvelope());
    ensure(coordinateSequence);
    this.coordinateSequence = coordinateSequence;
  }

  @Override
  protected void ensure(final ICoordinateSequence sequence) {
    super.ensure(sequence);
    if (sequence.getNumberOfCoordinates() < 2) {
      throw new IllegalArgumentException("Linestring needs more than one coordinates"); //$NON-NLS-1$
    }
  }

  @Override
  public int getCoordinateDimension() {
    return this.coordinateSequence.getDimension();
  }

  @Override
  public ICoordinateSequence getCoordinateSequence() {
    return this.coordinateSequence;
  }

  @Override
  public GeometryType getGeometryType() {
    return GeometryType.LINESTRING;
  }
}