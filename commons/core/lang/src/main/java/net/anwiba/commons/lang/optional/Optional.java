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
import net.anwiba.commons.lang.functional.IBlock;
import net.anwiba.commons.lang.functional.IConsumer;
import net.anwiba.commons.lang.functional.IConverter;
import net.anwiba.commons.lang.functional.IFunction;
import net.anwiba.commons.lang.functional.ISupplier;

import java.util.Objects;

public class Optional<T, E extends Exception> {

  static class Value<T, E extends Exception> implements IOptional<T, E> {

    private final Class<E> exceptionClass;
    private final T value;

    public Value(final Class<E> exceptionClass, final T value) {
      this.exceptionClass = Objects.requireNonNull(exceptionClass);
      this.value = Objects.requireNonNull(value);
    }

    @Override
    public int hashCode() {
      return this.value.hashCode();
    }

    @Override
    public boolean equals(final Object object) {
      if (object == this) {
        return true;
      }
      if (object instanceof Value) {
        @SuppressWarnings("rawtypes")
        final Value other = (Value) object;
        return Objects.equals(this.value, other.value);
      }
      return false;
    }

    @Override
    public IOptional<T, E> or(final T other) {
      return this;
    }

    @Override
    public IOptional<T, E> or(final IBlock<E> block) {
      return this;
    }

    @Override
    public IOptional<T, E> or(final ISupplier<T, E> supplier) {
      return this;
    }

    @Override
    public IOptional<T, E> failed(final ISupplier<T, E> supplier) {
      return this;
    }

    @Override
    public IOptional<T, E> failed(final IConverter<E, T, E> converter) {
      return this;
    }

    @Override
    public T get() throws E {
      return this.value;
    }

    @Override
    public T getObject() {
      return this.value;
    }

    @Override
    public E getCause() {
      throw new IllegalStateException();
    }

    @Override
    public IOptional<T, E> accept(final IAcceptor<T> acceptor) {
      return acceptor.accept(this.value) ? this : empty(this.exceptionClass);
    }

    @Override
    public IOptional<T, E> consume(final IConsumer<T, E> consumer) {
      try {
        consumer.consume(this.value);
        return this;
      } catch (final Exception exception) {
        return Optional.failed(this.exceptionClass, exception, null);
      }
    }

    @Override
    public <O> IOptional<O, E> convert(final IConverter<T, O, E> converter) {
      try {
        return of(this.exceptionClass, converter.convert(this.value));
      } catch (final Exception exception) {
        return Optional.failed(this.exceptionClass, exception, null);
      }
    }

    @Override
    public <O> IOptional<T, E> equals(final IConverter<T, O, E> converter, final O other) {
      try {
        if (Objects.equals(converter.convert(this.value), other)) {
          return this;
        }
        return empty(this.exceptionClass);
      } catch (final Exception exception) {
        return Optional.failed(this.exceptionClass, exception, null);
      }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <O> IOptional<O, E> instanceOf(final Class<O> clazz) {
      if (clazz.isInstance(this.value)) {
        return of(this.exceptionClass, (O) this.value);
      }
      return empty(this.exceptionClass);
    }

    @Override
    public <X extends Exception> T getOrThrow(final ISupplier<X, E> supplier) throws X, E {
      return this.value;
    }

    @Override
    public <X extends Exception> T getOrThrow(final IConverter<E, X, X> supplier) throws X {
      return this.value;
    }

    @Override
    public T getOr(final ISupplier<T, E> supplier) throws E {
      return this.value;
    }

    @Override
    public boolean isEmpty() {
      return false;
    }

    @Override
    public boolean isAccepted() {
      return true;
    }

    @Override
    public boolean isSuccessful() {
      return true;
    }

    @Override
    public boolean contains(final T other) {
      return Objects.equals(this.value, other);
    }

    @Override
    public java.util.Optional<T> toOptional() {
      return java.util.Optional.of(this.value);
    }

    @Override
    public IOptional<T, E> throwIfFaild() throws E {
      return this;
    }
  }

