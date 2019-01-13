/*
 * #%L
 *
 * %%
 * Copyright (C) 2007 - 2018 Andreas W. Bartels
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

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.IntFunction;

import net.anwiba.commons.lang.collection.IObjectList;
import net.anwiba.commons.lang.functional.IAcceptor;
import net.anwiba.commons.lang.functional.IAggregator;
import net.anwiba.commons.lang.functional.IAssimilator;
import net.anwiba.commons.lang.functional.IConsumer;
import net.anwiba.commons.lang.functional.IConverter;
import net.anwiba.commons.lang.functional.IFactory;
import net.anwiba.commons.lang.functional.ISupplier;
import net.anwiba.commons.lang.optional.IOptional;
import net.anwiba.commons.lang.optional.Optional;

public class FailedStream<T, E extends Exception> implements IStream<T, E> {

  private final Class<E> exceptionClass;
  private final E cause;

  FailedStream(final Class<E> exceptionClass, final E cause) {
    this.exceptionClass = Objects.requireNonNull(exceptionClass);
    this.cause = Objects.requireNonNull(cause);
  }

  @Override
  public IStream<T, E> distinct() {
    return this;
  }

  @Override
  public IStream<T, E> filter(final IAcceptor<T> funtion) {
    return this;
  }

  @Override
  public <O> IStream<O, E> convert(final IConverter<T, O, E> funtion) {
    return new FailedStream<>(this.exceptionClass, this.cause);
  }

  @Override
  public <O> IStream<O, E> flat(final IConverter<T, Iterable<O>, E> funtion) {
    return new FailedStream<>(this.exceptionClass, this.cause);
  }

  @Override
  public <O> IStream<O, E> convert(final IAggregator<Integer, T, O, E> aggregator) {
    return new FailedStream<>(this.exceptionClass, this.cause);
  }

  @Override
  public IStream<T, E> failed(final ISupplier<Iterable<T>, E> supplier) {
    try {
      return Streams.of(this.exceptionClass, supplier.supply());
    } catch (final Exception exception) {
      return stream(this.exceptionClass, exception);
    }
  }

  @Override
  public IStream<T, E> foreach(final IConsumer<T, E> consumer) {
    return this;
  }

  @Override
  public IStream<T, E> foreach(final IAssimilator<Integer, T, E> assimilator) {
    return this;
  }

  @Override
  public IStream<T, E> notNull() {
    return this;
  }

  @Override
  public IStream<T, E> revert() {
    return this;
  }

  @Override
  public <O> IStream<O, E> instanceOf(final Class<O> clazz) {
    return new FailedStream<>(this.exceptionClass, this.cause);
  }

  @Override
  public <O> O[] asArray(final IntFunction<O[]> function) throws E {
    throw this.cause;
  }

  @Override
  public Iterable<T> asIterable() throws E {
    throw this.cause;
  }

  @Override
  public Collection<T> asCollection() throws E {
    throw this.cause;
  }

  @Override
  public <O> List<O> asList() throws E {
    throw this.cause;
  }

  @Override
  public <O> Set<O> asSet() throws E {
    throw this.cause;
  }

  @Override
  public <K, V> Map<K, V> asMap(final IFactory<T, K, E> keyFactrory, final IFactory<T, V, E> valueFactrory) throws E {
    throw this.cause;
  }

  @Override
  public IObjectList<T> asObjectList() throws E {
    throw this.cause;
  }

  @Override
  public IOptional<T, E> first() {
    return Optional.failed(this.exceptionClass, this.cause);
  }

  @Override
  public IOptional<T, E> first(final IAcceptor<T> acceptor) {
    return Optional.failed(this.exceptionClass, this.cause);
  }

  @Override
  public <O> IOptional<O, E> aggregate(final O inital, final IAggregator<O, T, O, E> aggregator) {
    return Optional.failed(this.exceptionClass, this.cause);
  }

  @SuppressWarnings("unchecked")
  private static <T, E extends Exception> IStream<T, E> stream(
      final Class<E> exceptionClass,
      final Exception exception) {
    if (exceptionClass.isInstance(exception)) {
      return new FailedStream<>(exceptionClass, (E) exception);
    }
    if (exception instanceof RuntimeException) {
      throw (RuntimeException) exception;
    }
    throw new RuntimeException(exception.getMessage(), exception);
  }
}
