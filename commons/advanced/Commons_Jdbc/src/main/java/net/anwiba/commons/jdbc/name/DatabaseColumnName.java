/*
 * #%L
 * anwiba commons advanced
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
package net.anwiba.commons.jdbc.name;

public class DatabaseColumnName implements IDatabaseColumnName {

  private final IDatabaseTableName table;
  private final String columnName;

  public DatabaseColumnName(final IDatabaseTableName table, final String columnName) {
    super();
    this.table = table;
    this.columnName = columnName;
  }

  public DatabaseColumnName(final String schemaName, final String tableName, final String columnName) {
    this(new DatabaseTableName(schemaName, tableName), columnName);
  }

  @Override
  public IDatabaseTableName getDatabaseTable() {
    return this.table;
  }

  @Override
  public String getColumnName() {
    return this.columnName;
  }

  @Override
  public String getName() {
    return this.table.getName() + "." + this.columnName; //$NON-NLS-1$
  }

  @Override
  public String toString() {
    return getName();
  }

  @Override
  public String getTableName() {
    return this.table.getTableName();
  }

  @Override
  public String getSchemaName() {
    return this.table.getSchemaName();
  }
}
