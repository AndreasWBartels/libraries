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

@SuppressWarnings("nls")
public final class Complex extends Number {

  private static final long serialVersionUID = 1L;

  public static final Complex identity = new Complex(0.0, 1.0);

  private final double real;
  private final double imaginary;

  public Complex(final double real, final double imaginary) {
    this.real = real;
    this.imaginary = imaginary;
  }

  public Complex() {
    this(0.0, 0.0);
  }

  public Complex(final double real) {
    this(real, 0.0);
  }

  public Complex(final Complex other) {
    this(other.real(), other.imaginary());
  }

  public Complex(final Angle phi) {
    this(Angle.cos(phi), Angle.sin(phi));
  }

  public Complex(final double real, final Angle phi) {
    this(real * Angle.cos(phi), real * Angle.sin(phi));
  }

  public double real() {
    return this.real;
  }

  public double imaginary() {
    return this.imaginary;
  }

  public Complex conjugate() {
    return new Complex(real(), -imaginary());
  }

  public boolean isReal() {
    return 1.E-12 > MathWrapper.abs(imaginary());
  }

  public double magnitude() {
    return MathWrapper.sqrt(this.real * this.real + this.imaginary * this.imaginary);
  }

  @Override
  public double doubleValue() {
    if (!isReal()) {
      return 0.0;
    }
    return real();
  }

  @Override
  public float floatValue() {
    if (!isReal()) {
      return 0.0f;
    }
    return (float) real();
  }

  @Override
  public long longValue() {
    if (!isReal()) {
      return 0;
    }
    return (long) real();
  }

  @Override
  public int intValue() {
    if (!isReal()) {
      return 0;
    }
    return (int) real();
  }

  public Angle angle() {
    Angle result = new Angle();
    try {
      result = Angle.radian(MathWrapper.atan2(this.imaginary, this.real));
    } catch (final Exception e) {
    }
    return result;
  }

  public Complex add(final Complex value) {
    return new Complex(this.real + value.real(), this.imaginary + value.imaginary());
  }

  public Complex add(final double value) {
    return new Complex(real() + value, imaginary());
  }

  public Complex subtract(final Complex value) {
    return new Complex(this.real - value.real(), this.imaginary - value.imaginary());
  }

  public Complex subtract(final double value) {
    return new Complex(real() - value, imaginary());
  }

  public Complex multiply(final Complex factor) {
    return new Complex(this.real * factor.real() - this.imaginary * factor.imaginary(), this.real
        * factor.imaginary()
        + this.imaginary
        * factor.real());
  }

  public Complex multiply(final double factor) {
    return new Complex(factor * this.real, factor * this.imaginary);
  }

  public Complex divide(final Complex fraction) throws IllegalStateException {
    final double magnitude = fraction.magnitude();
    if (MathWrapper.abs(magnitude) < 1.E-12) {
      throw new IllegalStateException("denominator is zero");
    }
    return new Complex(
        (this.real * fraction.real() + this.imaginary * fraction.imaginary()) / (magnitude * magnitude),
        (this.imaginary * fraction.real() - this.real * fraction.imaginary()) / (magnitude * magnitude));
  }

  public static Complex sin(final Complex value) {
    final double r = MathWrapper.sin(value.real()) * MathWrapper.cosh(value.imaginary());
    final double i = MathWrapper.cos(value.real()) * MathWrapper.sinh(value.imaginary());
    return new Complex(r, i);
  }

  public static Complex cos(final Complex value) {
    final double r = MathWrapper.cos(value.real()) * MathWrapper.cosh(value.imaginary());
    final double i = -MathWrapper.sin(value.real()) * MathWrapper.sinh(value.imaginary());
    return new Complex(r, i);
  }

  public static Complex tan(final Complex value) {
    final double denominator = MathWrapper.cos(2. * value.real()) + MathWrapper.cosh(2 * value.imaginary());
    final double r = MathWrapper.sin(2. * value.real()) / denominator;
    final double i = MathWrapper.sinh(2. * value.imaginary()) / denominator;
    return new Complex(r, i);
  }

  public static Complex pow(final Complex value) {
    final double exponent = MathWrapper.pow(MathWrapper.E, value.real());
    return new Complex(exponent * MathWrapper.cos(value.imaginary()), exponent * MathWrapper.sin(value.imaginary()));
  }

  public static Complex sinh(final Complex value) {
    return new Complex((pow(value).subtract(pow(value.multiply(-1.)))).multiply(0.5));
  }

  public static Complex cosh(final Complex value) {
    final double r = cosh(value.real()) * MathWrapper.cos(value.imaginary());
    final double i = sinh(value.real()) * MathWrapper.sin(value.imaginary());
    return new Complex(r, i);
  }

  public static Complex tanh(final Complex value) {
    return new Complex(sinh(value).divide(cosh(value)));
  }

  public static double tanh(final double value) {
    return new Complex(value, 0.0).real();
  }

  public static Complex atan(final Complex value) {
    final Complex fraction = value.multiply(identity).add(1.).divide(value.multiply(identity).multiply(-1.).add(1.));
    return new Complex(identity.multiply(-0.5).multiply(Complex.ln(fraction)));
  }

  public static double sinh(final double value) {
    return (0.5 * (MathWrapper.exp(value) - MathWrapper.exp(-value)));
  }

  public static double cosh(final double value) {
    return (0.5 * (MathWrapper.exp(value) + MathWrapper.exp(-value)));
  }

  public static Complex exp(final Complex value) {
    final double rr = MathWrapper.exp(value.real()) * MathWrapper.cos(value.imaginary());
    final double ii = MathWrapper.exp(value.real()) * MathWrapper.sin(value.imaginary());
    return new Complex(rr, ii);
  }

  public static Complex ln(final Complex value) {
    final double rr = MathWrapper.log(value.magnitude()) / MathWrapper.log(MathWrapper.E);
    final double ii = value.angle().radian();
    return new Complex(rr, ii);
  }

  public static Complex sqrt(final Complex value) {
    final double magnitude = MathWrapper.sqrt(value.magnitude());
    final double phi = value.angle().radian() / 2.;
    return new Complex(magnitude * MathWrapper.cos(phi), magnitude * MathWrapper.sin(phi));
  }

  public static Complex asin(final Complex value) {
    Complex _value = new Complex(1., 0.0).subtract(value.multiply(value));
    _value = sqrt(_value);
    _value = _value.add(identity.multiply(value));
    _value = identity.multiply(ln(_value)).multiply(-1.);
    return _value;
  }

  public static Complex atanh(final Complex value) {
    Complex _value = value;
    _value = _value.add(1.).divide(_value.subtract(1.)).multiply(-1.);
    _value = ln(_value).multiply(0.5);
    return _value;
  }

  public static double atanh(final double value) {
    return atanh(new Complex(value, 0.0)).real();
  }

  @Override
  public String toString() {
    return new String(real() + (imaginary() < 0.0 ? "-" : "+") + MathWrapper.abs(imaginary()) + "i");
  }

}