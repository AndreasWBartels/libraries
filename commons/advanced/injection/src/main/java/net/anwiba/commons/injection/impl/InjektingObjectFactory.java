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

package net.anwiba.commons.injection.impl;

import java.util.Map;

import net.anwiba.commons.injection.IBinding;
import net.anwiba.commons.injection.IInjectingFactory;
import net.anwiba.commons.injection.IInjectionValueProvider;
import net.anwiba.commons.injection.ValueInjector;
import net.anwiba.commons.injection.utilities.IValueHolder;
import net.anwiba.commons.reflection.CreationException;

public final class InjektingObjectFactory {

  private final IInjectingObjectFactory injector;

  @SuppressWarnings("rawtypes")
  public final static InjektingObjectFactory create(
      final IInjectionValueProvider valueProvider,
      final Map<IBinding, IValueHolder> services,
      final Map<IBinding, IBinding> links) {
    return new InjektingObjectFactory(
        new ValueInjector(new InjectionValueProvider(valueProvider, services, links)));
  }

  public InjektingObjectFactory(final IInjectingObjectFactory reflectionValueInjector) {
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
