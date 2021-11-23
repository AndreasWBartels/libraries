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
package net.anwiba.commons.utilities.interval;

import java.io.Serializable;
import java.util.Arrays;

import net.anwiba.commons.lang.object.ObjectUtilities;

public class DoubleInterval implements Serializable {

  private static final long serialVersionUID = 1L;
  private final double minValue;
  private final double maxValue;
  private final double factor;

  public DoubleInterval(final double minValue, final double maxValue) {
    this.minValue = Math.min(minValue, maxValue);
    this.maxValue = Math.max(minValue, maxValue);
    this.factor = 1 / (this.maxValue - this.minValue);
  }

  public boolean inside(final double value) {
    if (Double.isNaN(value)) {
      return false;
    }
    return value > this.minValue && this.maxValue > value;
  }

  public boolean interact(final double value) {
    if (Double.isNaN(value)) {
      return false;
    }
    return value == this.minValue || value == this.maxValue || inside(value);
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof DoubleInterval)) {
      return false;
    }
    final DoubleInterval other = (DoubleInterval) obj;
    return Arrays
        .equals(new double[] { this.minValue, this.maxValue }, new double[] { other.minValue, other.maxValue });
  }

  @Override
  public int hashCode() {
    return ObjectUtilities.hashCode(Double.valueOf(this.minValue), Double.valueOf(this.maxValue));
  }

  public double fraction(final double value) {
    final double d = this.factor * (value - this.minValue);
    return d;
  }

  public double getMaximum() {
    return this.maxValue;
  }

  public double getMinimum() {
    return this.minValue;
  }

  @Override
  public String toString() {
    return Arrays.toString(new double[] { this.minValue, this.maxValue });
  }

  public double distance() {
    return Math.abs(getMaximum() - getMinimum());
  }
}
