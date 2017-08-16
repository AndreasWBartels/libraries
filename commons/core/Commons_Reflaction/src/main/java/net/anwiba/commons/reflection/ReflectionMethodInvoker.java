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

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

import net.anwiba.commons.privileged.PrivilegedActionInvoker;

public class ReflectionMethodInvoker<C, R> {

  private final PrivilegedActionInvoker<R> invoker = new PrivilegedActionInvoker<>(System.getSecurityManager());
  private final Class<? extends C> clazz;
  private final Function<Method[], String> methodNameExtractor;
  private final Function<Method[], Class<?>[]> argumentTypesExtractor;
  private final BiFunction<Object[], Class<?>[], Object[]> valuesConverter;

  public static <C, R> ReflectionMethodInvoker<C, R> createSetter(
      final Class<? extends C> clazz,
      final String annotationName,
      final String parameterName,
      final String parameterValue) {
    final Function<Method[], String> methodNameExtractor = methods -> extractSetterName(
        methods,
        annotationName,
        parameterName,
        parameterValue);
    final Function<Method[], Class<?>[]> argumentTypesExtractor = methods -> extractSetterArgumentTypes(
        methods,
        annotationName,
        parameterName,
        parameterValue);
    final BiFunction<Object[], Class<?>[], Object[]> valuesConverter = (values, classes) -> convertValues(
        values,
        classes);
    return new ReflectionMethodInvoker<>(clazz, methodNameExtractor, argumentTypesExtractor, valuesConverter);
  }

  private static Object[] convertValues(final Object[] values, final Class<?>[] classes) {
    final Object[] result = new Object[values.length];
    for (int i = 0; i < classes.length; i++) {
      result[i] = convertValue(values[i], classes[i]);
    }
    return result;
  }

  private static Object convertValue(final Object object, final Class<?> clazz) {
    if (object == null || !clazz.isArray() || (object.getClass().isArray() && clazz.isArray())) {
      return object;
    }
    final Class<? extends Object> objectClass = object.getClass();
    if (objectClass.isInstance(Collection.class)) {
      final Class<?> componentType = clazz.getComponentType();
      return createArray(componentType, (Collection) object);
    }
    return null;
  }

  private static <C> C[] createArray(final Class<C> type, final Collection collection) {
    final C[] array = (C[]) Array.newInstance(type, collection.size());
    int counter = 0;
    for (final Object object : collection) {
      array[counter++] = type.cast(object);
    }
    return array;
  }

  private static Class<?>[] extractSetterArgumentTypes(
      final Method[] methods,
      final String annotationName,
      final String parameterName,
      final String parameterValue) {
    return Optional
        .ofNullable(extract(methods, annotationName, parameterName, parameterValue, void.class))
        .map(method -> method.getParameterTypes())
        .orElseGet(() -> new Class[0]);
  }

  private static String extractSetterName(
      final Method[] methods,
      final String annotationName,
      final String parameterName,
      final String parameterValue) {
    return Optional
        .ofNullable(extract(methods, annotationName, parameterName, parameterValue, void.class))
        .map(method -> method.getName())
        .orElseGet(() -> null);
  }

  private static Method extract(
      final Method[] methods,
      final String annotationName,
      final String parameterName,
      final String parameterValue,
      final Class returnType) {
    for (final Method method : methods) {
      if (!method.getReturnType().equals(returnType)) {
        continue;
      }
      if (method.getParameterCount() != 1) {
        continue;
      }
      if (!hasAnnotation(method.getAnnotations(), annotationName, parameterName, parameterValue)) {
        continue;
      }
      return method;
    }
    return null;
  }

  private static boolean hasAnnotation(
      final Annotation[] annotations,
      final String annotationName,
      final String parameterName,
      final String parameterValue) {
    for (final Annotation annotation : annotations) {

      final Class<? extends Annotation> annotationType = annotation.annotationType();
      final String simpleName = annotationType.getSimpleName();
      if (!simpleName.equals(annotationName)) {
        continue;
      }

      try {
        final ReflectionMethodInvoker<? extends Annotation, String> invoker = new ReflectionMethodInvoker<Annotation, String>(
            annotationType,
            parameterName);
        if (Objects.equals(invoker.invoke(annotation), parameterValue)) {
          return true;
        }
      } catch (final InvocationTargetException exception) {
        return false;
      }
    }
    return false;
  }

  public ReflectionMethodInvoker(
      final Class<? extends C> clazz,
      final Function<Method[], String> methodNameExtractor,
      final Function<Method[], Class<?>[]> argumentTypesExtractor,
      final BiFunction<Object[], Class<?>[], Object[]> valuesConverter) {
    this.clazz = clazz;
    this.methodNameExtractor = methodNameExtractor;
    this.argumentTypesExtractor = argumentTypesExtractor;
    this.valuesConverter = valuesConverter;
  }

  public ReflectionMethodInvoker(
      final Class<? extends C> clazz,
      final String methodName,
      final Class<?>... argumentTypes) {
    this.clazz = clazz;
    this.methodNameExtractor = methods -> methodName;
    this.argumentTypesExtractor = methods -> argumentTypes;
    this.valuesConverter = (values, classes) -> values;
  }

  public R invoke(final Object object, final Object... arguments) throws InvocationTargetException {
    return this.invoker.invoke(
        new PrivilegedMethodInvokeAction<C, R>(
            this.clazz,
            this.methodNameExtractor,
            this.argumentTypesExtractor,
            this.valuesConverter,
            object,
            arguments));
  }
}