/*
 * #%L
 * anwiba commons
 * %%
 * Copyright (C) 2007 - 2019 Andreas W. Bartels
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
package net.anwiba.database.oracle.utilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;

import net.anwiba.commons.ensure.Ensure;
import net.anwiba.commons.jdbc.DatabaseUtilities;
import net.anwiba.commons.jdbc.name.DatabaseColumnName;
import net.anwiba.commons.jdbc.name.DatabaseSequenceName;
import net.anwiba.commons.jdbc.name.DatabaseTableName;
import net.anwiba.commons.jdbc.name.DatabaseTriggerName;
import net.anwiba.commons.jdbc.name.IDatabaseColumnName;
import net.anwiba.commons.jdbc.name.IDatabaseSequenceName;
import net.anwiba.commons.jdbc.name.IDatabaseTableName;
import net.anwiba.commons.jdbc.name.IDatabaseTriggerName;
import net.anwiba.commons.logging.ILevel;
import net.anwiba.commons.logging.ILogger;
import net.anwiba.commons.logging.Logging;

public class OracleSequenceUtilities {

  private final static long DEFAULT_SEQUENCE_START_VALUE = 1L;
  private final static ILogger logger = Logging.getLogger(OracleSequenceUtilities.class);

  @SuppressWarnings("nls")
  public static String getSequenceName(final Connection connection, final String schemaName, final String tableName)
      throws SQLException {
    Ensure.ensureArgumentNotNull(connection);
    Ensure.ensureArgumentNotNull(tableName);
    final String sqlStatement =
        "select SEQUENCE_NAME from ALL_SEQUENCES where SEQUENCE_OWNER = ? AND (SEQUENCE_NAME = 'SEQ_"
            + tableName
            + "' OR SEQUENCE_NAME = '"
            + tableName
            + "_SEQ')";
    return DatabaseUtilities.stringResult(
        connection,
        sqlStatement,
        value -> value.setObject(1, OracleUtilities.getSchemaName(connection, schemaName).toUpperCase()));
  }

  public static boolean hasSequenceForTable(final Connection connection, final String ownerName, final String tableName)
      throws SQLException {
    return (getSequenceName(connection, ownerName, tableName) != null);
  }

  public static void dropSequenceForTable(final Connection connection, final String schemaName, final String tableName)
      throws SQLException {
    final IDatabaseSequenceName sequenceName = createSequenceName(connection, schemaName, tableName);
    final String dropSequence = "DROP SEQUENCE  " + sequenceName.getName(); //$NON-NLS-1$
    DatabaseUtilities.execute(connection, dropSequence);
  }

  public static void createSequenceForTable(final Connection connection, final String tableName) throws SQLException {
    final IDatabaseSequenceName sequenceName = createSequenceName(connection, null, tableName);
    createSequence(connection, sequenceName, DEFAULT_SEQUENCE_START_VALUE);
  }

  public static void createCyclingSequence(
      final Connection connection,
      final String schemaName,
      final String sequenceName,
      final long maximum) throws SQLException {
    createCyclingSequence(
        connection,
        new DatabaseTableName(OracleUtilities.getSchemaName(connection, schemaName), sequenceName),
        maximum);
  }

  public static void createSequence(final Connection connection, final String schemaName, final String sequenceName)
      throws SQLException {
    createSequence(
        connection,
        new DatabaseSequenceName(OracleUtilities.getSchemaName(connection, schemaName), sequenceName),
        DEFAULT_SEQUENCE_START_VALUE);
  }

  public static void createSequence(
      final Connection connection,
      final String schemaName,
      final String sequenceName,
      final long startWith) throws SQLException {
    createSequence(
        connection,
        new DatabaseSequenceName(OracleUtilities.getSchemaName(connection, schemaName), sequenceName),
        startWith);
  }

  public static void createSequence(
      final Connection connection,
      final IDatabaseSequenceName sequenceName,
      final long startWith) throws SQLException {
    try {
      final String createSequence = //
          MessageFormat.format(
              "CREATE SEQUENCE  {0} MINVALUE 0 MAXVALUE 999999999999999999999999999 INCREMENT BY 1 START WITH {1} CACHE 20 NOORDER  NOCYCLE", //$NON-NLS-1$
              sequenceName.getName(),
              Long.toString(startWith));
      DatabaseUtilities.execute(connection, createSequence);
    } catch (final SQLException exception) {
      throw new SQLException(
          MessageFormat.format("creating sequence ''{0}'' failed ", sequenceName.getName()), //$NON-NLS-1$
          exception);
    }
  }

  public static void createCyclingSequence(
      final Connection connection,
      final IDatabaseTableName sequenceName,
      final long maximum) throws SQLException {
    try {
      final String createSequence = //
          MessageFormat.format(
              "CREATE SEQUENCE  {0} MINVALUE 0 MAXVALUE {1} INCREMENT BY 1 START WITH 0 CACHE 20 NOORDER CYCLE", //$NON-NLS-1$
              sequenceName.getName(),
              String.valueOf(maximum));
      DatabaseUtilities.execute(connection, createSequence);
    } catch (final SQLException exception) {
      throw new SQLException(
          MessageFormat.format("creating sequence ''{0}'' failed ", sequenceName.getName()), //$NON-NLS-1$
          exception);
    }
  }

  public static IDatabaseSequenceName createSequenceName(
      final Connection connection,
      final String schemaName,
      final String tableName) throws SQLException {
    return new DatabaseSequenceName(
        OracleUtilities.getSchemaName(connection, schemaName),
        OracleUtilities.createName(tableName.trim(), "SEQ")); //$NON-NLS-1$
  }

  public static IDatabaseTriggerName createTriggerName(
      final Connection connection,
      final String schemaName,
      final String tableName) throws SQLException {
    return new DatabaseTriggerName(
        OracleUtilities.getSchemaName(connection, schemaName),
        OracleUtilities.createName(tableName.trim(), "TIG")); //$NON-NLS-1$
  }

  public static void resetSequence(final Connection connection, final String schemaName, final String tableName)
      throws SQLException {
    final IDatabaseSequenceName sequenceName = createSequenceName(connection, schemaName, tableName);
    DatabaseUtilities
        .call(connection, OracleUtilitiesStatementString.CallableResetSequenceStatement, sequenceName.getName());
  }

  public static void adjustSequence(
      final Connection connection,
      final String schemaName,
      final String tableName,
      final String columnName) throws SQLException {
    final IDatabaseSequenceName sequenceName = createSequenceName(connection, schemaName, tableName);
    DatabaseUtilities.call(
        connection,
        OracleUtilitiesStatementString.CallableAdjustSequenceStatement,
        OracleUtilities.getSchemaName(connection, schemaName),
        tableName,
        columnName,
        sequenceName.getSchemaName(),
        sequenceName.getSequenceName());
  }

  public static void adjustSequence(
      final Connection connection,
      final String schemaName,
      final String sequenceName,
      final String tableName,
      final String columnName) throws SQLException {
    DatabaseUtilities.call(
        connection,
        OracleUtilitiesStatementString.CallableAdjustSequenceStatement,
        OracleUtilities.getSchemaName(connection, schemaName),
        tableName,
        columnName,
        OracleUtilities.getSchemaName(connection, schemaName),
        sequenceName);
  }

  public static void createSequenceTrigger(final Connection connection, final String tableName, final String columnName)
      throws SQLException {
    final IDatabaseSequenceName sequenceName = createSequenceName(connection, null, tableName);
    final IDatabaseTriggerName triggerName = createTriggerName(connection, null, tableName);
    createSequenceTrigger(
        connection,
        new DatabaseColumnName(
            new DatabaseTableName(OracleUtilities.getSchemaName(connection, null), tableName.trim()),
            columnName),
        triggerName,
        sequenceName);
  }

  public static void createSequenceTrigger(
      final Connection connection,
      final String schemaName,
      final String tableName,
      final String triggerName,
      final String sequenceName,
      final String columnName) throws SQLException {
    final String schema = OracleUtilities.getSchemaName(connection, schemaName);
    createSequenceTrigger(
        connection,
        new DatabaseColumnName(
            new DatabaseTableName(schema, tableName),
            columnName),
        new DatabaseTriggerName(schema, triggerName),
        new DatabaseSequenceName(schema, sequenceName));
  }

  public static void createSequenceTrigger(
      final Connection connection,
      final IDatabaseColumnName columnName,
      final IDatabaseTriggerName triggerName,
      final IDatabaseSequenceName sequenceName) throws SQLException {
    final String createTrigger = //
        MessageFormat.format(
            OracleUtilitiesStatementString.CallableCreateSequenceTrigger,
            triggerName.getName(), //0
            columnName.getDatabaseTable().getName(), // 1
            columnName.getColumnName(), // 2
            sequenceName.getName()); // 3
    DatabaseUtilities.create(connection, createTrigger);
  }

  public static long getNextIdValue(
      final Connection connection,
      final String schemaName,
      final String tableName,
      final String columnName,
      final String sequenceName) throws SQLException {
    final String qualifiedTableName = schemaName + "." + tableName; //$NON-NLS-1$
    if (sequenceName == null) {
      final String statementString = "SELECT MAX(" + columnName + ") FROM " + qualifiedTableName; //$NON-NLS-1$ //$NON-NLS-2$
      logger.log(ILevel.DEBUG, statementString);
      try (PreparedStatement statement = connection.prepareStatement(statementString)) {
        try (ResultSet resultSet = statement.executeQuery()) {
          if (!resultSet.next()) {
            throw new RuntimeException("Can't builde next id value for table " + qualifiedTableName); //$NON-NLS-1$
          }
          final long nextIdValue = resultSet.getLong(1);
          if (resultSet.wasNull()) {
            return 0;
          }
          return nextIdValue + 1;
        }
      }
    }
    try {
      return getNextIdValue(connection, sequenceName);
    } catch (final SQLException exception) {
      throw new SQLException("Can't builde next id value for table " + qualifiedTableName, exception); //$NON-NLS-1$
    }
  }

  public static long getNextIdValue(final Connection connection, final String sequenceName) throws SQLException {
    final String schemaName = OracleUtilities.getSchemaName(connection, null);
    return next(connection, schemaName, sequenceName);
  }

  public static long next(final Connection connection, final String schemaName, final String sequenceName)
      throws SQLException {
    final String statementString = MessageFormat.format("select {0}.{1}.NEXTVAL FROM DUAL", schemaName, sequenceName); //$NON-NLS-1$
    return DatabaseUtilities.next(connection, statementString);
  }

  public static boolean existsSequence(final Connection connection, final String schemaName, final String sequenceName)
      throws SQLException {
    final String statementString = //
        "           SELECT count(*)" //$NON-NLS-1$
            + "       FROM all_sequences" //$NON-NLS-1$
            + "      WHERE sequence_owner =  ?" //$NON-NLS-1$
            + "        AND sequence_name = ?"; //$NON-NLS-1$
    return DatabaseUtilities.booleanResult(connection, statementString, schemaName, sequenceName);
  }

  public static boolean existsSequence(final Connection connection, final IDatabaseSequenceName sequenceName)
      throws SQLException {
    return existsSequence(connection, sequenceName.getSchemaName(), sequenceName.getSequenceName());
  }

  public static void dropSequence(final Connection connection, final IDatabaseSequenceName sequenceName)
      throws SQLException {
    dropSequence(connection, sequenceName.getSchemaName(), sequenceName.getSequenceName());
  }

  public static void dropSequence(final Connection connection, final String schemaName, final String sequenceName)
      throws SQLException {
    final String statementString = //
        "           DROP SEQUENCE " //$NON-NLS-1$
            + OracleUtilities.getSchemaName(connection, schemaName)
            + "." //$NON-NLS-1$
            + sequenceName;
    DatabaseUtilities.execute(connection, statementString);
  }

  public static void reset(final Connection connection, final String schemaName, final String identifierSequenceName)
      throws SQLException {
    if (existsSequence(connection, schemaName, identifierSequenceName)) {
      dropSequence(connection, schemaName, identifierSequenceName);
    }
    createSequence(connection, schemaName, identifierSequenceName);
  }

}
