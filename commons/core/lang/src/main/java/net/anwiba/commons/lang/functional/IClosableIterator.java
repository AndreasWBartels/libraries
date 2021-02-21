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

public interface IClosableIterator<T, E extends Exception> extends ICloseable<E>, Iterable<T> {

  boolean hasNext() throws E;

  T next() throws E;

  @SuppressWarnings("resource")
  @Override
  default Iterator<T> iterator() {

    final IClosableIterator<T, E> iterator = this;
    return new Iterator<T>() {

      Exception exception;

      @Override
      public boolean hasNext() {
        try {
          return iterator.hasNext();
        } catch (final Exception hasNextException) {
          this.exception = hasNextException;
          return false;
        }
      }

      @Override
      public T next() throws NoSuchElementException {
        if (exception != null) {
          final NoSuchElementException noSuchElementException = new NoSuchElementException();
          noSuchElementException.addSuppressed(exception);
          throw noSuchElementException;
        }
        try {
          return iterator.next();
        } catch (final Exception exception1) {
          final NoSuchElementException noSuchElementException = new NoSuchElementException();
          noSuchElementException.addSuppressed(exception);
          throw noSuchElementException;
        }
      }
    };
  }
}
