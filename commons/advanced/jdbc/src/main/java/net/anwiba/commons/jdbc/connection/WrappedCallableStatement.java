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

import net.anwiba.commons.lang.functional.IProcedure;
import net.anwiba.commons.lang.functional.ISupplier;
import net.anwiba.commons.logging.ILogger;
import net.anwiba.commons.logging.Logging;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Map;

public class WrappedCallableStatement extends WrappedPreparedStatement implements CallableStatement {

  private static ILogger logger = Logging.getLogger(WrappedCallableStatement.class);
  private final CallableStatement statement;

  public WrappedCallableStatement(final String statementString, final CallableStatement statement) {
    super(statementString, statement);
    this.statement = statement;
  }

  public WrappedCallableStatement(final String statementString, final CallableStatement statement, IProcedure<Statement, SQLException> closeProcedure) {
    super(statementString, statement, closeProcedure);
    this.statement = statement;
  }

  protected <V> V getAndLog(final ISupplier<V, SQLException> supplier)
      throws SQLException {
    V value = supplier.supply();
    logger.debug(() -> connectionHash() + " " + statementHash() + " statement value set: "
        + ConnectionUtilities.toDebugString(value));
    return value;
  }

  @Override
  public CallableStatement getStatement() {
    return this.statement;
  }

