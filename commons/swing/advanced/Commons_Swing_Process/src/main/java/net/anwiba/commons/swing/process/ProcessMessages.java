/*
 * #%L
 * anwiba commons swing
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
package net.anwiba.commons.swing.process;

import net.anwiba.commons.nls.NLS;

public class ProcessMessages extends NLS {

  public static String MESSAGE;
  public static String MESSAGES;
  public static String PROCESS;
  public static String PROCESS_MANAGER;
  public static String PROCESSES;
  public static String TYPE;
  public static String LOGGING;
  public static String DATE;
  public static String DETAIL;

  static {
    NLS.initializeMessages(ProcessMessages.class);
  }
}
