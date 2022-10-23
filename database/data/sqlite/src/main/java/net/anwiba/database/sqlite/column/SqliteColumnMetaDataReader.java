/*
 * #%L
 *
 * %%
 * Copyright (C) 2007 - 2016 Andreas W. Bartels
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
package net.anwiba.database.sqlite.column;

import net.anwiba.commons.jdbc.DatabaseUtilities;
import net.anwiba.commons.jdbc.metadata.ColumnMetaData;
import net.anwiba.commons.jdbc.metadata.IColumnMetaData;
import net.anwiba.commons.jdbc.result.IResult;
import net.anwiba.commons.lang.functional.IConverter;
import net.anwiba.commons.lang.functional.IProcedure;
import net.anwiba.commons.lang.object.ObjectPair;
import net.anwiba.commons.utilities.regex.tokenizer.RegExpUtilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class SqliteColumnMetaDataReader {

  public List<IColumnMetaData> read(final Connection connection, final String tableName) throws SQLException {
    return DatabaseUtilities
        .results(
            connection,
            "PRAGMA table_info(\"" + tableName + "\");", //$NON-NLS-1$//$NON-NLS-2$
            (IProcedure<PreparedStatement, SQLException>) statement -> {},
            (IConverter<IResult, IColumnMetaData, SQLException>) value -> {
              final String rawType = value.getString(3);
              final String columnName = value.getString(2);
              final boolean isKey = value.getInteger(6, -1) == 1;
              final boolean isNullable = value.getInteger(4, -1) == 0;
              final ObjectPair<String, ObjectPair<Integer, Integer>> pair = parse(rawType);
              final String type = pair.getFirstObject();
              final int length = pair.getSecondObject().getFirstObject().intValue();
              final int scale = pair.getSecondObject().getSecondObject().intValue();
              try (ResultSet resultSet = connection.getMetaData()
                  .getColumns(null,
                      null,
                      tableName,
                      columnName)) {
                final boolean isAutoIncrement = Objects.equals("YES", resultSet.getString("IS_AUTOINCREMENT"));
                return new ColumnMetaData(
                    null,
                    tableName,
                    columnName,
                    extractType(type),
                    length,
                    scale,
                    isKey,
                    isAutoIncrement,
                    isNullable);
              }
            });
  }

  private String extractType(final String type) {
    int index = -1;
    if ((index = type.indexOf("(")) > -1) {
      return type.substring(0, index);
    }
    return type;
  }

  private ObjectPair<String, ObjectPair<Integer, Integer>> parse(final String rawType) {
    final List<String> names =
        Arrays.asList("CHARACTER", "VARCHAR", "VARYING CHARACTER", "NCHAR", "NVARCHAR");
    for (final String name : names) {
      if (rawType.matches(name + "\\(-?[0-9]+\\)")) {
        final Matcher matcher = Pattern.compile(name + "\\((-?[0-9]+)\\)").matcher(rawType);
        if (matcher.find(0)) {
          final String[] groups = RegExpUtilities.getGroups(matcher);
          if ("0".equals(groups[1])) {
            return new ObjectPair<>(name, new ObjectPair<>(Integer.valueOf(-1), Integer.valueOf(0)));
          }
          return new ObjectPair<>(name, new ObjectPair<>(Integer.valueOf(groups[1]), Integer.valueOf(0)));
        }
      }
      if (rawType.matches(name)) {
        return new ObjectPair<>(name, new ObjectPair<>(Integer.valueOf(-1), Integer.valueOf(0)));
      }
    }
    final List<String> numericNames = Arrays.asList("DECIMAL", "NUMERIC", "NUMBER");
    for (final String name : numericNames) {
      if (rawType.matches(name + "\\([0-9]+\\,-?[0-9]+\\)")) {
        final Matcher matcher = Pattern.compile(name + "\\(([0-9]+)\\,(-?[0-9]+)\\)").matcher(rawType);
        if (matcher.find(0)) {
          final String[] groups = RegExpUtilities.getGroups(matcher);
          return new ObjectPair<>(
              "NUMERIC",
              new ObjectPair<>(Integer.valueOf(groups[1]), Integer.valueOf(groups[2])));
        }
        return new ObjectPair<>("NUMERIC", new ObjectPair<>(Integer.valueOf(-1), Integer.valueOf(-1)));
      }
      if (rawType.matches(name + "\\([0-9]+\\)")) {
        final Matcher matcher = Pattern.compile(name + "\\(([0-9]+)\\)").matcher(rawType);
        if (matcher.find(0)) {
          final String[] groups = RegExpUtilities.getGroups(matcher);
          return new ObjectPair<>(
              "NUMERIC",
              new ObjectPair<>(Integer.valueOf(groups[1]), Integer.valueOf(0)));
        }
        return new ObjectPair<>("NUMERIC", new ObjectPair<>(Integer.valueOf(-1), Integer.valueOf(0)));
      }
      if (rawType.matches(name)) {
        return new ObjectPair<>("NUMERIC", new ObjectPair<>(Integer.valueOf(-1), Integer.valueOf(-1)));
      }
    }
    if (Objects.equals("DOUBLE PRECISION", rawType)) {
      return new ObjectPair<>(rawType, new ObjectPair<>(Integer.valueOf(-1), Integer.valueOf(-1)));
    }
    return new ObjectPair<>(rawType, new ObjectPair<>(Integer.valueOf(-1), Integer.valueOf(0)));
  }
}
