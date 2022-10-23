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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program. If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */
// Copyright (c) 2006 by Andreas W. Bartels
package net.anwiba.spatial.coordinate;

import net.anwiba.commons.lang.object.ObjectUtilities;
import net.anwiba.commons.utilities.ArrayUtilities;

import java.util.Arrays;

public class Coordinate implements ICoordinate {

  public static ICoordinate of(final double x, final double y) {
    return new Coordinate(x, y);
  }

  private static final long serialVersionUID = 1L;
  private final boolean isMeasured;
  private final double[] values;
  private int measuredIndex;

  public Coordinate(final double x, final double y) {
    this(new double[] { x, y }, false);

  }

  public Coordinate(final double x, final double y, final double n, final boolean isMeasured) {
    this(new double[] { x, y, n }, isMeasured);

  }

  public Coordinate(final double x, final double y, final double z, final double m) {
    this(new double[] { x, y, z, m }, true);
  }

  public Coordinate(final double[] values, final boolean isMeasured) {
    this.values = ArrayUtilities.copy(values);
    if (this.values.length < 2 || isMeasured && this.values.length < 3) {
      throw new IllegalArgumentException("Coordinate dimension is lower than 2"); //$NON-NLS-1$
    }
    if (isMeasured) {
      this.measuredIndex = this.values.length - 1;
    } else {
      this.measuredIndex = -1;
    }
    this.isMeasured = isMeasured;
  }

  @Override
  public int getDimension() {
    return this.isMeasured ? this.values.length - 1 : this.values.length;
  }

  @Override
  public boolean isMeasured() {
    return this.isMeasured;
  }

  @Override
  public double getValue(final int index) {
    return getOrdinate(index, "Coordinate has no value at index " + index); //$NON-NLS-1$
  }

  @Override
  public double getXValue() {
    return getOrdinate(X, "Coordinate has no x value"); //$NON-NLS-1$
  }

  @Override
  public double getYValue() {
    return getOrdinate(Y, "Coordinate has no y value"); //$NON-NLS-1$
  }

  @Override
  public double getZValue() {
    return getOrdinate(Z, "Coordinate has no z value"); //$NON-NLS-1$
  }

  private double getOrdinate(final int index, final String message) {
    try {
      final int p = this.isMeasured ? this.values.length - 2 : this.values.length - 1;
      if (p >= index) {
        return this.values[index];
      }
      throw new RuntimeException(message);
    } catch (final Exception exception) {
      throw new RuntimeException(message, exception);
    }
  }

  @Override
  public double getMeasuredValue() {
    if (!this.isMeasured) {
      throw new RuntimeException("Coordinate has no measured value"); //$NON-NLS-1$
    }
    return this.values[this.measuredIndex];
  }

  @Override
  public double[] getValues() {
    return ArrayUtilities.copy(this.values);
  }

  @Override
  public ICoordinate add(final ICoordinate other) {
    return calculate(other, (v, o) -> v + o);
  }

  @Override
  public ICoordinate subtract(final ICoordinate other) {
    return calculate(other, (v, o) -> v - o);
  }

  interface IDoubleOperator {

    double operat(double value, double other);

  }

  private ICoordinate calculate(final ICoordinate other, final IDoubleOperator operator) {
    final double[] result = new double[2
        + (getDimension() > 2 && other.getDimension() > 2 ? 1 : 0)
        + (isMeasured() && other.isMeasured() ? 1 : 0)];
    int i;
    for (i = 0; i < 2; i++) {
      result[i] = operator.operat(getValue(i), other.getValue(i));
    }
    if (getDimension() > 2 && other.getDimension() > 2) {
      result[i] = operator.operat(getZValue(), other.getZValue());
    }
    if (isMeasured() && other.isMeasured()) {
      result[i] = operator.operat(getMeasuredValue(), other.getMeasuredValue());
    }
    return new Coordinate(result, isMeasured() && other.isMeasured());
  }

  @Override
  public boolean touch(final ICoordinate other) {
    for (int i = 0; i < Math.min(other.getDimension(), getDimension()); i++) {
      if (other.getValue(i) != this.values[i]) {
        return false;
      }
    }
    return true;
  }

  @Override
  public boolean touch(final double x, final double y) {
    return x == this.values[ICoordinate.X] && y == this.values[ICoordinate.Y];
  }

  @Override
  public boolean equals(final Object object) {
    if (object == null || !(object instanceof ICoordinate)) {
      return false;
    }
    if (object instanceof Coordinate) {
      final Coordinate other = (Coordinate) object;
      return (this.isMeasured == other.isMeasured)
          && ObjectUtilities.equals(this.values, other.values);
    }
    final ICoordinate other = (ICoordinate) object;
    return (this.isMeasured == other.isMeasured())
        && ObjectUtilities.equals(this.values, other.getValues());
  }

  @Override
  public int hashCode() {
    long bits = 0;
    for (final double value : this.values) {
      bits ^= java.lang.Double.doubleToLongBits(value) * 31;
    }
    return (int) (bits ^ (bits >> 32));
  }

  @Override
  public int compareTo(final ICoordinate coordinate) {
    final double[] otherValues = coordinate.getValues();
    for (int i = 0; i < Math.min(otherValues.length, this.values.length); i++) {
      if (this.values[i] < otherValues[i]) {
        return -1;
      }
      if (this.values[i] > otherValues[i]) {
        return 1;
      }
    }
    return 0;
  }

  @Override
  public String toString() {
    return "Coordinate[" + Arrays.toString(getValues()) + "," + isMeasured() + "]";
  }

  @Override
  public ICoordinate withMeasured(final double value) {
    if (getDimension() == 2) {
      return new Coordinate(new double[] { getXValue(), getYValue(), value }, true);
    }
    return new Coordinate(new double[] { getXValue(), getYValue(), getZValue(), value }, true);
  }

  @Override
  public ICoordinate withAltitude(final double value) {
    if (isMeasured()) {
      return new Coordinate(new double[] { getXValue(), getYValue(), value, getMeasuredValue() }, true);
    }
    return new Coordinate(new double[] { getXValue(), getYValue(), value }, false);
  }

}
