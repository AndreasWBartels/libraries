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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

final public class PrivilegedObjectFactoryAction<C> extends AbstractPrivilegedAction<C> {
  private final Object[] arguments;
  private final Class<? extends C> clazz;
  private final Class<?>[] argumentTypes;

  public PrivilegedObjectFactoryAction(
      final Class<? extends C> clazz,
      final Class<?>[] argumentTypes,
      final Object[] arguments) {
    this.arguments = arguments;
    this.clazz = clazz;
    this.argumentTypes = argumentTypes;
  }

  @Override
  public C invoke() throws InvocationTargetException, Exception {
    final Constructor<? extends C> constructor = this.clazz.getDeclaredConstructor(this.argumentTypes);
    constructor.setAccessible(true);
    return constructor.newInstance(this.arguments);
  }
}