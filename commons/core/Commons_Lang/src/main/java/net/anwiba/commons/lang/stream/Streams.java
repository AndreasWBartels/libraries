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

package net.anwiba.commons.lang.stream;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import net.anwiba.commons.lang.functional.IIterable;
import net.anwiba.commons.lang.functional.IIterator;

public class Streams {

  public static final class Builder<E extends Exception> extends From<E> {

    public Builder(final Class<E> exceptionClass) {
      super(exceptionClass, 0);
    }

    public From<E> from(final int value) {
      return new From<>(getExceptionClass(), value);
    }
  }

  public static class From<E extends Exception> {

    private final int value;
    private final Class<E> exceptionClass;

    public From(final Class<E> exceptionClass, final int value) {
      this.exceptionClass = exceptionClass;
      this.value = value;
    }

    Class<E> getExceptionClass() {
      return this.exceptionClass;
    }

    public IStream<Integer, E> until(@SuppressWarnings("hiding") final int value) {
      final int from = this.value - 1;
      final int until = value;
      return create(this.exceptionClass, new IIterable<Integer, E>() {

        @Override
        public IIterator<Integer, E> iterator() {
          return new IIterator<Integer, E>() {

            private int last = from;
            private Integer value = null;

            @Override
            public boolean hasNext() throws E {
              if (this.value != null) {
                return true;
              }
              if (!(this.last + 1 < until)) {
                return false;
              }
              this.value = Integer.valueOf(++this.last);
              return true;
            }

            @Override
            public Integer next() throws E {
              try {
                if (!hasNext()) {
                  throw new NoSuchElementException();
                }
                return this.value;
              } finally {
                this.value = null;
              }
            }
          };
        }
      });
    }
  }

  public static Builder<RuntimeException> of() {
    return new Builder<>(RuntimeException.class);
  }

  public static <E extends Exception> Builder<E> create(final Class<E> exceptionClass) {
    return new Builder<>(exceptionClass);
  }

  public static <T> IStream<T, RuntimeException> of(final IIterable<T, RuntimeException> input) {
    if (input == null) {
      return create(RuntimeException.class, Collections.emptyList());
    }
    return new SequencedStream<>(RuntimeException.class, new IterableFilteringIterable<>(input, i -> i != null));
  }

  public static <T> IStream<T, RuntimeException> of(final Iterable<T> input) {
    if (input == null) {
      return create(RuntimeException.class, Collections.emptyList());
    }
    return new SequencedStream<>(
        RuntimeException.class,
        new JavaUtilIterableFilteringIterable<>(input, i -> i != null));
  }

  public static <T> IStream<T, RuntimeException> of(final T[] input) {
    if (input == null) {
      return create(RuntimeException.class, Collections.emptyList());
    }
    return create(RuntimeException.class, Arrays.asList(input));
  }

  public static IStream<Integer, RuntimeException> of(final int[] input) {
    if (input == null) {
      return create(RuntimeException.class, Collections.emptyList());
    }
    return create(RuntimeException.class, Arrays.stream(input).boxed().collect(Collectors.toList()));
  }

  public static IStream<Double, RuntimeException> of(final double[] input) {
    if (input == null) {
      return create(RuntimeException.class, Collections.emptyList());
    }
    return create(RuntimeException.class, Arrays.stream(input).boxed().collect(Collectors.toList()));
  }

  public static <T, E extends Exception> IStream<T, E> create(
      final Class<E> exceptionClass,
      final IIterable<T, E> input) {
    if (input == null) {
      return create(exceptionClass, Collections.emptyList());
    }
    return new SequencedStream<>(exceptionClass, new IterableFilteringIterable<>(input, i -> i != null));
  }

  public static <T, E extends Exception> IStream<T, E> create(final Class<E> exceptionClass, final Iterable<T> input) {
    if (input == null) {
      return create(exceptionClass, Collections.emptyList());
    }
    return new SequencedStream<>(exceptionClass, new JavaUtilIterableFilteringIterable<>(input, i -> i != null));
  }

  public static <T, E extends Exception> IStream<T, E> create(final Class<E> exceptionClass, final T[] input) {
    if (input == null) {
      return create(exceptionClass, Collections.emptyList());
    }
    return create(exceptionClass, Arrays.asList(input));
  }

  public static <T, E extends Exception> IStream<T, E> create(
      final Class<E> exceptionClass,
      final Iterator<T> iterator) {
    return create(exceptionClass, new IIterable<T, E>() {

      @Override
      public IIterator<T, E> iterator() {
        return new IIterator<T, E>() {

          @Override
          public boolean hasNext() throws E {
            return iterator.hasNext();
          }

          @Override
          public T next() throws E {
            return iterator.next();
          }
        };
      }
    });
  }

}
