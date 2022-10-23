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

import static net.anwiba.commons.utilities.math.MathWrapper.cos;
import static net.anwiba.commons.utilities.math.MathWrapper.sin;

import net.anwiba.commons.utilities.math.Angle;
import net.anwiba.commons.utilities.math.DirectionAngle;
import net.anwiba.spatial.coordinate.Coordinate;
import net.anwiba.spatial.coordinate.ICoordinate;

public class DefaultFromPolarCoordinateCalculator implements IFromPolarCoordinateCalculator {

  private final DefaultCoordinateDirectionCalculator directionCalculator = new DefaultCoordinateDirectionCalculator();

  @Override
  public ICoordinate calculate(final ICoordinate coordinate, final double radius, final DirectionAngle direction) {
    return new Coordinate(
        (coordinate.getXValue() + radius * sin(direction.getAngle().radian())),
        (coordinate.getYValue() + radius * cos(direction.getAngle().radian())));
  }

  @Override
  public ICoordinate calculate(final ICoordinate prior,
      final ICoordinate coordinate,
      final double radius,
      final Angle angle) {
    DirectionAngle direction = this.directionCalculator.calculate(coordinate, prior);
    return calculate(coordinate, radius, direction.add(angle));
  }

}
