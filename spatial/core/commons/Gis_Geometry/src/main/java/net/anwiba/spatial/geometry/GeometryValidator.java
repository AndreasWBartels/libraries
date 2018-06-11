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

import net.anwiba.spatial.coordinate.ICoordinate;
import net.anwiba.spatial.coordinate.ICoordinateSequence;

public class GeometryValidator {

  public static boolean isValidLinearRing(final ICoordinateSequence coordinateSequence) {
    try {
      ensureIsLinearRing(coordinateSequence);
      return true;
    } catch (final IllegalArgumentException e) {
      return false;
    }
  }

  public static void ensureIsLinearRing(final ICoordinateSequence sequence) {
    if (sequence.getDimension() < 2) {
      throw new IllegalArgumentException(
          "Coordinate dimension (" + sequence.getDimension() + ") is to small for a Linearring"); //$NON-NLS-1$ //$NON-NLS-2$
    }
    final int coordinateCount = sequence.getNumberOfCoordinates();
    if (coordinateCount < 3) {
      throw new IllegalArgumentException("Linearring needs more than two coordinates"); //$NON-NLS-1$
    }
    final ICoordinate firstCoordinate = sequence.getCoordinateN(0);
    final ICoordinate lastCoordinate = sequence.getCoordinateN(coordinateCount - 1);
    if (!firstCoordinate.equals(lastCoordinate)) {
      throw new IllegalArgumentException("first and last coordinare must be equal for Linearrings"); //$NON-NLS-1$   
    }
  }

}
