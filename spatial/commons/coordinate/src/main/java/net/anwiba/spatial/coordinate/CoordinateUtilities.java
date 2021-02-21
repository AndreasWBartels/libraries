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
// Copyright (c) 2006 by Andreas W. Bartels
package net.anwiba.spatial.coordinate;

import static net.anwiba.commons.utilities.math.MathWrapper.PI;
import static net.anwiba.commons.utilities.math.MathWrapper.abs;
import static net.anwiba.commons.utilities.math.MathWrapper.atan;
import static net.anwiba.commons.utilities.math.MathWrapper.cos;
import static net.anwiba.commons.utilities.math.MathWrapper.log10;
import static net.anwiba.commons.utilities.math.MathWrapper.max;
import static net.anwiba.commons.utilities.math.MathWrapper.min;
import static net.anwiba.commons.utilities.math.MathWrapper.pow;
import static net.anwiba.commons.utilities.math.MathWrapper.sin;
import static net.anwiba.commons.utilities.math.MathWrapper.sqrt;

import net.anwiba.spatial.coordinate.calculator.DefaultCoordinateDistanceCalculator;
import net.anwiba.spatial.coordinate.calculator.ICoordinateDistanceCalculator;
import net.anwiba.spatial.coordinate.calculator.RobustDeterminantCalculator;
import net.anwiba.spatial.coordinate.calculator.SmallPointCalculator;

public class CoordinateUtilities {

  public static ICoordinate[] getCoordinates(final ICoordinateSequence sequence) {
    final ICoordinate[] coordinates = new ICoordinate[sequence.getNumberOfCoordinates()];
    for (int i = 0; i < coordinates.length; i++) {
      coordinates[i] = sequence.getCoordinateN(i);
    }
    return coordinates;
  }

  public static double calculateArea(final ICoordinateSequence sequence) {
    if (sequence == null || sequence.getNumberOfCoordinates() < 3) {
      return 0;
    }
    double sum = 0;
    for (int i = 0; i < sequence.getNumberOfCoordinates(); i++) {
      final ICoordinate prior = getPrior(sequence, i);
      final ICoordinate coordinate = sequence.getCoordinateN(i);
      final ICoordinate next = getNext(sequence, i);
      sum += coordinate.getXValue() * (prior.getYValue() - next.getYValue());
    }
    return sum / 2;
  }

  private static ICoordinate getPrior(final ICoordinateSequence sequence, final int i) {
    if (i == 0) {
      return sequence.getCoordinateN(sequence.getNumberOfCoordinates() - 1);
    }
    return sequence.getCoordinateN(i - 1);
  }

  private static ICoordinate getNext(final ICoordinateSequence sequence, final int i) {
    if (i + 1 == sequence.getNumberOfCoordinates()) {
      return sequence.getCoordinateN(0);
    }
    return sequence.getCoordinateN(i + 1);
  }

  public static double calculateLength(final ICoordinateSequence sequence) {
    double sum = 0;
    ICoordinate prior = null;
    for (final ICoordinate coordinate : sequence.getCoordinates()) {
      if (prior != null) {
        sum += calculateDistance(prior, coordinate);
      }
      prior = coordinate;
    }
    return sum;
  }

  public static double calculateDistance(final ICoordinate c0, final ICoordinate c1) {
    final double sum = pow(c1.getXValue() - c0.getXValue(), 2) + pow(c1.getYValue() - c0.getYValue(), 2);
    return sqrt(sum);
  }

  public static ICoordinate calculateSmallPoint(
      final ICoordinate c0,
      final ICoordinate c1,
      final double s,
      final double r) {
    return new SmallPointCalculator(c0, c1).calculate(s, r);
  }

  final static ICoordinateDistanceCalculator coordinateDistanceCalculator = new DefaultCoordinateDistanceCalculator();

