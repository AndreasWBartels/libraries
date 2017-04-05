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
package net.anwiba.commons.swing.menu;

public interface IMenueConstant {
  final public static String FILE_MENU_ID = "fileId"; //$NON-NLS-1$
  final public static String HELP_MENU_ID = "helpId"; //$NON-NLS-1$
  public static final MenuDescription FILE_MENU = new MenuDescription(
      IMenueConstant.FILE_MENU_ID,
      MenuMessages.FILE,
      -1);
  public static final MenuDescription HELP_MENU = new MenuDescription(
      IMenueConstant.HELP_MENU_ID,
      MenuMessages.HELP,
      Integer.MAX_VALUE);
  public static final String DEVELOPMENT_MENU_ID = "developmentId"; //$NON-NLS-1$
  public static final MenuDescription DEVELOPMENT_MENU = new MenuDescription(
      IMenueConstant.DEVELOPMENT_MENU_ID,
      MenuMessages.DEVELOPMENT,
      Integer.MAX_VALUE - 10);
}
