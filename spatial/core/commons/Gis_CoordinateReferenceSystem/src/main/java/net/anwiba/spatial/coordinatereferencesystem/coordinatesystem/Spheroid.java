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

package net.anwiba.spatial.coordinatereferencesystem.coordinatesystem;

import static net.anwiba.commons.utilities.math.MathWrapper.*;

import java.io.Serializable;

import net.anwiba.commons.ensure.Ensure;
import net.anwiba.commons.lang.object.ObjectUtilities;
import net.anwiba.spatial.coordinatereferencesystem.Authority;

public class Spheroid implements Serializable {

  private static final double THREE_HALF = 3d / 2d;

  private static final long serialVersionUID = 1808740615014424866L;

  private static final String NAME_SPHERE = "SPHERE"; //$NON-NLS-1$
  private static final String NAME_WGS_84 = "WGS 84"; //$NON-NLS-1$
  private static final String NAME_GRS_80 = "GRS 1980"; //$NON-NLS-1$
  private static final String NAME_BESSEL_1841 = "Bessel 1841"; //$NON-NLS-1$

  public static final Spheroid SPHERE = new Spheroid(NAME_SPHERE, 6378137.0, Double.POSITIVE_INFINITY);
  public static final Spheroid GRS_80 = new Spheroid(
      NAME_GRS_80,
      6378137.0,
      298.257222101,
      new Authority("EPSG", 7019)); //$NON-NLS-1$
  public static final Spheroid WGS_84 = new Spheroid(
      NAME_WGS_84,
      6378137.0,
      298.257223563,
      new Authority("EPSG", 7030)); //$NON-NLS-1$
  public static final Spheroid BESSEL_1841 = new Spheroid(
      NAME_BESSEL_1841,
      6377397.155,
      299.1528128,
      new Authority("EPSG", 7004)); //$NON-NLS-1$

  final double PI_2 = PI / 2d;

  private final String name;
  private final double semiMajorAxis;
  private final double semiMinorAxis;
  private final double inverseFlattening;
  private final double eccentricity;
  private final double squareEccentricity;
  private Authority authority;
  private double halfEccentricity;
  private double oneMinusSquareEccentricity;

  public Spheroid(final String name, final double semiMajorAxis, final double inverseFlattening) {
    this(
        name,
        semiMajorAxis,
        inverseFlattening == 0 ? semiMajorAxis : semiMajorAxis - semiMajorAxis / inverseFlattening,
        inverseFlattening,
        null);
  }

  public Spheroid(
      final String name,
      final double semiMajorAxis,
      final double inverseFlattening,
      final Authority authority) {
    this(
        name,
        semiMajorAxis,
        inverseFlattening == 0 ? semiMajorAxis : semiMajorAxis - semiMajorAxis / inverseFlattening,
        inverseFlattening,
        authority);
  }

  public Spheroid(
      final String name,
      final double semiMajorAxis,
      final double semiMinorAxis,
      final double inverseFlattening,
      final Authority authority) {
    Ensure.ensureArgumentNotNull(name);
    Ensure.ensureArgumentNotZero(semiMajorAxis);
    this.name = name;
    this.semiMajorAxis = semiMajorAxis;
    this.inverseFlattening = inverseFlattening;
    if (inverseFlattening == 0) {
      this.semiMinorAxis = this.semiMajorAxis;
      this.eccentricity = sqrt(pow(this.semiMajorAxis, 2d) - pow(this.semiMinorAxis, 2d)) / semiMajorAxis;
      this.squareEccentricity = this.eccentricity * this.eccentricity;
      this.oneMinusSquareEccentricity = 1d - this.squareEccentricity;
      this.halfEccentricity = this.eccentricity / 2d;
      return;
    }
    this.squareEccentricity = (2.0 - 1.0 / inverseFlattening) / inverseFlattening;
    this.oneMinusSquareEccentricity = 1d - this.squareEccentricity;
    this.eccentricity = sqrt(this.squareEccentricity);
    this.halfEccentricity = this.eccentricity / 2d;
    this.semiMinorAxis = semiMinorAxis;
    this.authority = authority;
  }

  public String getName() {
    return this.name;
  }

  public double getSemiMajorAxis() {
    return this.semiMajorAxis;
  }

  public double getInverseFlattening() {
    return this.inverseFlattening;
  }

  public double getSquareEccentricity() {
    return this.squareEccentricity;
  }

  public double getOneMinusSquareEccentricity() {
    return this.oneMinusSquareEccentricity;
  }

  public double getEccentricity() {
    return this.eccentricity;
  }

  public double getSemiMinorAxis() {
    return this.semiMinorAxis;
  }

  public final double isometricLatitude(final double latitude) {
    final double sin = sin(latitude);
    final double _latitude = this.eccentricity * sin;
    return log(tan((this.PI_2 + latitude) / 2d) * pow((1d - _latitude) / (1d + _latitude), this.eccentricity / 2d));
  }

  public double getRadiusOfCrossCurvature(final double latitude) {
    final double W = sqrt(1d - this.squareEccentricity * pow(sin(latitude), 2d));
    return this.semiMajorAxis / W;
  }

  public double getRadiusOfPolCurvature() {
    return pow(this.semiMajorAxis, 2d) / this.semiMinorAxis;
  }

  public double getRadiusOfMeridianCurvature(final double latitude) {
    final double W = 1d - this.squareEccentricity * pow(sin(latitude), 2d);
    return this.semiMajorAxis / pow(W, THREE_HALF) * this.oneMinusSquareEccentricity;
  }

