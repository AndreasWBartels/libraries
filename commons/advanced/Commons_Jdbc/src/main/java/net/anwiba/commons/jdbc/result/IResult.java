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
package net.anwiba.commons.jdbc.result;

import java.sql.SQLException;

public interface IResult {

  int getRow() throws SQLException;

  String getString(int columnIndex) throws SQLException;

  String getString(String string) throws SQLException;

  Boolean getBoolean(int columnIndex) throws SQLException;

  boolean getBoolean(int columnIndex, boolean nullValue) throws SQLException;

  Byte getByte(int columnIndex) throws SQLException;

  byte getByte(int columnIndex, byte nullValue) throws SQLException;

  Short getShort(int columnIndex) throws SQLException;

  short getShort(int columnIndex, short nullValue) throws SQLException;

  Integer getInteger(int columnIndex) throws SQLException;

  int getInteger(int columnIndex, int nullValue) throws SQLException;

  Long getLong(int columnIndex) throws SQLException;

  long getLong(int columnIndex, int nullValue) throws SQLException;

  Float getFloat(int columnIndex) throws SQLException;

  float getFloat(int columnIndex, float nullValue) throws SQLException;

  Double getDouble(int columnIndex) throws SQLException;

  double getDouble(int columnIndex, double nullValue) throws SQLException;

  byte[] getBytes(int columnIndex) throws SQLException;

  java.sql.Date getDate(int columnIndex) throws SQLException;

  java.sql.Time getTime(int columnIndex) throws SQLException;

  java.sql.Timestamp getTimestamp(int columnIndex) throws SQLException;

  java.io.InputStream getAsciiStream(int columnIndex) throws SQLException;

  java.io.InputStream getBinaryStream(int columnIndex) throws SQLException;

  Object getObject(int columnIndex) throws SQLException;

  Object getObject(String columnName) throws SQLException;

  int getNumberOfValues() throws SQLException;

  boolean hasColumn(String string) throws SQLException;

}
