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

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Objects;

import net.anwiba.commons.lang.optional.Optional;
import net.anwiba.commons.lang.stream.Streams;
import net.anwiba.commons.utilities.property.IProperties;
import net.anwiba.commons.utilities.property.Properties;

public class DefaultDatabaseConnector implements IRegisterableDatabaseConnector {

  @Override
  public synchronized Connection connectReadOnly(
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

  public synchronized Connection connect(
      final String url,
      final String userName,
      final String password,
      final boolean isAutoCommitEnabled,
      final int timeout,
      final boolean isReadOnly)
      throws SQLException {
    return connect(url, userName, password, isAutoCommitEnabled, timeout, isReadOnly, Properties.empty());
  }

  public synchronized Connection connect(
      final String url,
      final String userName,
      final String password,
      final boolean isAutoCommitEnabled,
      final int timeout,
      final boolean isReadOnly,
      final IProperties properties)
      throws SQLException {
    if (timeout == -1) {
      return createConnection(url, userName, password, isAutoCommitEnabled, isReadOnly, properties);
    }
    final int loginTimeout = DriverManager.getLoginTimeout();
    try {
      DriverManager.setLoginTimeout(timeout);
      return createConnection(url, userName, password, isAutoCommitEnabled, isReadOnly, properties);
    } finally {
      DriverManager.setLoginTimeout(loginTimeout);
    }
  }

  public static Connection createConnection(
      final String url,
      final String user,
      final String password,
      final boolean isAutoCommitEnabled,
      final boolean isReadOnly,
      final IProperties properties)
      throws SQLException {
    Driver driver = DriverManager.getDriver(url);
    if (driver == null) {
      throw new SQLException(ConnectionUtilities.nullHash() + " connection create failed, unsupporterd url");
    }
    final Connection connection =
        driver.connect(url, getProperties(user, password, isReadOnly, properties));
    connection.setAutoCommit(isAutoCommitEnabled);
    connection.setReadOnly(isReadOnly);
    return connection;
  }

  public static java.util.Properties getProperties(
      final String userName,
      final String password,
      final boolean isReadOnly,
      final IProperties properties) {
    java.util.Properties settings = new java.util.Properties();
    Optional.of(userName).consume(value -> settings.put("user", value));
    Optional.of(password).consume(value -> settings.put("password", value));
    Streams.of(properties.properties())
        .filter(property -> Objects.nonNull(property.getValue()))
        .forEach(property -> settings.put(property.getName(), property.getValue()));
    return settings;
  }

  @Override
  public boolean isApplicable(final String context) {
    return true;
  }
}