  public double[] createMeridianArcCoefficients() {
    final double e2 = this.squareEccentricity;
    final double e4 = e2 * e2;
    final double e6 = e4 * e2;
    final double e8 = e4 * e4;
    final double[] coefficents = new double[5];
    coefficents[0] = 1d - e2 * 1d / 4d - e4 * 3d / 64d - e6 * 5d / 256d - e8 * 175d / 16384d;
    coefficents[1] = -e2 * 3d / 8d - e4 * 3d / 32d - e6 * 45d / 1024d - e8 * 105d / 4096d;
    coefficents[2] = e4 * 15d / 256d + e6 * 45d / 1024d + e8 * 525d / 16384d;
    coefficents[3] = -e6 * 35d / 3072d - e8 * 175d / 12288d;
    coefficents[4] = e8 * 315d / 131072d;
    return coefficents;
  }

  public double curvilinearAbscissa(final double latitude) {
    final double[] coefficients = createMeridianArcCoefficients();
    return coefficients[0] * latitude
        + coefficients[1] * sin(2 * latitude)
        + coefficients[2] * sin(4 * latitude)
        + coefficients[3] * sin(6 * latitude)
        + coefficients[4] * sin(8 * latitude);
  }

  public double[] createMeridianArcLengthSeriesExpansionCoefficients(final int numberOfIterations) {
    final int maximum = numberOfIterations < 1 ? 1 : numberOfIterations > 8 ? 8 : numberOfIterations;
    final double[] coefficients = new double[maximum];
    double c = 1.0;
    for (int n = 1; n <= maximum; n++) {
      final double n2 = 2.0 * n;
      c *= (n2 - 1.0) * (n2 - 3.0) / n2 / n2 * this.squareEccentricity;
      for (int m = 0; m < n; m++) {
        coefficients[m] += c;
      }
    }
    return coefficients;
  }

  @SuppressWarnings("nls")
  public double getEquatorToPointFromMeridianArc(final double[] meridianArcLengthCoefficients, final double s)
      throws ArithmeticException {
    final int numberOfIterations = 10;
    final double firstExpansion = getFirstCoefficientOfSeriesExpansion(meridianArcLengthCoefficients);
    final double beta0 = s / this.semiMajorAxis / firstExpansion;
    double beta = beta0;
    double betaold = 1.E30;
    int i = 0;
    while (++i < numberOfIterations && abs(beta - betaold) > 1.E-15) {
      betaold = beta;
      beta = beta0
          - getSecondCoefficientOfSeriesExpansion(meridianArcLengthCoefficients, beta)
              / 2.
              / firstExpansion
              * sin(2. * beta);
    }
    if (i == numberOfIterations) {
      throw new ArithmeticException("The equator to point from meridian arc method diverges");
    }
    return atan(tan(beta) / (1. - (1.0 - sqrt(this.oneMinusSquareEccentricity))));
  }

  public double getMeridianArcFromEquatorToPoint(final double[] meridianArcLengthCoefficients, final double phi) {
    final double beta = atan((1.0 - (1.0 - sqrt(this.oneMinusSquareEccentricity))) * tan(phi));
    final double first = getFirstCoefficientOfSeriesExpansion(meridianArcLengthCoefficients);
    final double second = getSecondCoefficientOfSeriesExpansion(meridianArcLengthCoefficients, beta);
    return this.semiMajorAxis * beta * first + this.semiMajorAxis / 2.0 * sin(2.0 * beta) * second;
  }

  private double getFirstCoefficientOfSeriesExpansion(final double[] meridianArcLengthCoefficients) {
    return 1.0 + meridianArcLengthCoefficients[0];
  }

  private double getSecondCoefficientOfSeriesExpansion(
      final double[] meridianArcLengthCoefficients,
      final double radius) {
    final double cos2 = cos(radius) * cos(radius);
    double result = meridianArcLengthCoefficients[0];
    double k = 1.0;
    for (int n = 1; n < meridianArcLengthCoefficients.length; n++) {
      k *= (2.0 * n) / (2.0 * n + 1.0) * cos2;
      result += meridianArcLengthCoefficients[n] * k;
    }
    return result;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    final int result = ObjectUtilities.hashCode(1, prime, this.semiMajorAxis);
    return ObjectUtilities.hashCode(result, prime, this.inverseFlattening);
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj == null || !(obj instanceof Spheroid)) {
      return false;
    }
    final Spheroid other = (Spheroid) obj;
    return this.semiMajorAxis == other.semiMajorAxis //
        && this.inverseFlattening == other.inverseFlattening;
  }

  public double latitude(final double isometricLatitude) {
    return latitude(isometricLatitude, 1E-11);
  }

  public final double latitude(final double isometricLatitude, final double epsilon) {
    final double exp_isometricLatitude = exp(isometricLatitude);
    double _latitude = 2 * atan(exp_isometricLatitude) - this.PI_2;
    double latitude = 1000;
    while (abs(latitude - _latitude) >= epsilon) {
      _latitude = latitude;
      final double esinlat = this.eccentricity * sin(_latitude);
      latitude = 2 * atan(pow((1 + esinlat) / (1 - esinlat), this.halfEccentricity) * exp_isometricLatitude)
          - this.PI_2;
    }
    return latitude;
  }

  public Authority getAuthority() {
    return this.authority;
  }
}
