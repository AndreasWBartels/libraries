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
package net.anwiba.commons.lang.counter;

public class IntCounter implements IIntCounter {

  private int value;
  private final int maxValue;
  private final int startValue;

  public IntCounter(final int value) {
    this(value, Integer.MAX_VALUE);
  }

  public IntCounter(final int startValue, final int maxValue) {
    this.value = startValue;
    this.startValue = startValue;
    this.maxValue = maxValue;
  }

  @Override
  public synchronized int value() {
    return this.value;
  }

  @Override
  public synchronized int next() {
    if (this.value < this.maxValue) {
      return ++this.value;
    }
    this.value = this.startValue;
    return this.value;
  }

  @Override
  public void increment() {
    next();
  }

  @Override
  public void decrement() {
    previous();
  }

  @Override
  public synchronized int previous() {
    if (this.value > this.startValue) {
      return --this.value;
    }
    this.value = this.maxValue;
    return this.value;
  }

  public synchronized long minimum() {
    this.value = this.startValue;
    return this.value;
  }

  public synchronized long maximum() {
    this.value = this.maxValue;
    return this.value;
  }
}