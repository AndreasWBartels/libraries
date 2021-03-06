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
package net.anwiba.commons.jdbc.constraint;

import java.util.ArrayList;
import java.util.List;

public class Constraint {

  private final List<String> columnNames = new ArrayList<>();
  private final String name;
  private final ConstraintType type;
  private final String condition;

  public Constraint(final String name, final ConstraintType type, final String condition) {
    this.name = name;
    this.type = type;
    this.condition = condition;
  }

  public String getConstraintName() {
    return name;
  }

  public void add(final String columnName) {
    columnNames.add(columnName);
  }

  public boolean contains(final String columnName) {
    return columnNames.contains(columnName);
  }

  public boolean isPrimaryKey() {
    return getType() == ConstraintType.PRIMARY_KEY;
  }

  public String getCondition() {
    return condition;
  }

  public List<String> getColumnNames() {
    return columnNames;
  }

  public ConstraintType getType() {
    return type;
  }
}