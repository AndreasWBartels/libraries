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
package net.anwiba.commons.jdbc.result;

import java.io.InputStream;
import java.sql.Array;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class ResultSetToResultAdapter implements IResult {

  private final ResultSet resultSet;

  public ResultSetToResultAdapter(final ResultSet resultSet) {
    this.resultSet = resultSet;
  }

  @Override
  public int getRow() throws SQLException {
    return this.resultSet.getRow();
  }

  @Override
  public String getString(final int columnIndex) throws SQLException {
    return this.resultSet.getString(columnIndex);
  }

  @Override
  public String getString(final String columnName) throws SQLException {
    return this.resultSet.getString(columnName);
  }

  @Override
  public Boolean getBoolean(final int columnIndex) throws SQLException {
    final boolean value = this.resultSet.getBoolean(columnIndex);
    if (this.resultSet.wasNull()) {
      return null;
    }
    return Boolean.valueOf(value);
  }

  @Override
  public boolean getBoolean(final int columnIndex, final boolean nullValue) throws SQLException {
    final boolean value = this.resultSet.getBoolean(columnIndex);
    if (this.resultSet.wasNull()) {
      return nullValue;
    }
    return value;
  }

  @Override
  public Byte getByte(final int columnIndex) throws SQLException {
    final byte value = this.resultSet.getByte(columnIndex);
    if (this.resultSet.wasNull()) {
      return null;
    }
    return Byte.valueOf(value);
  }

  @Override
  public byte getByte(final int columnIndex, final byte nullValue) throws SQLException {
    final byte value = this.resultSet.getByte(columnIndex);
    if (this.resultSet.wasNull()) {
      return nullValue;
    }
    return value;
  }

  @Override
  public Short getShort(final int columnIndex) throws SQLException {
    final short value = this.resultSet.getShort(columnIndex);
    if (this.resultSet.wasNull()) {
      return null;
    }
    return Short.valueOf(value);
  }

  @Override
  public short getShort(final int columnIndex, final short nullValue) throws SQLException {
    final short value = this.resultSet.getShort(columnIndex);
    if (this.resultSet.wasNull()) {
      return nullValue;
    }
    return value;
  }

  @Override
  public Integer getInteger(final int columnIndex) throws SQLException {
    final int value = this.resultSet.getInt(columnIndex);
    if (this.resultSet.wasNull()) {
      return null;
    }
    return Integer.valueOf(value);
  }

  @Override
  public int getInteger(final int columnIndex, final int nullValue) throws SQLException {
    final int value = this.resultSet.getInt(columnIndex);
    if (this.resultSet.wasNull()) {
      return nullValue;
    }
    return value;
  }

  @Override
  public Long getLong(final int columnIndex) throws SQLException {
    final long value = this.resultSet.getLong(columnIndex);
    if (this.resultSet.wasNull()) {
      return null;
    }
    return Long.valueOf(value);
  }

  @Override
  public long getLong(final int columnIndex, final long nullValue) throws SQLException {
    final long value = this.resultSet.getLong(columnIndex);
    if (this.resultSet.wasNull()) {
      return nullValue;
    }
    return value;
  }

  @Override
  public Float getFloat(final int columnIndex) throws SQLException {
    final float value = this.resultSet.getFloat(columnIndex);
    if (this.resultSet.wasNull()) {
      return null;
    }
    return Float.valueOf(value);
  }

  @Override
  public float getFloat(final int columnIndex, final float nullValue) throws SQLException {
    final float value = this.resultSet.getFloat(columnIndex);
    if (this.resultSet.wasNull()) {
      return nullValue;
    }
    return value;
  }

  @Override
  public Double getDouble(final int columnIndex) throws SQLException {
    final double value = this.resultSet.getDouble(columnIndex);
    if (this.resultSet.wasNull()) {
      return null;
    }
    return Double.valueOf(value);
  }

  @Override
  public double getDouble(final int columnIndex, final double nullValue) throws SQLException {
    final double value = this.resultSet.getDouble(columnIndex);
    if (this.resultSet.wasNull()) {
      return nullValue;
    }
    return value;
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
  public InputStream getBinaryStream(final int columnIndex) throws SQLException {
    return this.resultSet.getBinaryStream(columnIndex);
  }

  @Override
  public Object getObject(final int columnIndex) throws SQLException {
    return this.resultSet.getObject(columnIndex);
  }

  @Override
  public Object getObject(final String columnName) throws SQLException {
    return this.resultSet.getObject(columnName);
  }

  @Override
  public Array getArray(final int columnIndex) throws SQLException {
    return this.resultSet.getArray(columnIndex);
  }

  @Override
  public Array getArray(final String columnName) throws SQLException {
    return this.resultSet.getArray(columnName);
  }

  @Override
  public int getNumberOfValues() throws SQLException {
    return this.resultSet.getMetaData().getColumnCount();
  }

  @Override
  public List<String> getColumnNames() throws SQLException {
    final ResultSetMetaData metaData = this.resultSet.getMetaData();
    LinkedList<String> names = new LinkedList<>();
    final int columnCount = metaData.getColumnCount();
    for (int i = 0; i < columnCount; i++) {
      names.add(metaData.getColumnName(i + 1));
    }
    return names;
  }

  @Override
  public boolean hasColumn(final String columnName) throws SQLException {
    final ResultSetMetaData metaData = this.resultSet.getMetaData();
    final int columnCount = metaData.getColumnCount();
    for (int i = 0; i < columnCount; i++) {
      if (Objects.equals(metaData.getColumnName(i + 1), columnName)) {
        return true;
      }
    }
    return false;
  }

}
