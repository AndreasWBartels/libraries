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
package net.anwiba.database.sqlite.utilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

public class SqliteUtilities {

  private static net.anwiba.commons.logging.ILogger logger = net.anwiba.commons.logging.Logging
      .getLogger(SqliteUtilities.class);

  public static boolean exists(final Connection connection, final String tableName) throws SQLException {
    final String selectStatement = "pragma table_info('" + tableName + "');"; //$NON-NLS-1$ //$NON-NLS-2$
    logger.log(Level.FINE, "Query: Table " + tableName); //$NON-NLS-1$
    logger.log(Level.FINE, "Query: " + selectStatement); //$NON-NLS-1$
    try (PreparedStatement statement = connection.prepareStatement(selectStatement)) {
      if (statement.execute()) {
        try (ResultSet resultSet = statement.getResultSet()) {
          if (resultSet.next()) {
            return true;
          }
        }
      }
      return false;
    }
  }

}
