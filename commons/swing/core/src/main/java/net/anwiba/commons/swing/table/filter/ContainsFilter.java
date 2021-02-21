/*
 * #%L
 * anwiba commons swing
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
package net.anwiba.commons.swing.table.filter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.table.TableModel;

import net.anwiba.commons.swing.table.IRowFilter;
import net.anwiba.commons.swing.table.IRowMapper;

public class ContainsFilter implements IRowFilter {

  private final String[] strings;
  private final IColumToStringConverter converter;
  private final int[] columnIndexes;

  public ContainsFilter(final String string, final IColumToStringConverter converter) {
    this.strings = string.split(" ");
    this.converter = converter;
    this.columnIndexes = converter.getFilterableColumnIndicies();
  }

  @Override
  public IRowMapper filter(final TableModel tableModel) {
    final List<Integer> mapping = new ArrayList<>();
    for (int i = 0; i < tableModel.getRowCount(); i++) {
      boolean[] flags = new boolean[this.strings.length];
      Arrays.fill(flags, false);
      for (int j = 0; j < this.columnIndexes.length; j++) {
        validate(flags, this.converter.convert(j, tableModel.getValueAt(i, this.columnIndexes[j])));
      }
      if (isTrue(flags)) {
        mapping.add(Integer.valueOf(i));
      }
    }
    return new Mapper(mapping);
  }

  private boolean isTrue(final boolean[] flags) {
    boolean flag = true;
    for (boolean value : flags) {
      flag = flag && value;
    }
    return flag;
  }

  private void validate(final boolean[] flags, final String value) {
    if (value == null) {
      return;
    }
    for (int i = 0; i < this.strings.length; i++) {
      flags[i] = flags[i] || value.toLowerCase().contains(this.strings[i].toLowerCase());
    }
  }
}
