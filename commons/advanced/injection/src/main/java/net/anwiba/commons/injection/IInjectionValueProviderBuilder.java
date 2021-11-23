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

package net.anwiba.commons.injection;

import net.anwiba.commons.reflection.CreationException;

public interface IInjectionValueProviderBuilder {

  <T, S extends T> IInjectionValueProviderBuilder set(IBinding<T> clazz, S object);

  <T, S extends T> IInjectionValueProviderBuilder add(IBinding<T> clazz, S object);

  <T> IInjectionValueProviderBuilder set(IBinding<T> clazz, Class<? extends T> objectClass);

  <T> IInjectionValueProviderBuilder add(IBinding<T> clazz, Class<? extends T> objectClass);

  <T> IInjectionValueProviderBuilder set(IBinding<T> clazz, IInjectingFactory<T> objectFactory);

  <T> IInjectionValueProviderBuilder add(IBinding<T> clazz, IInjectingFactory<T> objectFactory);

  <T, S extends IInjectingSupplier<T>> IInjectionValueProviderBuilder setBySupplier(IBinding<T> clazz,
      Class<S> supplierClass);

  <T, S extends IInjectingSupplier<T>> IInjectionValueProviderBuilder addBySupplier(IBinding<T> clazz,
      Class<S> supplierClass);

//  <T, S extends IInjectingFactory<T>> IInjectionValueProviderBuilder setByFactory(IBinding<T> clazz,
//      Class<S> factoryClass);
//
//  <T, S extends IInjectingFactory<T>> IInjectionValueProviderBuilder addByFactory(IBinding<T> clazz,
//      Class<S> factoryClass);

  <T> IInjectionValueProviderBuilder link(IBinding<? extends T> clazz, IBinding<T> link);

  <T, S extends T> IInjectionValueProviderBuilder set(Class<T> clazz, S object);

  <T, S extends T> IInjectionValueProviderBuilder add(Class<T> clazz, S object);

  <T> IInjectionValueProviderBuilder set(Class<T> clazz, Class<? extends T> objectClass);

  <T> IInjectionValueProviderBuilder add(Class<T> clazz, Class<? extends T> objectClass);

  <T> IInjectionValueProviderBuilder set(Class<T> clazz, IInjectingFactory<T> objectFactory);

  <T> IInjectionValueProviderBuilder add(Class<T> clazz, IInjectingFactory<T> objectFactory);

  <T, S extends IInjectingSupplier<T>> IInjectionValueProviderBuilder setBySupplier(Class<T> clazz,
      Class<S> supplierClass);

  <T, S extends IInjectingSupplier<T>> IInjectionValueProviderBuilder addBySupplier(Class<T> clazz,
      Class<S> supplierClass);

  <T> IInjectionValueProviderBuilder link(Class<? extends T> clazz, Class<T> link);

  <T> IInjectionValueProviderBuilder set(Class<T> clazz);

  <T> IInjectionValueProviderBuilder add(Class<T> clazz);

  IInjectionValueProvider build() throws CreationException;

}
