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

import net.anwiba.commons.lang.functional.IAcceptor;
import net.anwiba.commons.lang.functional.IIterable;
import net.anwiba.commons.lang.functional.IIterator;

public final class IterableFilteringIterable<T, E extends Exception> implements IIterable<T, E> {

  private final IIterable<T, E> input;
  private final IAcceptor<T> acceptor;

  public IterableFilteringIterable(final IIterable<T, E> input, final IAcceptor<T> acceptor) {
    this.input = input;
    this.acceptor = acceptor;
  }

  @Override
  public IIterator<T, E> iterator() {
    return new IteratorFilteringIterator<>(this.input.iterator(), this.acceptor);
  }
} 
