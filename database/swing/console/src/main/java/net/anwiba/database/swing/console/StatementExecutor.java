/*
 * #%L
 *
 * %%
 * Copyright (C) 2007 - 2017 Andreas W. Bartels
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
package net.anwiba.database.swing.console;

import net.anwiba.commons.jdbc.DatabaseUtilities;
import net.anwiba.commons.lang.functional.IObserver;
import net.anwiba.commons.logging.ILevel;
import net.anwiba.commons.model.IBooleanModel;
import net.anwiba.commons.model.IObjectListModel;
import net.anwiba.commons.model.IObjectModel;
import net.anwiba.commons.thread.cancel.ICanceler;
import net.anwiba.commons.utilities.string.StringUtilities;
import net.anwiba.database.swing.console.result.ResultReseter;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class StatementExecutor {

  private static net.anwiba.commons.logging.ILogger logger = net.anwiba.commons.logging.Logging
      .getLogger(StatementExecutor.class);
  private final IObjectModel<Connection> connectionModel;
  private final IObjectModel<Statement> statementModel;
  private final IObjectModel<ResultSet> resultSetModel;
  private final IObjectModel<String> statusModel;
  private final Consumer<String> statementHistoryConsumer;
  private final IBooleanModel isDisconnectedModel;
  private final IBooleanModel isConnectedModel;
  private final ResultReseter resultReseter;
  private final IObjectListModel<String> statementValuesModel;

  public StatementExecutor(
      final IObjectModel<Connection> connectionModel,
      final ResultReseter resultReseter,
      final IObjectModel<Statement> statementModel,
      final IObjectListModel<String> statementValuesModel,
      final IObjectModel<ResultSet> resultSetModel,
      final IObjectModel<String> statusModel,
      final Consumer<String> statementHistoryConsumer,
      final IBooleanModel isDisconnectedModel,
      final IBooleanModel isConnectedModel) {
    super();
    this.connectionModel = connectionModel;
    this.resultReseter = resultReseter;
    this.statementModel = statementModel;
    this.statementValuesModel = statementValuesModel;
    this.resultSetModel = resultSetModel;
    this.statusModel = statusModel;
    this.statementHistoryConsumer = statementHistoryConsumer;
    this.isDisconnectedModel = isDisconnectedModel;
    this.isConnectedModel = isConnectedModel;
  }

  public void executeStatement(final ICanceler canceler, final String string) {
    try {
      this.statusModel.set(SqlConsoleMessages.working);
      if (StringUtilities.isNullOrTrimmedEmpty(string)) {
        this.statusModel.set(SqlConsoleMessages.done);
        return;
      }
      logger.log(ILevel.DEBUG, SqlConsoleMessages.executeStatement + string);
      final Connection connection = this.connectionModel.get();
      final boolean isClosed = connection.isClosed();
      if (isClosed) {
        this.isConnectedModel.set(!isClosed);
        this.isDisconnectedModel.set(isClosed);
        this.statusModel.set(SqlConsoleMessages.connectionIsClosed);
        return;
      }
      final PreparedStatement statement = createStatement(connection, clean(string));
      addStatementValuesIfPossible(statement, this.statementValuesModel.toList());
      statement.setFetchSize(100);
      statement.setFetchDirection(ResultSet.FETCH_UNKNOWN);
      try (IObserver observer = canceler.observer(DatabaseUtilities.silent(() -> statement.cancel()))) {
        this.resultReseter.reset();
        if (!statement.execute()) {
          this.statementHistoryConsumer.accept(string);
          this.statusModel.set(SqlConsoleMessages.done);
          statement.close();
          return;
        }
      }
      this.statementModel.set(statement);
      final ResultSet resultSet = statement.getResultSet();
      this.resultSetModel.set(resultSet);
      this.statementHistoryConsumer.accept(string);
      this.statusModel.set(SqlConsoleMessages.done);
    } catch (final SQLException exception) {
      this.resultReseter.reset();
      logger.log(ILevel.DEBUG, exception.getMessage(), exception);
      this.statusModel.set(exception.getMessage());
    }
  }

  private void addStatementValuesIfPossible(final PreparedStatement statement, final List<String> values)
      throws SQLException {
    final ParameterMetaData parameterMetaData = statement.getParameterMetaData();
    if (parameterMetaData.getParameterCount() == 0 || values.size() < parameterMetaData.getParameterCount()) {
      return;
    }
    for (int i = 0; i < values.size(); i++) {
      statement.setObject(i + 1, convertTo(values.get(i), getClassName(parameterMetaData, i)));
    }
  }

  private String getClassName(final ParameterMetaData parameterMetaData, final int i) throws SQLException {
    try {
      return parameterMetaData.getParameterClassName(i + 1);
    } catch (SQLException exception) {
      logger.warning(exception.getMessage());
      return "java.lang.String";
    }
  }

  private Object convertTo(final String string, final String parameterClassName)
      throws SQLException {
    if (string == null || string.isEmpty()) {
      return null;
    }
    try {
      return switch (parameterClassName) {
        case "java.lang.Short" -> Short.valueOf(string);
        case "java.lang.Integer" -> Integer.valueOf(string);
        case "java.lang.Long" -> Long.valueOf(string);
        case "java.lang.Float" -> Float.valueOf(string);
        case "java.lang.Double" -> Double.valueOf(string);
        case "java.lang.BigDecimal" -> new BigDecimal(string);
        case "java.lang.Boolean" -> Boolean.valueOf(string);
        case "java.sql.Time" -> java.sql.Time.valueOf(string);
        case "java.sql.Date" -> java.sql.Date.valueOf(string);
        case "java.sql.Timestamp" -> java.sql.Timestamp.valueOf(string);
        default -> string;
      };
    } catch (IllegalArgumentException exception) {
      throw new SQLException("converting value '" + string + "' failed, " + exception.getMessage(), exception);
    }
  }

  private String clean(final String string) {
    return Optional
        .ofNullable(string)
        .map(s -> s.trim())
        .map(t -> t.endsWith(";") ? t.substring(0, t.length() - 1) : t) //$NON-NLS-1$
        .orElse(string);
  }

  private PreparedStatement createStatement(final Connection connection, final String string) throws SQLException {
    final DatabaseMetaData metaData = connection.getMetaData();
    if (metaData.supportsResultSetConcurrency(
        ResultSet.TYPE_SCROLL_INSENSITIVE,
        ResultSet.CONCUR_READ_ONLY)) {
      return connection.prepareStatement(string, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
    }
    if (metaData.supportsResultSetConcurrency(
        ResultSet.TYPE_SCROLL_SENSITIVE,
        ResultSet.CONCUR_READ_ONLY)) {
      return connection.prepareStatement(string, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
    }
    return connection.prepareStatement(string);
  }

}
