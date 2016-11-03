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
package net.anwiba.commons.utilities.scale;

import java.text.DecimalFormat;

public class ScaleValue implements Comparable<IScaleValue>, IScaleValue {

  private static final long serialVersionUID = 1L;
  public static final ScaleValue NULL_VALUE = new ScaleValue(0D);
  private final double factor;
  private final DecimalFormat decimalFormat = new DecimalFormat("1 : #,##0"); //$NON-NLS-1$

  public ScaleValue(final double factor) {
    this.factor = factor;
  }

  @Override
  public double getFactor() {
    return this.factor;
  }

  @Override
  public int hashCode() {
    if (Double.isInfinite(1D / this.factor)) {
      return Double.valueOf(Double.NaN).hashCode();
    }
    return Double.valueOf(this.factor).hashCode();
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj == this) {
      return true;
    }
    if (!(obj instanceof IScaleValue)) {
      return super.equals(obj);
    }
    final IScaleValue other = (IScaleValue) obj;
    return this.factor == other.getFactor() //
        || (Double.isInfinite(1D / this.factor) && Double.isInfinite(1D / other.getFactor()));
  }

  @Override
  public String toString() {
    if (equals(NULL_VALUE)) {
      return ""; //$NON-NLS-1$
    }
    return this.decimalFormat.format(1D / this.factor);
  }

  @Override
  public int compareTo(final IScaleValue other) {
    if (equals(other)) {
      return 0;
    }
    if (equals(NULL_VALUE)) {
      return -1;
    }
    if (other.equals(NULL_VALUE) || this.factor > other.getFactor()) {
      return 1;
    }
    return -1;
  }
}