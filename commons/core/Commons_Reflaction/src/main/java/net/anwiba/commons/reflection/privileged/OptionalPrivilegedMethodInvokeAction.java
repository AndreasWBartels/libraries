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
package net.anwiba.commons.reflection.privileged;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

final public class OptionalPrivilegedMethodInvokeAction<C, R> extends AbstractPrivilegedAction<R> {
  private final Object object;
  private final Object[] arguments;
  private final Class<? extends C> clazz;
  private final Function<Method[], String> methodNameExtractor;
  private final Function<Method[], Class<?>[]> argumentTypesExtractor;
  private final BiFunction<Object[], Class<?>[], Object[]> valuesConverter;

  public OptionalPrivilegedMethodInvokeAction(
      final Class<? extends C> clazz,
      final Function<Method[], String> methodNameExtractor,
      final Function<Method[], Class<?>[]> argumentTypesExtractor,
      final BiFunction<Object[], Class<?>[], Object[]> valuesConverter,
      final Object object,
      final Object[] arguments) {
    this.clazz = clazz;
    this.methodNameExtractor = methodNameExtractor;
    this.argumentTypesExtractor = argumentTypesExtractor;
    this.valuesConverter = valuesConverter;
    this.object = object;
    this.arguments = arguments;
  }

  @Override
  protected R invoke() throws InvocationTargetException {
    final Method method = getMethod();
    if (method == null) {
      return null;
    }
    try {
      return invoke(method, method.isAccessible());
    } catch (final IllegalAccessException exception) {
      throw new InvocationTargetException(exception);
    }
  }

  @SuppressWarnings("unchecked")
  private R invoke(final Method method, final boolean accessible)
      throws IllegalAccessException,
      InvocationTargetException {
    try {
      method.setAccessible(true);
      return (R) method.invoke(this.object, this.valuesConverter.apply(this.arguments, method.getParameterTypes()));
    } finally {
      method.setAccessible(accessible);
    }
  }

  private Method getMethod() {
    final Method[] declaredMethods = this.clazz.getDeclaredMethods();
    final Method[] inhertedMethods = this.clazz.getMethods();
    Method[] methods = new Method[declaredMethods.length + inhertedMethods.length];
    System.arraycopy(declaredMethods, 0, methods, 0, declaredMethods.length);
    System.arraycopy(inhertedMethods, 0, methods, declaredMethods.length, inhertedMethods.length);
    final String methodName = this.methodNameExtractor.apply(methods);
    methods = Stream.of(methods).filter(method -> method.getName().equals(methodName)).toArray(Method[]::new);
    @SuppressWarnings("rawtypes")
    final Class[] argumentTypes = this.argumentTypesExtractor.apply(methods);
    return Stream
        .of(methods)
        .filter(method -> Arrays.equals(method.getParameterTypes(), argumentTypes))
        .findFirst()
        .orElseGet(() -> null);
  }
}
