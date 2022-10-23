/*
 * #%L
 * anwiba commons
 * %%
 * Copyright (C) 2007 - 2022 Andreas W. Bartels
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

import net.anwiba.commons.lang.functional.IBlock;
import net.anwiba.commons.lang.functional.IProcedure;
import net.anwiba.commons.logging.ILogger;
import net.anwiba.commons.logging.Logging;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;

public class WrappedPreparedStatement extends WrappedStatement implements PreparedStatement {

  private static ILogger logger = Logging.getLogger(WrappedPreparedStatement.class);
  private final PreparedStatement statement;
  private final String statementString;

  public WrappedPreparedStatement(final String statementString, final PreparedStatement statement) {
    super(statement);
    this.statementString = statementString;
    this.statement = statement;
  }

  public WrappedPreparedStatement(final String statementString, final PreparedStatement statement, IProcedure<Statement, SQLException> closeProcedure) {
    super(statement, closeProcedure);
    this.statementString = statementString;
    this.statement = statement;
  }

  protected void setAndLog(final Object value, final IBlock<SQLException> block)
      throws SQLException {
    logger.debug(() -> connectionHash() + " " + statementHash() + " statement value set: "
        + ConnectionUtilities.toDebugString(value));
    block.execute();
  }

  @Override
  public String toString() {
    return this.statementString;
  }

  @Override
  public PreparedStatement getStatement() {
    return this.statement;
  }

  @Override
  public int hashCode() {
    return this.statement.hashCode();
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj instanceof WrappedPreparedStatement other) {
      return this.statement.equals(other.statement);
    }
    return this.statement.equals(obj);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T unwrap(final Class<T> iface) throws SQLException {
    if (iface.isInstance(this.statement)) {
      return (T) this.statement;
    }
    return this.statement.unwrap(iface);
  }

  @Override
  public boolean isWrapperFor(final Class<?> iface) throws SQLException {
    return iface.isInstance(this.statement) || this.statement.isWrapperFor(iface);
  }

  @Override
  public ResultSetMetaData getMetaData() throws SQLException {
    return this.statement.getMetaData();
  }

  @Override
  public ParameterMetaData getParameterMetaData() throws SQLException {
    return this.statement.getParameterMetaData();
  }

  @Override
  public ResultSet executeQuery() throws SQLException {
    return executeAndLog(null, () -> this.statement.executeQuery());
  }

  @Override
  public int executeUpdate() throws SQLException {
    return executeAndLog(null, () -> this.statement.executeUpdate());
  }

  @Override
  public boolean execute() throws SQLException {
    return executeAndLog(null, () -> this.statement.execute());
  }

  @Override
  public void addBatch() throws SQLException {
    logger.debug(() -> connectionHash() + " " + statementHash() + " statement batch add to");
    this.statement.addBatch();
  }

  @Override
  public void clearParameters() throws SQLException {
    this.statement.clearParameters();
  }

  @Override
  public void setNull(final int parameterIndex, final int sqlType) throws SQLException {
    setAndLog(null, () -> this.statement.setNull(parameterIndex, sqlType));
  }

  @Override
  public void setBoolean(final int parameterIndex, final boolean x) throws SQLException {
    setAndLog(x, () -> this.statement.setBoolean(parameterIndex, x));
  }

  @Override
  public void setByte(final int parameterIndex, final byte x) throws SQLException {
    setAndLog(x, () -> this.statement.setByte(parameterIndex, x));
  }

  @Override
  public void setShort(final int parameterIndex, final short x) throws SQLException {
    setAndLog(x, () -> this.statement.setShort(parameterIndex, x));
  }

  @Override
  public void setInt(final int parameterIndex, final int x) throws SQLException {
    setAndLog(x, () -> this.statement.setInt(parameterIndex, x));
  }

  @Override
  public void setLong(final int parameterIndex, final long x) throws SQLException {
    setAndLog(x, () -> this.statement.setLong(parameterIndex, x));
  }

  @Override
  public void setFloat(final int parameterIndex, final float x) throws SQLException {
    setAndLog(x, () -> this.statement.setFloat(parameterIndex, x));
  }

  @Override
  public void setDouble(final int parameterIndex, final double x) throws SQLException {
    setAndLog(x, () -> this.statement.setDouble(parameterIndex, x));
  }

  @Override
  public void setBigDecimal(final int parameterIndex, final BigDecimal x) throws SQLException {
    setAndLog(x, () -> this.statement.setBigDecimal(parameterIndex, x));
  }

  @Override
  public void setString(final int parameterIndex, final String x) throws SQLException {
    setAndLog(x, () -> this.statement.setString(parameterIndex, x));
  }

  @Override
  public void setBytes(final int parameterIndex, final byte[] x) throws SQLException {
    setAndLog(x, () -> this.statement.setBytes(parameterIndex, x));
  }

  @Override
  public void setDate(final int parameterIndex, final Date x) throws SQLException {
    setAndLog(x, () -> this.statement.setDate(parameterIndex, x));
  }

  @Override
  public void setTime(final int parameterIndex, final Time x) throws SQLException {
    setAndLog(x, () -> this.statement.setTime(parameterIndex, x));
  }

  @Override
  public void setTimestamp(final int parameterIndex, final Timestamp x) throws SQLException {
    setAndLog(x, () -> this.statement.setTimestamp(parameterIndex, x));
  }

  @Override
  public void setAsciiStream(final int parameterIndex, final InputStream x, final int length) throws SQLException {
    setAndLog("inputstream", () -> this.statement.setAsciiStream(parameterIndex, x, length));
  }

  @Override
  public void setUnicodeStream(final int parameterIndex, final InputStream x, final int length) throws SQLException {
    this.statement.setUnicodeStream(parameterIndex, x, length);
  }

  @Override
  public void setBinaryStream(final int parameterIndex, final InputStream x, final int length) throws SQLException {
    setAndLog(x, () -> this.statement.setBinaryStream(parameterIndex, x, length));
  }

  @Override
  public void setObject(final int parameterIndex, final Object x, final int targetSqlType) throws SQLException {
    setAndLog(x, () -> this.statement.setObject(parameterIndex, x, targetSqlType));
  }

  @Override
  public void setObject(final int parameterIndex, final Object x) throws SQLException {
    setAndLog(x, () -> this.statement.setObject(parameterIndex, x));
  }

  @Override
  public void setCharacterStream(final int parameterIndex, final Reader reader, final int length) throws SQLException {
    setAndLog("reader", () -> this.statement.setCharacterStream(parameterIndex, reader, length));
  }

  @Override
  public void setRef(final int parameterIndex, final Ref x) throws SQLException {
    setAndLog(x, () -> this.statement.setRef(parameterIndex, x));
  }

  @Override
  public void setBlob(final int parameterIndex, final Blob x) throws SQLException {
    setAndLog(x, () -> this.statement.setBlob(parameterIndex, x));
  }

  @Override
  public void setClob(final int parameterIndex, final Clob x) throws SQLException {
    setAndLog(x, () -> this.statement.setClob(parameterIndex, x));
  }

  @Override
  public void setArray(final int parameterIndex, final Array x) throws SQLException {
    setAndLog(x, () -> this.statement.setArray(parameterIndex, x));
  }

  @Override
  public void setDate(final int parameterIndex, final Date x, final Calendar cal) throws SQLException {
    setAndLog(x, () -> this.statement.setDate(parameterIndex, x, cal));
  }

  @Override
  public void setTime(final int parameterIndex, final Time x, final Calendar cal) throws SQLException {
    setAndLog(x, () -> this.statement.setTime(parameterIndex, x, cal));
  }

  @Override
  public void setTimestamp(final int parameterIndex, final Timestamp x, final Calendar cal) throws SQLException {
    setAndLog(x, () -> this.statement.setTimestamp(parameterIndex, x, cal));
  }

  @Override
  public void setNull(final int parameterIndex, final int sqlType, final String typeName) throws SQLException {
    setAndLog(null, () -> this.statement.setNull(parameterIndex, sqlType, typeName));
  }

  @Override
  public void setURL(final int parameterIndex, final URL x) throws SQLException {
    setAndLog(x, () -> this.statement.setURL(parameterIndex, x));
  }

  @Override
  public void setRowId(final int parameterIndex, final RowId x) throws SQLException {
    setAndLog(x, () -> this.statement.setRowId(parameterIndex, x));
  }

  @Override
  public void setNString(final int parameterIndex, final String value) throws SQLException {
    setAndLog(value, () -> this.statement.setNString(parameterIndex, value));
  }

  @Override
  public void setNCharacterStream(final int parameterIndex, final Reader value, final long length) throws SQLException {
    setAndLog("reader", () -> this.statement.setNCharacterStream(parameterIndex, value, length));
  }

  @Override
  public void setNClob(final int parameterIndex, final NClob value) throws SQLException {
    setAndLog(value, () -> this.statement.setNClob(parameterIndex, value));
  }

  @Override
  public void setClob(final int parameterIndex, final Reader reader, final long length) throws SQLException {
    setAndLog("reader", () -> this.statement.setClob(parameterIndex, reader, length));
  }

  @Override
  public void setBlob(final int parameterIndex, final InputStream inputStream, final long length) throws SQLException {
    setAndLog("inputstream", () -> this.statement.setBlob(parameterIndex, inputStream, length));
  }

  @Override
  public void setNClob(final int parameterIndex, final Reader reader, final long length) throws SQLException {
    setAndLog("reader", () -> this.statement.setNClob(parameterIndex, reader, length));
  }

  @Override
  public void setSQLXML(final int parameterIndex, final SQLXML xmlObject) throws SQLException {
    setAndLog(xmlObject, () -> this.statement.setSQLXML(parameterIndex, xmlObject));
  }

  @Override
  public void setObject(final int parameterIndex, final Object x, final int targetSqlType, final int scaleOrLength)
      throws SQLException {
    setAndLog(x, () -> this.statement.setObject(parameterIndex, x, targetSqlType, scaleOrLength));
  }

  @Override
  public void setAsciiStream(final int parameterIndex, final InputStream x, final long length) throws SQLException {
    setAndLog("inputstream", () -> this.statement.setAsciiStream(parameterIndex, x, parameterIndex));
  }

  @Override
  public void setBinaryStream(final int parameterIndex, final InputStream x, final long length) throws SQLException {
    setAndLog("inputstream", () -> this.statement.setBinaryStream(parameterIndex, x, length));
  }

  @Override
  public void setCharacterStream(final int parameterIndex, final Reader reader, final long length) throws SQLException {
    setAndLog("reader", () -> this.statement.setCharacterStream(parameterIndex, reader, length));
  }

  @Override
  public void setAsciiStream(final int parameterIndex, final InputStream x) throws SQLException {
    setAndLog("inputstream", () -> this.statement.setAsciiStream(parameterIndex, x));
  }

  @Override
  public void setBinaryStream(final int parameterIndex, final InputStream x) throws SQLException {
    setAndLog("inputstream", () -> this.statement.setBinaryStream(parameterIndex, x));
  }

  @Override
  public void setCharacterStream(final int parameterIndex, final Reader reader) throws SQLException {
    setAndLog("reader", () -> this.statement.setCharacterStream(parameterIndex, reader));
  }

  @Override
  public void setNCharacterStream(final int parameterIndex, final Reader value) throws SQLException {
    setAndLog("reader", () -> this.statement.setNCharacterStream(parameterIndex, value));
  }

  @Override
  public void setClob(final int parameterIndex, final Reader reader) throws SQLException {
    setAndLog("reader", () -> this.statement.setClob(parameterIndex, reader));
  }

  @Override
  public void setBlob(final int parameterIndex, final InputStream inputStream) throws SQLException {
    setAndLog("inputstream", () -> this.statement.setBlob(parameterIndex, inputStream));
  }

  @Override
  public void setNClob(final int parameterIndex, final Reader reader) throws SQLException {
    setAndLog("reader", () -> this.statement.setNClob(parameterIndex, reader));
  }

}
