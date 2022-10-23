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
package net.anwiba.commons.jdbc.metadata;

public class ColumnMetaData implements IColumnMetaData {

  private final String name;
  private final String typeName;
  private final long length;
  private final int scale;
  private final boolean isKey;
  private final boolean isNullable;
  private final String schemaName;
  private final String tableName;
  private boolean isAutoIncrement;

  public ColumnMetaData(
      final String schemaName,
      final String tableName,
      final String columnName,
      final String typeName,
      final long length,
      final int scale,
      final boolean isKey,
      final boolean isAutoIncrement,
      final boolean isNullable) {
    this.schemaName = schemaName;
    this.tableName = tableName;
    this.name = columnName;
    this.typeName = typeName;
    this.length = length;
    this.scale = scale;
    this.isKey = isKey;
    this.isAutoIncrement = isAutoIncrement;
    this.isNullable = isNullable;
  }

  @Override
  public long getLength() {
    return this.length;
  }

  @Override
  public String getColumnName() {
    return this.name;
  }

  @Override
  public int getScale() {
    return this.scale;
  }

  @Override
  public String getTypeName() {
    return this.typeName;
  }

  @Override
  public boolean isKey() {
    return this.isKey;
  }

  @Override
  public boolean isAutoIncrement() {
    return this.isAutoIncrement;
  }
  
  @Override
  public boolean isNullable() {
    return this.isNullable;
  }

  @Override
  public String getLabel() {
    return this.name;
  }

  @Override
  public String getDescription() {
    return null;
  }

  @Override
  public String getSchemaName() {
    return this.schemaName;
  }

  @Override
  public String getTableName() {
    return this.tableName;
  }
}
