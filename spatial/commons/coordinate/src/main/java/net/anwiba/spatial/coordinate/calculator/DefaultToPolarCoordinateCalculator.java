/*
 * #%L
 * anwiba spatial
 * %%
 * Copyright (C) 2007 - 2022 Andreas Bartels
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

import net.anwiba.commons.utilities.math.DirectionAngle;
import net.anwiba.commons.utilities.math.DirectionOrientation;
import net.anwiba.spatial.coordinate.Coordinate;
import net.anwiba.spatial.coordinate.ICoordinate;

public class DefaultToPolarCoordinateCalculator implements IToPolarCoordinateCalculator {

  private final DefaultCoordinateDirectionCalculator directionCalculator = new DefaultCoordinateDirectionCalculator();
  private final DefaultCoordinateDistanceCalculator distanceCalculator = new DefaultCoordinateDistanceCalculator();

  @Override
  public ICoordinate calculate(final ICoordinate coordinate, final ICoordinate other) {
    double distance = this.distanceCalculator.calculate(coordinate, other);
    DirectionAngle direction = this.directionCalculator.calculate(coordinate, other);
    return Coordinate.of(distance, direction.convertTo(DirectionOrientation.GEOGRAPHIC_NORTH).degree());
  }

  @Override
  public ICoordinate calculate(final ICoordinate prior, final ICoordinate coordinate, final ICoordinate next) {
    double distance = this.distanceCalculator.calculate(coordinate, next);
    final DirectionAngle direction = this.directionCalculator.calculate(coordinate, next);
    double directionValue = DirectionAngle.of(
        DirectionOrientation.between(direction, this.directionCalculator.calculate(coordinate, prior)),
        DirectionOrientation.GEOGRAPHIC_NORTH)
        .degree();
    return Coordinate.of(distance, directionValue);
  }

}
