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
package net.anwiba.commons.swing.ui;

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class ObjectUiTableCellRenderer<T> extends DefaultTableCellRenderer {

  private final IObjectUi<T> objectUi;
  private final IObjectUiCellRendererConfiguration configuration;

  private static final long serialVersionUID = 1L;

  public ObjectUiTableCellRenderer(final IObjectUi<T> objectUi) {
    this(null, objectUi);
  }

  public ObjectUiTableCellRenderer(
      final IObjectUiCellRendererConfiguration configuration,
      final IObjectUi<T> objectUi) {
    this.configuration = configuration;
    this.objectUi = objectUi;
  }

  @Override
  public Component getTableCellRendererComponent(
      final JTable table,
      final Object value,
      final boolean isSelected,
      final boolean hasFocus,
      final int row,
      final int column) {
    try {
      @SuppressWarnings("unchecked")
      final T object = (T) value;
      final String text = this.objectUi.getText(object);
      final Icon icon = this.objectUi.getIcon(object);
      super.getTableCellRendererComponent(
          table,
          icon == null && (text == null || text.length() == 0) ? " " : text, //$NON-NLS-1$
          isSelected,
          hasFocus,
          row,
          column);
      setIcon(icon);
      setToolTipText(this.objectUi.getToolTipText(object));
      if (this.configuration == null) {
        return this;
      }
      setVerticalTextPosition(this.configuration.getVerticalTextPosition());
      setVerticalAlignment(this.configuration.getVerticalAlignment());
      setHorizontalTextPosition(this.configuration.getHorizontalTextPosition());
      setHorizontalAlignment(this.configuration.getHorizontalAlignment());
      setIconTextGap(this.configuration.getIconTextGap());
      setBorder(this.configuration.getBorder());
      return this;
    } catch (final ClassCastException exception) {
      super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
      return this;
    }
  }
}
