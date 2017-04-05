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
package net.anwiba.commons.swing.table.demo;

import net.anwiba.commons.swing.table.AbstractObjectTableModel;

import java.util.List;

public class DemoTableModel extends AbstractObjectTableModel<DemoObject> {

  public DemoTableModel(final List<DemoObject> objects) {
    super(objects);
  }

  private static final long serialVersionUID = 1L;

  @Override
  public int getColumnCount() {
    return 4;
  }

  @Override
  public Class<?> getColumnClass(final int columnIndex) {
    switch (columnIndex) {
      case 0:
        return Integer.class;
      case 1:
        return String.class;
      case 2:
        return Double.class;
      case 3:
        return Boolean.class;
    }
    return Object.class;
  }

  @Override
  public Object getValueAt(final int rowIndex, final int columnIndex) {
    final DemoObject object = get(rowIndex);
    switch (columnIndex) {
      case 0:
        return object.getNummer();
      case 1:
        return object.getName();
      case 2:
        return object.getValue();
      case 3:
        return object.getFlag();
    }
    return null;
  }
}