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
package net.anwiba.commons.lang.random;

import java.util.Random;

public final class RandomObjectGenerator {

  private final Random random;
  private final IIsNullDecider isNullDecider;
  private final int maximumStringLength;

  public RandomObjectGenerator(final long seed, final IIsNullDecider isNullDecider) {
    this(seed, isNullDecider, 32);
  }

  public RandomObjectGenerator(final long seed, final IIsNullDecider isNullDecider, final int maximumStringLength) {
    this.isNullDecider = isNullDecider;
    this.maximumStringLength = maximumStringLength;
    this.random = new Random(seed);
  }

  public String generateString() {
    if (this.isNullDecider.isNull()) {
      return null;
    }
    int length = 0;
    do {
      length = this.random.nextInt(this.maximumStringLength);
    } while (length == 0);
    final StringBuilder builder = new StringBuilder(length);
    for (int i = 0; i < length; i++) {
      builder.append(generateCharacter());
    }
    return builder.toString();
  }

  public char generateCharacter() {
    final int digit = this.random.nextInt(36);
    final char character = Character.forDigit(digit, 36);
    return character;
  }

  public Double generateDouble() {
    if (this.isNullDecider.isNull()) {
      return null;
    }
    return Double.valueOf(this.random.nextDouble());
  }

  public Boolean generateBoolean() {
    if (this.isNullDecider.isNull()) {
      return null;
    }
    return Boolean.valueOf(this.random.nextBoolean());
  }

  public Integer generateInteger() {
    if (this.isNullDecider.isNull()) {
      return null;
    }
    return Integer.valueOf(this.random.nextInt());
  }

  public Integer generateInteger(final int maximum) {
    if (this.isNullDecider.isNull()) {
      return null;
    }
    return Integer.valueOf(this.random.nextInt(maximum));
  }
}
