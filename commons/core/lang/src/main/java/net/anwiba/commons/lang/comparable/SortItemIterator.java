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

import java.text.DecimalFormatSymbols;
import java.util.Iterator;
import java.util.Locale;
import java.util.NoSuchElementException;

import net.anwiba.commons.lang.counter.IIntCounter;
import net.anwiba.commons.lang.counter.IntCounter;

public class SortItemIterator implements Iterator<Object> {

  private final char[] array;
  private final IIntCounter counter = new IntCounter(-1);
  private Object item = null;
  private final Locale locale = Locale.getDefault();
  private final DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(this.locale);

  public SortItemIterator(final String string) {
    this.array = string.toCharArray();
  }

  @Override
  public Object next() {
    if (this.item == null && !hasNext()) {
      throw new NoSuchElementException();
    }
    try {
      return this.item;
    } finally {
      this.item = null;
    }
  }

  @Override
  public boolean hasNext() {
    if (this.item != null) {
      return true;
    }
    if (this.counter.value() + 1 < this.array.length) {
      final char c = this.array[this.counter.next()];
      if (isDigit(c)) {
        this.item = getNumber(this.array);
        return true;
      }
      if (isDecimalSeparator(c) || isSign(c)) {
        if (this.counter.value() + 1 < this.array.length
            && isDigit(this.array[this.counter.value() + 1])
            && (this.counter.value() == 0 || isWhitespace(this.array[this.counter.value() - 1]))) {
          this.item = getNumber(this.array);
          return true;
        }
      }
      this.item = String.valueOf(c);
      return true;
    }
    return false;
  }

  private Object getNumber(final char[] chars) {
    final StringBuilder builder = new StringBuilder();
    append(builder, chars[this.counter.value()]);
    int index = this.counter.value() + 1;
    boolean isDouble = false;
    while (index < chars.length && (isDigit(chars[index]) || isSeperator(chars[index]))) {
      isDouble = isDouble || isDecimalSeparator(chars[index]);
      append(builder, chars[index]);
      index = this.counter.next() + 1;
    }
    return isDouble ? (Object) Double.valueOf(builder.toString()) : (Object) Long.valueOf(builder.toString());
  }

  private void append(final StringBuilder builder, final char c) {
    if (isDecimalSeparator(c)) {
      builder.append('.');
    } else if (isGroupingSeparator(c)) {
      // nothing to do
    } else {
      builder.append(c);
    }
  }

  private boolean isWhitespace(final char c) {
    return c == ' ' || c == '\t' || c == '\r' || c == '\n';
  }

  private boolean isSign(final char c) {
    return c == '-' || c == '+';
  }

  private boolean isSeperator(final char c) {
    return isGroupingSeparator(c) || isDecimalSeparator(c);
  }

  private boolean isDecimalSeparator(final char c) {
    return this.decimalFormatSymbols.getDecimalSeparator() == c;
  }

  private boolean isGroupingSeparator(final char c) {
    return this.decimalFormatSymbols.getGroupingSeparator() == c;
  }

  private boolean isDigit(final char c) {
    return c >= '0' && c <= '9';
  }

  @Override
  public void remove() {
    throw new UnsupportedOperationException();
  }
}