  static class Failed<T, E extends Exception> implements IOptional<T, E> {

    private final Class<E> exceptionClass;
    private final E cause;

    public Failed(final Class<E> exceptionClass, final E cause) {
      this.exceptionClass = Objects.requireNonNull(exceptionClass);
      this.cause = Objects.requireNonNull(cause);
    }

    @Override
    public IOptional<T, E> or(final T value) {
      return this;
    }

    @Override
    public IOptional<T, E> or(final IBlock<E> block) {
      return this;
    }

    @Override
    public IOptional<T, E> or(final ISupplier<T, E> supplier) {
      return this;
    }

    @Override
    public IOptional<T, E> failed(final ISupplier<T, E> supplier) {
      try {
        return of(this.exceptionClass, supplier.supply());
      } catch (final Exception exception) {
        return Optional.failed(this.exceptionClass, exception, getCause());
      }
    }

    @Override
    public IOptional<T, E> failed(final IConverter<E, T, E> converter) {
      try {
        return of(this.exceptionClass, converter.convert(this.cause));
      } catch (final Exception exception) {
        return Optional.failed(this.exceptionClass, exception, getCause());
      }
    }

    @Override
    public T get() throws E {
      throw this.cause;
    }

    @Override
    public T getObject() {
      throw new IllegalStateException();
    }

    @Override
    public E getCause() {
      return this.cause;
    }

    @Override
    public IOptional<T, E> accept(final IAcceptor<T> acceptor) {
      return this;
    }

    @Override
    public IOptional<T, E> consume(final IConsumer<T, E> converter) {
      return this;
    }

    @Override
    public <O> IOptional<O, E> convert(final IConverter<T, O, E> converter) {
      return Optional.failed(this.exceptionClass, this.cause);
    }

    @Override
    public <O> IOptional<T, E> equals(final IConverter<T, O, E> converter, final O value) {
      return Optional.failed(this.exceptionClass, this.cause);
    }

    @Override
    public <X extends Exception> T getOrThrow(final ISupplier<X, E> supplier) throws X, E {
      X supply = supplier.supply();
      supply.addSuppressed(this.cause);
      throw supply;
    }

    @Override
    public <X extends Exception> T getOrThrow(final IConverter<E, X, X> converter) throws X {
      throw converter.convert(this.cause);
    }

    @Override
    public T getOr(final ISupplier<T, E> supplier) throws E {
      throw this.cause;
    }

    @Override
    public boolean isEmpty() {
      return true;
    }

    @Override
    public boolean isAccepted() {
      return false;
    }

    @Override
    public boolean isSuccessful() {
      return false;
    }

    @Override
    public <O> IOptional<O, E> instanceOf(final Class<O> clazz) {
      return Optional.failed(this.exceptionClass, this.cause);
    }

    @Override
    public boolean contains(final T other) {
      return false;
    }

    @Override
    public java.util.Optional<T> toOptional() {
      throw new RuntimeException(this.cause.getMessage(), this.cause);
    }

    @Override
    public IOptional<T, E> throwIfFaild() throws E {
      throw this.cause;
    }
  }

  static class Empty<T, E extends Exception> implements IOptional<T, E> {

    private final Class<E> exceptionClass;

    public Empty(final Class<E> exceptionClass) {
      this.exceptionClass = Objects.requireNonNull(exceptionClass);
    }

    @Override
    public IOptional<T, E> or(final T value) {
      return of(this.exceptionClass, value);
    }

    @Override
    public IOptional<T, E> or(final IBlock<E> block) {
      try {
        block.execute();
        return this;
      } catch (final Exception exception) {
        return Optional.failed(this.exceptionClass, exception, null);
      }
    }

    @Override
    public IOptional<T, E> or(final ISupplier<T, E> supplier) {
      try {
        return of(this.exceptionClass, supplier.supply());
      } catch (final Exception exception) {
        return Optional.failed(this.exceptionClass, exception, null);
      }
    }