  public static ICoordinate calculateSmallPoint(final ICoordinate c0, final ICoordinate c1, final double s)
      throws CoordinateCalculationException {
    final double x0 = c0.getXValue();
    final double y0 = c0.getYValue();
    if (c1.touch(x0, y0)) {
      throw new CoordinateCalculationException("base points are equal"); //$NON-NLS-1$
    }
    final double x1 = c1.getXValue();
    final double y1 = c1.getYValue();
    final double d = coordinateDistanceCalculator.calculateDistance(x0, y0, x1, y1);
    final double xs = x0 + (x1 - x0) / d * s;
    final double ys = y0 + (y1 - y0) / d * s;
    if (c0.getDimension() > 2 && c1.getDimension() > 2) {
      final double z0 = c0.getZValue();
      final double z1 = c1.getZValue();
      final double zs = z0 + (z1 - z0) / d * s;
      return new Coordinate(xs, ys, zs, false);
    }
    return new Coordinate(xs, ys);
  }

  public static ICoordinate calculateBasePoint(
      final ICoordinate c0,
      final ICoordinate c1,
      final ICoordinate coordinate) {
    final double x0 = c0.getXValue();
    final double y0 = c0.getYValue();
    if (c1.touch(x0, y0)) {
      return new Coordinate(x0, y0);
    }
    final double x1 = c1.getXValue();
    final double y1 = c1.getYValue();
    final double x2 = coordinate.getXValue();
    final double y2 = coordinate.getYValue();
    if (x0 == x1) {
      return new Coordinate(x0, y2);
    }
    if (y0 == y1) {
      return new Coordinate(x2, y0);
    }
    final double m = (y1 - y0) / (x1 - x0);
    final double xs = (m * (y2 - y0) + x2 - x0) / (m * m + 1) + x0;
    final double ys = (xs - x0) * m + y0;
    return new Coordinate(xs, ys);
  }

  public static ICoordinate calculateIntersection(
      final ICoordinate c0,
      final ICoordinate c1,
      final ICoordinate c2,
      final ICoordinate c3)
      throws CoordinateCalculationException {
    final double x0 = c0.getXValue();
    final double y0 = c0.getYValue();
    if (c1.touch(x0, y0)) {
      throw new CoordinateCalculationException("base points 0 and 1 are equal"); //$NON-NLS-1$
    }
    final double x1 = c1.getXValue();
    final double y1 = c1.getYValue();
    final double x2 = c2.getXValue();
    final double y2 = c2.getYValue();
    if (c3.touch(x2, y2)) {
      throw new CoordinateCalculationException("base points 2 and 3 are equal"); //$NON-NLS-1$
    }
    final double x3 = c3.getXValue();
    final double y3 = c3.getYValue();
    if ((x0 == x1 && x2 == x3) || (y0 == y1 && y2 == y3)) {
      throw new CoordinateCalculationException("lines are parallel"); //$NON-NLS-1$
    }
    if (x0 == x1) {
      final double m1 = (y3 - y2) / (x3 - x2);
      return new Coordinate(x0, y2 + m1 * (x0 - x2));
    }
    if (x2 == x3) {
      final double m0 = (y1 - y0) / (x1 - x0);
      return new Coordinate(x2, y0 + m0 * (x3 - x0));
    }
    final double m0 = (y1 - y0) / (x1 - x0);
    final double m1 = (y3 - y2) / (x3 - x2);
    final double d = (y0 - y2 - m0 * (x0 - x2)) / (m1 - m0);
    return new Coordinate(d + x2, y2 + m1 * d);
  }

  public static ICoordinate getAvarageCoordinate(final ICoordinate... coordinates) {
    if (coordinates.length == 1) {
      return coordinates[0];
    }
    ICoordinate centroid = null;
    for (int i = 0; i < coordinates.length; i++) {
      final ICoordinate coordinate = coordinates[i];
      if (centroid == null) {
        centroid = coordinate;
        continue;
      }
      centroid = CoordinateUtilities.getAvarageCoordinate(centroid, coordinate, i + 1);
    }
    return centroid;
  }

  public static ICoordinate getAvarageCoordinate(final ICoordinate c0, final ICoordinate c1, final int n) {
    if (c0.isMeasured() && c1.isMeasured()) {
      final double[] values = new double[min(c0.getDimension(), c1.getDimension()) + 1];
      for (int i = 0; i < values.length - 1; i++) {
        values[i] = getAvarageValue(c0.getValue(i), c1.getValue(i), n);
      }
      values[values.length - 1] = getAvarageValue(c0.getMeasuredValue(), c1.getMeasuredValue(), n);
      return new Coordinate(values, true);
    }
    final double[] values = new double[min(c0.getDimension(), c1.getDimension())];
    for (int i = 0; i < values.length; i++) {
      values[i] = getAvarageValue(c0.getValue(i), c1.getValue(i), n);
    }
    return new Coordinate(values, false);
  }

