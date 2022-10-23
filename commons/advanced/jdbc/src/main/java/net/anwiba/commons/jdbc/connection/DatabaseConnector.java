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
package net.anwiba.commons.jdbc.connection;

import net.anwiba.commons.jdbc.DatabaseUtilities;
import net.anwiba.commons.lang.stream.Streams;
import net.anwiba.commons.logging.ILogger;
import net.anwiba.commons.logging.Logging;
import net.anwiba.commons.utilities.property.IProperties;
import net.anwiba.commons.utilities.registry.AbstractApplicableRegistry;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseConnector implements IDatabaseConnectorRegistry {

  private static ILogger logger = Logging.getLogger(DatabaseConnector.class);
  private final List<IPostConnectionProcedure> procedures = new ArrayList<>();
  private final AbstractApplicableRegistry<String, IRegisterableDatabaseConnector> registry =
      new AbstractApplicableRegistry<>(
          new DefaultDatabaseConnector());

  public DatabaseConnector(final List<IRegisterableDatabaseConnector> connectors) {
    Streams.of(connectors).foreach(c -> this.registry.add(c));
  }

  @Override
  public boolean isConnectable(final String url,
      final String userName,
      final String password,
      final IProperties properties) {
    IDatabaseConnector connector = null;
    return DatabaseUtilities.isSupported(url) 
        && (connector = this.registry.get(url)) != null
        && connector.isConnectable(url, userName, password, properties);
  }

  @Override
  public Connection connectReadOnly(
      final String url,
      final String userName,
      final String password,
      final int timeout,
      final IProperties properties)
      throws SQLException {
    return connect(url, userName, password, false, timeout, true, properties);
  }

  @Override
  public Connection connectWritable(
      final String url,
      final String userName,
      final String password,
      final boolean isAutoCommitEnabled,
      final int timeout,
      final IProperties properties)
      throws SQLException {
    return connect(url, userName, password, isAutoCommitEnabled, timeout, false, properties);
  }

  public Connection connect(
      final String url,
      final String userName,
      final String password,
      final boolean isAutoCommitEnabled,
      final int timeout,
      final boolean isReadOnly,
      final IProperties properties)
      throws SQLException {
    final IDatabaseConnector connector = this.registry.get(url);
    final Connection connection = isReadOnly
        ? connector.connectReadOnly(url, userName, password, timeout, properties)
        : connector.connectWritable(url, userName, password, isAutoCommitEnabled, timeout, properties);
    connection.setAutoCommit(true);
    connection.clearWarnings();
    setUp(connection);
    logger.debug(ConnectionUtilities.hash(connection) + " connection created"); //$NON-NLS-1$
    return wrap(connection);
  }

  private WrappingConnection wrap(final Connection connection) {
    if (connection instanceof WrappingConnection wrappingConnection) {
      return wrappingConnection;
    }
    return new WrappingConnection(connection);
  }

  @SuppressWarnings("unused")
  private void setUp(final Connection connection) throws SQLException {
    Streams
        .<IPostConnectionProcedure, SQLException>of(SQLException.class, this.procedures)
        .filter(p -> p.isApplicable(connection))
        .foreach(p -> p.execute(connection));
  }

  @Override
  public void add(final IPostConnectionProcedure procedure) {
    this.procedures.add(procedure);
  }

  @Override
  public void add(final IRegisterableDatabaseConnector connector) {
    this.registry.add(connector);
  }
}