  @Override
  public int hashCode() {
    return this.statement.hashCode();
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj instanceof WrappedCallableStatement other) {
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
  public void registerOutParameter(final int parameterIndex, final int sqlType) throws SQLException {
    this.statement.registerOutParameter(parameterIndex, sqlType);
  }

  @Override
  public void registerOutParameter(final int parameterIndex, final int sqlType, final int scale) throws SQLException {
    this.statement.registerOutParameter(parameterIndex, sqlType, scale);
  }

  @Override
  public void registerOutParameter(final int parameterIndex, final int sqlType, final String typeName)
      throws SQLException {
    this.statement.registerOutParameter(parameterIndex, sqlType, typeName);
  }

  @Override
  public void registerOutParameter(final String parameterName, final int sqlType) throws SQLException {
    this.statement.registerOutParameter(parameterName, sqlType);
  }

  @Override
  public void registerOutParameter(final String parameterName, final int sqlType, final int scale) throws SQLException {
    this.statement.registerOutParameter(parameterName, sqlType, scale);
  }

  @Override
  public void registerOutParameter(final String parameterName, final int sqlType, final String typeName)
      throws SQLException {
    this.statement.registerOutParameter(sqlType, sqlType, typeName);
  }

  @Override
  public boolean wasNull() throws SQLException {
    return this.statement.wasNull();
  }

  @Override
  public <T> T getObject(final int parameterIndex, final Class<T> type) throws SQLException {
    return this.statement.getObject(parameterIndex, type);
  }

  @Override
  public <T> T getObject(final String parameterName, final Class<T> type) throws SQLException {
    return this.statement.getObject(parameterName, type);
  }

  @Override
  public String getString(final int parameterIndex) throws SQLException {
    return this.statement.getString(parameterIndex);
  }

  @Override
  public boolean getBoolean(final int parameterIndex) throws SQLException {
    return this.statement.getBoolean(parameterIndex);
  }

  @Override
  public byte getByte(final int parameterIndex) throws SQLException {
    return this.statement.getByte(parameterIndex);
  }

  @Override
  public short getShort(final int parameterIndex) throws SQLException {
    return this.statement.getShort(parameterIndex);
  }

  @Override
  public int getInt(final int parameterIndex) throws SQLException {
    return this.statement.getInt(parameterIndex);
  }

  @Override
  public long getLong(final int parameterIndex) throws SQLException {
    return this.statement.getLong(parameterIndex);
  }

  @Override
  public float getFloat(final int parameterIndex) throws SQLException {
    return this.statement.getFloat(parameterIndex);
  }

  @Override
  public double getDouble(final int parameterIndex) throws SQLException {
    return this.statement.getDouble(parameterIndex);
  }

  @Override
  public BigDecimal getBigDecimal(final int parameterIndex, final int scale) throws SQLException {
    return this.statement.getBigDecimal(parameterIndex, scale);
  }

  @Override
  public byte[] getBytes(final int parameterIndex) throws SQLException {
    return this.statement.getBytes(parameterIndex);
  }

  @Override
  public Date getDate(final int parameterIndex) throws SQLException {
    return this.statement.getDate(parameterIndex);
  }

  @Override
  public Time getTime(final int parameterIndex) throws SQLException {
    return this.statement.getTime(parameterIndex);
  }

  @Override
  public Timestamp getTimestamp(final int parameterIndex) throws SQLException {
    return this.statement.getTimestamp(parameterIndex);
  }

  @Override
  public Object getObject(final int parameterIndex) throws SQLException {
    return this.statement.getObject(parameterIndex);
  }

  @Override
  public BigDecimal getBigDecimal(final int parameterIndex) throws SQLException {
    return this.statement.getBigDecimal(parameterIndex);
  }

  @Override
  public Object getObject(final int parameterIndex, final Map<String, Class<?>> map) throws SQLException {
    return this.statement.getObject(parameterIndex, map);
  }

  @Override
  public Ref getRef(final int parameterIndex) throws SQLException {
    return this.statement.getRef(parameterIndex);
  }

  @Override
  public Blob getBlob(final int parameterIndex) throws SQLException {
    return this.statement.getBlob(parameterIndex);
  }

  @Override
  public Clob getClob(final int parameterIndex) throws SQLException {
    return this.statement.getClob(parameterIndex);
  }

  @Override
  public Array getArray(final int parameterIndex) throws SQLException {
    return this.statement.getArray(parameterIndex);
  }

  @Override
  public Date getDate(final int parameterIndex, final Calendar cal) throws SQLException {
    return this.statement.getDate(parameterIndex, cal);
  }

  @Override
  public Time getTime(final int parameterIndex, final Calendar cal) throws SQLException {
    return this.statement.getTime(parameterIndex, cal);
  }

  @Override
  public Timestamp getTimestamp(final int parameterIndex, final Calendar cal) throws SQLException {
    return getTimestamp(parameterIndex, cal);
  }

  @Override
  public URL getURL(final int parameterIndex) throws SQLException {
    return this.statement.getURL(parameterIndex);
  }

  @Override
  public String getString(final String parameterName) throws SQLException {
    return this.statement.getString(parameterName);
  }

  @Override
  public boolean getBoolean(final String parameterName) throws SQLException {
    return this.statement.getBoolean(parameterName);
  }

  @Override
  public byte getByte(final String parameterName) throws SQLException {
    return this.statement.getByte(parameterName);
  }

  @Override
  public short getShort(final String parameterName) throws SQLException {
    return this.statement.getShort(parameterName);
  }

  @Override
  public int getInt(final String parameterName) throws SQLException {
    return this.statement.getInt(parameterName);
  }

  @Override
  public long getLong(final String parameterName) throws SQLException {
    return this.statement.getLong(parameterName);
  }

  @Override
  public float getFloat(final String parameterName) throws SQLException {
    return this.statement.getFloat(parameterName);
  }

  @Override
  public double getDouble(final String parameterName) throws SQLException {
    return this.statement.getDouble(parameterName);
  }

  @Override
  public byte[] getBytes(final String parameterName) throws SQLException {
    return this.statement.getBytes(parameterName);
  }

  @Override
  public Date getDate(final String parameterName) throws SQLException {
    return this.statement.getDate(parameterName);
  }

  @Override
  public Time getTime(final String parameterName) throws SQLException {
    return this.statement.getTime(parameterName);
  }

  @Override
  public Timestamp getTimestamp(final String parameterName) throws SQLException {
    return this.statement.getTimestamp(parameterName);
  }

  @Override
  public Object getObject(final String parameterName) throws SQLException {
    return this.statement.getObject(parameterName);
  }

  @Override
  public BigDecimal getBigDecimal(final String parameterName) throws SQLException {
    return this.statement.getBigDecimal(parameterName);
  }

  @Override
  public Object getObject(final String parameterName, final Map<String, Class<?>> map) throws SQLException {
    return this.statement.getObject(parameterName, map);
  }

  @Override
  public Ref getRef(final String parameterName) throws SQLException {
    return this.statement.getRef(parameterName);
  }

  @Override
  public Blob getBlob(final String parameterName) throws SQLException {
    return this.statement.getBlob(parameterName);
  }

  @Override
  public Clob getClob(final String parameterName) throws SQLException {
    return this.statement.getClob(parameterName);
  }

  @Override
  public Array getArray(final String parameterName) throws SQLException {
    return this.statement.getArray(parameterName);
  }

  @Override
  public Date getDate(final String parameterName, final Calendar cal) throws SQLException {
    return this.statement.getDate(parameterName, cal);
  }

  @Override
  public Time getTime(final String parameterName, final Calendar cal) throws SQLException {
    return this.statement.getTime(parameterName, cal);
  }

  @Override
  public Timestamp getTimestamp(final String parameterName, final Calendar cal) throws SQLException {
    return this.statement.getTimestamp(parameterName, cal);
  }

  @Override
  public URL getURL(final String parameterName) throws SQLException {
    return this.statement.getURL(parameterName);
  }

  @Override
  public RowId getRowId(final int parameterIndex) throws SQLException {
    return this.statement.getRowId(parameterIndex);
  }

  @Override
  public RowId getRowId(final String parameterName) throws SQLException {
    return this.statement.getRowId(parameterName);
  }

  @Override
  public SQLXML getSQLXML(final int parameterIndex) throws SQLException {
    return this.statement.getSQLXML(parameterIndex);
  }

  @Override
  public SQLXML getSQLXML(final String parameterName) throws SQLException {
    return this.statement.getSQLXML(parameterName);
  }

  @Override
  public String getNString(final int parameterIndex) throws SQLException {
    return this.statement.getNString(parameterIndex);
  }

  @Override
  public String getNString(final String parameterName) throws SQLException {
    return this.statement.getNString(parameterName);
  }

  @Override
  public Reader getNCharacterStream(final int parameterIndex) throws SQLException {
    return this.statement.getNCharacterStream(parameterIndex);
  }

  @Override
  public Reader getNCharacterStream(final String parameterName) throws SQLException {
    return this.statement.getNCharacterStream(parameterName);
  }

  @Override
  public Reader getCharacterStream(final int parameterIndex) throws SQLException {
    return this.statement.getCharacterStream(parameterIndex);
  }

  @Override
  public Reader getCharacterStream(final String parameterName) throws SQLException {
    return this.statement.getCharacterStream(parameterName);
  }

  @Override
  public void setURL(final String parameterName, final URL val) throws SQLException {
    setAndLog(val, () -> this.statement.setURL(parameterName, val));
  }

  @Override
  public void setNull(final String parameterName, final int sqlType) throws SQLException {
    setAndLog(null, () -> this.statement.setNull(parameterName, sqlType));
  }

  @Override
  public void setBoolean(final String parameterName, final boolean x) throws SQLException {
    setAndLog(x, () -> this.statement.setBoolean(parameterName, x));
  }

  @Override
  public void setByte(final String parameterName, final byte x) throws SQLException {
    setAndLog(x, () -> this.statement.setByte(parameterName, x));
  }

  @Override
  public void setShort(final String parameterName, final short x) throws SQLException {
    setAndLog(x, () -> this.statement.setShort(parameterName, x));
  }

  @Override
  public void setInt(final String parameterName, final int x) throws SQLException {
    setAndLog(x, () -> this.statement.setInt(parameterName, x));
  }

  @Override
  public void setLong(final String parameterName, final long x) throws SQLException {
    setAndLog(x, () -> this.statement.setLong(parameterName, x));
  }

  @Override
  public void setFloat(final String parameterName, final float x) throws SQLException {
    setAndLog(x, () -> this.statement.setFloat(parameterName, x));
  }

  @Override
  public void setDouble(final String parameterName, final double x) throws SQLException {
    setAndLog(x, () -> this.statement.setDouble(parameterName, x));
  }

  @Override
  public void setBigDecimal(final String parameterName, final BigDecimal x) throws SQLException {
    setAndLog(x, () -> this.statement.setBigDecimal(parameterName, x));
  }

  @Override
  public void setString(final String parameterName, final String x) throws SQLException {
    setAndLog(x, () -> this.statement.setString(parameterName, x));
  }

  @Override
  public void setBytes(final String parameterName, final byte[] x) throws SQLException {
    setAndLog(x, () -> this.statement.setBytes(parameterName, x));
  }

  @Override
  public void setDate(final String parameterName, final Date x) throws SQLException {
    setAndLog(x, () -> this.statement.setDate(parameterName, x));
  }

  @Override
  public void setTime(final String parameterName, final Time x) throws SQLException {
    setAndLog(x, () -> this.statement.setTime(parameterName, x));
  }

  @Override
  public void setTimestamp(final String parameterName, final Timestamp x) throws SQLException {
    setAndLog(x, () -> this.statement.setTimestamp(parameterName, x));
  }

  @Override
  public void setAsciiStream(final String parameterName, final InputStream x, final int length) throws SQLException {
    setAndLog("inputstream", () -> this.statement.setAsciiStream(parameterName, x, length));
  }

  @Override
  public void setBinaryStream(final String parameterName, final InputStream x, final int length) throws SQLException {
    setAndLog("binarystream", () -> this.statement.setBinaryStream(parameterName, x, length));
  }

  @Override
  public void setObject(final String parameterName, final Object x, final int targetSqlType, final int scale)
      throws SQLException {
    setAndLog(x, () -> this.statement.setObject(parameterName, x, targetSqlType, scale));
  }

  @Override
  public void setObject(final String parameterName, final Object x, final int targetSqlType) throws SQLException {
    setAndLog(x, () -> this.statement.setObject(parameterName, x, targetSqlType));
  }

  @Override
  public void setObject(final String parameterName, final Object x) throws SQLException {
    setAndLog(x, () -> this.statement.setObject(parameterName, x));
  }

  @Override
  public void setCharacterStream(final String parameterName, final Reader reader, final int length)
      throws SQLException {
    setAndLog("reader", () -> this.statement.setCharacterStream(length, reader, length));
  }

  @Override
  public void setDate(final String parameterName, final Date x, final Calendar cal) throws SQLException {
    setAndLog(x, () -> this.statement.setDate(parameterName, x, cal));
  }

  @Override
  public void setTime(final String parameterName, final Time x, final Calendar cal) throws SQLException {
    setAndLog(x, () -> this.statement.setTime(parameterName, x, cal));
  }

  @Override
  public void setTimestamp(final String parameterName, final Timestamp x, final Calendar cal) throws SQLException {
    setAndLog(x, () -> this.statement.setTimestamp(parameterName, x, cal));
  }

  @Override
  public void setNull(final String parameterName, final int sqlType, final String typeName) throws SQLException {
    setAndLog(null, () -> this.statement.setNull(parameterName, sqlType, typeName));
  }

  @Override
  public void setRowId(final String parameterName, final RowId x) throws SQLException {
    setAndLog(x, () -> this.statement.setRowId(parameterName, x));
  }

  @Override
  public void setNString(final String parameterName, final String value) throws SQLException {
    setAndLog(value, () -> this.statement.setNString(parameterName, value));
  }

  @Override
  public void setNCharacterStream(final String parameterName, final Reader value, final long length)
      throws SQLException {
    setAndLog("reader", () -> this.statement.setNCharacterStream(parameterName, value, length));
  }

  @Override
  public void setNClob(final String parameterName, final NClob value) throws SQLException {
    setAndLog(value, () -> this.statement.setNClob(parameterName, value));
  }

  @Override
  public void setClob(final String parameterName, final Reader reader, final long length) throws SQLException {
    setAndLog("reader", () -> this.statement.setClob(parameterName, reader, length));
  }

  @Override
  public void setBlob(final String parameterName, final InputStream inputStream, final long length)
      throws SQLException {
    setAndLog("inputstream", () -> this.statement.setBlob(parameterName, inputStream, length));
  }

  @Override
  public void setNClob(final String parameterName, final Reader reader, final long length) throws SQLException {
    setAndLog("reader", () -> this.statement.setNCharacterStream(parameterName, reader, length));
  }

  @Override
  public NClob getNClob(final int parameterIndex) throws SQLException {
    return this.statement.getNClob(parameterIndex);
  }

  @Override
  public NClob getNClob(final String parameterName) throws SQLException {
    return this.statement.getNClob(parameterName);
  }

  @Override
  public void setSQLXML(final String parameterName, final SQLXML xmlObject) throws SQLException {
    setAndLog(xmlObject, () -> this.statement.setSQLXML(parameterName, xmlObject));
  }

  @Override
  public void setBlob(final String parameterName, final Blob x) throws SQLException {
    setAndLog(x, () -> this.statement.setBlob(parameterName, x));
  }

  @Override
  public void setClob(final String parameterName, final Clob x) throws SQLException {
    setAndLog(x, () -> this.statement.setClob(parameterName, x));
  }

  @Override
  public void setAsciiStream(final String parameterName, final InputStream x, final long length) throws SQLException {
    setAndLog("inputstream", () -> this.statement.setAsciiStream(parameterName, x, length));
  }

  @Override
  public void setBinaryStream(final String parameterName, final InputStream x, final long length) throws SQLException {
    setAndLog("inputstream", () -> this.statement.setBinaryStream(parameterName, x, length));
  }

  @Override
  public void setCharacterStream(final String parameterName, final Reader reader, final long length)
      throws SQLException {
    setAndLog("reader", () -> this.statement.setCharacterStream(parameterName, reader, length));
  }

  @Override
  public void setAsciiStream(final String parameterName, final InputStream x) throws SQLException {
    setAndLog("inputstream", () -> this.statement.setAsciiStream(parameterName, x));
  }

  @Override
  public void setBinaryStream(final String parameterName, final InputStream x) throws SQLException {
    setAndLog("inputstream", () -> this.statement.setBinaryStream(parameterName, x));
  }

  @Override
  public void setCharacterStream(final String parameterName, final Reader reader) throws SQLException {
    setAndLog("reader", () -> this.statement.setCharacterStream(parameterName, reader));
  }

  @Override
  public void setNCharacterStream(final String parameterName, final Reader value) throws SQLException {
    setAndLog("reader", () -> this.statement.setNCharacterStream(parameterName, value));
  }

  @Override
  public void setClob(final String parameterName, final Reader reader) throws SQLException {
    setAndLog("reader", () -> this.statement.setClob(parameterName, reader));
  }

  @Override
  public void setBlob(final String parameterName, final InputStream inputStream) throws SQLException {
    setAndLog("inputstream", () -> this.statement.setBlob(parameterName, inputStream));
  }

  @Override
  public void setNClob(final String parameterName, final Reader reader) throws SQLException {
    setAndLog("reader", () -> this.statement.setNClob(parameterName, reader));
  }
}
