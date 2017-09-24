/*
 * #%L
 *
 * %%
 * Copyright (C) 2007 - 2017 Andreas W. Bartels (bartels@anwiba.de)
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

public final class ToStringComparator<T> implements Comparator<T> {

  @Override
  public int compare(final T value, final T other) {
    if (value == other) {
      return 0;
    }
    if (value == null) {
      return -1;
    }
    if (other == null) {
      return 1;
    }
    final String valueString = value.toString();
    final String otherString = other.toString();
    return valueString.compareTo(otherString);
  }
}
