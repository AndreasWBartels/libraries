/*
 * #%L
 *
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program. If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */
package net.anwiba.database.swing.console.result;

import java.io.IOException;
import java.io.Reader;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import net.anwiba.commons.logging.ILevel;
import net.anwiba.commons.reference.utilities.IoUtilities;

public class ResultSetToRowConverter {

  private static net.anwiba.commons.logging.ILogger logger = net.anwiba.commons.logging.Logging
      .getLogger(ResultSetToRowConverter.class);
  private final List<Integer> readOrder = new ArrayList<>();
  private final int count;
  private final List<Integer> types;
  private final List<String> typeNames;

  public ResultSetToRowConverter(
      final List<Integer> types,
      final List<String> typeNames,
      final int count) {
    this.types = types;
    this.typeNames = typeNames;
    this.count = count;
    for (int i = 0; i < this.count; i++) {
      // HACK for legacy oracle data types
      if (types.get(i).intValue() == -1
          && (Objects.equals(typeNames.get(i), "LONG") //$NON-NLS-1$
              || Objects.equals(typeNames.get(i), "LONG RAW"))) {  //$NON-NLS-1$
        this.readOrder.add(0, Integer.valueOf(i));
        continue;
      }
      this.readOrder.add(Integer.valueOf(i));
    }
  }

  public List<Object> convert(final ResultSet result) throws SQLException {
    final List<Object> row = new ArrayList<>(this.count);
    for (int i = 0; i < this.count; i++) {
      row.add(null);
    }
    for (int i = 0; i < this.count; i++) {
      final int index = this.readOrder.get(i).intValue();
      Integer typeCode = this.types.get(index);
      if (typeCode.intValue() == -1) {

        // HACK for legacy oracle data types, needs also a registered object getter strategies to
        // prevent out of memory
        // exceptions

        if (Objects.equals(this.typeNames.get(index), "LONG RAW")) { //$NON-NLS-1$
          row.set(index, "Large binary object");
          continue;
        }
        if (Objects.equals(this.typeNames.get(index), "LONG")) { //$NON-NLS-1$
          try (Reader reader = result.getCharacterStream(index + 1)) {
            if (reader == null) {
              row.set(index, null);
              continue;
            }
            final String string = IoUtilities.toString(reader, 1024); // $NON-NLS-1$
            row.set(index, string.length() == 1024 ? string + "..." : string);
            continue;
          } catch (final SQLException | IOException exception) {
            logger.log(ILevel.DEBUG, exception.getMessage(), exception);
            row.set(index, null);
            continue;
          }
          // try (InputStream reader = result.getUnicodeStream(index + 1)) {
          // if (reader == null) {
          // row.set(index, null);
          // continue;
          // }
          // final String string = IoUtilities.toString(reader, "UTF-16"); //$NON-NLS-1$
          // row.set(index, string);
          // continue;
          // } catch (final SQLException | IOException exception) {
          // logger.log(ILevel.DEBUG, exception.getMessage(), exception);
          // row.set(index, null);
          // continue;
          // }
        }
        continue;
      } else if (typeCode.intValue() == 1111) {

        // HACK for raster postgis data types, needs also a registered object getter strategies to
        // prevent out of memory
        // exceptions

        if (Objects.equals(this.typeNames.get(index), "raster")) { //$NON-NLS-1$
          try (Reader reader = result.getCharacterStream(index + 1)) {
            if (reader == null) {
              row.set(index, null);
              continue;
            }
            final String string = IoUtilities.toString(reader, 1024); // $NON-NLS-1$
            row.set(index, string.length() == 1024 ? string + "..." : string);
            continue;
          } catch (final SQLException | IOException exception) {
            logger.log(ILevel.DEBUG, exception.getMessage(), exception);
            row.set(index, null);
            continue;
          }
        }
      }
      row.set(index, result.getObject(index + 1));
    }
    return row;
  }
}
