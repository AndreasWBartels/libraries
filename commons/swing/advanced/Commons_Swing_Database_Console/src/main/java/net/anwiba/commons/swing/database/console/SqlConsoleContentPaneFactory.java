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

import java.awt.Window;
import java.sql.Connection;

import net.anwiba.commons.jdbc.connection.IDatabaseConnector;
import net.anwiba.commons.jdbc.connection.IJdbcConnectionDescription;
import net.anwiba.commons.jdbc.database.IDatabaseFacadeProvider;
import net.anwiba.commons.model.IBooleanModel;
import net.anwiba.commons.model.IObjectModel;
import net.anwiba.commons.preferences.IPreferences;
import net.anwiba.commons.swing.database.console.result.IDataBaseTableCellValueToStringConverterProvider;
import net.anwiba.commons.swing.dialog.IContentPaneFactory;
import net.anwiba.commons.swing.dialog.pane.IContentPanel;
import net.anwiba.commons.thread.process.IProcessManager;

public final class SqlConsoleContentPaneFactory implements IContentPaneFactory {

  private final IBooleanModel isConnectedModel;
  private final IObjectModel<Connection> connectionModel;
  private final IJdbcConnectionDescription description;
  private final IBooleanModel isDisconnectedModel;
  private final IDatabaseConnector databaseConnector;
  private final IDataBaseTableCellValueToStringConverterProvider dataBaseTableCellValueToStringConverterProvider;
  private final IDatabaseFacadeProvider databaseFacadeProvider;
  private final IProcessManager processManager;
  private final String schema;

  public SqlConsoleContentPaneFactory(
      final IProcessManager processManager,
      final IDatabaseConnector databaseConnector,
      final IDatabaseFacadeProvider databaseFacadeProvider,
      final IJdbcConnectionDescription description,
      final String schema,
      final IDataBaseTableCellValueToStringConverterProvider dataBaseTableCellValueToStringConverterProvider,
      final IBooleanModel isConnectedModel,
      final IObjectModel<Connection> connectionModel,
      final IBooleanModel isDisconnectedModel) {
    this.processManager = processManager;
    this.databaseConnector = databaseConnector;
    this.databaseFacadeProvider = databaseFacadeProvider;
    this.schema = schema;
    this.dataBaseTableCellValueToStringConverterProvider = dataBaseTableCellValueToStringConverterProvider;
    this.isConnectedModel = isConnectedModel;
    this.connectionModel = connectionModel;
    this.description = description;
    this.isDisconnectedModel = isDisconnectedModel;
  }

  @Override
  public IContentPanel create(final Window owner, final IPreferences preferences) {
    return new SqlConsoleContentPane(
        preferences,
        this.processManager,
        this.databaseConnector,
        this.databaseFacadeProvider.getFacade(this.description),
        this.description,
        this.schema,
        this.dataBaseTableCellValueToStringConverterProvider,
        this.connectionModel,
        this.isDisconnectedModel,
        this.isConnectedModel);
  }
}