    @Override
    public int hashCode() {
      return Empty.class.hashCode();
    }

    @Override
    public boolean equals(final Object object) {
      if (object == this) {
        return true;
      }
      return object instanceof Empty;
    }

    @Override
    public IOptional<T, E> failed(final ISupplier<T, E> supplier) {
      return this;
    }

    @Override
    public IOptional<T, E> failed(final IConverter<E, T, E> value) {
      return this;
    }

    @Override
    public T get() throws E {
      return null;
    }

    @Override
    public T getObject() {
      throw new IllegalStateException();
    }

    @Override
    public E getCause() {
      throw new IllegalStateException();
    }

    @Override
    public IOptional<T, E> accept(final IAcceptor<T> acceptor) {
      return this;
    }

    @Override
    public IOptional<T, E> consume(final IConsumer<T, E> converter) {
      return this;
    }

    @Override
    public <O> IOptional<O, E> convert(final IConverter<T, O, E> converter) {
      return new Empty<>(this.exceptionClass);
    }

    @Override
    public <O> IOptional<T, E> equals(final IConverter<T, O, E> converter, final O value) {
      return this;
    }

    @Override
    public <X extends Exception> T getOrThrow(final ISupplier<X, E> supplier) throws X, E {
      throw supplier.supply();
    }

    @Override
    public <X extends Exception> T getOrThrow(final IConverter<E, X, X> converter) throws X {
      throw converter.convert(null);
    }

    @Override
    public T getOr(final ISupplier<T, E> supplier) throws E {
      return supplier.supply();
    }

    @Override
    public boolean isEmpty() {
      return true;
    }

    @Override
    public boolean isAccepted() {
      return false;
    }

    @Override
    public boolean isSuccessful() {
      return true;
    }

    @Override
    public <O> IOptional<O, E> instanceOf(final Class<O> clazz) {
      return empty(this.exceptionClass);
    }

    @Override
    public boolean contains(final T other) {
      return false;
    }

    @Override
    public java.util.Optional<T> toOptional() {
      return java.util.Optional.empty();
    }

    @Override
    public IOptional<T, E> throwIfFaild() throws E {
      return this;
    }
  }

  public static <T> IOptional<T, RuntimeException> empty() {
    return empty(RuntimeException.class);
  }

  public static <T, E extends Exception> IOptional<T, E> empty(final Class<E> exceptionClass) {
    return new Empty<>(exceptionClass);
  }

  public static <T> IOptional<T, RuntimeException> of(final T value) {
    return of(RuntimeException.class, value);
  }

  public static <T> IOptional<T, RuntimeException> of(final java.util.Optional<T> optional) {
    return optional.isPresent() ? of(optional.get()) : empty();
  }

  public static <T, E extends Exception> IOptional<T, E> of(final Class<E> exceptionClass, final T value) {
    if (value == null) {
      return empty(exceptionClass);
    }
    return new Value<>(exceptionClass, value);
  }

  public static <I, O, E extends Exception> IOptional<O, E> bind(
      final IOptional<I, E> optional,
      final IFunction<I, O, E> function) {
    return optional.convert(i -> function.execute(i));
  }

  public static <T, E extends Exception> IOptional<T, E> failed(final Class<E> exceptionClass, final E cause) {
    return new Failed<>(exceptionClass, cause);
  }

  @SuppressWarnings("unchecked")
  private static <T, E extends Exception> IOptional<T, E> failed(
      final Class<E> exceptionClass,
      final Exception exception,
      final E cause) {
    if (cause != null) {
      exception.addSuppressed(cause);
    }
    if (exceptionClass.isInstance(exception)) {
      return new Failed<>(exceptionClass, (E) exception);
    }
    if (exception instanceof RuntimeException) {
      throw (RuntimeException) exception;
    }
    throw new RuntimeException(exception.getMessage(), exception);
  }

}
