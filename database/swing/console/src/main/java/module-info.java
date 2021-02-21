/*
 * #%L
 * anwiba database
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
module net.anwiba.database.swing.console {

  exports net.anwiba.database.swing.console;
  exports net.anwiba.database.swing.console.converter;

  requires java.datatransfer;
  requires java.desktop;
  requires java.sql;
  requires net.anwiba.commons.jdbc;
  requires net.anwiba.commons.lang;
  requires net.anwiba.commons.logging;
  requires net.anwiba.commons.message;
  requires net.anwiba.commons.model;
  requires net.anwiba.commons.nls;
  requires net.anwiba.commons.preferences;
  requires net.anwiba.commons.reference;
  requires net.anwiba.commons.swing;
  requires net.anwiba.commons.swing.icon;
  requires net.anwiba.commons.swing.icons.gnome.contrast.high;
  requires net.anwiba.commons.thread;
  requires net.anwiba.commons.utilities;
  requires org.eclipse.imagen;
}
