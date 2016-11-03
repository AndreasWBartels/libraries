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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import net.anwiba.commons.privileged.AbstractPrivilegedAction;

final class PrivilegedMethodInvokeAction<C, R> extends AbstractPrivilegedAction<R> {
  private final Object object;
  private final Object[] arguments;
  private final Class<? extends C> clazz;
  private final String methodName;
  private final Class<?>[] argumentTypes;

  PrivilegedMethodInvokeAction(
      final Class<? extends C> clazz,
      final String methodName,
      final Class<?>[] argumentTypes,
      final Object object,
      final Object[] arguments) {
    this.clazz = clazz;
    this.methodName = methodName;
    this.argumentTypes = argumentTypes;
    this.object = object;
    this.arguments = arguments;
  }

  @SuppressWarnings("unchecked")
  @Override
  protected R invoke() throws InvocationTargetException, Exception {
    final Method method = this.clazz.getDeclaredMethod(this.methodName, this.argumentTypes);
    method.setAccessible(true);
    return (R) method.invoke(this.object, this.arguments);
  }
}