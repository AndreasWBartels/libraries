/*
 * #%L
 * anwiba commons advanced
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
package net.anwiba.commons.lang.number;

import java.math.BigDecimal;
import java.math.BigInteger;

public class ComparableNumber extends Number implements Comparable<ComparableNumber> {

  private static final long serialVersionUID = -6368954577382780447L;
  private final Number number;

  public ComparableNumber(final Number number) {
    if (number instanceof ComparableNumber) {
      this.number = ((ComparableNumber) number).getNumber();
      return;
    }
    this.number = number;
  }

  @Override
  public int compareTo(final ComparableNumber other) {
    if (this.number == null && (other == null || other.getNumber() == null)) {
      return 1;
    }
    if (this.number == null) {
      return -1;
    }
    if (other == null || other.getNumber() == null) {
      return 1;
    }
    final Number value = other.getNumber();
    if (this.number instanceof BigDecimal && value instanceof BigDecimal) {
      return ((BigDecimal) this.number).compareTo((BigDecimal) value);
    }
    if (this.number instanceof BigInteger && value instanceof BigInteger) {
      return ((BigInteger) this.number).compareTo((BigInteger) value);
    }
    if (this.number instanceof Long && value instanceof Long) {
      return ((Long) this.number).compareTo((Long) value);
    }
    if (this.number instanceof Integer && value instanceof Integer) {
      return ((Integer) this.number).compareTo((Integer) value);
    }
    return Double.compare(this.number.doubleValue(), value.doubleValue());
  }

  public Number getNumber() {
    return this.number;
  }

  @Override
  public double doubleValue() {
    return this.number.doubleValue();
  }

  @Override
  public float floatValue() {
    return this.number.floatValue();
  }

  @Override
  public int intValue() {
    return this.number.intValue();
  }

  @Override
  public long longValue() {
    return this.number.longValue();
  }

  @Override
  public String toString() {
    return this.number.toString();
  }

  @Override
  public boolean equals(final Object other) {
    if (other == null) {
      return false;
    }
    if (other instanceof ComparableNumber) {
      return compareTo(((ComparableNumber) other)) == 0;
    }
    if (other instanceof Number) {
      return compareTo(new ComparableNumber((Number) other)) == 0;
    }
    return false;
  }

  @Override
  public int hashCode() {
    return this.number.hashCode();
  }
}
