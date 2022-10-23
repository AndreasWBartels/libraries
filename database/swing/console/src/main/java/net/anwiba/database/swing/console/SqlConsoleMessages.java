/*
 * #%L
 *
 * %%
 * Copyright (C) 2007 - 2018 Andreas W. Bartels (bartels@anwiba.de)
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
package net.anwiba.database.swing.console;

import net.anwiba.commons.nls.NLS;

public class SqlConsoleMessages extends NLS {

  public static String columnPrivileges;
  public static String connect;
  public static String connectionIsClosed;
  public static String constraints;
  public static String content;
  public static String createStatement;
  public static String disconnect;
  public static String done;
  public static String empty;
  public static String emptyResult;
  public static String execute;
  public static String executeStatement;
  public static String indicies;
  public static String loadTableContent;
  public static String noResult;
  public static String open;
  public static String properties;
  public static String reload;
  public static String reloadDatabaseSchema;
  public static String save;
  public static String sequences;
  public static String SQLConsole;
  public static String tablePrivileges;
  public static String tables;
  public static String triggers;
  public static String views;
  public static String working;
  public static String columns;

  static {
    initialize(SqlConsoleMessages.class, (c, r) -> c.getResourceAsStream(r));
  }

  private SqlConsoleMessages() {
  }
}
