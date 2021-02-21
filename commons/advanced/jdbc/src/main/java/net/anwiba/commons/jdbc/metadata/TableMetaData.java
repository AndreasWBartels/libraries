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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.anwiba.commons.ensure.Ensure;
import net.anwiba.commons.jdbc.constraint.Constraint;

public class TableMetaData implements ITableMetaData {

  private final List<IColumnMetaData> columnMetaDatas = new ArrayList<>();
  private final Map<String, Constraint> constraints;

  public TableMetaData(final List<IColumnMetaData> columnMetaDatas, final Map<String, Constraint> constraints) {
    Ensure.ensureArgumentNotNull(columnMetaDatas);
    this.constraints = constraints;
    this.columnMetaDatas.addAll(columnMetaDatas);
  }

  @Override
  public Map<String, Constraint> getConstraints() {
    return this.constraints;
  }

  @Override
  public IColumnMetaData getColumnMetaData(final int index) {
    return this.columnMetaDatas.get(index);
  }

  @Override
  public int getColumnCount() {
    return this.columnMetaDatas.size();
  }

  @Override
  public Iterator<IColumnMetaData> iterator() {
    return Collections.unmodifiableList(this.columnMetaDatas).iterator();
  }
}