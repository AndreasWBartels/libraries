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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */
package net.anwiba.commons.jdbc.connection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import net.anwiba.commons.lang.optional.Optional;
import net.anwiba.commons.lang.stream.Streams;
import net.anwiba.commons.utilities.registry.AbstractApplicableRegistry;

public class DatabaseConnectorRegistry implements IDatabaseConnectorRegistry {

  private final List<IPostConnectionProcedure> procedures = new ArrayList<>();
  private final AbstractApplicableRegistry<String, IRegisterableDatabaseConnector> registry = new AbstractApplicableRegistry<>(
      new DefaultDatabaseConnector());

  @Override
  public Connection connectReadOnly(final IJdbcConnectionDescription description) throws SQLException {
    return connectReadOnly(description, -1);
  }

  @Override
  public Connection connectReadOnly(final IJdbcConnectionDescription description, final int timeout)
      throws SQLException {
    return connectReadOnly(description.getUrl(), description.getUserName(), description.getPassword(), timeout);
  }

  @Override
  public Connection connectReadOnly(final String url, final String userName, final String password, final int timeout)
      throws SQLException {
    return connect(url, userName, password, timeout, true);
  }

  @Override
  public Connection connectWritable(final IJdbcConnectionDescription description) throws SQLException {
    return connectWritable(description, -1);
  }

  @Override
  public Connection connectWritable(final IJdbcConnectionDescription description, final int timeout)
      throws SQLException {
    return connectWritable(description.getUrl(), description.getUserName(), description.getPassword(), timeout);
  }

  @Override
  public Connection connectWritable(final String url, final String userName, final String password, final int timeout)
      throws SQLException {
    return connect(url, userName, password, timeout, false);
  }

  public Connection connect(
      final String url,
      final String userName,
      final String password,
      final int timeout,
      final boolean isReadOnly)
      throws SQLException {
    final IDatabaseConnector connector = this.registry.get(url);
    final Connection connection = isReadOnly
        ? connector.connectReadOnly(url, userName, password, timeout)
        : connector.connectWritable(url, userName, password, timeout);
    connection.setAutoCommit(true);
    connection.clearWarnings();
    setUp(
        Optional
            .of(connection) //
            .instanceOf(WrappingConnection.class)
            .convert(w -> w.getConnection())
            .getOr(() -> connection));
    return connection;
  }

  @SuppressWarnings("unused")
  private void setUp(final Connection connection) throws SQLException {
    Streams
        .<IPostConnectionProcedure, SQLException> of(SQLException.class, this.procedures)
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

  @Override
  public boolean isConnectable(final String url, final String userName, final String password) {
    final IDatabaseConnector connector = this.registry.get(url);
    return connector.isConnectable(url, userName, password);
  }

}
