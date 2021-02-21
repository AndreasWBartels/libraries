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
package net.anwiba.commons.injection.impl;

import java.lang.reflect.AnnotatedElement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

import net.anwiba.commons.annotation.Emptiable;
import net.anwiba.commons.injection.IBinding;
import net.anwiba.commons.injection.IInjectionValueProvider;
import net.anwiba.commons.injection.IValueInjector;
import net.anwiba.commons.injection.binding.ClassBinding;
import net.anwiba.commons.reflection.CreationException;

public abstract class AbstractTypeValueProvider<T extends AnnotatedElement> {
  private final IInjectionValueProvider valuesProvider;
  private final ImitateObjectProxyFactory imitateFactory;
  private final IValueInjectionAnalyser analyser;
  private final IValueInjector valueInjector;

  public AbstractTypeValueProvider(
      final IValueInjectionAnalyser analyser,
      final IValueInjector valueInjector,
      final IInjectionValueProvider valuesProvider,
      final ImitateObjectProxyFactory imitateFactory) {
    this.analyser = analyser;
    this.valueInjector = valueInjector;
    this.valuesProvider = valuesProvider;
    this.imitateFactory = imitateFactory;
  }

  protected abstract Class<?> getType(final T element);

  @SuppressWarnings("rawtypes")
  protected abstract IBinding createBinding(final T element);

  @SuppressWarnings({ "rawtypes", "unchecked" })
  public Object getValue(final T element) {
    final Class<?> type = getType(element);
    if (type.isAssignableFrom(IValueInjector.class)) {
      return this.valueInjector;
    }
    final IBinding binding = createBinding(element);
    if (!this.valuesProvider.contains(binding)) {
      if (this.analyser.isNullable(element)) {
        return null;
      }
      final IInjektionAnalyserResult result = this.analyser.analyse(type);
      if (resolveable(result)) {
        try {
          return this.valueInjector.create(type);
        } catch (final CreationException exception) {
          throw new IllegalStateException(exception.getMessage(), exception);
        }
      }
      if (this.analyser.isImitable(element, type)) {
        try {
          return this.imitateFactory.create(type);
        } catch (final CreationException exception) {
          throw new IllegalStateException(exception.getMessage(), exception);
        }
      }
    }
    if (isIterable(element, type)) {
      final Collection<?> value = this.valuesProvider.getAll(binding);
      if (value != null) {
        return value;
      }
      final Emptiable emptiable = element.getAnnotation(Emptiable.class);
      if (emptiable != null && emptiable.value()) {
        return new ArrayList<>();
      }
      throw new IllegalStateException("missing injektion value for field '" + type + "'"); //$NON-NLS-1$ //$NON-NLS-2$
    }
    if (this.valuesProvider.contains(binding)) {
      return this.valuesProvider.get(binding);
    }
    throw new IllegalStateException("missing injektion value for field '" + type + "'"); //$NON-NLS-1$ //$NON-NLS-2$
  }

  protected abstract boolean isIterable(T element, final Class<?> type);

  @SuppressWarnings({ "rawtypes", "unchecked" })
  private boolean resolveable(final IInjektionAnalyserResult result) {
    if (Objects.equals(IInjektionAnalyserResult.UNRESOLVEABLE, result)) {
      return false;
    }
    if (result.isIndependent()) {
      return true;
    }
    final Iterable<IBinding> bindings = result.getBindings();
    for (final IBinding binding : bindings) {
      if (this.valuesProvider.contains(binding)) {
        continue;
      }
      if (result.isIterable(binding)) {
        if (result.isEmptiable(binding)) {
          continue;
        }
        return false;
      }
      if (result.isNullable(binding)) {
        continue;
      }
      if (result.isImitable(binding)) {
        continue;
      }
      if (!(binding instanceof ClassBinding)) {
        return false;
      }
      if (this.analyser.analyse(binding.getBoundedClass()).isIndependent()) {
        continue;
      }
      return false;
    }
    return true;
  }
}
