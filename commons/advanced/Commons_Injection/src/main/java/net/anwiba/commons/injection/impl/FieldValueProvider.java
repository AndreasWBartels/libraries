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

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import net.anwiba.commons.injection.IBinding;
import net.anwiba.commons.injection.IInjectionValueProvider;
import net.anwiba.commons.injection.IValueInjector;

public class FieldValueProvider extends AbstractTypeValueProvider<Field> {

  private final BindingFactory bindingFactory;
  private final NameProvider nameProvider;

  public FieldValueProvider(
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

  @Override
  @SuppressWarnings("rawtypes")
  protected IBinding createBinding(final Field field) {
    final Class<?> clazz = field.getType();
    final String name = this.nameProvider.getName(field, field.getName());
    if (clazz.isAssignableFrom(Iterable.class)) {
      final Class<?> genericType = getIterableType(field);
      return this.bindingFactory.create(genericType, name);
    }
    return this.bindingFactory.create(clazz, name);
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  private Class<? extends Type> getIterableType(final Field field) {
    if (ParameterizedType.class.isAssignableFrom(field.getGenericType().getClass())) {
      final ParameterizedType genericType = (ParameterizedType) field.getGenericType();
      final Type[] actualTypeArguments = genericType.getActualTypeArguments();
      return (Class) actualTypeArguments[0];
    }
    return field.getGenericType().getClass();
  }

  @Override
  protected Class<?> getType(final Field field) {
    return field.getType();
  }

  @Override
  protected boolean isIterable(final Field field, final Class<?> type) {
    return Iterable.class.isAssignableFrom(type) && field.getGenericType() instanceof ParameterizedType;
  }
}
