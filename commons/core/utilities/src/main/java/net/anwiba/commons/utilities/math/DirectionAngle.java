/*
 * #%L
 * anwiba commons
 * %%
 * Copyright (C) 2007 - 2022 Andreas W. Bartels
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

public class DirectionAngle {

  public static DirectionAngle of(final Angle angle, final DirectionOrientation orientation) {
    return new DirectionAngle(angle, orientation);
  }

  public static DirectionAngle radian(final double value, final DirectionOrientation orientation) {
    return of(Angle.radian(value), orientation);
  }

  public static DirectionAngle degree(final double value, final DirectionOrientation orientation) {
    return of(Angle.degree(value), orientation);
  }

  public static DirectionAngle gon(final double value, final DirectionOrientation orientation) {
    return of(Angle.gon(value), orientation);
  }

  private final Angle angle;
  private final DirectionOrientation orientation;

  private DirectionAngle(final Angle angle, final DirectionOrientation orientation) {
    this.angle = angle.modulo();
    this.orientation = orientation;
  }

  public Angle getAngle() {
    return this.angle;
  }

  public DirectionOrientation getOrientation() {
    return this.orientation;
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.angle, this.orientation);
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    DirectionAngle other = (DirectionAngle) obj;
    return Objects.equals(this.angle, other.angle) && this.orientation == other.orientation;
  }

  public double radian() {
    return this.angle.radian();
  }

  public double degree() {
    return this.angle.degree();
  }

  public double gon() {
    return this.angle.gon();
  }

  public DirectionAngle nonNegativ() {
    return DirectionAngle.of(this.angle.moduloPositive(), this.orientation);
  }

  public DirectionAngle add(final Angle angle) {
    return DirectionAngle.of(angle.add(angle), this.orientation);
  }

  public DirectionAngle subtract(final Angle angle) {
    return DirectionAngle.of(angle.subtract(angle), this.orientation);
  }

  public DirectionAngle toMath() {
    return DirectionOrientation.convertTo(this, DirectionOrientation.MATH);
  }

  public DirectionAngle toGeograpic() {
    return DirectionOrientation.convertTo(this, DirectionOrientation.GEOGRAPHIC_NORTH);
  }

  public DirectionAngle convertTo(final DirectionOrientation orientation) {
    return DirectionOrientation.convertTo(this, orientation);
  }

  public DirectionAngle addClockwise(final Angle angle) {
    return DirectionOrientation.addClockwise(this, angle);
  }

  public DirectionAngle subtractClockwise(final Angle angle) {
    return DirectionOrientation.subtractClockwise(this, angle);
  }

}