  public static double getAvarageValue(final double m1, final double d, final int n) {
    return m1 + ((d - m1) / n);
  }

  public static boolean isInterior(final ICoordinate c0, final ICoordinate c1, final ICoordinate coordinate) {
    final double xmin = min(c0.getXValue(), c1.getXValue());
    final double xmax = max(c0.getXValue(), c1.getXValue());
    final double xc = coordinate.getXValue();
    if (!(xmin <= xc && xc <= xmax)) {
      return false;
    }
    final double ymin = min(c0.getYValue(), c1.getYValue());
    final double ymax = max(c0.getYValue(), c1.getYValue());
    final double yc = coordinate.getYValue();
    if (!(ymin <= yc && yc <= ymax)) {
      return false;
    }
    if (xmin == xmax) {
      return true;
    }
    if (ymin == ymax) {
      return true;
    }
    final double m1 = (c1.getYValue() - c0.getYValue()) / (c1.getXValue() - c0.getXValue());
    final double m2 = (yc - c0.getYValue()) / (xc - c0.getXValue());
    final double abs = abs(m1 - m2);
    return abs < 0.00000001;
  }

  public static boolean isPointInRing(final ICoordinate coordinate, final ICoordinateSequence ring) {
    final double x = coordinate.getXValue();
    final double y = coordinate.getYValue();
    boolean isInside = false;
    ICoordinate previous = null;
    for (final ICoordinate next : ring.getCoordinates()) {
      if (previous == null) {
        previous = next;
        continue;
      }
      final double yp = previous.getYValue();
      final double yn = next.getYValue();
      if (yp > y && yn <= y || yn > y && yp <= y) {
        final double y0 = yp - y;
        final double y1 = yn - y;
        if (RobustDeterminantCalculator.signOfDet(previous.getXValue() - x, y0, next.getXValue() - x, y1)
            / (y1 - y0) > 0.0) {
          if (y == yn) {
            continue;
          }
          isInside = !isInside;
        }
      }
      previous = next;
    }
    return isInside;
  }

  public static boolean interact(
      final ICoordinate coordinate,
      final ICoordinate otherCoordinate,
      final double tolerance) {
    final double abs = abs(calculateDistance(coordinate, otherCoordinate));
    if (Double.isNaN(tolerance) || tolerance == 0) {
      final double cLog10 = max(log10(abs(coordinate.getXValue())), log10(abs(coordinate.getYValue())));
      final double log10 = log10(abs);
      return cLog10 - log10 > 12;
    }
    return tolerance > abs || (tolerance == 0 && abs == 0);
  }

  public static boolean isCrossing(
      final ICoordinate c0,
      final ICoordinate c1,
      final ICoordinate c2,
      final ICoordinate c3) {
    try {
      final ICoordinate crossPoint = calculateIntersection(c0, c1, c2, c3);
      if (isInsideRectangle(c0, c1, crossPoint) && isInsideRectangle(c2, c3, crossPoint)) {
        return true;
      }
      if ((c0.touch(crossPoint) || c1.touch(crossPoint)) && (c2.touch(crossPoint) || c3.touch(crossPoint))) {
        return true;
      }
      return false;
    } catch (final CoordinateCalculationException exception) {
      return false;
    }
  }

  public static boolean isInsideRectangle(final ICoordinate c0, final ICoordinate c1, final ICoordinate coordinate) {
    final double xmin = min(c0.getXValue(), c1.getXValue());
    final double xmax = max(c0.getXValue(), c1.getXValue());
    final double xc = coordinate.getXValue();
    if (!(xmin <= xc && xc <= xmax)) {
      return false;
    }
    final double ymin = min(c0.getYValue(), c1.getYValue());
    final double ymax = max(c0.getYValue(), c1.getYValue());
    final double yc = coordinate.getYValue();
    if (!(ymin <= yc && yc <= ymax)) {
      return false;
    }
    return true;
  }

