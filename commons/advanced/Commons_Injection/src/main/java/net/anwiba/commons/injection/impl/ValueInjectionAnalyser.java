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

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.anwiba.commons.annotation.Emptiable;
import net.anwiba.commons.annotation.Imitable;
import net.anwiba.commons.annotation.Injection;
import net.anwiba.commons.annotation.Nullable;
import net.anwiba.commons.injection.IBinding;
import net.anwiba.commons.injection.IInjectingFactory;
import net.anwiba.commons.injection.binding.ClassBinding;
import net.anwiba.commons.injection.binding.NamedClassBinding;

public class ValueInjectionAnalyser implements IValueInjectionAnalyser {

  final private InjectableConstructorsGetter injectableElementGetter = new InjectableConstructorsGetter();
  final private NameProvider nameProvider = new NameProvider();

  @SuppressWarnings("unchecked")
  @Override
  public <T> IInjektionAnalyserResult analyse(final IInjectingFactory<T> factory) {
    final String createMethodName = "create"; //$NON-NLS-1$
    final Method[] methods = factory.getClass().getMethods();
    final List<Method> createMethods = Stream
        .of(methods) //
        .filter(m -> createMethodName.equals(m.getName()))
        .filter(m -> m.getAnnotation(Injection.class) != null)
        .collect(Collectors.toList());
    if (createMethods.size() != 1) {
      throw new IllegalArgumentException();
    }
    final List<IInjektionAnalyserValueResult> results = new ArrayList<>();
    final Method method = createMethods.get(0);
    for (final Parameter parameter : method.getParameters()) {
      results.add(analyse(parameter));
    }
    try {
      final Class<T> clazz = (Class<T>) method.getReturnType();
      return InjektionAnalyserResult.create(clazz, results, factory);
    } catch (final ClassCastException exception) {
      throw new IllegalArgumentException();
    }
  }

  @Override
  public <T> IInjektionAnalyserResult analyse(final Class<T> clazz) {
    try {
      final Field[] fieldArray = clazz.getDeclaredFields();
      final Constructor<T> constructor = this.injectableElementGetter.getConstructor(clazz);
      if (constructor == null) {
        return IInjektionAnalyserResult.UNRESOLVEABLE;
      }
      final List<IInjektionAnalyserValueResult> results = new ArrayList<>();
      for (final Parameter parameter : constructor.getParameters()) {
        results.add(analyse(parameter));
      }
      for (final Field field : fieldArray) {
        if (!isInjectable(field)) {
          continue;
        }
        results.add(analyse(field));
      }
      return InjektionAnalyserResult.create(clazz, results);
    } catch (final IllegalStateException exception) {
      throw new IllegalStateException(clazz.getName() + ", " + exception.getMessage(), exception); //$NON-NLS-1$
    }
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  private IInjektionAnalyserValueResult analyse(final Parameter parameter) {
    final boolean isNullable = isNullable(parameter);
    final Class type = getType(parameter);
    final boolean isImitable = isImitable(parameter, type);
    final boolean isIterable = isIterable(type) && parameter.getParameterizedType() instanceof ParameterizedType;
    final String name = this.nameProvider.getName(parameter, null);
    if (isIterable) {
      final Class iterableType = getIterableType(parameter);
      final boolean isEmptiable = isEmptiable(parameter);
      return new InjektionAnalyserValueResult(
          binding(iterableType, name),
          isNullable,
          isImitable,
          isIterable,
          isEmptiable);
    }
    return new InjektionAnalyserValueResult(binding(type, name), isNullable, isImitable, isIterable, false);
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  private IInjektionAnalyserValueResult analyse(final Field field) {
    final boolean isNullable = isNullable(field);
    final Class type = getType(field);
    final boolean isImitable = isImitable(field, type) && field.getGenericType() instanceof ParameterizedType;
    final String name = this.nameProvider.getName(field, field.getName());
    final boolean isIterable = isIterable(type);
    if (isIterable) {
      final Class iterableType = getIterableType(field);
      final boolean isEmptiable = isEmptiable(field);
      return new InjektionAnalyserValueResult(
          binding(iterableType, name),
          isNullable,
          isImitable,
          isIterable,
          isEmptiable);
    }
    return new InjektionAnalyserValueResult(binding(type, name), isNullable, isImitable, isIterable, false);
  }

  private <T> IBinding<T> binding(final Class<T> clazz, final String name) {
    return Optional.ofNullable(name).map(n -> (IBinding<T>) new NamedClassBinding<>(clazz, n)).orElseGet(
        () -> new ClassBinding<>(clazz));
  }

  @SuppressWarnings({ "rawtypes" })
  private boolean isIterable(final Class type) {
    return Iterable.class.isAssignableFrom(type);
  }

  @SuppressWarnings("rawtypes")
  private Class getType(final Parameter parameter) {
    return parameter.getType();
  }

  @SuppressWarnings("rawtypes")
  private Class getType(final Field field) {
    return field.getType();
  }

  @Override
  public boolean isNullable(final AnnotatedElement element) {
    final Nullable annotation = element.getAnnotation(Nullable.class);
    return annotation != null && annotation.value();
  }

  private boolean isEmptiable(final AnnotatedElement element) {
    final Emptiable annotation = element.getAnnotation(Emptiable.class);
    return annotation != null && annotation.value();
  }

  @SuppressWarnings("rawtypes")
  @Override
  public boolean isImitable(final AnnotatedElement element, final Class type) {
    if (!type.isInterface()) {
      return false;
    }
    final Method[] methods = type.getMethods();
    for (final Method method : methods) {
      final Class<?> returnType = method.getReturnType();
      if (Void.class.equals(returnType) || void.class.equals(returnType)) {
        continue;
      }
      if (method.isDefault()) {
        continue;
      }
      return false;
    }
    final Imitable annotation = element.getAnnotation(Imitable.class);
    return annotation != null && annotation.value();
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

  @SuppressWarnings({ "unchecked", "rawtypes" })
  private Class<? extends Type> getIterableType(final Parameter parameter) {
    final Type type = parameter.getParameterizedType();
    if (type instanceof Class) {
      return (Class) type;
    }
    final ParameterizedType parameterizedType = (ParameterizedType) type;
    final Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
    return (Class) actualTypeArguments[0];
  }

  @Override
  public boolean isInjectable(final AnnotatedElement element) {
    final Injection annotation = element.getAnnotation(Injection.class);
    return annotation != null && annotation.value();
  }

}
