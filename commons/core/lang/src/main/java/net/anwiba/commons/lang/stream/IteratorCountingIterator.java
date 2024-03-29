/*
 * #%L
 *
 * %%
 * Copyright (C) 2007 - 2017 Andreas W. Bartels
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

import net.anwiba.commons.lang.counter.IntCounter;
import net.anwiba.commons.lang.functional.IAcceptor;
import net.anwiba.commons.lang.functional.IAggregator;
import net.anwiba.commons.lang.functional.IIterator;
import net.anwiba.commons.lang.optional.IOptional;
import net.anwiba.commons.lang.optional.Optional;

public class IteratorCountingIterator<I, O, E extends Exception> implements IIterator<O, E> {

  private final IntCounter counter = new IntCounter(-1);
  private final IIterator<I, E> iterator;
  private final IAcceptor<I> acceptor;
  private final IAggregator<Integer, I, O, E> aggegator;
  private IOptional<O, RuntimeException> item = null;

  public IteratorCountingIterator(
      final IIterator<I, E> input,
      final IAcceptor<I> acceptor,
      final IAggregator<Integer, I, O, E> aggegator) {
    this.iterator = input;
    this.acceptor = acceptor;
    this.aggegator = aggegator;
  }

  @Override
  public boolean hasNext() throws E {
    if (this.item != null) {
      return true;
    }
    while (this.iterator.hasNext()) {
      final I i = this.iterator.next();
      if (this.acceptor.accept(i)) {
        this.item = Optional.of(this.aggegator.aggregate(Integer.valueOf(this.counter.next()), i));
        return true;
      }
    }
    return false;
  }

  @Override
  public O next() throws E {
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
