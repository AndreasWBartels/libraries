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
package net.anwiba.commons.swing.table.renderer;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import net.anwiba.commons.lang.object.ObjectUtilities;
import net.anwiba.commons.swing.ui.IObjectUi;

public class ObjectUiTableCellRenderer<T> extends DefaultTableCellRenderer {
  private static final long serialVersionUID = 1L;
  private final IObjectUi<T> objectUi;
  private final Class<T> clazz;
  private final int horizontalAlignment;

  public ObjectUiTableCellRenderer(final IObjectUi<T> objectUi, final Class<T> clazz, final int horizontalAlignment) {
    this.objectUi = objectUi;
    this.clazz = clazz;
    this.horizontalAlignment = horizontalAlignment;
  }

  @SuppressWarnings("unchecked")
  @Override
  public Component getTableCellRendererComponent(
      final JTable table,
      final Object value,
      final boolean isSelected,
      final boolean hasFocus,
      final int row,
      final int column) {
    super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    setHorizontalAlignment(this.horizontalAlignment);
    if (this.clazz.isInstance(value)) {
      setIcon(this.objectUi.getIcon((T) value));
      setText(this.objectUi.getText((T) value));
      return this;
    }
    setIcon(null);
    setText(ObjectUtilities.toString(value));
    return this;
  }
}