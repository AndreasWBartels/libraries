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

import net.anwiba.commons.reflection.privileged.PrivilegedActionInvoker;
import net.anwiba.commons.reflection.privileged.PrivilegedObjectFactoryAction;

public class ReflectionConstructorInvoker<C> {

  private final PrivilegedActionInvoker<C> invoker = new PrivilegedActionInvoker<>(System.getSecurityManager());
  private final Class<? extends C> clazz;
  private final Class<?>[] argumentTypes;

  public ReflectionConstructorInvoker(final Class<? extends C> clazz, final Class<?>... argumentTypes) {
    this.clazz = clazz;
    this.argumentTypes = argumentTypes;
  }

  public C invoke(final Object... arguments) throws InvocationTargetException {
    return this.invoker.invoke(new PrivilegedObjectFactoryAction<>(this.clazz, this.argumentTypes, arguments));
  }
}