  public static boolean isBetween(
      final ICoordinate c0,
      final ICoordinate c1,
      final ICoordinate coordinate,
      final double tolerance) {
    if (!EnvelopeUtilities.createEnvelope(EnvelopeUtilities.createEnvelope(c0, c1), tolerance).interact(coordinate)) {
      return false;
    }
    if (interact(coordinate, c0, tolerance)) {
      return true;
    }
    if (interact(coordinate, c1, tolerance)) {
      return true;
    }
    final ICoordinate base = calculateBasePoint(c0, c1, coordinate);
    if (interact(coordinate, base, tolerance)) {
      if (isInterior(c0, c1, base)) {
        return true;
      }
    }
    return false;
  }

  public static ICoordinate calculatePolarCoordinate(
      final ICoordinate coordinate,
      final double angel,
      final double distance) {
    return new Coordinate(
        (coordinate.getXValue() + distance * sin(angel)),
        (coordinate.getYValue() + distance * cos(angel)));
  }

  public static double calculateAngle(final ICoordinate c0, final ICoordinate c1) {
    final double a = c1.getXValue() - c0.getXValue();
    final double b = c1.getYValue() - c0.getYValue();
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

  public static ICoordinate getMinimum(final ICoordinate coordinate, final ICoordinate other) {
    if (coordinate == null) {
      return other;
    }
    if (other == null) {
      return coordinate;
    }
    final double x = min(coordinate.getXValue(), other.getXValue());
    final double y = min(coordinate.getYValue(), other.getYValue());
    if (min(coordinate.getDimension(), other.getDimension()) == 2) {
      if (coordinate.isMeasured() && other.isMeasured()) {
        return new Coordinate(x, y, min(coordinate.getMeasuredValue(), other.getMeasuredValue()), true);
      }
      return new Coordinate(x, y);
    }
    if (coordinate.isMeasured() && other.isMeasured()) {
      return new Coordinate(
          x,
          y,
          min(coordinate.getZValue(), other.getZValue()),
          min(coordinate.getMeasuredValue(), other.getMeasuredValue()));
    }
    return new Coordinate(x, y, min(coordinate.getZValue(), other.getZValue()), false);
  }

  public static ICoordinate getMaximum(final ICoordinate coordinate, final ICoordinate other) {
    if (coordinate == null) {
      return other;
    }
    if (other == null) {
      return coordinate;
    }
    final double x = max(coordinate.getXValue(), other.getXValue());
    final double y = max(coordinate.getYValue(), other.getYValue());
    if (min(coordinate.getDimension(), other.getDimension()) == 2) {
      if (coordinate.isMeasured() && other.isMeasured()) {
        return new Coordinate(x, y, max(coordinate.getMeasuredValue(), other.getMeasuredValue()), true);
      }
      return new Coordinate(x, y);
    }
    if (coordinate.isMeasured() && other.isMeasured()) {
      return new Coordinate(
          x,
          y,
          max(coordinate.getZValue(), other.getZValue()),
          max(coordinate.getMeasuredValue(), other.getMeasuredValue()));
    }
    return new Coordinate(x, y, max(coordinate.getZValue(), other.getZValue()), false);
  }

  public static ICoordinate createAdapted(
      final ICoordinate coordinate,
      final int coordinateValueIndex,
      final double value) {
    final double[] values = coordinate.getValues();
    values[coordinateValueIndex] = value;
    return new Coordinate(values, coordinate.isMeasured());
  }

  public static ICoordinate getMaximum(final ICoordinate... coordinates) {
    ICoordinate result = null;
    for (final ICoordinate coordinate : coordinates) {
      if (result == null) {
        result = coordinate;
        continue;
      }
      result = CoordinateUtilities.getMaximum(result, coordinate);
    }
    return result;
  }

  public static ICoordinate getMinimum(final ICoordinate... coordinates) {
    ICoordinate result = null;
    for (final ICoordinate coordinate : coordinates) {
      if (result == null) {
        result = coordinate;
        continue;
      }
      result = CoordinateUtilities.getMinimum(result, coordinate);
    }
    return result;
  }
}
