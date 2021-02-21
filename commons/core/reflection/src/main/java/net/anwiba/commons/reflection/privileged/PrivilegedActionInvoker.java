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
import java.security.AccessController;
import java.security.PrivilegedAction;

public class PrivilegedActionInvoker<T> {

  public final SecurityManager securityManager;

  public PrivilegedActionInvoker(final SecurityManager securityManager) {
    this.securityManager = securityManager;
  }

  public T invoke(final PrivilegedAction<T> action) throws InvocationTargetException {
    try {
      if (this.securityManager == null) {
        return action.run();
      }
      return AccessController.doPrivileged(action);
    } catch (final RuntimeException exception) {
      if (exception.getCause() instanceof InvocationTargetException) {
        throw (InvocationTargetException) exception.getCause();
      }
      if (exception.getCause() != null) {
        throw new InvocationTargetException(exception.getCause());
      }
      throw exception;
    }
  }
}