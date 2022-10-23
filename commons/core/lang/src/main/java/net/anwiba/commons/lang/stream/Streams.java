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
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import net.anwiba.commons.lang.functional.IAcceptor;
import net.anwiba.commons.lang.functional.IClosableIterator;
import net.anwiba.commons.lang.functional.ICloseable;
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
      return of(this.exceptionClass, new IIterable<Integer, E>() {

        @Override
        public IIterator<Integer, E> iterator() {
          return new IIterator<>() {

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

  public static From<RuntimeException> from(final int value) {
    return new Builder<>(RuntimeException.class).from(value);
  }

  public static <E extends Exception> From<E> from(final Class<E> exceptionClass, final int value) {
    return new Builder<>(exceptionClass).from(value);
  }

  public static IStream<Integer, RuntimeException> until(final int value) {
    return new Builder<>(RuntimeException.class).until(value);
  }

  public static <E extends Exception> IStream<Integer, E> until(final Class<E> exceptionClass, final int value) {
    return new Builder<>(exceptionClass).until(value);
  }

  public static <T> IStream<T, RuntimeException> of() {
    return of(List.of());
  }

  public static <T> IStream<T, RuntimeException> of(final Iterable<T> input) {
    if (input == null) {
      return of(RuntimeException.class, Collections.emptyList());
    }
    return of(RuntimeException.class, new JavaUtilIterableFilteringIterable<>(input, i -> i != null));
  }

  public static <T> IStream<T, RuntimeException> of(final Iterable<T> input, final IAcceptor<T> acceptor) {
    if (input == null) {
      return of(RuntimeException.class, Collections.emptyList());
    }
    return of(RuntimeException.class, new JavaUtilIterableFilteringIterable<>(input, acceptor));
  }

  public static <T> IStream<T, RuntimeException> of(final IClosableIterator<T, RuntimeException> input) {
    if (input == null) {
      return of(RuntimeException.class, Collections.emptyList());
    }
    return of(RuntimeException.class, new JavaUtilIterableFilteringIterable<>(input, i -> i != null), input);
  }

  public static <T> IStream<T, RuntimeException> of(final IIterable<T, RuntimeException> input) {
    if (input == null) {
      return of(RuntimeException.class, Collections.emptyList());
    }
    return of(RuntimeException.class, new IterableFilteringIterable<>(input, i -> i != null));
  }

  public static <T> IStream<T, RuntimeException> of(final T[] input) {
    if (input == null) {
      return of(RuntimeException.class, Collections.emptyList());
    }
    return of(RuntimeException.class, Arrays.asList(input));
  }

  public static IStream<Integer, RuntimeException> of(final int[] input) {
    if (input == null) {
      return of(RuntimeException.class, Collections.emptyList());
    }
    return of(RuntimeException.class, Arrays.stream(input).boxed().collect(Collectors.toList()));
  }

  public static IStream<Double, RuntimeException> of(final double[] input) {
    if (input == null) {
      return of(RuntimeException.class, Collections.emptyList());
    }
    return of(RuntimeException.class, Arrays.stream(input).boxed().collect(Collectors.toList()));
  }

  public static IStream<Double, RuntimeException> of(final float[] input) {
    if (input == null) {
      return of(RuntimeException.class, Collections.emptyList());
    }
    double[] values = new double[input.length];
    for (int i = 0; i < input.length; i++) {
      values[i] = input[i];
    }
    return of(values);
  }

  public static <T, E extends Exception> IStream<T, E> of(final Class<E> exceptionClass, final IIterable<T, E> input) {
    if (input == null) {
      return of(exceptionClass, Collections.emptyList());
    }
    return of(exceptionClass, new IterableFilteringIterable<>(input, i -> i != null), () -> {});
  }

  public static <T, E extends Exception> IStream<T, E> of(final Class<E> exceptionClass,
      final IClosableIterator<T, E> input) {
    if (input == null) {
      return of(exceptionClass, Collections.emptyList());
    }
    return of(exceptionClass, new JavaUtilIterableFilteringIterable<>(input, i -> i != null), input);
  }

  public static <T, E extends Exception> IStream<T, E> of(final Class<E> exceptionClass, final T[] input) {
    if (input == null) {
      return of(exceptionClass, Collections.emptyList());
    }
    return of(exceptionClass, Arrays.asList(input));
  }

  public static <T, E extends Exception> IStream<T, E> of(final Class<E> exceptionClass,
      final IIterable<T, E> input,
      final ICloseable<E> closeable) {
    if (input == null) {
      return of(exceptionClass, Collections.emptyList(), closeable);
    }
    return new SequencedStream<>(exceptionClass, new IterableFilteringIterable<>(input, i -> i != null), closeable);
  }

  public static <T, E extends Exception> IStream<T, E> of(final Class<E> exceptionClass, final Iterable<T> input) {
    if (input == null) {
      return of(exceptionClass, Collections.emptyList(), () -> {});
    }
    return new SequencedStream<>(exceptionClass,
        new JavaUtilIterableFilteringIterable<>(input, i -> i != null),
        () -> {});
  }

  public static <T, E extends Exception> IStream<T, E> of(final Class<E> exceptionClass,
      final Iterable<T> input,
      final ICloseable<E> closeable) {
    if (input == null) {
      return of(exceptionClass, Collections.emptyList(), closeable);
    }
    return new SequencedStream<>(exceptionClass,
        new JavaUtilIterableFilteringIterable<>(input, i -> i != null),
        closeable);
  }

  public static <T, E extends Exception> IStream<T, E> of(final Class<E> exceptionClass, final Iterator<T> iterator) {
    return of(exceptionClass, new IIterable<T, E>() {

      @Override
      public IIterator<T, E> iterator() {
        return new IIterator<>() {

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

  public static <T, E extends Exception> IStream<T, E> of(final Class<E> exceptionClass, final Enumeration<T> input) {
    if (input == null) {
      return of(exceptionClass, Collections.emptyList());
    }
    return of(exceptionClass, input.asIterator());
  }

  public static <T> IStream<T, RuntimeException> of(final Enumeration<T> input) {
    if (input == null) {
      return of(RuntimeException.class, Collections.emptyList());
    }
    return of(RuntimeException.class, input.asIterator());
  }

}
