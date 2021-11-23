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

import java.util.Iterator;

import net.anwiba.commons.lang.functional.IAcceptor;

public final class IntegerIterator implements Iterator<Integer> {
  private final int maxValue;
  int currentValue = Integer.MIN_VALUE;
  int value = Integer.MIN_VALUE;
  private final IAcceptor<Integer> acceptor;

  public IntegerIterator(final int minValue, final int maxValue) {
    this(minValue, maxValue, new IAcceptor<Integer>() {

      @Override
      public boolean accept(final Integer value) {
        return true;
      }
    });
  }

  public IntegerIterator(final int minValue, final int maxValue, final IAcceptor<Integer> validator) {
    this.acceptor = validator;
    this.currentValue = minValue - 1;
    this.maxValue = maxValue;
  }

  @Override
  public boolean hasNext() {
    if (this.value != Integer.MIN_VALUE) {
      return true;
    }
    while (this.currentValue < this.maxValue) {
      if (this.acceptor.accept(Integer.valueOf(++this.currentValue))) {
        this.value = this.currentValue;
        return true;
      }
    }
    return false;
  }

  @Override
  public Integer next() {
    try {
      if (this.value != Integer.MIN_VALUE || hasNext()) {
        return Integer.valueOf(this.value);
      }
      return null;
    } finally {
      this.value = Integer.MIN_VALUE;
    }
  }

  @Override
  public void remove() {
    throw new UnsupportedOperationException();
  }
}