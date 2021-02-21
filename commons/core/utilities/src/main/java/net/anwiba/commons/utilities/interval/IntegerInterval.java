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

import java.util.Arrays;
import java.util.Iterator;

import net.anwiba.commons.lang.object.ObjectUtilities;

public class IntegerInterval implements Iterable<Integer> {

  private final int minValue;
  private final int maxValue;

  public IntegerInterval(final int minValue, final int maxValue) {
    this.minValue = minValue;
    this.maxValue = maxValue;
  }

  public boolean inside(final int value) {
    return value > Math.min(this.minValue, this.maxValue) && Math.max(this.minValue, this.maxValue) > value;
  }

  public boolean interact(final int value) {
    return value == this.minValue || value == this.maxValue || inside(value);
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof IntegerInterval)) {
      return false;
    }
    final IntegerInterval other = (IntegerInterval) obj;
    return Arrays.equals(new int[]{ this.minValue, this.maxValue }, new int[]{ other.minValue, other.maxValue });
  }

  @Override
  public int hashCode() {
    return ObjectUtilities.hashCode(Double.valueOf(this.minValue), Double.valueOf(this.maxValue));
  }

  public int getMaxValue() {
    return this.maxValue;
  }

  public int getMinValue() {
    return this.minValue;
  }

  @Override
  public Iterator<Integer> iterator() {
    return new IntegerIterator(this.minValue, this.maxValue);
  }
}
