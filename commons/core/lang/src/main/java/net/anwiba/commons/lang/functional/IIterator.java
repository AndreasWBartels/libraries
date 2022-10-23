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

package net.anwiba.commons.lang.functional;

import java.util.Iterator;
import java.util.NoSuchElementException;

public interface IIterator<O, E extends Exception> {

  static <T> IIterator<T, RuntimeException> of(final Iterator<T> iterator) {
    return new IIterator<T, RuntimeException>() {

      @Override
      public boolean hasNext() throws RuntimeException {
        return iterator.hasNext();
      }

      @Override
      public T next() throws RuntimeException {
        return iterator.next();
      }
    };
  }

  static <T> IIterator<T, RuntimeException> empty() {
    return new IIterator<T, RuntimeException>() {

      @Override
      public boolean hasNext() throws RuntimeException {
        return false;
      }

      @Override
      public T next() throws RuntimeException {
        throw new NoSuchElementException();
      }
    };
  }

  boolean hasNext() throws E;

  O next() throws E;

  default Iterator<O> iterator() {

    final IIterator<O, E> iterator = this;
    return new Iterator<O>() {

      Exception hasNextException;

      @Override
      public boolean hasNext() {
        try {
          return iterator.hasNext();
        } catch (final Exception exception) {
          this.hasNextException = exception;
          return false;
        }
      }

      @Override
      public O next() throws NoSuchElementException {
        if (this.hasNextException != null) {
          throw handle(this.hasNextException);
        }
        try {
          return iterator.next();
        } catch (final Exception exception) {
          throw handle(exception);
        }
      }

      private NoSuchElementException handle(final Exception exception) {
        if (exception instanceof NoSuchElementException) {
          return (NoSuchElementException) exception;
        } else if (exception instanceof RuntimeException) {
          throw (RuntimeException) exception;
        } else {
          return new NoSuchElementException(exception);
        }
      }
    };
  }
}
