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
import net.anwiba.commons.lang.functional.IConverter;

public final class ConvertingIterableIterable<I, O, E extends Exception> implements IIterable<O, E> {

  private final IIterable<I, E> input;
  private final IAcceptor<I> acceptor;
  private final IConverter<I, O, E> converter;

  public ConvertingIterableIterable(
      final IIterable<I, E> input,
      final IAcceptor<I> acceptor,
      final IConverter<I, O, E> converter) {
    this.input = input;
    this.acceptor = acceptor;
    this.converter = converter;
  }

  @Override
  public IIterator<O, E> iterator() {
    return new ConvertingIteratorIterator<>(this.input.iterator(), this.acceptor, this.converter);
  }
}
