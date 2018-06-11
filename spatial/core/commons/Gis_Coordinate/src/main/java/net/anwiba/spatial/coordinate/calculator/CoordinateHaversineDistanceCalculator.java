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
package net.anwiba.spatial.coordinate.calculator;

import static net.anwiba.commons.utilities.math.MathWrapper.*;

public class CoordinateHaversineDistanceCalculator implements ICoordinateDistanceCalculator {

  private final double earthRadius;

  public CoordinateHaversineDistanceCalculator(final double earthRadius) {
    this.earthRadius = earthRadius;
  }

  @Override
  public double calculateDistance(
      final double lambda,
      final double phi,
      final double otherLambda,
      final double otherPhi) {
    final double latitudeDistance = toRadians(otherPhi - phi);
    final double longitudeDistance = toRadians(otherLambda - lambda);
    final double a = sin(latitudeDistance / 2) * sin(latitudeDistance / 2)
        + cos(toRadians(phi)) * cos(toRadians(otherPhi)) * sin(longitudeDistance / 2) * sin(longitudeDistance / 2);
    return this.earthRadius * (2 * atan2(sqrt(a), sqrt(1 - a)));
  }

}
