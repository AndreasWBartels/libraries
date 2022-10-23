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

import net.anwiba.commons.jdbc.DatabaseUtilities;
import net.anwiba.commons.jdbc.constraint.Constraint;
import net.anwiba.commons.jdbc.constraint.ConstraintType;
import net.anwiba.commons.jdbc.constraint.ConstraintsUtilities;
import net.anwiba.commons.jdbc.metadata.ColumnMetaData;
import net.anwiba.commons.jdbc.metadata.IColumnMetaData;
import net.anwiba.commons.jdbc.metadata.ITableMetaData;
import net.anwiba.commons.jdbc.metadata.TableMetaData;
import net.anwiba.commons.jdbc.name.DatabaseIndexName;
import net.anwiba.commons.jdbc.name.IDatabaseIndexName;
import net.anwiba.commons.jdbc.result.IResult;
import net.anwiba.commons.jdbc.type.TableType;
import net.anwiba.commons.lang.exception.UnreachableCodeReachedException;
import net.anwiba.commons.lang.functional.IConverter;
import net.anwiba.commons.lang.optional.IOptional;
import net.anwiba.commons.logging.ILevel;
import net.anwiba.commons.logging.ILogger;
import net.anwiba.commons.logging.Logging;
import net.anwiba.database.postgresql.PostgresqlTableType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class PostgresqlUtilities {

  private static ILogger logger = Logging.getLogger(PostgresqlUtilities.class.getName());

  public static void makeVacuumAnalyze(final Connection connection, final String schemaName, final String tableName)
      throws SQLException {
    try (Statement statement = connection.createStatement()) {
      final String activateVacumAnalyze = "VACUUM ANALYZE \"" + schemaName + "\".\"" + tableName + "\""; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
      logger.log(ILevel.FINE, "Statement: " + activateVacumAnalyze); //$NON-NLS-1$
      statement.execute(activateVacumAnalyze);
    }
  }

  public static void makeVacuumAnalyze(
      final Connection connection,
      final String schemaName,
      final String tableName,
      final String columnName)
      throws SQLException {
    try (Statement statement = connection.createStatement()) {
      final String activateVacumAnalyze =
          "VACUUM ANALYZE \"" + schemaName + "\".\"" + tableName + "\" ( \"" + columnName + "\" )"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
      logger.log(ILevel.FINE, "Statement: " + activateVacumAnalyze); //$NON-NLS-1$
      statement.execute(activateVacumAnalyze);
    }
  }

  public static IDatabaseIndexName getIndexName(
      final Connection connection,
      final String schemaName,
      final String tableName,
      final String columnName)
      throws SQLException {
    logger.log(ILevel.FINE, "Query: Schema " + schemaName + " table " + tableName + " column " + columnName); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    logger.log(ILevel.FINE, "Query: " + PostgresqlUtilitiesStatementStrings.IndexNameStatement); //$NON-NLS-1$
    try (PreparedStatement statement =
        connection.prepareStatement(PostgresqlUtilitiesStatementStrings.IndexNameStatement);) {
      statement.setString(1, schemaName);
      statement.setString(2, tableName);
      statement.setString(3, columnName);
      try {
        if (!statement.execute()) {
          return null;
        }
      } catch (final Exception exception) {
        logger.log(ILevel.WARNING, "Query faild: " + PostgresqlUtilitiesStatementStrings.IndexNameStatement, exception); //$NON-NLS-1$
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
      final String columnName)
      throws SQLException {
    logger.log(ILevel.FINE, "Query: Schema " + schemaName + " table " + tableName + " column " + columnName); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    logger.log(ILevel.FINE, "Query: " + PostgresqlUtilitiesStatementStrings.IsIndexedStatement); //$NON-NLS-1$
    try (PreparedStatement statement =
        connection.prepareStatement(PostgresqlUtilitiesStatementStrings.IsIndexedStatement)) {
      statement.setString(1, schemaName);
      statement.setString(2, tableName);
      statement.setString(3, columnName);
      try {
        if (!statement.execute()) {
          return false;
        }
      } catch (final Exception exception) {
        logger.log(ILevel.WARNING, "Query faild: " + PostgresqlUtilitiesStatementStrings.IsIndexedStatement, exception); //$NON-NLS-1$
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

  public static void cluster(
      final Connection connection,
      final String schemaName,
      final String tableName,
      final String indexName)
      throws SQLException {
    try (Statement statement = connection.createStatement();) {
      final String activateVacumAnalyze = "CLUSTER \"" + schemaName + "\".\"" + tableName + "\" table_name USING \"" //$NON-NLS-1$ //$NON-NLS-2$
          + schemaName + "\".\"" + indexName + "\"";
      logger.log(ILevel.FINE, "Statement: " + activateVacumAnalyze); //$NON-NLS-1$
      statement.execute(activateVacumAnalyze);
    }
  }

  public static String createStatement(
      final Connection connection,
      final String schemaName,
      final String tableName)
      throws SQLException {
    String statementString = "SELECT 'CREATE TABLE ' || pn.nspname || '.' || pc.relname "
        + "   || E'(\\n' ||\n   string_agg(pa.attname || ' ' || pg_catalog.format_type(pa.atttypid, pa.atttypmod) "
        + "   || coalesce(' DEFAULT ' "
        + "               || (\n     SELECT pg_catalog.pg_get_expr(d.adbin, d.adrelid)\n"
        + "                            FROM pg_catalog.pg_attrdef d\n"
        + "                           WHERE d.adrelid = pa.attrelid\n"
        + "                             AND d.adnum = pa.attnum\n"
        + "                             AND pa.atthasdef\n"
        + "                 ),\n"
        + "   '') || ' ' ||\n"
        + "              CASE pa.attnotnull\n"
        + "                  WHEN TRUE THEN 'NOT NULL'\n"
        + "                  ELSE 'NULL'\n"
        + "              END, E',\\n') ||\n"
        + "   coalesce((SELECT E',\\n' || string_agg('CONSTRAINT ' || pc1.conname || ' ' || pg_get_constraintdef(pc1.oid), E',\\n' ORDER BY pc1.conindid)\n"
        + "            FROM pg_constraint pc1\n"
        + "            WHERE pc1.conrelid = pa.attrelid), '') ||\n"
        + "   E');'\n"
        + "FROM pg_catalog.pg_attribute pa\n"
        + "JOIN pg_catalog.pg_class pc\n"
        + "    ON pc.oid = pa.attrelid\n"
        + "    AND pc.relname = ?\n"
        + "JOIN pg_catalog.pg_namespace pn\n"
        + "    ON pn.oid = pc.relnamespace\n"
        + "    AND pn.nspname = ?\n"
        + "WHERE pa.attnum > 0\n"
        + "    AND NOT pa.attisdropped\n"
        + "GROUP BY pn.nspname, pc.relname, pa.attrelid;";
    logger.log(ILevel.FINE, "Query: Schema " + schemaName + " table " + tableName); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    logger.log(ILevel.FINE, "Query: " + statementString); //$NON-NLS-1$
    try (PreparedStatement statement =
        connection.prepareStatement(statementString)) {
      statement.setString(2, schemaName);
      statement.setString(1, tableName);
      try {
        if (!statement.execute()) {
          return null;
        }
      } catch (final Exception exception) {
        logger.log(ILevel.WARNING, "Query faild: " + statementString, exception); //$NON-NLS-1$
        return null;
      }
      try (ResultSet resultSet = statement.getResultSet()) {
        if (resultSet.next()) {
          return resultSet.getString(1);
        }
        return null;
      }
    }
  }

  public static List<IColumnMetaData> readColumnMetaData(
      final Connection connection,
      final String schemaName,
      final String tableName,
      final Map<String, Constraint> constraints)
      throws SQLException {
    final List<IColumnMetaData> columnMetaDatas = new ArrayList<>();
    logger.log(ILevel.FINE, "Query: Schema " + schemaName + " table " + tableName); //$NON-NLS-1$ //$NON-NLS-2$
    logger.log(ILevel.FINE, "Query: " + PostgresqlUtilitiesStatementStrings.ColumnMetaDataStatement); //$NON-NLS-1$
    try (PreparedStatement statement = connection
        .prepareStatement(PostgresqlUtilitiesStatementStrings.ColumnMetaDataStatement)) {
      statement.setString(1, schemaName);
      statement.setString(2, tableName);
      if (statement.execute()) {
        try (ResultSet resultSet = statement.getResultSet()) {
          while (resultSet.next()) {
            final String columnName = resultSet.getString(1);
            final String typeName = resultSet.getString(2);
            final int length = resultSet.getInt(3);
            final int scale = resultSet.getInt(4);
            final boolean isNullable = resultSet.getString(5).equals("t"); //$NON-NLS-1$
            final boolean isPrimaryKey = ConstraintsUtilities.isPrimaryKey(constraints, columnName);
            final boolean isAutoIncrement = resultSet.getString(6).equals("t");
            final IColumnMetaData metaData = new ColumnMetaData(
                schemaName,
                tableName,
                columnName,
                typeName,
                length,
                scale,
                isPrimaryKey,
                isAutoIncrement,
                isNullable);
            columnMetaDatas.add(metaData);
          }
        }
      }
      return columnMetaDatas;
    }
  }

  public static Map<String, Constraint> readConstraints(
      final Connection connection,
      final String schemaName,
      final String tableName)
      throws SQLException {
    TableType tableType = readTableType(connection, schemaName, tableName);
    if (Objects.equals(TableType.MATERIALIZED_VIEW, tableType)) {
      return readConstraintsByMatrializedViewIndexes(connection, schemaName, tableName);
    }
    return DatabaseUtilities
        .readConstraints(
            connection,
            PostgresqlUtilitiesStatementStrings.ConstraintTypesStatement,
            schemaName,
            tableName);
  }

  private static Map<String, Constraint>
      readConstraintsByMatrializedViewIndexes(final Connection connection,
          final String schemaName,
          final String tableName) throws SQLException {
    final Map<String, Constraint> constraints = new HashMap<>();
    DatabaseUtilities
        .results(connection,
            "select t.relname as table_name,\n" +
                "       i.relname as index_name,\n" +
                "       a.attname as column_name,\n" +
                "       ix.indisunique as is_unique\n" +
                "  from pg_namespace s,\n" +
                "       pg_class t,\n" +
                "       pg_class i,\n" +
                "       pg_index ix,\n" +
                "       pg_attribute a\n" +
                " where t.oid = ix.indrelid\n" +
                "   and i.oid = ix.indexrelid\n" +
                "   and a.attrelid = t.oid\n" +
                "   and a.attnum = ANY(ix.indkey)\n" +
                "   and t.relkind in ('m', 'r')\n" +
                "   and s.nspname = ?\n" +
                "   and t.relname = ?\n" +
                "   and s.oid = t.relnamespace\n" +
                "order by t.relname, i.relname",
            DatabaseUtilities.setter(schemaName, tableName),
            (IConverter<IResult, Void, SQLException>) result -> {
              if (result.getBoolean(4, false)) {
                final Constraint constraint = DatabaseUtilities.getConstraint(
                    constraints,
                    result.getString(2),
                    ConstraintType.UNIQUE,
                    null);
                constraint.add(result.getString(3));
              }
              return null;
            });
    return constraints;
  }

  public static ITableMetaData readTableMetaData(
      final Connection connection,
      final String schemaName,
      final String tableName)
      throws SQLException {
    final Map<String, Constraint> constraints = readConstraints(
        connection,
        schemaName,
        tableName);
    final List<IColumnMetaData> columnMetadata = readColumnMetaData(
        connection,
        schemaName,
        tableName,
        constraints);
    return new TableMetaData(columnMetadata, constraints);
  }

  private static TableType readTableType(
      final Connection connection,
      final String schemaName,
      final String tableName)
      throws SQLException {
    return DatabaseUtilities
        .result(connection,
            "select case when t.relkind = 'r' then 'BASE TABLE'\n" +
                "                when t.relkind = 'v' then 'VIEW'\n" +
                "                when t.relkind = 'm' then 'MATERIALIZED VIEW'\n" +
                "                else 'UNKNOWN'\n" +
                "           end as table_type\n" +
                "      from pg_namespace s,\n" +
                "           pg_class t\n" +
                "      where s.nspname = ?\n" +
                "        and t.relname = ?\n" +
                "        and s.oid = t.relnamespace",
            DatabaseUtilities.setter(schemaName, tableName),
            (IConverter<IOptional<IResult, SQLException>, TableType,
                SQLException>) optional -> optional.convert(result -> {
                  return getTableType(result.getString(1));
                }).getOr(() -> TableType.UNKNOWN));
  }

  public static TableType getTableType(final String postgresqlTableTypeName) {
    if (postgresqlTableTypeName == null) {
      throw new UnreachableCodeReachedException();
    }
    return PostgresqlTableType.valueOf(postgresqlTableTypeName.replace(' ', '_')).getTableType();
  }
}