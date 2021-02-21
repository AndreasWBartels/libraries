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

package net.anwiba.spatial.geometry.operator;

import net.anwiba.spatial.coordinate.CoordinateCalculationException;
import net.anwiba.spatial.coordinate.CoordinateUtilities;
import net.anwiba.spatial.coordinate.ICoordinate;

public class LineIntersector {

  private ICoordinate intersection;

  public void computeIntersection(
      final ICoordinate startPoint,
      final ICoordinate endPoint,
      final ICoordinate point,
      final ICoordinate otherPoint) {
    if (CoordinateUtilities.isCrossing(startPoint, endPoint, point, otherPoint)) {
      try {
        this.intersection = CoordinateUtilities.calculateIntersection(startPoint, endPoint, point, otherPoint);
      } catch (final CoordinateCalculationException exception) {
        this.intersection = null;
      }
    } else {
      this.intersection = null;
    }
  }

  public boolean hasIntersection() {
    return this.intersection != null;
  }

  public boolean isIntersection(final ICoordinate point) {
    if (point == null) {
      return false;
    }
    return point.equals(this.intersection);
  }

}
