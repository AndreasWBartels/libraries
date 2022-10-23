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
module net.anwiba.commons.jdbc {
  exports net.anwiba.commons.jdbc.software;
  exports net.anwiba.commons.jdbc.database;
  exports net.anwiba.commons.jdbc.value;
  exports net.anwiba.commons.jdbc.name;
  exports net.anwiba.commons.jdbc.resource;
  exports net.anwiba.commons.jdbc;
  exports net.anwiba.commons.jdbc.connection;
  exports net.anwiba.commons.jdbc.constraint;
  exports net.anwiba.commons.jdbc.result;
  exports net.anwiba.commons.jdbc.type;
  exports net.anwiba.commons.jdbc.metadata;

  requires java.sql;
  requires java.sql.rowset;
  requires net.anwiba.commons.ensure;
  requires net.anwiba.commons.lang;
  requires net.anwiba.commons.logging;
  requires net.anwiba.commons.nls;
  requires net.anwiba.commons.utilities;
  requires net.anwiba.commons.version;
  requires net.anwiba.commons.thread;
}
