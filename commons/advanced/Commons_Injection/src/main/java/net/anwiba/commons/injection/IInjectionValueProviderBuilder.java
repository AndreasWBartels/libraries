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

  public abstract <T, S extends T> IInjectionValueProviderBuilder set(IBinding<T> clazz, S object);

  public abstract <T, S extends T> IInjectionValueProviderBuilder add(IBinding<T> clazz, S object);

  public abstract <T> IInjectionValueProviderBuilder set(IBinding<T> clazz, Class<? extends T> objectClass);

  public abstract <T> IInjectionValueProviderBuilder add(IBinding<T> clazz, Class<? extends T> objectClass);

  public abstract <T> IInjectionValueProviderBuilder set(IBinding<T> clazz, IInjectingFactory<T> objectFactory);

  public abstract <T> IInjectionValueProviderBuilder add(IBinding<T> clazz, IInjectingFactory<T> objectFactory);

  public abstract <T> IInjectionValueProviderBuilder link(IBinding<T> clazz, IBinding<? extends T> link);

  public abstract <T, S extends T> IInjectionValueProviderBuilder set(Class<T> clazz, S object);

  public abstract <T, S extends T> IInjectionValueProviderBuilder add(Class<T> clazz, S object);

  public abstract <T> IInjectionValueProviderBuilder set(Class<T> clazz, Class<? extends T> objectClass);

  public abstract <T> IInjectionValueProviderBuilder add(Class<T> clazz, Class<? extends T> objectClass);

  public abstract <T> IInjectionValueProviderBuilder set(Class<T> clazz, IInjectingFactory<T> objectFactory);

  public abstract <T> IInjectionValueProviderBuilder add(Class<T> clazz, IInjectingFactory<T> objectFactory);

  public abstract <T> IInjectionValueProviderBuilder link(Class<T> clazz, Class<? extends T> link);

  public abstract IInjectionValueProvider build() throws CreationException;

}
