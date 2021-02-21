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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program. If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */
package net.anwiba.commons.jdbc.constraint;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import net.anwiba.commons.lang.functional.IAcceptor;
import net.anwiba.commons.utilities.collection.IterableUtilities;

public class ConstraintsUtilities {

  public static Set<String> getIdentifierNames(final Map<String, Constraint> constraints) {
    HashSet<String> result = new HashSet<>();
    for (final Constraint constraint : constraints.values()) {
      if (constraint.isPrimaryKey()) {
        result.addAll(constraint.getColumnNames());
        break;
      }
    }
    if (!result.isEmpty()) {
      return result;
    }
    Constraint uniqueConstraint = null;
    for (final Constraint constraint : constraints.values()) {
      if (Objects.equals(ConstraintType.UNIQUE, constraint.getType())) {
        if (uniqueConstraint == null) {
          uniqueConstraint = constraint;
          continue;
        }
        if (constraint.getColumnNames().size() < uniqueConstraint.getColumnNames().size()) {
          uniqueConstraint = constraint;
          continue;
        }
      }
    }
    if (uniqueConstraint != null) {
      result.addAll(uniqueConstraint.getColumnNames());
    }
    return result;
  }

  public static boolean isPrimaryKey(
      final Map<String, Constraint> constraints,
      final String columnName) {
    for (final Constraint constraint : constraints.values()) {
      if (constraint.isPrimaryKey() && constraint.contains(columnName)) {
        return true;
      }
    }
    return false;
  }

  public static Constraint[] getColumnConstaints(
      final String columnName,
      final Map<String, Constraint> constraints) {
    final List<Constraint> columnConstraints = IterableUtilities
        .asList(constraints.values(), new IAcceptor<Constraint>() {

          @Override
          public boolean accept(final Constraint constraint) {
            return constraint.contains(columnName);
          }
        });
    return columnConstraints.toArray(new Constraint[columnConstraints.size()]);
  }

  public static boolean containsBooleanConstraints(
      final String name,
      final Constraint[] constraints) {
    return IterableUtilities
        .containsAcceptedItems(Arrays.asList(constraints), new IAcceptor<Constraint>() {

          @Override
          public boolean accept(final Constraint constraint) {
            return constraint.getCondition() != null
                && constraint.getCondition().equals(name + " IN (0,1)"); //$NON-NLS-1$
          }
        });
  }

}
