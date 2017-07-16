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
package net.anwiba.commons.swing.utilities;

import java.awt.Font;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.font.FontRenderContext;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

public class JTableUtilities {

  public static String getToolTipText(final JTable table, final MouseEvent event) {
    return getToolTipText(table, event.getPoint());
  }

  public static String getToolTipText(final JTable table, final Point point) {
    final int rowIndex = getRealRowIndexAtPoint(table, point);
    final int columnIndex = getRealColumnIndexAtPoint(table, point);
    final int columnAtPoint = table.columnAtPoint(point);
    final int rowAtPoint = table.rowAtPoint(point);
    final TableCellRenderer tableCellRenderer = table.getCellRenderer(rowAtPoint, columnAtPoint);
    if (tableCellRenderer instanceof JLabel) {
      final String value = ((JLabel) tableCellRenderer.getTableCellRendererComponent(
          table,
          table.getValueAt(rowAtPoint, columnAtPoint),
          false,
          false,
          rowIndex,
          columnIndex)).getText();
      if (value == null) {
        return null;
      }
      final int columnWidth = getColumnWidth(table, columnAtPoint);
      final double valueWidth = getValueWidth(tableCellRenderer, value);
      if (valueWidth > columnWidth - 2) {
        return value;
      }
    }
    return null;
  }

  private static double getValueWidth(final TableCellRenderer tableCellRenderer, final String value) {
    final Font font = ((JLabel) tableCellRenderer).getFont();
    final FontRenderContext frc = ((JLabel) tableCellRenderer).getFontMetrics(font).getFontRenderContext();
    return font.getStringBounds(value, frc).getWidth();
  }

  private static int getColumnWidth(final JTable table, final int columnAtPoint) {
    final TableColumn column = table.getColumnModel().getColumn(columnAtPoint);
    return column.getWidth();
  }

  public static int getRealColumnIndexAtPoint(final JTable table, final Point point) {
    return table.convertColumnIndexToModel(table.columnAtPoint(point));
  }

  public static int getRealRowIndexAtPoint(final JTable table, final Point point) {
    final int rowIndex = table.rowAtPoint(point);
    final RowSorter<? extends TableModel> rowSorter = table.getRowSorter();
    if (rowSorter == null) {
      return rowIndex;
    }
    return rowSorter.convertRowIndexToModel(rowIndex);
  }
}
