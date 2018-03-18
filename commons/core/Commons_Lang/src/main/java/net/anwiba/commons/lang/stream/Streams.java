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
import java.util.NoSuchElementException;

import net.anwiba.commons.lang.functional.IIterable;
import net.anwiba.commons.lang.functional.IIterator;

public class Streams {

  public static final class Builder<E extends Exception> extends From<E> {

    public Builder() {
      super(0);
    }

    public From<E> from(final int value) {
      return new From<>(value);
    }
  }

  public static class From<E extends Exception> {

    private final int value;

    public From(final int value) {
      this.value = value;
    }

    public IStream<Integer, E> until(final int value) {
      final int from = this.value - 1;
      final int until = value;
      return create(new IIterable<Integer, E>() {

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
    return new Builder<>();
  }

  public static <E extends Exception> Builder<E> create() {
    return new Builder<>();
  }

  public static <T> IStream<T, RuntimeException> of(final IIterable<T, RuntimeException> input) {
    if (input == null) {
      return create(Collections.emptyList());
    }
    return new SequencedStream<>(new FilteringIterableIterable<>(input, i -> i != null));
  }

  public static <T> IStream<T, RuntimeException> of(final Iterable<T> input) {
    if (input == null) {
      return create(Collections.emptyList());
    }
    return new SequencedStream<>(new FilteredJavaUtilIterableIterable<>(input, i -> i != null));
  }

  public static <T> IStream<T, RuntimeException> of(final T[] input) {
    if (input == null) {
      return create(Collections.emptyList());
    }
    return create(Arrays.asList(input));
  }

  public static <T, E extends Exception> IStream<T, E> create(final IIterable<T, E> input) {
    if (input == null) {
      return create(Collections.emptyList());
    }
    return new SequencedStream<>(new FilteringIterableIterable<>(input, i -> i != null));
  }

  public static <T, E extends Exception> IStream<T, E> create(final Iterable<T> input) {
    if (input == null) {
      return create(Collections.emptyList());
    }
    return new SequencedStream<>(new FilteredJavaUtilIterableIterable<>(input, i -> i != null));
  }

  public static <T, E extends Exception> IStream<T, E> create(final T[] input) {
    if (input == null) {
      return create(Collections.emptyList());
    }
    return create(Arrays.asList(input));
  }

}
