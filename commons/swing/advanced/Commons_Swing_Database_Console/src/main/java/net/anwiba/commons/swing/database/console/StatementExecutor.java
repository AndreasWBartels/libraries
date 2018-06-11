/*
 * #%L
 * *
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
package net.anwiba.commons.swing.database.console;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

import javax.swing.DefaultComboBoxModel;

import net.anwiba.commons.lang.object.ObjectUtilities;
import net.anwiba.commons.logging.ILevel;
import net.anwiba.commons.model.IBooleanModel;
import net.anwiba.commons.model.IObjectModel;
import net.anwiba.commons.swing.database.console.result.ResultReseter;
import net.anwiba.commons.swing.utilities.GuiUtilities;
import net.anwiba.commons.utilities.string.StringUtilities;

public class StatementExecutor {

  private static net.anwiba.commons.logging.ILogger logger = net.anwiba.commons.logging.Logging
      .getLogger(StatementExecutor.class);
  private final IObjectModel<Connection> connectionModel;
  private final IObjectModel<Statement> statementModel;
  private final IObjectModel<ResultSet> resultSetModel;
  private final IObjectModel<String> statusModel;
  private final DefaultComboBoxModel<String> historyComboBoxModel;
  private final IBooleanModel isDisconnectedModel;
  private final IBooleanModel isConnectedModel;
  private final ResultReseter resultReseter;

  public StatementExecutor(
      final IObjectModel<Connection> connectionModel,
      final ResultReseter resultReseter,
      final IObjectModel<Statement> statementModel,
      final IObjectModel<ResultSet> resultSetModel,
      final IObjectModel<String> statusModel,
      final DefaultComboBoxModel<String> historyComboBoxModel,
      final IBooleanModel isDisconnectedModel,
      final IBooleanModel isConnectedModel) {
    super();
    this.connectionModel = connectionModel;
    this.resultReseter = resultReseter;
    this.statementModel = statementModel;
    this.resultSetModel = resultSetModel;
    this.statusModel = statusModel;
    this.historyComboBoxModel = historyComboBoxModel;
    this.isDisconnectedModel = isDisconnectedModel;
    this.isConnectedModel = isConnectedModel;
  }

  public void executeStatement(final String string) {
    try {
      this.statusModel.set(SqlConsoleMessages.working);
      if (StringUtilities.isNullOrTrimmedEmpty(string)) {
        this.statusModel.set(SqlConsoleMessages.done);
        return;
      }
      logger.log(ILevel.DEBUG, SqlConsoleMessages.executeStatement + string);
      final boolean isClosed = this.connectionModel.get().isClosed();
      if (isClosed) {
        this.isConnectedModel.set(!isClosed);
        this.isDisconnectedModel.set(isClosed);
        this.statusModel.set(SqlConsoleMessages.connectionIsClosed);
        return;
      }
      @SuppressWarnings("resource")
      final Statement statement = createStatement();
      if (!statement.execute(clean(string))) {
        updateHistoryComboBoyModel(string);
        this.statusModel.set(SqlConsoleMessages.done);
        statement.close();
        return;
      }
      this.resultReseter.reset();
      this.statementModel.set(statement);
      @SuppressWarnings("resource")
      final ResultSet resultSet = statement.getResultSet();
      this.resultSetModel.set(resultSet);
      updateHistoryComboBoyModel(string);
      this.statusModel.set(SqlConsoleMessages.done);
    } catch (final SQLException exception) {
      logger.log(ILevel.DEBUG, exception.getMessage(), exception);
      this.statusModel.set(exception.getMessage());
    }
  }

  private void updateHistoryComboBoyModel(final String string) {
    for (int i = this.historyComboBoxModel.getSize() - 1; i > -1; i--) {
      if (ObjectUtilities.equals(this.historyComboBoxModel.getElementAt(i), string)) {
        return;
      }
    }
    GuiUtilities.invokeLater(() -> {
      if (this.historyComboBoxModel.getSize() > 30) {
        this.historyComboBoxModel.removeElementAt(0);
      }
      this.historyComboBoxModel.addElement(string);
    });
  }

  private String clean(final String string) {
    return Optional
        .ofNullable(string)
        .map(s -> s.trim())
        .map(t -> t.endsWith(";") ? t.substring(0, t.length() - 1) : t) //$NON-NLS-1$
        .orElse(string);
  }

  private Statement createStatement() throws SQLException {
    if (this.connectionModel.get().getMetaData().supportsResultSetConcurrency(
        ResultSet.TYPE_SCROLL_INSENSITIVE,
        ResultSet.CONCUR_READ_ONLY)) {
      return this.connectionModel.get().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
    }
    if (this.connectionModel.get().getMetaData().supportsResultSetConcurrency(
        ResultSet.TYPE_SCROLL_SENSITIVE,
        ResultSet.CONCUR_READ_ONLY)) {
      return this.connectionModel.get().createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
    }
    return this.connectionModel.get().createStatement();
  }

}
