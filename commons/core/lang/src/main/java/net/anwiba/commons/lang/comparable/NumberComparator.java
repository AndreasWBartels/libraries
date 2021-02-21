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
package net.anwiba.commons.lang.comparable;

import java.util.Comparator;

public class NumberComparator implements Comparator<Number> {

  @Override
  public int compare(final Number o1, final Number o2) {
    return compareImpl(o1, o2);
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  public static int compareImpl(final Number value, final Number other) {
    if (value == other) {
      return 0;
    }
    if (value == null) {
      return -1;
    }
    if (other == null) {
      return 1;
    }
    if (value.getClass() == other.getClass()) {
      return ((Comparable) value).compareTo(other);
    }
    return Double.compare(value.doubleValue(), other.doubleValue());
  }
}
