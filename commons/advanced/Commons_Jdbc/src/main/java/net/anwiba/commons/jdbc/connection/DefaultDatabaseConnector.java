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
import java.sql.DriverManager;
import java.sql.SQLException;

import net.anwiba.commons.jdbc.DatabaseUtilities;

public class DefaultDatabaseConnector implements IRegisterableDatabaseConnector {

  private static final int TIMEOUT = 10;

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
  public synchronized Connection connectReadOnly(
      final String url,
      final String userName,
      final String password,
      final int timeout) throws SQLException {
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

  public synchronized Connection connect(
      final String url,
      final String userName,
      final String password,
      final int timeout,
      final boolean isReadOnly) throws SQLException {
    if (timeout == -1) {
      return DatabaseUtilities.createConnection(url, userName, password, isReadOnly);
    }
    final int loginTimeout = DriverManager.getLoginTimeout();
    try {
      DriverManager.setLoginTimeout(timeout);
      return DatabaseUtilities.createConnection(url, userName, password, isReadOnly);
    } finally {
      DriverManager.setLoginTimeout(loginTimeout);
    }
  }

  @Override
  public boolean isApplicable(final String context) {
    return true;
  }

  @Override
  public boolean isConnectable(final IJdbcConnectionDescription description) {
    return isConnectable(description.getUrl(), description.getUserName(), description.getPassword());
  }

  @Override
  public boolean isConnectable(final String url, final String userName, final String password) {
    try (Connection connection = connectReadOnly(url, userName, password, TIMEOUT)) {
      return true;
    } catch (final SQLException exception) {
      return false;
    }
  }
}
