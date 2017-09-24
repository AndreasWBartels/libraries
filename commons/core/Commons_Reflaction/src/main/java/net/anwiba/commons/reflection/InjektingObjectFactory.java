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

package net.anwiba.commons.reflection;

import java.util.Map;

import net.anwiba.commons.reflection.utilities.IValueHolder;

public final class InjektingObjectFactory {

  private final IReflectionValueInjector injector;

  @SuppressWarnings("rawtypes")
  public final static InjektingObjectFactory create(
      final IReflectionValueProvider valueProvider,
      final Map<Class, IValueHolder> services,
      final Map<Class, Class> links) {
    return new InjektingObjectFactory(
        new ReflectionValueInjector(new ReflectionValueProvider(valueProvider, services, links)));
  }

  public InjektingObjectFactory(final IReflectionValueInjector reflectionValueInjector) {
    this.injector = reflectionValueInjector;
  }

  @SuppressWarnings("unchecked")
  public Object create(final IInjektionObjectDescription result) throws CreationException {
    if (result.isFactory()) {
      return create(result.getFactory());
    }
    return create(result.getType());
  }

  private <T> T create(final IInjectingFactory<T> factory) throws CreationException {
    return this.injector.create(factory);
  }

  private <T> T create(final Class<? extends T> clazz) throws CreationException {
    return this.injector.create(clazz);
  }
}
