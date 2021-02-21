/*
 * #%L
 * anwiba commons core
 * %%
 * Copyright (C) 2007 - 2016 Andreas W. Bartels
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
package net.anwiba.commons.ensure;

public class Ensure {

  public static void ensureArgumentNotNull(final Object object) {
    if (object == null) {
      throw new IllegalArgumentException("argument is null"); //$NON-NLS-1$
    }
  }

  public static void ensureArgumentNotZero(final double value) {
    if (value == 0 || Double.isNaN(value)) {
      throw new IllegalArgumentException("argument is zero"); //$NON-NLS-1$
    }
  }

  public static void ensureArgumentIsInside(final int value, final int min, final int max) {
    if (value < min || max < value) {
      throw new IllegalArgumentException(
          "argument is not inside [" //$NON-NLS-1$
              + min
              + " .. " //$NON-NLS-1$
              + max
              + "]"); //$NON-NLS-1$
    }
  }

  public static void ensureArgumentsEqual(final int value, final int other) {
    if (value != other) {
      throw new IllegalArgumentException("argument are not equal"); //$NON-NLS-1$
    }
  }

  public static void ensureArgumentIsNotEmpty(final Object[] array) {
    ensureArgumentNotNull(array);
    if (array.length == 0) {
      throw new IllegalArgumentException("array is empty"); //$NON-NLS-1$
    }
  }

  public static void ensureArgumentContentIsGreaterThan(final int value, final int limit) {
    if (value <= limit) {
      throw new IllegalArgumentException("arguments number of content is not greater than " + limit); //$NON-NLS-1$
    }
  }

  public static void ensureArgumentIsNotEmpty(final String value) {
    ensureArgumentNotNull(value);
    if (value.trim().length() == 0) {
      throw new IllegalArgumentException("arrgument length is 0"); //$NON-NLS-1$
    }
  }

  public static void ensureArgumentIsInside(final double value, final double min, final double max) {
    if (value < min || max < value) {
      throw new IllegalArgumentException(
          "argument is not inside [" //$NON-NLS-1$
              + min
              + " .. " //$NON-NLS-1$
              + max
              + "]"); //$NON-NLS-1$
    }
  }

  public static void ensureArgumentNull(final String message, final Object object) {
    if (object == null) {
      return;
    }
    throw new IllegalArgumentException(message);
  }

  public static <T> void ensureThat(final T actual, final ICondition<T> condition) {
    if (!condition.accept(actual)) {
      throw new ContractFailedException(message(actual, condition));
    }
  }

  public static <T> void ensureThatArgument(final T actual, final ICondition<T> condition) {
    if (!condition.accept(actual)) {
      throw new IllegalArgumentException(message(actual, condition));
    }
  }

  public static <T> void ensureThatArgument(final String message, final T actual, final ICondition<T> condition) {
    if (!condition.accept(actual)) {
      throw new IllegalArgumentException(message);
    }
  }

  public static <T> void ensureThat(final String message, final T actual, final ICondition<T> condition) {
    if (!condition.accept(actual)) {
      throw new ContractFailedException(message);
    }
  }

  private static <T> String message(final T actual, final ICondition<T> condition) {
    final StringBuilder builder = new StringBuilder();
    builder
        .append("Expected predicate: ") //$NON-NLS-1$
        .append(condition.toText())
        .append("\n     got: ") //$NON-NLS-1$
        .append(actual)
        .append("\n"); //$NON-NLS-1$
    return builder.toString();
  }

}