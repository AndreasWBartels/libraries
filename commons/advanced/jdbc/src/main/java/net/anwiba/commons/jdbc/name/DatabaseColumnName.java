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

import net.anwiba.commons.lang.object.ObjectUtilities;

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

  public DatabaseColumnName(final String catalogName,final String schemaName, final String tableName, final String columnName) {
    this(new DatabaseTableName(catalogName, schemaName, tableName), columnName);
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

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = prime + ((this.table == null) ? 0 : this.table.hashCode());
    return prime * result + ((this.columnName == null) ? 0 : this.columnName.hashCode());
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    return obj instanceof IDatabaseColumnName other
        && ObjectUtilities.equals(this.table, other.getDatabaseTable()) //
        && ObjectUtilities.equals(this.columnName, other.getColumnName());
  }
}