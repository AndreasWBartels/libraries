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
import java.sql.SQLException;

public interface IDatabaseConnector {

  default Connection connectReadOnly(final IJdbcConnectionDescription description)
      throws SQLException {
    return connectReadOnly(description, -1);
  }

  default Connection connectReadOnly(
      final IJdbcConnectionDescription description,
      final int timeout)
      throws SQLException {
    return connectReadOnly(
        description.getUrl(),
        description.getUserName(),
        description.getPassword(),
        timeout);
  }

  Connection connectReadOnly(String url, String userName, String password, int timeout)
      throws SQLException;

  default Connection connectWritable(
      final IJdbcConnectionDescription description,
      boolean isAutoCommitEnabled)
      throws SQLException {
    return connectWritable(description, isAutoCommitEnabled, -1);
  }

  default Connection connectWritable(
      final IJdbcConnectionDescription description,
      boolean isAutoCommitEnabled,
      final int timeout)
      throws SQLException {
    return connectWritable(
        description.getUrl(),
        description.getUserName(),
        description.getPassword(),
        isAutoCommitEnabled,
        timeout);
  }

  Connection connectWritable(
      String url,
      String userName,
      String password,
      boolean isAutoCommitEnabled,
      int timeout)
      throws SQLException;

  default boolean isConnectable(final IJdbcConnectionDescription description) {
    return isConnectable(
        description.getUrl(),
        description.getUserName(),
        description.getPassword());
  }

  boolean isConnectable(String url, String userName, String password);

}