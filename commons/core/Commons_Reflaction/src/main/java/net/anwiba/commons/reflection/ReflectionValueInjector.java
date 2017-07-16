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

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.anwiba.commons.privileged.PrivilegedActionInvoker;
import net.anwiba.commons.reflection.annotation.Injection;
import net.anwiba.commons.reflection.annotation.Nullable;

public class ReflectionValueInjector implements IReflectionValueInjector {

  final private InjectableElementGetter injectableElementGetter = new InjectableElementGetter();
  private final PrivilegedActionInvoker<Void> invoker = new PrivilegedActionInvoker<>(System.getSecurityManager());
  private final IReflectionValueProvider values;

  public ReflectionValueInjector(final IReflectionValueProvider values) {
    this.values = values;
  }

  @Override
  @SuppressWarnings("nls")
  public <T> T create(final IInjectingFactory<T> factory) throws CreationException {
    final Method[] methods = factory.getClass().getMethods();
    final List<Method> createMethods = Stream
        .of(methods) //
        .filter(m -> "create".equals(m.getName()))
        .filter(m -> m.getAnnotation(Injection.class) != null)
        .collect(Collectors.toList());
    if (createMethods.isEmpty()) {
      throw new CreationException("");
    }
    if (createMethods.size() > 1) {
      throw new CreationException("");
    }
    try {
      final Method method = createMethods.get(0);
      final Class<?>[] parameterTypes = method.getParameterTypes();
      @SuppressWarnings("unchecked")
      final Class<? extends IInjectingFactory<T>> factoryClass = (Class<? extends IInjectingFactory<T>>) factory
          .getClass();
      final ReflectionMethodInvoker<? extends IInjectingFactory<T>, T> methodInvoker = new ReflectionMethodInvoker<>(
          factoryClass,
          "create",
          parameterTypes);
      final Object[] methodValues = getValues(method.getParameters());
      return methodInvoker.invoke(factory, methodValues);
    } catch (final IllegalStateException exception) {
      throw new CreationException("Couldn't invoke method create instance of class '" //$NON-NLS-1$
          + factory.getClass().getName()
          + "'", exception); //$NON-NLS-1$
    } catch (final InvocationTargetException exception) {
      throw new CreationException("Couldn't invoke method create instance of class '" //$NON-NLS-1$
          + factory.getClass().getName()
          + "'", exception.getCause()); //$NON-NLS-1$
    }
  }

  @Override
  public <T> T create(final Class<T> clazz) throws CreationException {
    try {
      final Constructor<T> constructor = this.injectableElementGetter.getConstructor(clazz);
      final ReflectionConstructorInvoker<T> constructorInvoker = new ReflectionConstructorInvoker<>(
          clazz,
          constructor.getParameterTypes());
      final Object[] constructorValues = getValues(constructor.getParameters());
      final T object = constructorInvoker.invoke(constructorValues);
      inject(object);
      return object;
    } catch (final InvocationTargetException exception) {
      throw new CreationException("Couldn't create instance for class '" //$NON-NLS-1$
          + clazz.getName()
          + "'", exception.getCause()); //$NON-NLS-1$
    } catch (final InjectionException | IllegalArgumentException | IllegalStateException exception) {
      throw new CreationException("Couldn't create instance for class '" //$NON-NLS-1$
          + clazz.getName()
          + "'", exception); //$NON-NLS-1$
    }
  }

  private Object[] getValues(final Parameter[] parameters) {
    final List<Object> result = new ArrayList<>();
    for (final Parameter parameter : parameters) {
      result.add(getValue(parameter));
    }
    return result.toArray(new Object[result.size()]);
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T> void inject(final T object) throws InjectionException {
    final Class<T> clazz = (Class<T>) object.getClass();
    final Field[] fieldArray = clazz.getDeclaredFields();
    for (final Field field : fieldArray) {
      try {
        setValue(field, object);
      } catch (final InvocationTargetException exception) {
        throw new InjectionException("Couldn't inject value to field '" //$NON-NLS-1$
            + field.getName()
            + "' of class '" //$NON-NLS-1$
            + object.getClass().getName()
            + "'", exception.getCause()); //$NON-NLS-1$
      } catch (final IllegalStateException exception) {
        throw new InjectionException("Couldn't inject value to field '" //$NON-NLS-1$
            + field.getName()
            + "' of class '" //$NON-NLS-1$
            + object.getClass().getName()
            + "'", exception); //$NON-NLS-1$
      }
    }
  }

  private <T> void setValue(final Field field, final T object) throws InvocationTargetException {
    if (!this.injectableElementGetter.injectable(field)) {
      return;
    }
    final Object value = getValue(field);
    this.invoker.invoke(new PrivilegedFieldSetterAction(object, field.getName(), value));
  }

  private Object getValue(final Field field) {
    final Class<?> clazz = field.getType();
    if (clazz.isAssignableFrom(Iterable.class)) {
      final Class<?> genericType = getIterableType(field);
      final Collection<?> value = this.values.getAll(genericType);
      if (value != null) {
        return value;
      }
      return new ArrayList<>();
    }
    if (this.values.contains(clazz)) {
      return this.values.get(clazz);
    }
    final Nullable nullable = field.getAnnotation(Nullable.class);
    if (nullable != null && nullable.value()) {
      return null;
    }
    throw new IllegalStateException("missing injektion value for field '" + field.getName() + "'"); //$NON-NLS-1$ //$NON-NLS-2$
  }

  private Object getValue(final Parameter parameter) {
    final Class<?> clazz = parameter.getType();
    if (clazz.isInstance(ReflectionValueInjector.class)) {
      return this;
    }
    if (this.values.contains(clazz)) {
      return this.values.get(clazz);
    }
    if (clazz.isAssignableFrom(Iterable.class)) {
      final Class<?> genericType = getIterableType(parameter);
      final Collection<?> value = this.values.getAll(genericType);
      if (value != null) {
        return value;
      }
      return new ArrayList<>();
    }
    final Nullable nullable = parameter.getAnnotation(Nullable.class);
    if (nullable != null && nullable.value()) {
      return null;
    }
    throw new IllegalStateException("missing injektion value for field '" + parameter.getType().getName() + "'"); //$NON-NLS-1$ //$NON-NLS-2$
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
