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

public class Counter implements ICounter {

  private long value;
  private final long maxValue;
  private final long startValue;

  public Counter(final long value) {
    this(value, Long.MAX_VALUE);
  }

  public Counter(final long startValue, final long maxValue) {
    this.value = startValue;
    this.startValue = startValue;
    this.maxValue = maxValue;
  }

  @Override
  public synchronized long value() {
    return this.value;
  }

  @Override
  public synchronized long next() {
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
  public synchronized long previous() {
    if (this.value > this.startValue) {
      return --this.value;
    }
    this.value = this.maxValue;
    return this.value;
  }

  public synchronized void add(@SuppressWarnings("hiding") final int value) {
    this.value += value;
  }

  public synchronized long minimum() {
    this.value = this.startValue;
    return this.value;
  }

  public synchronized long maximum() {
    this.value = this.maxValue;
    return this.value;
  }

  @Override
  public void set(final long value) {
    this.value = value;
  }
}