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
package net.anwiba.database.sqlite.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.sqlite.SQLiteConfig;

import net.anwiba.commons.jdbc.DatabaseUtilities;
import net.anwiba.commons.jdbc.connection.WrappingConnection;
import net.anwiba.commons.lang.functional.IProcedure;
import net.anwiba.commons.logging.ILevel;
import net.anwiba.commons.logging.ILogger;
import net.anwiba.commons.logging.Logging;
import net.anwiba.database.sqlite.ISqliteConnstants;

public final class SqliteDatabaseConnector implements ISqliteDatabaseConnector {

  private static ILogger logger = Logging.getLogger(SqliteDatabaseConnector.class.getName());
  private final ISqliteDatabaseConnectorConfiguration configuration;
  private final Map<String, ISqliteCapabilities> capabilities = Collections.synchronizedMap(new HashMap<>());
  private static final int TIMEOUT = 10;

  public SqliteDatabaseConnector(final ISqliteDatabaseConnectorConfiguration configuration) {
    this.configuration = configuration;
  }

  @Override
  public boolean isApplicable(final String context) {
    return context != null && context.startsWith(ISqliteConnstants.PROTOCOL);
  }

  @Override
  public boolean isConnectable(final String url, final String userName, final String password) {
    try (Connection connection = connectReadOnly(url, userName, password, TIMEOUT)) {
      return true;
    } catch (final SQLException exception) {
      logger.log(ILevel.WARNING, exception.getLocalizedMessage(), exception);
      return false;
    }
  }

  @Override
  public synchronized Connection connectReadOnly(
      final String url,
      final String userName,
      final String password,
      final int timeout)
      throws SQLException {
    return connect(url, userName, password, timeout, true);
  }

  @Override
  public Connection connectWritable(final String url, final String userName, final String password, final int timeout)
      throws SQLException {
    return connect(url, userName, password, timeout, false);
  }

  @SuppressWarnings("nls")
  public synchronized Connection connect(
      final String url,
      final String userName,
      final String password,
      final int timeout,
      final boolean isReadOnly)
      throws SQLException {
    logger.log(ILevel.DEBUG, "connect to '" + url + "' readonly '" + isReadOnly + "'");
    final Properties properties = getProperties(userName, password, isReadOnly, timeout);
    return getConnetion(url, properties, timeout);
  }

  @SuppressWarnings("unused")
  private Properties getProperties(
      final String userName,
      final String password,
      final boolean isReadOnly,
      final int timeout) {
    final SQLiteConfig config = new SQLiteConfig();
    config.setReadOnly(isReadOnly);
    config.setSharedCache(true);
    config.enableLoadExtension(true);
    return config.toProperties();
  }

  @SuppressWarnings("resource")
  private Connection getConnetion(final String url, final Properties properties, final int timeout)
      throws SQLException {
    final Connection connection = createConnection(url, properties, timeout);
    if (!this.capabilities.containsKey(url)) {
      this.capabilities.put(url, checkCapabilities(connection));
    }
    return wrap(url, connection);
  }

  public Connection createConnection(final String url, final Properties properties, final int timeout)
      throws SQLException {
    if (timeout == -1) {
      return DriverManager.getConnection(url, properties);
    }
    final int loginTimeout = DriverManager.getLoginTimeout();
    try {
      DriverManager.setLoginTimeout(timeout);
      return DriverManager.getConnection(url, properties);
    } finally {
      DriverManager.setLoginTimeout(loginTimeout);
    }
  }

