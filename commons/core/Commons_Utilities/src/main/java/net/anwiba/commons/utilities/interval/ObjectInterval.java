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

import net.anwiba.commons.lang.object.ObjectUtilities;

public class ObjectInterval<T extends Comparable<T>> implements IObjectInterval<T> {

  private final T startValue;
  private final T endValue;

  public ObjectInterval(final T startValue, final T endValue) {
    this.startValue = startValue;
    this.endValue = endValue;
  }

  @Override
  public boolean inside(final T value) {
    return value.compareTo(this.startValue) > 0 && 0 > value.compareTo(this.endValue);
  }

  @Override
  public boolean interact(final T value) {
    return value.compareTo(this.startValue) == 0 || 0 == value.compareTo(this.endValue) || inside(value);
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof ObjectInterval)) {
      return false;
    }
    final ObjectInterval<?> other = (ObjectInterval<?>) obj;
    return ObjectUtilities.equals(new Object[] { this.startValue, this.endValue }, new Object[] { other.startValue,
        other.endValue });
  }

  @Override
  public int hashCode() {
    return ObjectUtilities.hashCode(this.startValue, this.endValue);
  }
}
