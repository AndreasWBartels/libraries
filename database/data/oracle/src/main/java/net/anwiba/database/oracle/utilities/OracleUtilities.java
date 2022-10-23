/*
 * #%L
 * anwiba commons database
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
package net.anwiba.database.oracle.utilities;

import net.anwiba.commons.jdbc.DatabaseUtilities;
import net.anwiba.commons.jdbc.constraint.Constraint;
import net.anwiba.commons.jdbc.constraint.ConstraintsUtilities;
import net.anwiba.commons.jdbc.metadata.ColumnMetaData;
import net.anwiba.commons.jdbc.metadata.IColumnMetaData;
import net.anwiba.commons.jdbc.metadata.ITableMetaData;
import net.anwiba.commons.jdbc.metadata.TableMetaData;
import net.anwiba.commons.jdbc.name.DatabaseNamesConverter;
import net.anwiba.commons.jdbc.name.DatabaseTableName;
import net.anwiba.commons.jdbc.name.IDatabaseTableName;
import net.anwiba.commons.jdbc.result.IResult;
import net.anwiba.commons.lang.exception.CanceledException;
import net.anwiba.commons.lang.functional.IConverter;
import net.anwiba.commons.lang.functional.IProcedure;
import net.anwiba.commons.logging.ILevel;
import net.anwiba.commons.logging.ILogger;
import net.anwiba.commons.logging.Logging;
import net.anwiba.commons.message.IMessageCollector;
import net.anwiba.commons.message.Message;
import net.anwiba.commons.message.MessageType;
import net.anwiba.commons.thread.cancel.ICanceler;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class OracleUtilities {

  private static ILogger logger = Logging.getLogger(OracleUtilities.class.getName());

  public static void gatherTableStatistic(final Connection connection, final String schemaName, final String tableName)
      throws SQLException {
    CallableStatement statement = null;
    try {
      statement = connection.prepareCall(OracleUtilitiesStatementString.GatherTableStatisticCall);
      statement.setString(1, schemaName);
      statement.setString(2, tableName);
      statement.execute();
    } finally {
      DatabaseUtilities.close(statement);
    }
  }

  public static String createName(final String name, final String suffix) {
    return DatabaseNamesConverter.createUpperCaseFactory(31 - suffix.length()).convert(name) + "_" + suffix; //$NON-NLS-1$
  }

  public static IDatabaseTableName getIndexName(
      final Connection connection,
      final String schemaName,
      final String tableName,
      final String columnName)
      throws SQLException {
    logger.log(ILevel.FINE, "Query: Schema " + schemaName + " table " + tableName + " column " + columnName); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    logger.log(ILevel.FINE, "Query: " + OracleUtilitiesStatementString.IndexNameStatement); //$NON-NLS-1$
    try (
        PreparedStatement statement = connection.prepareStatement(OracleUtilitiesStatementString.IndexNameStatement);) {
      statement.setString(1, schemaName);
      statement.setString(2, tableName);
      statement.setString(3, columnName);
      try {
        if (!statement.execute()) {
          return null;
        }
      } catch (final Exception exception) {
        logger.log(ILevel.WARNING, "Query faild: " + OracleUtilitiesStatementString.IndexNameStatement, exception); //$NON-NLS-1$
        return null;
      }
      try (ResultSet resultSet = statement.getResultSet();) {
        if (resultSet.next()) {
          final String schema = resultSet.getString(1);
          final String index = resultSet.getString(2);
          return new DatabaseTableName(schema, index);
        }
        return null;
      }
    }
  }

  public static boolean existsTable(final Connection connection, final String schemaName, final String tableName)
      throws SQLException {
    final String statementString = OracleUtilitiesStatementString.ExistsTablePreparedStatement;
    logger.log(ILevel.DEBUG, "execute SQL OWNER: " + getSchemaName(connection, schemaName)); //$NON-NLS-1$
    logger.log(ILevel.DEBUG, "execute SQL TABLE_NAME: " + tableName); //$NON-NLS-1$
    final IProcedure<PreparedStatement, SQLException> preparedStatementClosure = DatabaseUtilities
        .setter(getSchemaName(connection, schemaName).toUpperCase(), tableName);
    return DatabaseUtilities.execute(connection, statementString, preparedStatementClosure);
  }

  public static String getSchemaName(final Connection connection, final String schemaName) throws SQLException {
    return (schemaName == null ? connection.getMetaData().getUserName() : schemaName);
  }

  public static ITableMetaData readTableMetaData(
      final IMessageCollector monitor,
      final ICanceler canceler,
      final Connection connection,
      final String schemaName,
      final String tableName)
      throws SQLException,
      CanceledException {
    final Map<String, Constraint> constraints = readConstraintTypes(
        monitor,
        canceler,
        connection,
        schemaName,
        tableName);
    final List<IColumnMetaData> columnMetadata = readColumnMetaData(
        monitor,
        canceler,
        connection,
        schemaName,
        tableName,
        constraints);
    return new TableMetaData(columnMetadata, constraints);
  }

  private static Map<String, Constraint> readConstraintTypes(
      final IMessageCollector monitor,
      final ICanceler canceler,
      final Connection connection,
      final String schemaName,
      final String tableName)
      throws SQLException {
    final String selectStatement = //
        OracleUtilitiesStatementString.ConstraintTypesStatement;
    return DatabaseUtilities.readConstraints(connection, selectStatement, schemaName, tableName);
  }

  public static List<IColumnMetaData> readColumnMetaData(
      final IMessageCollector monitor,
      final ICanceler canceler,
      final Connection connection,
      final String schemaName,
      final String tableName,
      final Map<String, Constraint> constraints)
      throws SQLException,
      CanceledException {
    final String statementString = OracleUtilitiesStatementString.ColumnMetaDataStatement;
    logger.log(ILevel.DEBUG, "Query: Schema " + schemaName + " table " + tableName); //$NON-NLS-1$ //$NON-NLS-2$
    logger.log(ILevel.DEBUG, "Query: " + statementString); //$NON-NLS-1$
    return DatabaseUtilities
        .results(
            canceler.observerFactory(),
            connection,
            statementString,
            DatabaseUtilities.setter(schemaName, tableName),
            (IConverter<IResult, IColumnMetaData, SQLException>) resultSet -> {
              try {
                return createColumnMetaData(monitor,
                    canceler,
                    resultSet,
                    schemaName,
                    tableName,
                    constraints);
              } catch (final Throwable exception) {
                monitor
                    .addMessage(
                        Message.create(
                            "Reading geometry metadata faild", //$NON-NLS-1$
                            null,
                            exception,
                            MessageType.ERROR));
                logger.log(ILevel.SEVERE, exception);
                return null;
              }
            });
  }

  private static IColumnMetaData createColumnMetaData(final IMessageCollector monitor,
      final ICanceler canceler,
      final IResult resultSet,
      final String schemaName,
      final String tableName,
      final Map<String, Constraint> constraints) throws SQLException {
    final String columnName = resultSet.getString(1);
    final String typeName = resultSet.getString(2);
    final int length = resultSet.getInteger(3);
    final int scale = resultSet.getInteger(4);
    final boolean isPrimaryKey = ConstraintsUtilities.isPrimaryKey(constraints, columnName);
    final boolean isNullable = Objects.equals("TRUE", resultSet.getString(5));
    final boolean isAutoIncrement = Objects.equals("TRUE", resultSet.getString(6));
    return new ColumnMetaData(
        schemaName,
        tableName,
        columnName,
        clean(typeName),
        length,
        scale,
        isPrimaryKey,
        isAutoIncrement,
        isNullable);
  }

  private static String clean(final String typeName) {
    final int indexOf = typeName.indexOf('(');
    if (indexOf != -1) {
      return typeName.substring(0, indexOf);
    }
    return typeName;
  }
}
