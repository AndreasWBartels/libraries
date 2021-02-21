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

import java.util.Iterator;
import java.util.NoSuchElementException;

import net.anwiba.commons.lang.functional.IAcceptor;
import net.anwiba.commons.lang.functional.IConverter;
import net.anwiba.commons.lang.functional.IIterator;

public final class IteratorFlattingIterator<I, O, E extends Exception> implements IIterator<O, E> {

  private final IIterator<I, E> iterator;
  private Iterator<O> itemIterator = null;
  private final IAcceptor<I> acceptor;
  private final IConverter<I, Iterable<O>, E> converter;
  private O item = null;

  public IteratorFlattingIterator(
      final IIterator<I, E> input,
      final IAcceptor<I> acceptor,
      final IConverter<I, Iterable<O>, E> converter) {
    this.iterator = input;
    this.acceptor = acceptor;
    this.converter = converter;
  }

  @Override
  public boolean hasNext() throws E {
    if (this.item != null) {
      return true;
    }
    if (_hasNext()) {
      this.item = this.itemIterator.next();
      return true;
    }
    return false;
  }

  private boolean _hasNext() throws E {
    if (this.itemIterator != null && this.itemIterator.hasNext()) {
      return true;
    }
    while (this.iterator.hasNext()) {
      final I i = this.iterator.next();
      if (this.acceptor.accept(i) && (this.itemIterator = this.converter.convert(i).iterator()) != null) {
        return true;
      }
    }
    return false;
  }

  @Override
  public O next() throws E {
    try {
      if (hasNext()) {
        return this.item;
      }
      throw new NoSuchElementException();
    } finally {
      this.item = null;
    }
  }
}
