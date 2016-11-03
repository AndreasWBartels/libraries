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
import net.anwiba.commons.lang.functional.IConverter;
import net.anwiba.commons.lang.object.ObjectUtilities;
import net.anwiba.commons.lang.stream.IConsumer;
import net.anwiba.commons.lang.stream.ISupplier;

public class Optional<T, E extends Exception> implements IOptional<T, E> {

  private final T value;
  private final IAcceptor<T> acceptor;

  private Optional(final IAcceptor<T> acceptor, final T value) {
    this.acceptor = acceptor;
    this.value = value;
  }

  public static <T, E extends Exception> IOptional<T, E> create(final T value) {
    return new Optional<>(i -> i != null, value);
  }

  @Override
  public T get() throws E {
    if (isAccepted()) {
      return this.value;
    }
    return null;
  }

  @Override
  public IOptional<T, E> accept(final IAcceptor<T> acceptor) throws E {
    if (isAccepted()) {
      return new Optional<>(acceptor, this.value);
    }
    return create(null);
  }

  private boolean isAccepted() {
    return this.acceptor.accept(this.value);
  }

  @Override
  public <O> IOptional<O, E> convert(final IConverter<T, O, E> converter) throws E {
    if (isAccepted()) {
      return create(converter.convert(this.value));
    }
    return create(null);
  }

  @Override
  public <O> void consum(final IConsumer<T, E> consumer) throws E {
    if (isAccepted()) {
      consumer.consume(this.value);
    }
  }

  @Override
  public <O> IOptional<T, E> equals(final IConverter<T, O, E> converter, final O other) throws E {
    if (isAccepted() && ObjectUtilities.equals(converter.convert(this.value), other)) {
      return this;
    }
    return create(null);
  }

  @Override
  public <X extends Exception> T getOrThrow(final X throwable) throws E, X {
    if (isAccepted()) {
      return this.value;
    }
    throw throwable;
  }

  @Override
  public <X extends Exception> T getOrThrow(final ISupplier<X, E> supplier) throws E, X {
    if (isAccepted()) {
      return this.value;
    }
    throw supplier.supply();
  }

  @Override
  public T getOr(final T value) throws E {
    if (isAccepted()) {
      return this.value;
    }
    return value;
  }

  @Override
  public T getOr(final ISupplier<T, E> supplier) throws E {
    if (isAccepted()) {
      return this.value;
    }
    return supplier.supply();
  }

}
