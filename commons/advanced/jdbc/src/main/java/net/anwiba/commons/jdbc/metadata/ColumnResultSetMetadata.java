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

import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import net.anwiba.commons.lang.exception.UnreachableCodeReachedException;

public class ColumnResultSetMetadata implements IColumnMetaData {

  private final ResultSetMetaData resultSetMetaData;
  private final int index;

  public ColumnResultSetMetadata(final ResultSetMetaData resultSetMetaData, final int index) {
    this.resultSetMetaData = resultSetMetaData;
    this.index = index;
  }

  @Override
  public String getColumnName() {
    try {
      return this.resultSetMetaData.getColumnName(this.index);
    } catch (final SQLException exception) {
      throw new UnreachableCodeReachedException();
    }
  }

  @Override
  public int getLength() {
    try {
      final int precision = this.resultSetMetaData.getPrecision(this.index);
      return precision > 0 ? precision : -1;
    } catch (final SQLException exception) {
      throw new UnreachableCodeReachedException();
    }
  }

  @Override
  public int getScale() {
    try {
      final int scale = this.resultSetMetaData.getScale(this.index);
      return scale >= 0 ? scale : -1;
    } catch (final SQLException exception) {
      throw new UnreachableCodeReachedException();
    }
  }

  @Override
  public String getTypeName() {
    try {
      return this.resultSetMetaData.getColumnTypeName(this.index);
    } catch (final SQLException exception) {
      throw new UnreachableCodeReachedException();
    }
  }

  @Override
  public boolean isKey() {
    try {
      return this.resultSetMetaData.isAutoIncrement(this.index);
    } catch (final SQLException exception) {
      throw new UnreachableCodeReachedException();
    }
  }

  @Override
  public boolean isNullable() {
    try {
      return 0 != this.resultSetMetaData.isNullable(this.index);
    } catch (final SQLException exception) {
      throw new UnreachableCodeReachedException();
    }
  }

  @Override
  public String getLabel() {
    try {
      return this.resultSetMetaData.getColumnLabel(this.index);
    } catch (final SQLException exception) {
      throw new UnreachableCodeReachedException();
    }
  }

  @Override
  public String getDescription() {
    return null;
  }

  @Override
  public String getSchemaName() {
    try {
      return this.resultSetMetaData.getSchemaName(this.index);
    } catch (final SQLException exception) {
      throw new UnreachableCodeReachedException();
    }
  }

  @Override
  public String getTableName() {
    try {
      return this.resultSetMetaData.getTableName(this.index);
    } catch (final SQLException exception) {
      throw new UnreachableCodeReachedException();
    }
  }
}
