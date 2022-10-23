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

import java.util.NoSuchElementException;

import net.anwiba.commons.lang.functional.IAcceptor;
import net.anwiba.commons.lang.functional.IIterator;
import net.anwiba.commons.lang.optional.IOptional;
import net.anwiba.commons.lang.optional.Optional;

public final class IteratorFilteringIterator<T, E extends Exception> implements IIterator<T, E> {

  private final IIterator<T, E> iterator;
  private final IAcceptor<T> acceptor;
  private IOptional<T, RuntimeException> item = null;

  public IteratorFilteringIterator(final IIterator<T, E> input, final IAcceptor<T> acceptor) {
    this.iterator = input;
    this.acceptor = acceptor;
  }

  @Override
  public boolean hasNext() throws E {
    if (this.item != null) {
      return true;
    }
    while (this.iterator.hasNext()) {
      final T i = this.iterator.next();
      if (this.acceptor.accept(i) && i != null) {
        this.item = Optional.of(i);
        return true;
      }
    }
    return false;
  }

  @Override
  public T next() throws E {
    try {
      if (hasNext()) {
        return this.item.get();
      }
      throw new NoSuchElementException();
    } finally {
      this.item = null;
    }
  }
}
