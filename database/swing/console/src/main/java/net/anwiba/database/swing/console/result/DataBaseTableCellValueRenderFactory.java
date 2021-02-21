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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */
package net.anwiba.database.swing.console.result;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import net.anwiba.commons.jdbc.connection.IJdbcConnectionDescription;
import net.anwiba.commons.lang.object.IObjectToStringConverter;
import net.anwiba.database.swing.console.converter.IDataBaseTableCellValueToStringConverterProvider;

public class DataBaseTableCellValueRenderFactory implements IDataBaseTableCellValueRenderFactory {

  private final IDataBaseTableCellValueToStringConverterProvider provider;

  public DataBaseTableCellValueRenderFactory(final IDataBaseTableCellValueToStringConverterProvider provider) {
    this.provider = provider;
  }

  @Override
  public TableCellRenderer create(final IJdbcConnectionDescription description, final String columnTypeName) {
    final IObjectToStringConverter<Object> objectToStringConverter = this.provider.get(description, columnTypeName);
    if (objectToStringConverter == null) {
      return null;
    }
    return new DefaultTableCellRenderer() {

      private static final long serialVersionUID = 1L;

      @Override
      public Component getTableCellRendererComponent(
          final JTable table,
          final Object value,
          final boolean isSelected,
          final boolean hasFocus,
          final int row,
          final int column) {
        if (value == null) {
          super.getTableCellRendererComponent(
              table,
              "", //$NON-NLS-1$
              isSelected,
              hasFocus,
              row,
              column);
          return this;
        }
        super.getTableCellRendererComponent(
            table,
            objectToStringConverter.toString(value),
            isSelected,
            hasFocus,
            row,
            column);
        return this;
      }
    };
  }

}
