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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.anwiba.commons.ensure.Ensure;
import net.anwiba.commons.jdbc.constraint.Constraint;
import net.anwiba.commons.lang.exception.UnreachableCodeReachedException;

public class TableResultSetMetaData implements ITableMetaData {

  private final ResultSetMetaData resultSetMetaData;
  private final Map<String, Constraint> constraints = new HashMap<>();

  public TableResultSetMetaData(final ResultSetMetaData resultSetMetaData) {
    Ensure.ensureArgumentNotNull(resultSetMetaData);
    this.resultSetMetaData = resultSetMetaData;
  }

  @Override
  public IColumnMetaData getColumnMetaData(final int index) {
    return new ColumnResultSetMetadata(this.resultSetMetaData, index + 1);
  }

  @Override
  public int getColumnCount() {
    try {
      return this.resultSetMetaData.getColumnCount();
    } catch (final SQLException exception) {
      throw new UnreachableCodeReachedException();
    }
  }

  @Override
  public Map<String, Constraint> getConstraints() {
    return this.constraints;
  }

  @Override
  public Iterator<IColumnMetaData> iterator() {
    // TODO Auto-generated method stub
    return null;
  }
}