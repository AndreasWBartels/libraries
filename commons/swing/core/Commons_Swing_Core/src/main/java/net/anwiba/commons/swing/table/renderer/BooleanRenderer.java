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

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.UIResource;
import javax.swing.table.TableCellRenderer;

@SuppressWarnings("serial")
public class BooleanRenderer extends JCheckBox implements TableCellRenderer, UIResource {
  private static final Border noFocusBorder = new EmptyBorder(1, 1, 1, 1);
  private final Component nullComponent = new JPanel();

  public BooleanRenderer() {
    super();
    setHorizontalAlignment(SwingConstants.CENTER);
    setBorderPainted(true);
  }

  @Override
  public Component getTableCellRendererComponent(
      final JTable table,
      final Object value,
      final boolean isSelected,
      final boolean hasFocus,
      final int row,
      final int column) {
    if (value == null) {
      if (isSelected) {
        this.nullComponent.setForeground(table.getSelectionForeground());
        this.nullComponent.setBackground(table.getSelectionBackground());
      } else {
        this.nullComponent.setForeground(table.getForeground());
        this.nullComponent.setBackground(table.getBackground());
      }
      return this.nullComponent;
    }
    if (isSelected) {
      setForeground(table.getSelectionForeground());
      super.setBackground(table.getSelectionBackground());
    } else {
      setForeground(table.getForeground());
      setBackground(table.getBackground());
    }
    setSelected((((Boolean) value).booleanValue()));
    if (hasFocus) {
      setBorder(UIManager.getBorder("Table.focusCellHighlightBorder")); //$NON-NLS-1$
    } else {
      setBorder(noFocusBorder);
    }
    return this;
  }
}