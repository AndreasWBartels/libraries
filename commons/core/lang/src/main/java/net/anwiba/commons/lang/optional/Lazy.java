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
package net.anwiba.commons.lang.optional;

import net.anwiba.commons.lang.functional.ISupplier;

public class Lazy<T, E extends Exception> implements ILazy<T, E> {

  private final Class<E> exceptionClass;
  private final ISupplier<T, E> supplier;
  private IOptional<T, E> optional;

  Lazy(final Class<E> exceptionClass, final ISupplier<T, E> supplier) {
    this.exceptionClass = exceptionClass;
    this.supplier = supplier;
  }

  @Override
  public T get() throws E {
    check();
    return this.optional.get();
  }

  private synchronized void check() {
    if (this.optional == null) {
      this.optional = Optional.of(this.exceptionClass, (T) null).or(this.supplier);
    }
  }

  @Override
  public IOptional<T, E> optional() {
    check();
    return this.optional;
  }

  public static <T> ILazy<T, RuntimeException> of(final ISupplier<T, RuntimeException> supplier) {
    return of(RuntimeException.class, supplier);
  }

  public static <T, E extends Exception> ILazy<T, E> of(final Class<E> exceptionClass, final ISupplier<T, E> supplier) {
    return new Lazy<>(exceptionClass, supplier);
  }

  @SuppressWarnings("unchecked")
  public static <T, E extends Exception> ILazy<T, E> narrow(final ILazy<? extends T, E> lazy) {
    return (ILazy<T, E>) lazy;
  }

}
