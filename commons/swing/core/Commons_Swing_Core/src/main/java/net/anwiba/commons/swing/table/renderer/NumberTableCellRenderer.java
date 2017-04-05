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

import net.anwiba.commons.lang.object.ObjectUtilities;

import java.awt.Component;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

@SuppressWarnings("serial")
public class NumberTableCellRenderer extends DefaultTableCellRenderer {

  private final NumberFormat format;

  public NumberTableCellRenderer() {
    this(null);
  }

  public NumberTableCellRenderer(final String pattern) {
    this.format = pattern != null ? new DecimalFormat(pattern) : null;
  }

  @Override
  public final Component getTableCellRendererComponent(
      final JTable table,
      final Object value,
      final boolean isSelected,
      final boolean hasFocus,
      final int row,
      final int column) {
    super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    setHorizontalAlignment(SwingConstants.RIGHT);
    if (value instanceof Number) {
      final Number number = (Number) value;
      setText(this.format != null ? this.format.format(number) : number.toString());
      return this;
    }
    setText(ObjectUtilities.toString(value));
    return this;
  }
}
