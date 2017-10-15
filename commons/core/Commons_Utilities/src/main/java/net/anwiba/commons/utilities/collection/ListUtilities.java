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

import java.util.ArrayList;
import java.util.List;

import net.anwiba.commons.lang.functional.IAcceptor;
import net.anwiba.commons.lang.functional.IConverter;
import net.anwiba.commons.lang.functional.IEqualComperator;
import net.anwiba.commons.lang.object.ObjectUtilities;
import net.anwiba.commons.utilities.ArrayUtilities;

public class ListUtilities {

  public static <T> List<T> normalize(final Iterable<T> iterable) {
    return new ArrayList<>(IterableUtilities.asSet(iterable));
  }

  public static <T> List<T> getContainsNot(final List<T> list, final Iterable<T> other) {
    return IterableUtilities.asList(other, new IAcceptor<T>() {

      @Override
      public boolean accept(final T value) {
        return !list.contains(other);
      }
    });
  }

  public static boolean equals(final List<?> objects, final List<?> others) {
    if (objects == others) {
      return true;
    }
    if (objects == null || others == null || objects.size() != others.size()) {
      return false;
    }
    for (int i = 0; i < objects.size(); i++) {
      if (!ObjectUtilities.equals(objects.get(i), others.get(i))) {
        return false;
      }
    }
    return true;
  }

  public static <T> List<T> filter(final Iterable<T> values, final IAcceptor<T> validator) {
    final ArrayList<T> result = new ArrayList<>();
    for (final T value : values) {
      if (validator.accept(value)) {
        result.add(value);
      }
    }
    return result;
  }

  public static <T> int[] indices(final List<T> list, final Iterable<T> values, final IEqualComperator<T> comperator) {
    final ArrayList<Integer> result = new ArrayList<>();
    for (final T value : values) {
      for (int i = 0; i < list.size(); i++) {
        final Integer index = Integer.valueOf(i);
        if (comperator.equals(value, list.get(i)) && !result.contains(index)) {
          result.add(index);
        }
      }
    }
    return ArrayUtilities.primitives(result.toArray(new Integer[result.size()]));
  }

  public static <I, O, E extends Exception> List<O> convert(
      final Iterable<I> values,
      final IConverter<I, O, E> converter) throws E {
    final List<O> result = new ArrayList<>();
    for (final I value : values) {
      result.add(converter.convert(value));
    }
    return result;
  }

  @SuppressWarnings("unchecked")
  public static <I> List<I> concat(final Iterable<I>... values) {
    final List<I> result = new ArrayList<>();
    for (final Iterable<I> value : values) {
      for (final I item : value) {
        result.add(item);
      }
    }
    return result;
  }
}