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

public class ScaleRange implements IScaleRange {

  private static final long serialVersionUID = 1L;
  private final IScaleValue min;
  private final IScaleValue max;
  public static final IScaleValue MIN_VALUE = ScaleValue.NULL_VALUE;
  public static final IScaleValue MAX_VALUE = new ScaleValue(1D);
  public static final IScaleRange NEUTRAL_RANGE = new ScaleRange(MIN_VALUE, MAX_VALUE);

  public ScaleRange(final double min, final double max) {
    this(new ScaleValue(min), new ScaleValue(max));
  }

  public ScaleRange(final IScaleValue min, final IScaleValue max) {
    this.min = getMin(min);
    this.max = getMax(max);
  }

  private IScaleValue getMin(@SuppressWarnings("hiding") final IScaleValue min) {
    if (min.equals(ScaleValue.NULL_VALUE) || min.getFactor() < ScaleRange.MIN_VALUE.getFactor()) {
      return ScaleRange.MIN_VALUE;
    }
    return min;
  }

  private IScaleValue getMax(@SuppressWarnings("hiding") final IScaleValue max) {
    if (max.getFactor() > ScaleRange.MAX_VALUE.getFactor() || max.equals(ScaleValue.NULL_VALUE)) {
      return ScaleRange.MAX_VALUE;
    }
    return max;
  }

  @Override
  public IScaleValue getMin() {
    return this.min;
  }

  @Override
  public IScaleValue getMax() {
    return this.max;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    final int result = prime + ((this.min == null) ? 0 : this.min.hashCode());
    return prime * result + ((this.max == null) ? 0 : this.max.hashCode());
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof IScaleRange)) {
      return false;
    }
    final IScaleRange other = (IScaleRange) obj;
    return this.min.equals(other.getMin()) && this.max.equals(other.getMax());
  }

  @Override
  public boolean contains(final IScaleValue scaleValue) {
    return equals(NEUTRAL_RANGE)
        || this.min.getFactor() <= scaleValue.getFactor() && scaleValue.getFactor() <= this.max.getFactor();
  }
}
