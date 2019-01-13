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
package net.anwiba.commons.injection;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.anwiba.commons.annotation.Injection;
import net.anwiba.commons.injection.impl.BindingFactory;
import net.anwiba.commons.injection.impl.FieldValueProvider;
import net.anwiba.commons.injection.impl.IValueInjectionAnalyser;
import net.anwiba.commons.injection.impl.ImitateObjectProxyFactory;
import net.anwiba.commons.injection.impl.InjectableConstructorsGetter;
import net.anwiba.commons.injection.impl.NameProvider;
import net.anwiba.commons.injection.impl.ParameterValuesProvider;
import net.anwiba.commons.injection.impl.ValueInjectionAnalyser;
import net.anwiba.commons.reflection.CreationException;
import net.anwiba.commons.reflection.ReflectionConstructorInvoker;
import net.anwiba.commons.reflection.ReflectionMethodInvoker;
import net.anwiba.commons.reflection.privileged.PrivilegedActionInvoker;
import net.anwiba.commons.reflection.privileged.PrivilegedFieldSetterAction;

public class ValueInjector implements IValueInjector {

  final private InjectableConstructorsGetter injectableElementGetter = new InjectableConstructorsGetter();
  private final PrivilegedActionInvoker<Void> invoker = new PrivilegedActionInvoker<>(System.getSecurityManager());
  private final ImitateObjectProxyFactory imitateFactory = new ImitateObjectProxyFactory();
  private final ParameterValuesProvider parameterValuesProvider;
  private final FieldValueProvider fieldValueProvider;
  private final IValueInjectionAnalyser analyser = new ValueInjectionAnalyser();

  public ValueInjector(final IInjectionValueProvider valuesProvider) {
    final BindingFactory bindingFactory = new BindingFactory();
    final NameProvider nameProvider = new NameProvider();
    this.parameterValuesProvider = new ParameterValuesProvider(
        this.analyser,
        this,
        valuesProvider,
        bindingFactory,
        nameProvider,
        this.imitateFactory);
    this.fieldValueProvider = new FieldValueProvider(
        this.analyser,
        this,
        valuesProvider,
        bindingFactory,
        nameProvider,
        this.imitateFactory);
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
      final Object[] methodValues = this.parameterValuesProvider.getValues(method.getParameters());
      return methodInvoker.invoke(factory, methodValues);
    } catch (final IllegalStateException exception) {
      throw new CreationException(
          "Couldn't invoke method create instance of class '" //$NON-NLS-1$
              + factory.getClass().getName()
              + "'", //$NON-NLS-1$
          exception);
    } catch (final InvocationTargetException exception) {
      throw new CreationException(
          "Couldn't invoke method create instance of class '" //$NON-NLS-1$
              + factory.getClass().getName()
              + "'", //$NON-NLS-1$
          exception.getCause());
    }
  }

  @Override
  public <T> T create(final Class<T> clazz) throws CreationException {
    try {
      final Constructor<T> constructor = this.injectableElementGetter.getConstructor(clazz);
      final ReflectionConstructorInvoker<T> constructorInvoker = new ReflectionConstructorInvoker<>(
          clazz,
          constructor.getParameterTypes());
      final Object[] constructorValues = this.parameterValuesProvider.getValues(constructor.getParameters());
      final T object = constructorInvoker.invoke(constructorValues);
      inject(object);
      return object;
    } catch (final InvocationTargetException exception) {
      throw new CreationException(
          "Couldn't create instance for class '" //$NON-NLS-1$
              + clazz.getName()
              + "'", //$NON-NLS-1$
          exception.getCause());
    } catch (final InjectionException | IllegalArgumentException | IllegalStateException exception) {
      throw new CreationException(
          "Couldn't create instance for class '" //$NON-NLS-1$
              + clazz.getName()
              + "', " //$NON-NLS-1$
              + exception.getMessage(),
          exception);
    }
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T> void inject(final T object) throws InjectionException {
    final Class<T> clazz = (Class<T>) object.getClass();
    final Field[] fieldArray = clazz.getDeclaredFields();
    for (final Field field : fieldArray) {
      inject(object, field);
    }
  }

  private <T> void inject(final T object, final Field field) throws InjectionException {
    try {
      setValue(field, object);
    } catch (final InvocationTargetException exception) {
      throw new InjectionException(
          "Couldn't inject value to field '" //$NON-NLS-1$
              + field.getName()
              + "' of class '" //$NON-NLS-1$
              + object.getClass().getName()
              + "'", //$NON-NLS-1$
          exception.getCause());
    } catch (final IllegalStateException exception) {
      throw new InjectionException(
          "Couldn't inject value to field '" //$NON-NLS-1$
              + field.getName()
              + "' of class '" //$NON-NLS-1$
              + object.getClass().getName()
              + "'", //$NON-NLS-1$
          exception);
    }
  }

  private <T> void setValue(final Field field, final T object) throws InvocationTargetException {
    if (!this.analyser.isInjectable(field)) {
      return;
    }
    final Object value = this.fieldValueProvider.getValue(field);
    this.invoker.invoke(new PrivilegedFieldSetterAction(object, field.getName(), value));
  }
}
