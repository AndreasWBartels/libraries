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

public enum ConstraintType {
  CHECK('C'), FOREIGN_KEY('F'), PRIMARY_KEY('P'), UNIQUE('U'), NONE('N');

  private final char id;

  private ConstraintType(final char id) {
    this.id = id;
  }

  public static ConstraintType getTypeById(final char id) {
    final ConstraintType[] values = values();
    for (final ConstraintType constraintType : values) {
      if (constraintType.id == Character.toUpperCase(id)) {
        return constraintType;
      }
    }
    return NONE;
  }

  public static ConstraintType getTypeById(final String string) {
    if (string == null || string.length() != 1) {
      return ConstraintType.NONE;
    }
    return getTypeById(string.charAt(0));
  }
}
