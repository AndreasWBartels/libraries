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

import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import net.anwiba.commons.injection.IBinding;
import net.anwiba.commons.injection.IInjectionValueProvider;
import net.anwiba.commons.injection.IValueInjector;

public class ParameterValuesProvider extends AbstractTypeValueProvider<Parameter> {

  private final BindingFactory bindingFactory;
  private final NameProvider nameProvider;

  public ParameterValuesProvider(
      final IValueInjectionAnalyser analyser,
      final IValueInjector valueInjector,
      final IInjectionValueProvider valuesProvider,
      final BindingFactory bindingFactory,
      final NameProvider nameProvider,
      final ImitateObjectProxyFactory imitateFactory) {
    super(analyser, valueInjector, valuesProvider, imitateFactory);
    this.bindingFactory = bindingFactory;
    this.nameProvider = nameProvider;
  }

  public Object[] getValues(final Parameter[] parameters) {
    final List<Object> result = new ArrayList<>();
    for (final Parameter parameter : parameters) {
      result.add(getValue(parameter));
    }
    return result.toArray(new Object[result.size()]);
  }

  @Override
  @SuppressWarnings("rawtypes")
  protected IBinding createBinding(final Parameter parameter) {
    final Class<?> clazz = parameter.getType();
    final String name = this.nameProvider.getName(parameter, parameter.getName());
    if (Iterable.class.isAssignableFrom(clazz) && parameter.getParameterizedType() instanceof ParameterizedType) {
      final Class<?> genericType = getIterableType(parameter);
      return this.bindingFactory.create(genericType, name);
    }
    return this.bindingFactory.create(clazz, name);
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  private Class<? extends Type> getIterableType(final Parameter parameter) {
    try {
      final Type type = parameter.getParameterizedType();
      if (type instanceof Class) {
        return (Class) type;
      }
      final ParameterizedType parameterizedType = (ParameterizedType) type;
      final Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
      return (Class) actualTypeArguments[0];
    } catch (final ClassCastException exception) {
      throw exception;
    }
  }

  @Override
  protected Class<?> getType(final Parameter parameter) {
    return parameter.getType();
  }

  @Override
  protected boolean isIterable(final Parameter parameter, final Class<?> type) {
    return Iterable.class.isAssignableFrom(type) && parameter.getParameterizedType() instanceof ParameterizedType;
  }
}
