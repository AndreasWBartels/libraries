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

import net.anwiba.commons.reflection.annotation.Injection;
import net.anwiba.commons.reflection.annotation.Named;
import net.anwiba.commons.reflection.annotation.Nullable;
import net.anwiba.commons.reflection.binding.ClassBinding;
import net.anwiba.commons.reflection.binding.NamedClassBinding;

public class ReflectionValueInjectionAnalyser implements IReflectionValueInjectionAnalyser {

  final private InjectableElementGetter injectableElementGetter = new InjectableElementGetter();

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
      final List<IInjektionAnalyserValueResult> results = new ArrayList<>();
      for (final Parameter parameter : constructor.getParameters()) {
        results.add(analyse(parameter));
      }
      for (final Field field : fieldArray) {
        if (!this.injectableElementGetter.injectable(field)) {
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
    final boolean isIterable = isIterable(type);
    final String name = getName(null, parameter);
    if (isIterable) {
      final Class iterableType = getIterableType(parameter);
      return new InjektionAnalyserValueResult(binding(iterableType, name), isNullable, isIterable);
    }
    return new InjektionAnalyserValueResult(binding(type, name), isNullable, isIterable);
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  private IInjektionAnalyserValueResult analyse(final Field field) {
    final boolean isNullable = isNullable(field);
    final Class type = getType(field);
    final String name = getName(field.getName(), field);
    final boolean isIterable = isIterable(type);
    if (isIterable) {
      final Class iterableType = getIterableType(field);
      return new InjektionAnalyserValueResult(binding(iterableType, name), isNullable, isIterable);
    }
    return new InjektionAnalyserValueResult(binding(type, name), isNullable, isIterable);
  }

  private <T> IBinding<T> binding(final Class<T> clazz, final String name) {
    return Optional.ofNullable(name).map(n -> (IBinding<T>) new NamedClassBinding<>(clazz, n)).orElseGet(
        () -> new ClassBinding<>(clazz));
  }

  private String getName(final String name, final AnnotatedElement annotatedElement) {
    return Optional
        .ofNullable(annotatedElement.getAnnotation(Named.class))
        .map(a -> a.value())
        .map(s -> s.trim())
        .map(n -> {
          if (n.isEmpty()) {
            if (name == null) {
              throw new IllegalStateException("Missing annotation value"); //$NON-NLS-1$
            }
            return name;
          }
          return n;
        })
        .orElseGet(() -> null);
  }

  private boolean isNullable(final Parameter parameter) {
    final Nullable nullable = parameter.getAnnotation(Nullable.class);
    final boolean isNullable = nullable != null && nullable.value();
    return isNullable;
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  private boolean isIterable(final Class type) {
    return type.isAssignableFrom(Iterable.class);
  }

  @SuppressWarnings("rawtypes")
  private Class getType(final Parameter parameter) {
    return parameter.getType();
  }

  @SuppressWarnings("rawtypes")
  private Class getType(final Field field) {
    return field.getType();
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
