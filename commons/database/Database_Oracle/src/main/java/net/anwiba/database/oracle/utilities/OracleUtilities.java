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

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

import net.anwiba.commons.jdbc.DatabaseUtilities;
import net.anwiba.commons.jdbc.name.DatabaseNamesConverter;
import net.anwiba.commons.jdbc.name.DatabaseTableName;
import net.anwiba.commons.jdbc.name.IDatabaseTableName;
import net.anwiba.commons.lang.functional.IProcedure;
import net.anwiba.commons.logging.ILevel;
import net.anwiba.commons.logging.ILogger;
import net.anwiba.commons.logging.Logging;

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
      final String columnName) throws SQLException {
    logger.log(Level.FINE, "Query: Schema " + schemaName + " table " + tableName + " column " + columnName); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    logger.log(Level.FINE, "Query: " + OracleUtilitiesStatementString.IndexNameStatement); //$NON-NLS-1$
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
        logger.log(Level.WARNING, "Query faild: " + OracleUtilitiesStatementString.IndexNameStatement, exception); //$NON-NLS-1$
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
    logger.log(ILevel.DEBUG, "execute SQL OWNER: " + DatabaseUtilities.getSchemaName(connection, schemaName)); //$NON-NLS-1$
    logger.log(ILevel.DEBUG, "execute SQL TABLE_NAME: " + tableName); //$NON-NLS-1$
    final IProcedure<PreparedStatement, SQLException> preparedStatementClosure = DatabaseUtilities
        .setterProcedur(
            DatabaseUtilities.getSchemaName(connection, schemaName).toUpperCase(),
            tableName);
    return DatabaseUtilities.execute(connection, statementString, preparedStatementClosure);
  }

}
