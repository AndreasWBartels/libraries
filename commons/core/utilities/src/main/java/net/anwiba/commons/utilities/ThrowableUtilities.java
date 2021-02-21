/*
 * #%L
 * anwiba commons
 * %%
 * Copyright (C) 2007 - 2021 Andreas W. Bartels
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
package net.anwiba.commons.utilities;

import java.util.HashSet;

import net.anwiba.commons.lang.functional.IAcceptor;

public class ThrowableUtilities {

  public static boolean validate(final Exception exception, final IAcceptor<Throwable> acceptor) {
    return validate(new HashSet<Throwable>(),
        exception,
        acceptor);
  }

  private static boolean
      validate(final HashSet<Throwable> visited, final Throwable exception, final IAcceptor<Throwable> acceptor) {
    if (visited.contains(exception)) {
      return false;
    }
    visited.add(exception);
    if (acceptor.accept(exception)) {
      return true;
    }
    final Throwable cause = exception.getCause();
    if (cause != null && validate(visited, cause, acceptor)) {
      return true;
    }
    for (Throwable suppressed : exception.getSuppressed()) {
      if (validate(visited, suppressed, acceptor)) {
        return true;
      }
    }
    return false;
  }

}
