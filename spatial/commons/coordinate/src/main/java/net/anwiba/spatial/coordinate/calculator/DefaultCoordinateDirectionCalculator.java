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

import static net.anwiba.commons.utilities.math.MathWrapper.PI;
import static net.anwiba.commons.utilities.math.MathWrapper.atan;

import net.anwiba.commons.utilities.math.Angle;
import net.anwiba.commons.utilities.math.DirectionAngle;
import net.anwiba.commons.utilities.math.DirectionOrientation;
import net.anwiba.spatial.coordinate.ICoordinate;

public class DefaultCoordinateDirectionCalculator implements ICoordinateDirectionCalculator {

  @Override
  public Angle calculate(final ICoordinate prior, final ICoordinate center, final ICoordinate next) {
    DirectionAngle direction = calculate(center, next);
    DirectionAngle otherDirection = calculate(center, prior);
    return DirectionOrientation.between(direction, otherDirection);
  }

  @Override
  public DirectionAngle calculate(final ICoordinate prior, final ICoordinate next) {
    final double a = next.getXValue() - prior.getXValue();
    final double b = next.getYValue() - prior.getYValue();
    return DirectionAngle.of(Angle.radian(calculate(a, b)), DirectionOrientation.MATH);
  }

  private double calculate(final double a, final double b) {
    if (a == 0) {
      if (b > 0) {
        return 0;
      }
      return PI;
    }
    if (b == 0) {
      if (a > 0) {
        return PI / 2;
      }
      return PI / 2 * 3;
    }
    final double alpha = atan(a / b);
    if (b < 0) {
      return PI + alpha;
    }
    if (a < 0) {
      return PI * 2 + alpha;
    }
    return alpha;
  }

}
