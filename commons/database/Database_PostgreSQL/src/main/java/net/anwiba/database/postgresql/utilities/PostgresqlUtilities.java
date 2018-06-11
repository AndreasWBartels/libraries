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
package net.anwiba.database.postgresql.utilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;

import net.anwiba.commons.jdbc.DatabaseUtilities;
import net.anwiba.commons.jdbc.name.DatabaseIndexName;
import net.anwiba.commons.jdbc.name.IDatabaseIndexName;
import net.anwiba.commons.logging.ILogger;
import net.anwiba.commons.logging.Logging;

public class PostgresqlUtilities {

  private static ILogger logger = Logging.getLogger(PostgresqlUtilities.class.getName());

  public static void makeVacuumAnalyze(
      final Connection connection,
      final String schemaName,
      final String tableName,
      final String columnName) throws SQLException {
    Statement statement = null;
    try {
      statement = connection.createStatement();
      final String activateVacumAnalyze = "VACUUM ANALYZE " + schemaName + "." + tableName + " ( " + columnName + " )"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
      logger.log(Level.FINE, "Statement: " + activateVacumAnalyze); //$NON-NLS-1$
      statement.execute(activateVacumAnalyze);
    } finally {
      DatabaseUtilities.close(statement);
    }
  }

  public static IDatabaseIndexName getIndexName(
      final Connection connection,
      final String schemaName,
      final String tableName,
      final String columnName) throws SQLException {
    logger.log(Level.FINE, "Query: Schema " + schemaName + " table " + tableName + " column " + columnName); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    logger.log(Level.FINE, "Query: " + PostgresqlUtilitiesStatementStrings.IndexNameStatement); //$NON-NLS-1$
    try (PreparedStatement statement = connection
        .prepareStatement(PostgresqlUtilitiesStatementStrings.IndexNameStatement);) {
      statement.setString(1, schemaName);
      statement.setString(2, tableName);
      statement.setString(3, columnName);
      try {
        if (!statement.execute()) {
          return null;
        }
      } catch (final Exception exception) {
        logger.log(Level.WARNING, "Query faild: " + PostgresqlUtilitiesStatementStrings.IndexNameStatement, exception); //$NON-NLS-1$
        return null;
      }
      try (ResultSet resultSet = statement.getResultSet();) {
        if (resultSet.next()) {
          final String schema = resultSet.getString(1);
          final String index = resultSet.getString(2);
          return new DatabaseIndexName(schema, index);
        }
        return null;
      }
    }
  }

  public static boolean isIndexed(
      final Connection connection,
      final String schemaName,
      final String tableName,
      final String columnName) throws SQLException {
    logger.log(Level.FINE, "Query: Schema " + schemaName + " table " + tableName + " column " + columnName); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    logger.log(Level.FINE, "Query: " + PostgresqlUtilitiesStatementStrings.IsIndexedStatement); //$NON-NLS-1$
    try (PreparedStatement statement = connection
        .prepareStatement(PostgresqlUtilitiesStatementStrings.IsIndexedStatement)) {
      statement.setString(1, schemaName);
      statement.setString(2, tableName);
      statement.setString(3, columnName);
      try {
        if (!statement.execute()) {
          return false;
        }
      } catch (final Exception exception) {
        logger.log(Level.WARNING, "Query faild: " + PostgresqlUtilitiesStatementStrings.IsIndexedStatement, exception); //$NON-NLS-1$
        return false;
      }
      try (ResultSet resultSet = statement.getResultSet()) {
        if (resultSet.next()) {
          return resultSet.getInt(1) != 0;
        }
        return false;
      }
    }
  }
}
