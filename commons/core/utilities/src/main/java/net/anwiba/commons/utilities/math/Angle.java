/*
 * #%L
 * anwiba commons core
 * %%
 * Copyright (C) 2007 - 2016 Andreas Bartels
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

public final class Angle {

  public static final double TWO_PI = 2. * MathWrapper.PI;
  public static final double RHO_DEGREE = 180. / MathWrapper.PI;
  public static final double RHO_GON = 200. / MathWrapper.PI;
  public static final double RHO_SEMI_CIRCLE = 1. / MathWrapper.PI;

  private double value;

  public Angle() {
    this.value = 0.;
  }

  private Angle(final double value) {
    this.value = value;
  }

  public static Angle radian(final double x) {
    return new Angle(x % TWO_PI);
  }

  public static Angle gon(final double value) {
    return new Angle((value / RHO_GON) % TWO_PI);
  }

  public static Angle degree(final double value) {
    return new Angle((value / RHO_DEGREE) % TWO_PI);
  }

  public static Angle semiCircle(final double value) {
    return new Angle((value / RHO_SEMI_CIRCLE) % TWO_PI);
  }

  public static Angle degree(final int degree, final int minute, final double second) {
    return degree(degree + minute / 60. + second / 3600.0);
  }

  public double radian() {
    return this.value;
  }

  public double degree() {
    return radian() * RHO_DEGREE;
  }

  public double gon() {
    return radian() * RHO_GON;
  }

  public double arcSeconds() {
    return degree() * 3600.0;
  }

  public double semiCircle() {
    return radian() * RHO_SEMI_CIRCLE;
  }

  public Angle add(final Angle angle) {
    this.value += angle.radian();
    this.value %= TWO_PI;
    return this;
  }

  public Angle subtract(final Angle angle) {
    this.value -= angle.radian();
    this.value %= TWO_PI;
    return this;
  }

  public static double sin(final Angle value) {
    return MathWrapper.sin(value.radian());
  }

  public static double cos(final Angle value) {
    return MathWrapper.cos(value.radian());
  }
}
