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

import net.anwiba.commons.lang.functional.IProcedure;
import net.anwiba.commons.lang.functional.ISupplier;
import net.anwiba.commons.logging.ILogger;
import net.anwiba.commons.logging.Logging;

import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

public final class WrappingConnection implements Connection {

  private static ILogger logger = Logging.getLogger(WrappingConnection.class);
  private final Connection connection;
  private final IProcedure<WrappingConnection, SQLException> closeProcedure;
  private String connectionHash;

  public WrappingConnection(
      final Connection connection) {
    this(connection, wrappingConnection -> {
      try {
        @SuppressWarnings("resource")
        final Connection wrappedConnection = wrappingConnection.getConnection();
        logger.fine(() -> ConnectionUtilities.hash(wrappingConnection) + " connection close"); //$NON-NLS-1$
        wrappedConnection.close();
        logger.debug(() -> ConnectionUtilities.hash(wrappingConnection) + " connection closed"); //$NON-NLS-1$
      } catch (final SQLException exception) {
        logger.debug(() -> ConnectionUtilities.hash(wrappingConnection) + "connection couldn't close"); //$NON-NLS-1$
        throw exception;
      }
    });
  }

  public WrappingConnection(
      final Connection connection,
      final IProcedure<WrappingConnection, SQLException> closeProcedure) {
    this.connection = connection;
    this.closeProcedure = closeProcedure;
  }

  public Connection getConnection() {
    return this.connection;
  }

  @Override
  public int hashCode() {
    return this.connection.hashCode();
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj instanceof WrappingConnection other) {
      return this.connection.equals(other.connection);
    }
    return this.connection.equals(obj);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T unwrap(final Class<T> iface) throws SQLException {
    if (iface.isInstance(this.connection)) {
      return (T) this.connection;
    }
    return this.connection.unwrap(iface);
  }

  @Override
  public boolean isWrapperFor(final Class<?> iface) throws SQLException {
    return iface.isInstance(this.connection) || this.connection.isWrapperFor(iface);
  }

  private String connectionHash() {
    if (this.connectionHash == null) {
      this.connectionHash = ConnectionUtilities.hash(this);
    }
    return this.connectionHash;
  }

  private <S extends Statement> S createAndLog(final String statementString, final ISupplier<S, SQLException> supplier)
      throws SQLException {
    try {
      logger.fine(() -> connectionHash() + " " + ConnectionUtilities.nullHash()
          + (statementString == null ? " statement create" : " statement create: " + statementString));
      S wrappedStatement = supplier.supply();
      logger.debug(() -> connectionHash() + " " + ConnectionUtilities.hash(wrappedStatement)
          + (statementString == null ? " statement created" : " statement created: " + statementString));
      return wrappedStatement;
    } catch (SQLException exception) {
      logger.debug(() -> connectionHash() + " " + ConnectionUtilities.nullHash()
          + (statementString == null ? " statement creation failed"
              : " statement creation failed: " + statementString));
      throw exception;
    }
  }

  @Override
  public void setTypeMap(final Map<String, Class<?>> map) throws SQLException {
    this.connection.setTypeMap(map);
  }

  @Override
  public void setTransactionIsolation(final int level) throws SQLException {
    this.connection.setTransactionIsolation(level);
  }

  @Override
  public void setSchema(final String schema) throws SQLException {
    this.connection.setSchema(schema);
  }

  @Override
  public Savepoint setSavepoint(final String name) throws SQLException {
    return this.connection.setSavepoint(name);
  }

  @Override
  public Savepoint setSavepoint() throws SQLException {
    return this.connection.setSavepoint();
  }

  @Override
  public void setReadOnly(final boolean readOnly) throws SQLException {
    this.connection.setReadOnly(readOnly);
  }

  @Override
  public void setNetworkTimeout(final Executor executor, final int milliseconds) throws SQLException {
    this.connection.setNetworkTimeout(executor, milliseconds);
  }

  @Override
  public void setHoldability(final int holdability) throws SQLException {
    this.connection.setHoldability(holdability);
  }

  @Override
  public void setClientInfo(final String name, final String value) throws SQLClientInfoException {
    this.connection.setClientInfo(name, value);
  }

  @Override
  public void setClientInfo(final Properties properties) throws SQLClientInfoException {
    this.connection.setClientInfo(properties);
  }

  @Override
  public void setCatalog(final String catalog) throws SQLException {
    this.connection.setCatalog(catalog);
  }

  @Override
  public void setAutoCommit(final boolean autoCommit) throws SQLException {
    this.connection.setAutoCommit(autoCommit);
  }

  @Override
  public void rollback(final Savepoint savepoint) throws SQLException {
    this.connection.rollback(savepoint);
  }

  @Override
  public void rollback() throws SQLException {
    this.connection.rollback();
  }

  @Override
  public void releaseSavepoint(final Savepoint savepoint) throws SQLException {
    this.connection.releaseSavepoint(savepoint);
  }

