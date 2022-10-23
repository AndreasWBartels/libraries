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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program. If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */
package net.anwiba.database.sqlite.connection;

import net.anwiba.commons.jdbc.DatabaseUtilities;
import net.anwiba.commons.jdbc.connection.ConnectionUtilities;
import net.anwiba.commons.lang.optional.Optional;
import net.anwiba.commons.lang.stream.Streams;
import net.anwiba.commons.logging.ILevel;
import net.anwiba.commons.logging.ILogger;
import net.anwiba.commons.logging.Logging;
import net.anwiba.commons.utilities.property.IProperties;
import net.anwiba.database.sqlite.ISqliteConnstants;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

import org.sqlite.SQLiteConfig;

public final class SqliteDatabaseConnector implements ISqliteDatabaseConnector {

  private static ILogger logger = Logging.getLogger(SqliteDatabaseConnector.class);
  private final ISqliteDatabaseConnectorConfiguration configuration;
  private final Map<String, ISqliteCapabilities> capabilities = Collections
      .synchronizedMap(new HashMap<>());

  public SqliteDatabaseConnector(final ISqliteDatabaseConnectorConfiguration configuration) {
    this.configuration = configuration;
  }

  @Override
  public boolean isApplicable(final String context) {
    return context != null && context.startsWith(ISqliteConnstants.PROTOCOL);
  }

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
      final boolean isReadOnly,
      final IProperties properties)
      throws SQLException {
    logger.debug(() -> "connect to '" + url + "' readonly '" + isReadOnly + "'");
    if (isReadOnly) {
      return getConnetion(url, false, timeout, getProperties(userName, password, isReadOnly, timeout, properties));
    }
    return getConnetion(url,
        isAutoCommitEnabled,
        timeout,
        getProperties(userName, password, isReadOnly, timeout, properties));
  }

  @SuppressWarnings("unused")
  private Properties getProperties(
      final String userName,
      final String password,
      final boolean isReadOnly,
      final int timeout,
      final IProperties properties) {
    final SQLiteConfig config = new SQLiteConfig();
    config.setReadOnly(isReadOnly);
    config.setSharedCache(true);
    config.enableLoadExtension(true);
    Properties settings = config.toProperties();
    Optional.of(userName).consume(value -> settings.put("user", value));
    Optional.of(password).consume(value -> settings.put("password", value));
    Streams.of(properties.properties())
        .filter(property -> Objects.nonNull(property.getValue()))
        .forEach(property -> settings.put(property.getName(), property.getValue()));
    return settings;
  }

  private Connection
      getConnetion(final String url, final boolean isAutoCommitEnabled, final int timeout, final Properties properties)
          throws SQLException {
    final Connection connection = createConnection(url, properties, timeout);
    connection.setAutoCommit(isAutoCommitEnabled);
    if (!this.capabilities.containsKey(url)) {
      this.capabilities.put(url, checkCapabilities(connection));
    }
    return connection;
  }

  private Connection createConnection(
      final String url,
      final Properties properties,
      final int timeout)
      throws SQLException {
    Driver driver = DriverManager.getDriver(url);
    if (driver == null) {
      throw new SQLException(ConnectionUtilities.nullHash() + " connection create failed, unsupporterd url");
    }
    if (timeout == -1) {
      return driver.connect(url, properties);
    }
    final int loginTimeout = DriverManager.getLoginTimeout();
    try {
      return driver.connect(url, properties);
    } finally {
      DriverManager.setLoginTimeout(loginTimeout);
    }
  }

  private ISqliteCapabilities checkCapabilities(final Connection connection) throws SQLException {
    final SqliteCapabilitiesBuilder builder = new SqliteCapabilitiesBuilder();
    try (final Statement statement = connection.createStatement()) {
      statement.setQueryTimeout(30); // set timeout to 30 sec.
      final String sqliteVersion = getSqliteVersion(statement);
      builder.setSqliteVersion(sqliteVersion);
      logger.fine(() -> "sqlite version '" + sqliteVersion + "'");
      for (final ILibrary extension : this.configuration.getExtensions()) {
        try {
          logger.fine(() -> "try to load extention '" + extension.getName() + "'");
          logger.fine(() -> "extention loaded '" + extension.getResource() + "'");
        } catch (final Exception exception) {
          logger.log(ILevel.WARNING, "Couldn't load extention '" + extension.getName() + "'");
          logger.log(ILevel.DEBUG, exception.getMessage(), exception);
        }
      }
      final String spatiaLiteVersion = getSpatiaLiteVersion(statement);
      logger.fine(() -> "spatialite version '" + spatiaLiteVersion + "'");
      if (spatiaLiteVersion != null) {
        builder.setSpatiaLiteVersion(spatiaLiteVersion);
      } else {
        final ILibrary spatialiteLibrary = this.configuration.getSpatialite();
        if (spatialiteLibrary != null) {
          try {
            logger.fine(() -> "try to load extention 'spatialite'");
            statement.execute("SELECT load_extension('" + spatialiteLibrary.getResource() + "')");
            logger.fine(() -> "extention loaded '" + spatialiteLibrary.getResource() + "'");
            final String loadedSpatiaLiteVersion = getSpatiaLiteVersion(statement);
            logger.fine(() -> "loaded spatialite version '" + loadedSpatiaLiteVersion + "'");
            builder.setSpatiaLiteVersion(loadedSpatiaLiteVersion);
          } catch (final Exception exception) {
            logger.warning(() -> "Couldn't load extention '" + spatialiteLibrary.getResource() + "'");
            logger.debug(() -> exception.getMessage(), exception);
          }
        }
      }
    }
    checkSpatiaLiteDatabaseVersion(builder, connection);
    return builder.build();
  }

  private void checkSpatiaLiteDatabaseVersion(
      final SqliteCapabilitiesBuilder builder,
      final Connection connection)
      throws SQLException {
    if (DatabaseUtilities
        .count(
            connection,
            "SELECT count(*) FROM sqlite_master WHERE type=? AND name = ?", //$NON-NLS-1$
            "table", //$NON-NLS-1$
            "geometry_columns") //$NON-NLS-1$
        == 0) {
      logger.fine(() -> "no spatialite"); //$NON-NLS-1$
      return;
    }
    if (DatabaseUtilities
        .contains(
            connection,
            "PRAGMA table_info('geometry_columns')", //$NON-NLS-1$
            "name", //$NON-NLS-1$
            "type")) { //$NON-NLS-1$
      logger.fine(() -> "spatialite 2.0 structur spatialite"); //$NON-NLS-1$
      builder.setSpatiaLiteDatabaseVersion("2.0"); //$NON-NLS-1$
      return;
    }
    logger.fine(() -> "spatialite 4.0 structur spatialite"); //$NON-NLS-1$
    builder.setSpatiaLiteDatabaseVersion("4.0"); //$NON-NLS-1$
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
