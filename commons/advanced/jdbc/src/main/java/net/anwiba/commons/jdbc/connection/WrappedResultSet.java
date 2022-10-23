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
import net.anwiba.commons.lang.functional.ISupplier;
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
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Map;

public class WrappedResultSet implements ResultSet {

  private static ILogger logger = Logging.getLogger(WrappedStatement.class);
  private final ResultSet resultSet;
  private String connectionHash;
  private String statementHash;
  private String resultSetHash;
  private Statement statement;

  public WrappedResultSet(final ResultSet resultSet, final Statement statement) {
    this.resultSet = resultSet;
    this.statement = statement;
  }

  public ResultSet getResultSet() {
    return this.resultSet;
  }

  @Override
  public int hashCode() {
    return this.resultSet.hashCode();
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj instanceof WrappedResultSet other) {
      return this.resultSet.equals(other.resultSet);
    }
    return this.resultSet.equals(obj);
  }

  @Override
  public <T> T unwrap(final Class<T> iface) throws SQLException {
    if (iface.isInstance(this.resultSet)) {
      return (T) this.resultSet;
    }
    return this.resultSet.unwrap(iface);
  }

  @Override
  public boolean isWrapperFor(final Class<?> iface) throws SQLException {
    return iface.isInstance(this.resultSet) || this.resultSet.isWrapperFor(iface);
  }

  protected String connectionHash() {
    try {
      if (this.connectionHash == null) {
        this.connectionHash = ConnectionUtilities.hash(getStatement().getConnection());
      }
      return this.connectionHash;
    } catch (SQLException exception) {
      return ConnectionUtilities.nullHash();
    }
  }

  protected String statementHash() {
    try {
      if (this.statementHash == null) {
        this.statementHash = ConnectionUtilities.hash(getStatement().getConnection());
      }
      return this.statementHash;
    } catch (SQLException exception) {
      return ConnectionUtilities.nullHash();
    }
  }

  protected String resultSetHash() {
    if (this.resultSetHash == null) {
      this.resultSetHash = ConnectionUtilities.hash(this);
    }
    return this.resultSetHash;
  }

  protected void updateAndLog(final Object value, final IBlock<SQLException> block)
      throws SQLException {
    logger.debug(() -> connectionHash() + " " + statementHash() + " statement value set: "
        + ConnectionUtilities.toDebugString(value));
    block.execute();
  }

  protected <V> V getAndLog(final ISupplier<V, SQLException> supplier)
      throws SQLException {
    V value = supplier.supply();
    logger.debug(() -> connectionHash() + " " + statementHash() + " statement value set: "
        + ConnectionUtilities.toDebugString(value));
    return value;
  }

  @Override
  public boolean next() throws SQLException {
    return this.resultSet.next();
  }

  @Override
  public void close() throws SQLException {
    logger.fine(() -> connectionHash() + " " + statementHash() + " " + resultSetHash() + " resultset close");
    this.resultSet.close();
    logger.debug(() -> connectionHash() + " " + statementHash() + " " + resultSetHash() + " resultset closed");
  }

  @Override
  public SQLWarning getWarnings() throws SQLException {
    return this.resultSet.getWarnings();
  }

  @Override
  public void clearWarnings() throws SQLException {
    this.resultSet.clearWarnings();
  }

  @Override
  public String getCursorName() throws SQLException {
    return this.resultSet.getCursorName();
  }

  @Override
  public ResultSetMetaData getMetaData() throws SQLException {
    return this.resultSet.getMetaData();
  }

  @Override
  public int findColumn(final String columnLabel) throws SQLException {
    return this.resultSet.findColumn(columnLabel);
  }

  @Override
  public boolean isClosed() throws SQLException {
    return this.resultSet.isClosed();
  }

  @Override
  public boolean isBeforeFirst() throws SQLException {
    return this.resultSet.isBeforeFirst();
  }

  @Override
  public boolean isAfterLast() throws SQLException {
    return this.resultSet.isAfterLast();
  }

  @Override
  public boolean isFirst() throws SQLException {
    return this.resultSet.isFirst();
  }

  @Override
  public boolean isLast() throws SQLException {
    return this.resultSet.isLast();
  }

  @Override
  public void beforeFirst() throws SQLException {
    this.resultSet.beforeFirst();
  }

  @Override
  public void afterLast() throws SQLException {
    this.resultSet.afterLast();
  }

  @Override
  public boolean first() throws SQLException {
    return this.resultSet.first();
  }

  @Override
  public boolean last() throws SQLException {
    return this.resultSet.last();
  }

  @Override
  public int getRow() throws SQLException {
    return this.resultSet.getRow();
  }

  @Override
  public boolean absolute(final int row) throws SQLException {
    return this.resultSet.absolute(row);
  }

  @Override
  public boolean relative(final int rows) throws SQLException {
    return this.resultSet.relative(rows);
  }

  @Override
  public boolean previous() throws SQLException {
    return this.resultSet.previous();
  }

  @Override
  public void setFetchDirection(final int direction) throws SQLException {
    this.resultSet.setFetchDirection(direction);
  }

  @Override
  public int getFetchDirection() throws SQLException {
    return this.resultSet.getFetchDirection();
  }

  @Override
  public void setFetchSize(final int rows) throws SQLException {
    this.resultSet.setFetchSize(rows);
  }

  @Override
  public int getFetchSize() throws SQLException {
    return this.resultSet.getFetchSize();
  }

  @Override
  public int getType() throws SQLException {
    return this.resultSet.getType();
  }

  @Override
  public int getConcurrency() throws SQLException {
    return this.resultSet.getConcurrency();
  }

  @Override
  public boolean rowUpdated() throws SQLException {
    return this.resultSet.rowUpdated();
  }

  @Override
  public boolean rowInserted() throws SQLException {
    return this.resultSet.rowInserted();
  }

  @Override
  public boolean rowDeleted() throws SQLException {
    return this.resultSet.rowDeleted();
  }

  @Override
  public void insertRow() throws SQLException {
    this.resultSet.insertRow();
  }

  @Override
  public void updateRow() throws SQLException {
    this.resultSet.updateRow();
  }

  @Override
  public void deleteRow() throws SQLException {
    this.resultSet.deleteRow();
  }

  @Override
  public void refreshRow() throws SQLException {
    this.resultSet.refreshRow();
  }

  @Override
  public void cancelRowUpdates() throws SQLException {
    logger.fine(() -> connectionHash() + " " + statementHash() + " " + resultSetHash() + " resultset update cancel");
    this.resultSet.cancelRowUpdates();
    logger.debug(() -> connectionHash() + " " + statementHash() + " " + resultSetHash() + " resultset update canceled");
  }

  @Override
  public void moveToInsertRow() throws SQLException {
    this.resultSet.moveToInsertRow();
  }

  @Override
  public void moveToCurrentRow() throws SQLException {
    this.resultSet.moveToCurrentRow();
  }

  @Override
  public Statement getStatement() throws SQLException {
    return statement;
  }

  @Override
  public int getHoldability() throws SQLException {
    return this.resultSet.getHoldability();
  }


  @Override
  public Object getObject(final int columnIndex) throws SQLException {
    return this.resultSet.getObject(columnIndex);
  }

  @Override
  public Object getObject(final String columnLabel) throws SQLException {
    return this.resultSet.getObject(columnLabel);
  }

  @Override
  public Object getObject(final int columnIndex, final Map<String, Class<?>> map) throws SQLException {
    return this.resultSet.getObject(columnIndex, map);
  }

  @Override
  public Object getObject(final String columnLabel, final Map<String, Class<?>> map) throws SQLException {
    return this.resultSet.getObject(columnLabel, map);
  }

  @Override
  public <T> T getObject(final int columnIndex, final Class<T> type) throws SQLException {
    return this.resultSet.getObject(columnIndex, type);
  }

  @Override
  public <T> T getObject(final String columnLabel, final Class<T> type) throws SQLException {
    return this.resultSet.getObject(columnLabel, type);
  }

  @Override
  public boolean wasNull() throws SQLException {
    return this.resultSet.wasNull();
  }

  @Override
  public String getString(final int columnIndex) throws SQLException {
    return this.resultSet.getString(columnIndex);
  }

  @Override
  public boolean getBoolean(final int columnIndex) throws SQLException {
    return this.resultSet.getBoolean(columnIndex);
    }

  @Override
  public byte getByte(final int columnIndex) throws SQLException {
    return this.resultSet.getByte(columnIndex);
  }

  @Override
  public short getShort(final int columnIndex) throws SQLException {
    return this.resultSet.getShort(columnIndex);
  }

  @Override
  public int getInt(final int columnIndex) throws SQLException {
    return this.resultSet.getInt(columnIndex);
  }

  @Override
  public long getLong(final int columnIndex) throws SQLException {
    return this.resultSet.getLong(columnIndex);
  }

  @Override
  public float getFloat(final int columnIndex) throws SQLException {
    return this.resultSet.getFloat(columnIndex);
  }

  @Override
  public double getDouble(final int columnIndex) throws SQLException {
    return this.resultSet.getDouble(columnIndex);
  }

  @Override
  public BigDecimal getBigDecimal(final int columnIndex, final int scale) throws SQLException {
    return this.resultSet.getBigDecimal(columnIndex);
  }

  @Override
  public byte[] getBytes(final int columnIndex) throws SQLException {
    return this.resultSet.getBytes(columnIndex);
  }

  @Override
  public Date getDate(final int columnIndex) throws SQLException {
    return this.resultSet.getDate(columnIndex);
  }

  @Override
  public Time getTime(final int columnIndex) throws SQLException {
    return this.resultSet.getTime(columnIndex);
  }

  @Override
  public Timestamp getTimestamp(final int columnIndex) throws SQLException {
    return this.resultSet.getTimestamp(columnIndex);
  }

  @Override
  public InputStream getAsciiStream(final int columnIndex) throws SQLException {
    return this.resultSet.getAsciiStream(columnIndex);
  }

  @Override
  public InputStream getUnicodeStream(final int columnIndex) throws SQLException {
    return this.resultSet.getUnicodeStream(columnIndex);
  }

  @Override
  public InputStream getBinaryStream(final int columnIndex) throws SQLException {
    return this.resultSet.getBinaryStream(columnIndex);
  }

  @Override
  public String getString(final String columnLabel) throws SQLException {
    return this.resultSet.getString(columnLabel);
  }

  @Override
  public boolean getBoolean(final String columnLabel) throws SQLException {
    return this.resultSet.getBoolean(columnLabel);
  }

  @Override
  public byte getByte(final String columnLabel) throws SQLException {
    return this.resultSet.getByte(columnLabel);
  }

  @Override
  public short getShort(final String columnLabel) throws SQLException {
    return this.resultSet.getShort(columnLabel);
  }

  @Override
  public int getInt(final String columnLabel) throws SQLException {
    return this.resultSet.getInt(columnLabel);
  }

  @Override
  public long getLong(final String columnLabel) throws SQLException {
    return this.resultSet.getLong(columnLabel);
  }

  @Override
  public float getFloat(final String columnLabel) throws SQLException {
    return this.resultSet.getFloat(columnLabel);
  }

  @Override
  public double getDouble(final String columnLabel) throws SQLException {
    return this.resultSet.getDouble(columnLabel);
  }

  @Override
  public BigDecimal getBigDecimal(final String columnLabel, final int scale) throws SQLException {
    return this.resultSet.getBigDecimal(columnLabel, scale);
  }

  @Override
  public byte[] getBytes(final String columnLabel) throws SQLException {
    return this.resultSet.getBytes(columnLabel);
  }

  @Override
  public Date getDate(final String columnLabel) throws SQLException {
    return this.resultSet.getDate(columnLabel);
  }

  @Override
  public Time getTime(final String columnLabel) throws SQLException {
    return this.resultSet.getTime(columnLabel);
  }

  @Override
  public Timestamp getTimestamp(final String columnLabel) throws SQLException {
    return this.resultSet.getTimestamp(columnLabel);
  }

  @Override
  public InputStream getAsciiStream(final String columnLabel) throws SQLException {
    return this.resultSet.getAsciiStream(columnLabel);
  }

  @Override
  public InputStream getUnicodeStream(final String columnLabel) throws SQLException {
    return this.resultSet.getUnicodeStream(columnLabel);
  }

  @Override
  public InputStream getBinaryStream(final String columnLabel) throws SQLException {
    return this.resultSet.getBinaryStream(columnLabel);
  }

  @Override
  public Reader getCharacterStream(final int columnIndex) throws SQLException {
    return this.resultSet.getCharacterStream(columnIndex);
  }

  @Override
  public Reader getCharacterStream(final String columnLabel) throws SQLException {
    return this.resultSet.getCharacterStream(columnLabel);
  }

  @Override
  public BigDecimal getBigDecimal(final int columnIndex) throws SQLException {
    return this.resultSet.getBigDecimal(columnIndex);
  }

  @Override
  public BigDecimal getBigDecimal(final String columnLabel) throws SQLException {
    return this.resultSet.getBigDecimal(columnLabel);
  }

  @Override
  public Ref getRef(final int columnIndex) throws SQLException {
    return this.resultSet.getRef(columnIndex);
  }

  @Override
  public Blob getBlob(final int columnIndex) throws SQLException {
    return this.resultSet.getBlob(columnIndex);
  }

  @Override
  public Clob getClob(final int columnIndex) throws SQLException {
    return this.resultSet.getClob(columnIndex);
  }

  @Override
  public Array getArray(final int columnIndex) throws SQLException {
    return this.resultSet.getArray(columnIndex);
  }

  @Override
  public Ref getRef(final String columnLabel) throws SQLException {
    return this.resultSet.getRef(columnLabel);
  }

  @Override
  public Blob getBlob(final String columnLabel) throws SQLException {
    return this.resultSet.getBlob(columnLabel);
  }

  @Override
  public Clob getClob(final String columnLabel) throws SQLException {
    return this.resultSet.getClob(columnLabel);
  }

  @Override
  public Array getArray(final String columnLabel) throws SQLException {
    return this.resultSet.getArray(columnLabel);
  }

  @Override
  public Date getDate(final int columnIndex, final Calendar cal) throws SQLException {
    return this.resultSet.getDate(columnIndex, cal);
  }

  @Override
  public Date getDate(final String columnLabel, final Calendar cal) throws SQLException {
    return this.resultSet.getDate(columnLabel, cal);
  }

  @Override
  public Time getTime(final int columnIndex, final Calendar cal) throws SQLException {
    return this.resultSet.getTime(columnIndex, cal);
  }

  @Override
  public Time getTime(final String columnLabel, final Calendar cal) throws SQLException {
    return this.resultSet.getTime(columnLabel, cal);
  }

  @Override
  public Timestamp getTimestamp(final int columnIndex, final Calendar cal) throws SQLException {
    return this.resultSet.getTimestamp(columnIndex, cal);
  }

  @Override
  public Timestamp getTimestamp(final String columnLabel, final Calendar cal) throws SQLException {
    return this.resultSet.getTimestamp(columnLabel, cal);
  }

  @Override
  public URL getURL(final int columnIndex) throws SQLException {
    return this.resultSet.getURL(columnIndex);
  }

  @Override
  public URL getURL(final String columnLabel) throws SQLException {
    return this.resultSet.getURL(columnLabel);
  }

  @Override
  public RowId getRowId(final int columnIndex) throws SQLException {
    return this.resultSet.getRowId(columnIndex);
  }

  @Override
  public RowId getRowId(final String columnLabel) throws SQLException {
    return this.resultSet.getRowId(columnLabel);
  }

  @Override
  public NClob getNClob(final int columnIndex) throws SQLException {
    return this.resultSet.getNClob(columnIndex);
  }

  @Override
  public NClob getNClob(final String columnLabel) throws SQLException {
    return this.resultSet.getNClob(columnLabel);
  }

  @Override
  public SQLXML getSQLXML(final int columnIndex) throws SQLException {
    return this.resultSet.getSQLXML(columnIndex);
  }

  @Override
  public SQLXML getSQLXML(final String columnLabel) throws SQLException {
    return this.resultSet.getSQLXML(columnLabel);
  }

  @Override
  public String getNString(final int columnIndex) throws SQLException {
    return this.resultSet.getNString(columnIndex);
  }

  @Override
  public String getNString(final String columnLabel) throws SQLException {
    return this.resultSet.getNString(columnLabel);
  }

  @Override
  public Reader getNCharacterStream(final int columnIndex) throws SQLException {
    return this.resultSet.getNCharacterStream(columnIndex);
  }

  @Override
  public Reader getNCharacterStream(final String columnLabel) throws SQLException {
    return this.resultSet.getNCharacterStream(columnLabel);
  }

  @Override
  public void updateObject(final String columnLabel, final Object x, final int scaleOrLength) throws SQLException {
    this.resultSet.updateObject(columnLabel, x, null, scaleOrLength);
  }

  @Override
  public void updateObject(final String columnLabel, final Object x) throws SQLException {
    this.resultSet.updateObject(columnLabel, x);
  }

  @Override
  public void updateNull(final int columnIndex) throws SQLException {
    this.resultSet.updateNull(columnIndex);
  }

  @Override
  public void updateBoolean(final int columnIndex, final boolean x) throws SQLException {
    this.resultSet.updateBoolean(columnIndex, x);
  }

  @Override
  public void updateByte(final int columnIndex, final byte x) throws SQLException {
    this.resultSet.updateByte(columnIndex, x);
  }

  @Override
  public void updateShort(final int columnIndex, final short x) throws SQLException {
    this.resultSet.updateShort(columnIndex, x);
  }

  @Override
  public void updateInt(final int columnIndex, final int x) throws SQLException {
    this.resultSet.updateInt(columnIndex, x);
  }

  @Override
  public void updateLong(final int columnIndex, final long x) throws SQLException {
    this.resultSet.updateLong(columnIndex, x);
  }

  @Override
  public void updateFloat(final int columnIndex, final float x) throws SQLException {
    this.resultSet.updateFloat(columnIndex, x);
  }

  @Override
  public void updateDouble(final int columnIndex, final double x) throws SQLException {
    this.resultSet.updateDouble(columnIndex, x);
  }

  @Override
  public void updateBigDecimal(final int columnIndex, final BigDecimal x) throws SQLException {
    this.resultSet.updateBigDecimal(columnIndex, x);
  }

  @Override
  public void updateString(final int columnIndex, final String x) throws SQLException {
    this.resultSet.updateString(columnIndex, x);
  }

  @Override
  public void updateBytes(final int columnIndex, final byte[] x) throws SQLException {
    this.updateBytes(columnIndex, x);
  }

  @Override
  public void updateDate(final int columnIndex, final Date x) throws SQLException {
    this.resultSet.updateDate(columnIndex, x);
  }

  @Override
  public void updateTime(final int columnIndex, final Time x) throws SQLException {
    this.resultSet.updateTime(columnIndex, x);
  }

  @Override
  public void updateTimestamp(final int columnIndex, final Timestamp x) throws SQLException {
    this.resultSet.updateTimestamp(columnIndex, x);
  }

  @Override
  public void updateAsciiStream(final int columnIndex, final InputStream x, final int length) throws SQLException {
    this.resultSet.updateAsciiStream(columnIndex, x, length);
  }

  @Override
  public void updateBinaryStream(final int columnIndex, final InputStream x, final int length) throws SQLException {
    this.resultSet.updateBinaryStream(columnIndex, x, length);
  }

  @Override
  public void updateCharacterStream(final int columnIndex, final Reader x, final int length) throws SQLException {
    this.resultSet.updateCharacterStream(columnIndex, x, length);
  }

  @Override
  public void updateObject(final int columnIndex, final Object x, final int scaleOrLength) throws SQLException {
    this.resultSet.updateObject(columnIndex, x, scaleOrLength);
  }

  @Override
  public void updateObject(final int columnIndex, final Object x) throws SQLException {
    this.resultSet.updateObject(columnIndex, x);
  }

  @Override
  public void updateNull(final String columnLabel) throws SQLException {
    this.resultSet.updateNull(columnLabel);
  }

  @Override
  public void updateBoolean(final String columnLabel, final boolean x) throws SQLException {
    this.resultSet.updateBoolean(columnLabel, x);
  }

  @Override
  public void updateByte(final String columnLabel, final byte x) throws SQLException {
    this.resultSet.updateByte(columnLabel, x);
  }

  @Override
  public void updateShort(final String columnLabel, final short x) throws SQLException {
    this.resultSet.updateShort(columnLabel, x);
  }

  @Override
  public void updateInt(final String columnLabel, final int x) throws SQLException {
    this.resultSet.updateInt(columnLabel, x);
  }

  @Override
  public void updateLong(final String columnLabel, final long x) throws SQLException {
    this.resultSet.updateLong(columnLabel, x);
  }

  @Override
  public void updateFloat(final String columnLabel, final float x) throws SQLException {
    this.resultSet.updateFloat(columnLabel, x);
  }

  @Override
  public void updateDouble(final String columnLabel, final double x) throws SQLException {
    this.resultSet.updateDouble(columnLabel, x);
  }

  @Override
  public void updateBigDecimal(final String columnLabel, final BigDecimal x) throws SQLException {
    this.resultSet.updateBigDecimal(columnLabel, x);
  }

  @Override
  public void updateString(final String columnLabel, final String x) throws SQLException {
    this.resultSet.updateString(columnLabel, x);
  }

  @Override
  public void updateBytes(final String columnLabel, final byte[] x) throws SQLException {
    this.resultSet.updateBytes(columnLabel, x);
  }

  @Override
  public void updateDate(final String columnLabel, final Date x) throws SQLException {
    this.resultSet.updateDate(columnLabel, x);
  }

  @Override
  public void updateTime(final String columnLabel, final Time x) throws SQLException {
    this.resultSet.updateTime(columnLabel, x);
  }

  @Override
  public void updateTimestamp(final String columnLabel, final Timestamp x) throws SQLException {
    this.resultSet.updateTimestamp(columnLabel, x);
  }

  @Override
  public void updateAsciiStream(final String columnLabel, final InputStream x, final int length) throws SQLException {
    this.resultSet.updateAsciiStream(columnLabel, x, length);
  }

  @Override
  public void updateBinaryStream(final String columnLabel, final InputStream x, final int length) throws SQLException {
    this.resultSet.updateBinaryStream(columnLabel, x, length);
  }

  @Override
  public void updateCharacterStream(final String columnLabel, final Reader reader, final int length)
      throws SQLException {
    this.resultSet.updateCharacterStream(columnLabel, reader, length);
  }

  @Override
  public void updateRef(final int columnIndex, final Ref x) throws SQLException {
    this.resultSet.updateRef(columnIndex, x);
  }

  @Override
  public void updateRef(final String columnLabel, final Ref x) throws SQLException {
    this.resultSet.updateRef(columnLabel, x);
  }

  @Override
  public void updateBlob(final int columnIndex, final Blob x) throws SQLException {
    this.resultSet.updateBlob(columnIndex, x);
  }

  @Override
  public void updateBlob(final String columnLabel, final Blob x) throws SQLException {
    this.resultSet.updateBlob(columnLabel, x);
  }

  @Override
  public void updateClob(final int columnIndex, final Clob x) throws SQLException {
    this.resultSet.updateClob(columnIndex, x);
  }

  @Override
  public void updateClob(final String columnLabel, final Clob x) throws SQLException {
    this.resultSet.updateClob(columnLabel, x);
  }

  @Override
  public void updateArray(final int columnIndex, final Array x) throws SQLException {
    this.resultSet.updateArray(columnIndex, x);
  }

  @Override
  public void updateArray(final String columnLabel, final Array x) throws SQLException {
    this.resultSet.updateArray(columnLabel, x);
  }

  @Override
  public void updateRowId(final int columnIndex, final RowId x) throws SQLException {
    this.resultSet.updateRowId(columnIndex, x);
  }

  @Override
  public void updateRowId(final String columnLabel, final RowId x) throws SQLException {
    this.resultSet.updateRowId(columnLabel, x);
  }

  @Override
  public void updateNString(final int columnIndex, final String nString) throws SQLException {
    this.resultSet.updateNString(columnIndex, nString);
  }

  @Override
  public void updateNString(final String columnLabel, final String nString) throws SQLException {
    this.resultSet.updateNString(columnLabel, nString);
  }

  @Override
  public void updateNClob(final int columnIndex, final NClob nClob) throws SQLException {
    this.resultSet.updateNClob(columnIndex, nClob);
  }

  @Override
  public void updateNClob(final String columnLabel, final NClob nClob) throws SQLException {
    this.resultSet.updateNClob(columnLabel, nClob);
  }

  @Override
  public void updateNCharacterStream(final int columnIndex, final Reader x, final long length) throws SQLException {
    this.resultSet.updateNCharacterStream(columnIndex, x);
  }

  @Override
  public void updateNCharacterStream(final String columnLabel, final Reader reader, final long length)
      throws SQLException {
    this.resultSet.updateNCharacterStream(columnLabel, reader, length);
  }

  @Override
  public void updateAsciiStream(final int columnIndex, final InputStream x, final long length) throws SQLException {
    this.resultSet.updateAsciiStream(columnIndex, x, length);
  }

  @Override
  public void updateBinaryStream(final int columnIndex, final InputStream x, final long length) throws SQLException {
    this.resultSet.updateBinaryStream(columnIndex, x, length);
  }

  @Override
  public void updateCharacterStream(final int columnIndex, final Reader x, final long length) throws SQLException {
    this.resultSet.updateCharacterStream(columnIndex, x, length);
  }

  @Override
  public void updateAsciiStream(final String columnLabel, final InputStream x, final long length) throws SQLException {
    this.resultSet.updateAsciiStream(columnLabel, x, length);
  }

  @Override
  public void updateBinaryStream(final String columnLabel, final InputStream x, final long length) throws SQLException {
    this.resultSet.updateBinaryStream(columnLabel, x, length);
  }

  @Override
  public void updateCharacterStream(final String columnLabel, final Reader reader, final long length)
      throws SQLException {
    this.resultSet.updateNCharacterStream(columnLabel, reader, length);
  }

  @Override
  public void updateBlob(final int columnIndex, final InputStream inputStream, final long length) throws SQLException {
    this.resultSet.updateBlob(columnIndex, inputStream, length);
  }

  @Override
  public void updateBlob(final String columnLabel, final InputStream inputStream, final long length)
      throws SQLException {
    this.resultSet.updateBlob(columnLabel, inputStream, length);
  }

  @Override
  public void updateClob(final int columnIndex, final Reader reader, final long length) throws SQLException {
    this.resultSet.updateClob(columnIndex, reader, length);
  }

  @Override
  public void updateClob(final String columnLabel, final Reader reader, final long length) throws SQLException {
    this.resultSet.updateClob(columnLabel, reader, length);
  }

  @Override
  public void updateNClob(final int columnIndex, final Reader reader, final long length) throws SQLException {
    this.resultSet.updateNClob(columnIndex, reader, length);
  }

  @Override
  public void updateNClob(final String columnLabel, final Reader reader, final long length) throws SQLException {
    this.resultSet.updateNClob(columnLabel, reader, length);
  }

  @Override
  public void updateNCharacterStream(final int columnIndex, final Reader x) throws SQLException {
    this.resultSet.updateNCharacterStream(columnIndex, x);
  }

  @Override
  public void updateNCharacterStream(final String columnLabel, final Reader reader) throws SQLException {
    this.resultSet.updateNCharacterStream(columnLabel, reader);
  }

  @Override
  public void updateAsciiStream(final int columnIndex, final InputStream x) throws SQLException {
    this.resultSet.updateAsciiStream(columnIndex, x);
  }

  @Override
  public void updateBinaryStream(final int columnIndex, final InputStream x) throws SQLException {
    this.resultSet.updateBinaryStream(columnIndex, x);
  }

  @Override
  public void updateCharacterStream(final int columnIndex, final Reader x) throws SQLException {
    this.resultSet.updateCharacterStream(columnIndex, x);
  }

  @Override
  public void updateAsciiStream(final String columnLabel, final InputStream x) throws SQLException {
    this.resultSet.updateAsciiStream(columnLabel, x);
  }

  @Override
  public void updateBinaryStream(final String columnLabel, final InputStream x) throws SQLException {
    this.resultSet.updateBinaryStream(columnLabel, x);
  }

  @Override
  public void updateCharacterStream(final String columnLabel, final Reader reader) throws SQLException {
    this.resultSet.updateCharacterStream(columnLabel, reader);
  }

  @Override
  public void updateBlob(final int columnIndex, final InputStream inputStream) throws SQLException {
    this.resultSet.updateBlob(columnIndex, inputStream);
  }

  @Override
  public void updateBlob(final String columnLabel, final InputStream inputStream) throws SQLException {
    this.resultSet.updateBlob(columnLabel, inputStream);
  }

  @Override
  public void updateClob(final int columnIndex, final Reader reader) throws SQLException {
    this.resultSet.updateClob(columnIndex, reader);
  }

  @Override
  public void updateClob(final String columnLabel, final Reader reader) throws SQLException {
    this.resultSet.updateClob(columnLabel, reader);
  }

  @Override
  public void updateNClob(final int columnIndex, final Reader reader) throws SQLException {
    this.resultSet.updateNClob(columnIndex, reader);
  }

  @Override
  public void updateNClob(final String columnLabel, final Reader reader) throws SQLException {
    this.resultSet.updateNClob(columnLabel, reader);
  }

  @Override
  public void updateSQLXML(final int columnIndex, final SQLXML xmlObject) throws SQLException {
    this.resultSet.updateSQLXML(columnIndex, xmlObject);
  }

  @Override
  public void updateSQLXML(final String columnLabel, final SQLXML xmlObject) throws SQLException {
    this.resultSet.updateSQLXML(columnLabel, xmlObject);
  }
}
