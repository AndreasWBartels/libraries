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
import net.anwiba.commons.lang.functional.IConverter;

public final class ConvertingIteratorIterator<I, O, E extends Exception> implements IIterator<O, E> {

  private final IIterator<I, E> iterator;
  private final IAcceptor<I> acceptor;
  private final IConverter<I, O, E> converter;
  private O item = null;

  public ConvertingIteratorIterator(
      final IIterator<I, E> input,
      final IAcceptor<I> acceptor,
      final IConverter<I, O, E> converter) {
    this.iterator = input;
    this.acceptor = acceptor;
    this.converter = converter;
  }

  @Override
  public boolean hasNext() throws E {
    while (this.iterator.hasNext()) {
      final I i = this.iterator.next();
      if (!this.acceptor.accept(i)) {
        continue;
      }
      this.item = this.converter.convert(i);
      return true;
    }
    return false;
  }

  @Override
  public O next() throws E {
    try {
      if (this.item != null || hasNext()) {
        return this.item;
      }
      throw new NoSuchElementException();
    } finally {
      this.item = null;
    }
  }
}