  @SuppressWarnings("nls")
  private ISqliteCapabilities checkCapabilities(final Connection connection) throws SQLException {
    final SqliteCapabilitiesBuilder builder = new SqliteCapabilitiesBuilder();
    try (final Statement statement = connection.createStatement()) {
      statement.setQueryTimeout(30); // set timeout to 30 sec.
      final String sqliteVersion = getSqliteVersion(statement);
      builder.setSqliteVersion(sqliteVersion);
      logger.log(ILevel.DEBUG, "sqlite version '" + sqliteVersion + "'");
      for (final ILibrary extension : this.configuration.getExtensions()) {
        try {
          logger.log(ILevel.DEBUG, "try to load extention '" + extension.getName() + "'");
          logger.log(ILevel.DEBUG, "extention loaded '" + extension.getResource() + "'");
        } catch (final Exception exception) {
          logger.log(ILevel.WARNING, "Couldn't load extention '" + extension.getName() + "'");
          logger.log(ILevel.DEBUG, exception.getMessage(), exception);
        }
      }
      final String spatiaLiteVersion = getSpatiaLiteVersion(statement);
      logger.log(ILevel.DEBUG, "spatialite version '" + spatiaLiteVersion + "'");
      if (spatiaLiteVersion != null) {
        builder.setSpatiaLiteVersion(spatiaLiteVersion);
      } else {
        final ILibrary spatialiteLibrary = this.configuration.getSpatialite();
        if (spatialiteLibrary != null) {
          try {
            logger.log(ILevel.DEBUG, "try to load extention 'spatialite'");
            statement.execute("SELECT load_extension('" + spatialiteLibrary.getResource() + "')");
            logger.log(ILevel.DEBUG, "extention loaded '" + spatialiteLibrary.getResource() + "'");
            final String loadedSpatiaLiteVersion = getSpatiaLiteVersion(statement);
            logger.log(ILevel.DEBUG, "loaded spatialite version '" + loadedSpatiaLiteVersion + "'");
            builder.setSpatiaLiteVersion(loadedSpatiaLiteVersion);
          } catch (final Exception exception) {
            logger.log(ILevel.WARNING, "Couldn't load extention '" + spatialiteLibrary.getResource() + "'");
            logger.log(ILevel.DEBUG, exception.getMessage(), exception);
          }
        }
      }
    }
    checkSpatiaLiteDatabaseVersion(builder, connection);
    return builder.build();
  }

  private void checkSpatiaLiteDatabaseVersion(final SqliteCapabilitiesBuilder builder, final Connection connection)
      throws SQLException {
    if (DatabaseUtilities.count(
        connection,
        "SELECT count(*) FROM sqlite_master WHERE type=? AND name = ?", //$NON-NLS-1$
        "table", //$NON-NLS-1$
        "geometry_columns") == 0) { //$NON-NLS-1$
      logger.log(ILevel.DEBUG, "no spatialite"); //$NON-NLS-1$
      return;
    }
    if (DatabaseUtilities.containts(
        connection,
        "PRAGMA table_info('geometry_columns')", //$NON-NLS-1$
        "name", //$NON-NLS-1$
        "type")) { //$NON-NLS-1$
      logger.log(ILevel.DEBUG, "spatialite 2.0 structur spatialite"); //$NON-NLS-1$
      builder.setSpatiaLiteDatabaseVersion("2.0"); //$NON-NLS-1$
      return;
    }
    logger.log(ILevel.DEBUG, "spatialite 4.0 structur spatialite"); //$NON-NLS-1$
    builder.setSpatiaLiteDatabaseVersion("4.0"); //$NON-NLS-1$
  }

  private WrappingConnection wrap(@SuppressWarnings("unused") final String url, final Connection connection) {
    return new WrappingConnection(connection, new IProcedure<WrappingConnection, SQLException>() {

      @Override
      public void execute(final WrappingConnection wrappingConnection) throws SQLException {
        try {
          @SuppressWarnings("resource")
          final Connection wrappedConnection = wrappingConnection.getConnection();
          logger.log(ILevel.DEBUG, "close connection"); //$NON-NLS-1$
          wrappedConnection.close();
        } catch (final SQLException exception) {
          logger.log(ILevel.WARNING, "Couldn't close connection"); //$NON-NLS-1$
          throw exception;
        }
      }
    });
  }

  private String getSpatiaLiteVersion(final Statement statement) {
    return DatabaseUtilities.getAsString(statement, "SELECT spatialite_version()"); //$NON-NLS-1$
  }

  private String getSqliteVersion(final Statement statement) {
    return DatabaseUtilities.getAsString(statement, "SELECT sqlite_version()"); //$NON-NLS-1$
  }

  @Override
  public ISqliteCapabilitiesProvider getSqliteCapabilitiesProvider() {

    @SuppressWarnings("hiding")
    final Map<String, ISqliteCapabilities> capabilities = this.capabilities;
    return new ISqliteCapabilitiesProvider() {

      private ISqliteCapabilities getSqliteCapabilities(
          final String url,
          final String userName,
          final String password) {
        check(url, userName, password);
        return capabilities.get(url);
      }

      private void check(final String url, final String userName, final String password) {
        isConnectable(url, userName, password);
      }

      @Override
      public boolean canChange(final String url, final String userName, final String password) {
        @SuppressWarnings("hiding")
        final ISqliteCapabilities capabilities = getSqliteCapabilities(url, userName, password);
        if (capabilities == null) {
          return false;
        }
        return capabilities.canChange();
      }

      @Override
      public boolean isExtended(final String url, final String userName, final String password) {
        @SuppressWarnings("hiding")
        final ISqliteCapabilities capabilities = getSqliteCapabilities(url, userName, password);
        if (capabilities == null) {
          return false;
        }
        return capabilities.isExtended();
      }
    };
  }

}