  @Override
  public PreparedStatement prepareStatement(
      final String sql,
      final int resultSetType,
      final int resultSetConcurrency,
      final int resultSetHoldability)
      throws SQLException {
    return createAndLog(sql,
        () -> new WrappedPreparedStatement(sql,
            this.connection.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability)));
  }

  @Override
  public PreparedStatement prepareStatement(final String sql, final int resultSetType, final int resultSetConcurrency)
      throws SQLException {
    return createAndLog(sql,
        () -> new WrappedPreparedStatement(sql,
            this.connection.prepareStatement(sql, resultSetType, resultSetConcurrency)));
  }

  @Override
  public PreparedStatement prepareStatement(final String sql, final String[] columnNames) throws SQLException {
    return createAndLog(sql,
        () -> new WrappedPreparedStatement(sql, this.connection.prepareStatement(sql, columnNames)));
  }

  @Override
  public PreparedStatement prepareStatement(final String sql, final int[] columnIndexes) throws SQLException {
    return createAndLog(sql,
        () -> new WrappedPreparedStatement(sql, this.connection.prepareStatement(sql, columnIndexes)));
  }

  @Override
  public PreparedStatement prepareStatement(final String sql, final int autoGeneratedKeys) throws SQLException {
    return createAndLog(sql,
        () -> new WrappedPreparedStatement(sql, this.connection.prepareStatement(sql, autoGeneratedKeys)));
  }

  @Override
  public PreparedStatement prepareStatement(final String sql) throws SQLException {
    return createAndLog(sql, () -> new WrappedPreparedStatement(sql, this.connection.prepareStatement(sql)));
  }

  @Override
  public CallableStatement prepareCall(
      final String sql,
      final int resultSetType,
      final int resultSetConcurrency,
      final int resultSetHoldability)
      throws SQLException {
    return createAndLog(sql,
        () -> new WrappedCallableStatement(sql,
            this.connection.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability)));
  }

  @Override
  public CallableStatement prepareCall(final String sql, final int resultSetType, final int resultSetConcurrency)
      throws SQLException {
    return new WrappedCallableStatement(sql, this.connection.prepareCall(sql, resultSetType, resultSetConcurrency));
  }

  @Override
  public CallableStatement prepareCall(final String sql) throws SQLException {
    return new WrappedCallableStatement(sql, this.connection.prepareCall(sql));
  }

  @Override
  public String nativeSQL(final String sql) throws SQLException {
    return this.connection.nativeSQL(sql);
  }

  @Override
  public boolean isValid(final int timeout) throws SQLException {
    return this.connection.isValid(timeout);
  }

  @Override
  public boolean isReadOnly() throws SQLException {
    return this.connection.isReadOnly();
  }

  @Override
  public boolean isClosed() throws SQLException {
    return this.connection.isClosed();
  }

  @Override
  public SQLWarning getWarnings() throws SQLException {
    return this.connection.getWarnings();
  }

  @Override
  public Map<String, Class<?>> getTypeMap() throws SQLException {
    return this.connection.getTypeMap();
  }

  @Override
  public int getTransactionIsolation() throws SQLException {
    return this.connection.getTransactionIsolation();
  }

  @Override
  public String getSchema() throws SQLException {
    return this.connection.getSchema();
  }

  @Override
  public int getNetworkTimeout() throws SQLException {
    return this.connection.getNetworkTimeout();
  }

  @Override
  public DatabaseMetaData getMetaData() throws SQLException {
    return this.connection.getMetaData();
  }

  @Override
  public int getHoldability() throws SQLException {
    return this.connection.getHoldability();
  }

  @Override
  public String getClientInfo(final String name) throws SQLException {
    return this.connection.getClientInfo(name);
  }

  @Override
  public Properties getClientInfo() throws SQLException {
    return this.connection.getClientInfo();
  }

  @Override
  public String getCatalog() throws SQLException {
    return this.connection.getCatalog();
  }

  @Override
  public boolean getAutoCommit() throws SQLException {
    return this.connection.getAutoCommit();
  }

  @Override
  public Struct createStruct(final String typeName, final Object[] attributes) throws SQLException {
    return this.connection.createStruct(typeName, attributes);
  }

  @Override
  public Statement createStatement(
      final int resultSetType,
      final int resultSetConcurrency,
      final int resultSetHoldability)
      throws SQLException {
    return createAndLog(null,
        () -> new WrappedStatement(
            this.connection.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability)));
  }

  @Override
  public Statement createStatement(final int resultSetType, final int resultSetConcurrency) throws SQLException {
    return createAndLog(null,
        () -> new WrappedStatement(this.connection.createStatement(resultSetType, resultSetConcurrency)));
  }

  @Override
  public Statement createStatement() throws SQLException {
    return createAndLog(null, () -> new WrappedStatement(this.connection.createStatement()));
  }

  @Override
  public SQLXML createSQLXML() throws SQLException {
    return this.connection.createSQLXML();
  }

  @Override
  public NClob createNClob() throws SQLException {
    return this.connection.createNClob();
  }

  @Override
  public Clob createClob() throws SQLException {
    return this.connection.createClob();
  }

  @Override
  public Blob createBlob() throws SQLException {
    return this.connection.createBlob();
  }

  @Override
  public Array createArrayOf(final String typeName, final Object[] elements) throws SQLException {
    return this.connection.createArrayOf(typeName, elements);
  }

  @Override
  public void commit() throws SQLException {
    this.connection.commit();
  }

  @Override
  public void close() throws SQLException {
    this.closeProcedure.execute(this);
  }

  @Override
  public void clearWarnings() throws SQLException {
    this.connection.clearWarnings();
  }

  @Override
  public void abort(final Executor executor) throws SQLException {
    this.connection.abort(executor);
  }
}