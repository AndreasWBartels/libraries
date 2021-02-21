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
package net.anwiba.commons.utilities;

public class OperationSystemUtilities {

  private static final String OPERATION_SYSTEM = System.getProperty("os.name"); //$NON-NLS-1$
  private static final String BSD = "BSD"; //$NON-NLS-1$
  private static final String UNIX = "UNIX"; //$NON-NLS-1$
  private static final String MAC = "MAC"; //$NON-NLS-1$
  private static final String WINDOW = "WINDOW"; //$NON-NLS-1$
  private static final String LINUX = "LINUX"; //$NON-NLS-1$

  public static boolean isLinux() {
    return validate(LINUX);
  }

  public static boolean isWindows() {
    return validate(WINDOW);
  }

  public static boolean isMac() {
    return validate(MAC);
  }

  public static boolean isBSD() {
    return validate(BSD);
  }

  public static boolean isUnix() {
    return isLinux() || isMac() || isBSD() || validate(UNIX);
  }

  public static String getOperationSystemName() {
    return OPERATION_SYSTEM;
  }

  private static boolean validate(final String name) {
    final String osName = getOperationSystemName();
    return osName.toUpperCase().contains(name);
  }

}
