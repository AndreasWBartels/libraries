/*
 * #%L
 * anwiba commons
 * %%
 * Copyright (C) 2007 - 2021 Andreas W. Bartels
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
package net.anwiba.commons.utilities.math;

import java.util.Objects;

public enum DirectionOrientation {

  MATH(0, Orientation.COUNTERCLOCKWISE),
  GEOGRAPHIC_NORTH(MathWrapper.PI / 2, Orientation.CLOCKWISE);
  //  GEOGRAPHIC_NORTH(MathWrapper.PI/2, Orientation.CLOCKWISE),
  //  GEOGRAPHIC_EAST(0, Orientation.CLOCKWISE),
  //  GEOGRAPHIC_SOUTH(MathWrapper.PI + MathWrapper.PI/2, Orientation.CLOCKWISE),
  //  GEOGRAPHIC_WEST(MathWrapper.PI, Orientation.CLOCKWISE);

  public enum Orientation {
    CLOCKWISE(-1), COUNTERCLOCKWISE(1);

    private final double factor;

    Orientation(final double factor) {
      this.factor = factor;
    }
  }

  private double deltaInDegreeToMath;
  private Orientation orientation;

  DirectionOrientation(final double deltaInRadianToMath, final Orientation orientation) {
    this.deltaInDegreeToMath = deltaInRadianToMath;
    this.orientation = orientation;
  }

  Angle toMath(final Angle value) {
    return Angle.radian(this.orientation.factor * value.radian() + this.deltaInDegreeToMath);
  }

  Angle fromMath(final Angle value) {
    return Angle.radian(this.orientation.factor * (value.radian() - this.deltaInDegreeToMath));
  }

  public static DirectionAngle convertTo(final DirectionAngle direction, final DirectionOrientation orientation) {
    if (Objects.equals(direction.getOrientation(), orientation)) {
      return direction;
    }
    Angle mathDirectionAngle = direction.getOrientation().toMath(direction.getAngle());
    return DirectionAngle.of(orientation.fromMath(mathDirectionAngle), orientation);
  }

  public static DirectionAngle addClockwise(final DirectionAngle direction, final Angle angle) {
    final DirectionOrientation orientation = direction.getOrientation();
    final Angle mathDirectionAngle = orientation.toMath(direction.getAngle());
    return DirectionAngle.of(orientation.fromMath(mathDirectionAngle.subtract(angle)), orientation);
  }

  public static DirectionAngle subtractClockwise(final DirectionAngle direction, final Angle angle) {
    final DirectionOrientation orientation = direction.getOrientation();
    final Angle mathDirectionAngle = orientation.toMath(direction.getAngle());
    return DirectionAngle.of(orientation.fromMath(mathDirectionAngle.add(angle)), orientation);
  }

  public static Angle between(final DirectionAngle direction, final DirectionAngle other) {
    final double mathDirectionAngle = direction.getOrientation().toMath(direction.getAngle().nonNegativ()).radian();
    final double mathOtherAngle = other.getOrientation().toMath(other.getAngle().nonNegativ()).radian();

    if (mathDirectionAngle < mathDirectionAngle) {
      return Angle.radian(mathDirectionAngle - mathOtherAngle);
    }
    return Angle.radian((Angle.TWO_PI - mathDirectionAngle) + mathOtherAngle);
  }
}
