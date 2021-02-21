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

import java.text.Collator;
import java.util.Comparator;
import java.util.Iterator;

public final class AlpaNumericStringComparator implements Comparator<String> {

  private final Comparator<Object> stringComparator = Collator.getInstance();
  private final Comparator<Number> numberComparator = new NumberComparator();

  @SuppressWarnings({ "unchecked", "rawtypes" })
  @Override
  public int compare(final String string, final String other) {
    final Iterator<Object> iterator = new SortItemIterator(string);
    final Iterator<Object> otherIterator = new SortItemIterator(other);
    while (iterator.hasNext() && otherIterator.hasNext()) {
      final Object value = iterator.next();
      final Object otherValue = otherIterator.next();
      if (value instanceof String && otherValue instanceof String) {
        final int compare = this.stringComparator.compare(value, otherValue);
        if (compare == 0) {
          continue;
        }
        return compare;
      }
      if (value instanceof Number && otherValue instanceof Number) {
        final int compare = this.numberComparator.compare((Number) value, (Number) otherValue);
        if (compare == 0) {
          continue;
        }
        return compare;
      }
      if (value.getClass() == otherValue.getClass() && value instanceof Comparable) {
        return ((Comparable) value).compareTo(otherValue);
      }
      if (value instanceof Number) {
        if (otherValue instanceof String) {
          final char c = ((String) otherValue).charAt(0);
          if (Character.isWhitespace(c)) {
            return 1;
          }
        }
        return -1;
      }
      if (otherValue instanceof Number) {
        if (value instanceof String) {
          final char c = ((String) value).charAt(0);
          if (Character.isWhitespace(c)) {
            return -1;
          }
        }
      }
      return 1;
    }
    if (!iterator.hasNext() && !otherIterator.hasNext()) {
      return 0;
    }
    if (!iterator.hasNext()) {
      return -1;
    }
    return 1;
  }
}
