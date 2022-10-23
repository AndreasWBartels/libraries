/*
 * #%L
 *
 * %%
 * Copyright (C) 2007 - 2018 Andreas W. Bartels
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
package net.anwiba.commons.jdbc;

import net.anwiba.commons.lang.counter.Counter;
import net.anwiba.commons.lang.counter.ICounter;
import net.anwiba.commons.lang.optional.If;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class BatchTransfer implements IBatchTransfer {

  private final String selectExistsStatementString;
  private final String insertStatementString;
  private final String updateStatementString;
  private final List<Object[]> values = new LinkedList<>();
  private final int numberOfIdentifiers;
  private final int numberOfColumns;
  private final Connection connection;

  public BatchTransfer(
      final Connection connection,
      final int numberOfIdentifiers,
      final int numberOfColumns,
      final String selectExistsStatement,
      final String insertStatement,
      final String updateStatement) {
    this.connection = connection;
    this.numberOfIdentifiers = numberOfIdentifiers;
    this.numberOfColumns = numberOfColumns;
    this.selectExistsStatementString = selectExistsStatement;
    this.insertStatementString = insertStatement;
    this.updateStatementString = updateStatement;
  }

  @Override
  public void add(@SuppressWarnings("hiding") final Object... values) {
    if (this.numberOfColumns != values.length) {
      throw new IllegalArgumentException();
    }
    this.values.add(values);
  }

  @Override
  public int[] transfer() throws SQLException {
    try (PreparedStatement selectExistsStatement =
        DatabaseUtilities.createStatement(this.connection, this.selectExistsStatementString)) {
      try (PreparedStatement insertStatement =
          DatabaseUtilities.createStatement(this.connection, this.insertStatementString)) {
        try (PreparedStatement updateStatement =
            DatabaseUtilities.createStatement(this.connection, this.updateStatementString)) {
          return transfer(selectExistsStatement, insertStatement, updateStatement);
        }
      }
    }
  }

  private int[] transfer(
      final PreparedStatement selectExistsStatement,
      final PreparedStatement insertStatement,
      final PreparedStatement updateStatement)
      throws SQLException {
    final ICounter insertCounter = new Counter(0);
    final ICounter updateCounter = new Counter(0);
    for (final Object[] objects : this.values) {
      if (DatabaseUtilities.count(selectExistsStatement, DatabaseUtilities.setter(identifiers(objects))) > 0) {
        DatabaseUtilities
            .add(updateStatement, DatabaseUtilities.setter(concat(values(objects), identifiers(objects))));
        updateCounter.increment();
      } else {
        DatabaseUtilities.add(insertStatement, DatabaseUtilities.setter(objects));
        insertCounter.increment();
      }
    }
    final List<Integer> results = new LinkedList<>();
    If.isTrue(insertCounter.value() > 0).execute(() -> {
      results.addAll(IntStream.of((DatabaseUtilities.transfer(insertStatement))).boxed().collect(Collectors.toList()));
    });
    If.isTrue(updateCounter.value() > 0).execute(() -> {
      results.addAll(IntStream.of((DatabaseUtilities.transfer(updateStatement))).boxed().collect(Collectors.toList()));
    });
    return results.stream().mapToInt(i -> i.intValue()).toArray();
  }

  private Object[] identifiers(final Object[] objects) {
    final Object[] identifiers = new Object[this.numberOfIdentifiers];
    System.arraycopy(objects, 0, identifiers, 0, this.numberOfIdentifiers);
    return identifiers;
  }

  private Object[] values(final Object[] objects) {
    @SuppressWarnings("hiding")
    final Object[] values = new Object[objects.length - this.numberOfIdentifiers];
    System.arraycopy(objects, this.numberOfIdentifiers, values, 0, objects.length - this.numberOfIdentifiers);
    return values;
  }

  private Object[] concat(final Object[] objects, final Object[] others) {
    @SuppressWarnings("hiding")
    final Object[] values = new Object[objects.length + others.length];
    System.arraycopy(objects, 0, values, 0, objects.length);
    System.arraycopy(others, 0, values, objects.length, others.length);
    return values;
  }

}
