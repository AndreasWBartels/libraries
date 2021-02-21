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
package net.anwiba.commons.swing.dialog;

import net.anwiba.commons.nls.NLS;

public class DialogMessages extends NLS {

  public static String LOAD;
  public static String ADD;
  public static String APPLY;
  public static String BACK;
  public static String CANCEL;
  public static String CLOSE;
  public static String CLOSE_DETAILS;
  public static String Dialog;
  public static String Empty;
  public static String ERROR;
  public static String FINISH;
  public static String FORMAT_DATE;
  public static String INFO;
  public static String Initialize;
  public static String NAME;
  public static String NEXT;
  public static String NEW;
  public static String NO;
  public static String OK;
  public static String OPEN;
  public static String OPEN_DETAILS;
  public static String OPEN_URL;
  public static String OPEN_AND_CLOSE;
  public static String PROPERTIES;
  public static String REMOVE;
  public static String REMOVE_ALL;
  public static String QUERY;
  public static String SAVE;
  public static String SAVE_AND_CLOSE;
  public static String TRY;
  public static String VALUE;
  public static String YES;

  static {
    initialize(DialogMessages.class, (c, r) -> c.getResourceAsStream(r));
  }
}