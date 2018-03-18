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
package net.anwiba.commons.swing.process;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

import net.anwiba.commons.message.MessageType;
import net.anwiba.commons.swing.dialog.MessageTypeUI;
import net.anwiba.commons.swing.icon.GuiIconSize;

public class ProcessMessageContextTableCellRenderer extends DefaultTableCellRenderer {

  private static final long serialVersionUID = 1L;

  @Override
  public Component getTableCellRendererComponent(
      final JTable table,
      final Object value,
      final boolean isSelected,
      final boolean hasFocus,
      final int row,
      final int column) {
    setIcon(null);
    super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    if (value instanceof MessageType) {
      setText(null);
      setIcon(MessageTypeUI.getIcon((MessageType) value, GuiIconSize.SMALL));
      setHorizontalAlignment(SwingConstants.CENTER);
      setVerticalAlignment(SwingConstants.CENTER);
      return this;
    }
    return this;
  }
}