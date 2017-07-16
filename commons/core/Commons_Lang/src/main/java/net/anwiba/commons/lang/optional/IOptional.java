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

package net.anwiba.commons.lang.optional;

import net.anwiba.commons.lang.functional.IAcceptor;
import net.anwiba.commons.lang.functional.IConsumer;
import net.anwiba.commons.lang.functional.IConverter;
import net.anwiba.commons.lang.functional.ISupplier;

public interface IOptional<T, E extends Exception> {

  public IOptional<T, E> consum(IConsumer<T, E> converter) throws E;

  public IOptional<T, E> or(IConsumer<T, E> converter) throws E;

  public <O> IOptional<O, E> convert(IConverter<T, O, E> converter) throws E;

  public T get() throws E;

  public <X extends Exception> T getOrThrow(ISupplier<X, E> supplier) throws X, E;

  <X extends Exception> T getOrThrow(X throwable) throws E, X;

  public IOptional<T, E> accept(IAcceptor<T> acceptor) throws E;

  public <O> IOptional<T, E> equals(IConverter<T, O, E> converter, O value) throws E;

  T getOr(ISupplier<T, E> supplier) throws E;

  T getOr(T value) throws E;

  boolean isAccepted();

}
