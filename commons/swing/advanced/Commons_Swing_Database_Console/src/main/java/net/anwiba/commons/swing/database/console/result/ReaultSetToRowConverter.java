/*
 * #%L
 * *
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */
package net.anwiba.commons.swing.database.console.result;

import java.io.IOException;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import net.anwiba.commons.logging.ILevel;
import net.anwiba.commons.reference.utilities.IoUtilities;

public class ReaultSetToRowConverter {

  private static net.anwiba.commons.logging.ILogger logger = net.anwiba.commons.logging.Logging
      .getLogger(ReaultSetToRowConverter.class);
  private final List<Integer> readOrder = new ArrayList<>();
  private final int count;
  private final List<Integer> types;
  private final List<String> typeNames;

  public ReaultSetToRowConverter(final List<Integer> types, final List<String> typeNames, final int count) {
    this.types = types;
    this.typeNames = typeNames;
    this.count = count;
    for (int i = 0; i < this.count; i++) {
      if (types.get(i) == -1
          && (Objects.equals(typeNames.get(i), "LONG") || Objects.equals(typeNames.get(i), "LONG RAW"))) { //$NON-NLS-1$ //$NON-NLS-2$
        this.readOrder.add(0, i);
        continue;
      }
      this.readOrder.add(i);
    }
  }

  public List<Object> convert(final ResultSet result) throws SQLException {
    final List<Object> row = new ArrayList<>(this.count);
    for (int i = 0; i < this.count; i++) {
      row.add(null);
    }
    for (int i = 0; i < this.count; i++) {
      final Integer index = this.readOrder.get(i);
      if (this.types.get(index) == -1) {
        if (Objects.equals(this.typeNames.get(index), "LONG")) { //$NON-NLS-1$
          try (InputStream reader = result.getUnicodeStream(index + 1)) {
            if (reader == null) {
              row.set(index, null);
              continue;
            }
            final String string = IoUtilities.toString(reader, "UTF-16"); //$NON-NLS-1$
            row.set(index, string);
            continue;
          } catch (final SQLException | IOException exception) {
            logger.log(ILevel.DEBUG, exception.getMessage(), exception);
            row.set(index, null);
            continue;
          }
        }
        continue;
      }
      row.set(index, result.getObject(index + 1));
    }
    return row;
  }
}
