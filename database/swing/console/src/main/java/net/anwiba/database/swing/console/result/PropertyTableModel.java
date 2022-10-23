/*
 * #%L
 * anwiba database
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
package net.anwiba.database.swing.console.result;

import net.anwiba.commons.jdbc.metadata.Property;
import net.anwiba.commons.lang.optional.Optional;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

public class PropertyTableModel extends AbstractTableModel implements TableModel {

  private final List<Property> properties = new ArrayList<>();

  public PropertyTableModel(final List<Property> properties) {
    this.properties.addAll(properties);
  }

  @Override
  public int getRowCount() {
    return this.properties.size();
  }

  @Override
  public int getColumnCount() {
    return 5;
  }

  @Override
  public Object getValueAt(final int rowIndex, final int columnIndex) {
    if (rowIndex < 0 || this.properties.size() < rowIndex + 1) {
      return null;
    }
    return Optional.of(this.properties.get(rowIndex))
        .convert(p -> switch (columnIndex) {
          case 0 -> p.name();
          case 1 -> p.value();
          case 2 -> p.description();
          case 3 -> p.maximumLength();
          case 4 -> p.defaultValue();
          default -> null;
        })
        .get();
  }

  @Override
  public String getColumnName(final int columnIndex) {
    return switch (columnIndex) {
      case 0 -> "name";
      case 1 -> "value";
      case 2 -> "description";
      case 3 -> "maximum length";
      case 4 -> "default value";
      default -> super.getColumnName(columnIndex);
    };
  }

  @Override
  public Class<?> getColumnClass(final int columnIndex) {
    return switch (columnIndex) {
      case 0 -> String.class;
      case 1 -> Object.class;
      case 2 -> String.class;
      case 4 -> Integer.class;
      case 5 -> Object.class;
      default -> super.getColumnClass(columnIndex);
    };
  }

}
