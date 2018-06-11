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
package net.anwiba.commons.utilities.collection;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.anwiba.commons.lang.functional.ConversionException;
import net.anwiba.commons.lang.functional.IAcceptor;
import net.anwiba.commons.lang.functional.IConverter;
import net.anwiba.commons.utilities.ArrayUtilities;

public class IterableUtilities {

  public static <T> Set<T> asSet(final Iterable<T> iterable) {
    final Set<T> result = new HashSet<>();
    for (final T item : iterable) {
      result.add(item);
    }
    return result;
  }

  public static <T> Set<T> asSet(final Iterable<T> iterable, final IAcceptor<T> validator) {
    final Set<T> result = new HashSet<>();
    for (final T item : iterable) {
      if (validator.accept(item)) {
        result.add(item);
      }
    }
    return result;
  }

  public static <T> Iterable<T> reverse(final Iterable<T> iterable) {
    final List<T> list = asList(iterable);
    Collections.reverse(list);
    return list;
  }

  public static <T> T[] toArray(final Iterable<T> iterable, final Class<T> clazz) {
    final List<T> list = asList(iterable);
    @SuppressWarnings("unchecked")
    final T[] array = (T[]) Array.newInstance(clazz, list.size());
    return list.toArray(array);
  }

  public static int[] toPrimativArray(final Iterable<Integer> iterable) {
    final List<Integer> list = asList(iterable);
    final Integer[] dummy = (Integer[]) Array.newInstance(Integer.class, list.size());
    final Integer[] array = list.toArray(dummy);
    return ArrayUtilities.primitives(array);
  }

  public static <T> List<T> asList(final Iterable<T> iterable) {
    final List<T> result = new ArrayList<>();
    for (final T item : iterable) {
      result.add(item);
    }
    return result;
  }

  public static <T> List<T> asList(final Iterable<T> iterable, final IAcceptor<T> validator) {
    final List<T> result = new ArrayList<>();
    for (final T item : iterable) {
      if (validator.accept(item)) {
        result.add(item);
      }
    }
    return result;
  }

  public static <T> boolean containsAcceptedItems(final Iterable<T> values, final IAcceptor<T> validator) {
    for (final T value : values) {
      if (validator.accept(value)) {
        return true;
      }
    }
    return false;
  }

  public static <I, O, E extends Exception> Iterable<O> convert(
      final Iterable<I> values,
      final IConverter<I, O, E> converter) throws E {
    final List<O> result = new ArrayList<>();
    for (final I value : values) {
      result.add(converter.convert(value));
    }
    return result;
  }

  @SuppressWarnings("unchecked")
  public static <I> Iterable<I> concat(final Iterable<I>... values) {
    final List<I> result = new ArrayList<>();
    for (final Iterable<I> value : values) {
      for (final I item : value) {
        result.add(item);
      }
    }
    return result;
  }

  public static <I> Iterable<I> normalize(final Iterable<I> values) {
    final List<I> result = new ArrayList<>();
    for (final I item : values) {
      if (item == null) {
        continue;
      }
      result.add(item);
    }
    return result;
  }

  public static <T> String toString(final Iterable<T> iterable, final String seperator) {
    final StringBuilder builder = new StringBuilder();
    boolean flag = false;
    for (final T value : iterable) {
      if (flag) {
        builder.append(seperator);
      }
      builder.append(value);
      flag = true;
    }
    return builder.toString();
  }

  public static <T> String toString(
      final Iterable<T> iterable,
      final String seperator,
      final IConverter<T, String, ConversionException> converter) throws ConversionException {
    final StringBuilder builder = new StringBuilder();
    boolean flag = false;
    for (final T value : iterable) {
      if (flag) {
        builder.append(seperator);
      }
      builder.append(converter.convert(value));
      flag = true;
    }
    return builder.toString();
  }
}
