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

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.anwiba.commons.reflection.annotation.Injection;
import net.anwiba.commons.reflection.annotation.Nullable;

public class ReflectionValueInjectionAnalyser implements IReflectionValueInjectionAnalyser {

  @Override
  public <T> IInjektionAnalyserResult analyse(final IInjectingFactory<T> factory) {
    final String createMethodName = "create";
    final Method[] methods = factory.getClass().getMethods();
    final List<Method> createMethods = Stream.of(methods) //
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
    final Field[] fieldArray = clazz.getDeclaredFields();
    final Constructor<T> constructor = getConstructor(clazz);
    final List<IInjektionAnalyserValueResult> results = new ArrayList<>();
    if (injectable(constructor)) {
      for (final Parameter parameter : constructor.getParameters()) {
        results.add(analyse(parameter));
      }
    }
    for (final Field field : fieldArray) {
      final IInjektionAnalyserValueResult result = analyse(field);
      if (result == null) {
        continue;
      }
      results.add(result);
    }
    return InjektionAnalyserResult.create(clazz, results);
  }

  private IInjektionAnalyserValueResult analyse(final Parameter parameter) {
    final boolean isNullable = isNullable(parameter);
    final Class type = getType(parameter);
    final boolean isIterable = isIterable(type);
    if (isIterable) {
      final Class iterableType = getIterableType(parameter);
      return new InjektionAnalyserValueResult(iterableType, isNullable, isIterable);
    }
    return new InjektionAnalyserValueResult(type, isNullable, isIterable);
  }

  private IInjektionAnalyserValueResult analyse(final Field field) {
    if (!injectable(field)) {
      return null;
    }
    final boolean isNullable = isNullable(field);
    final Class type = getType(field);
    final boolean isIterable = isIterable(type);
    if (isIterable) {
      final Class iterableType = getIterableType(field);
      return new InjektionAnalyserValueResult(iterableType, isNullable, isIterable);
    }
    return new InjektionAnalyserValueResult(type, isNullable, isIterable);
  }

  private boolean isNullable(final Parameter parameter) {
    final Nullable nullable = parameter.getAnnotation(Nullable.class);
    final boolean isNullable = nullable != null && nullable.value();
    return isNullable;
  }

  private boolean isIterable(final Class type) {
    return type.isAssignableFrom(Iterable.class);
  }

  private Class getType(final Parameter parameter) {
    return parameter.getType();
  }

  private Class getType(final Field field) {
    return field.getType();
  }

  @SuppressWarnings("unchecked")
  private <T> Constructor<T> getConstructor(final Class<T> clazz) {
    final Constructor<?>[] constructors = clazz.getConstructors();
    if (constructors.length != 1) {
      for (final Constructor<?> constructor : constructors) {
        if (constructor.getParameterCount() == 0) {
          return (Constructor<T>) constructor;
        }
      }
      throw new IllegalArgumentException();
    }
    final Constructor<?> constructor = constructors[0];
    if (constructor.getParameterCount() == 0 || injectable(constructor)) {
      return (Constructor<T>) constructor;
    }
    throw new IllegalArgumentException();
  }

  private boolean injectable(final AccessibleObject object) {
    final Injection annotation = object.getAnnotation(Injection.class);
    return annotation != null && annotation.value();
  }

  private boolean isNullable(final Field field) {
    final Nullable nullable = field.getAnnotation(Nullable.class);
    final boolean isNullable = nullable != null && nullable.value();
    return isNullable;
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
    final ParameterizedType parameterizedType = (ParameterizedType) parameter.getParameterizedType();
    final Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
    return (Class) actualTypeArguments[0];
  }
}
