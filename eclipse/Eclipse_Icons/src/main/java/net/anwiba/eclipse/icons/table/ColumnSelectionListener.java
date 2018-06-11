/*
 * #%L
 * anwiba commons core
 * %%
 * Copyright (C) 2007 - 2017 Andreas Bartels
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

package net.anwiba.eclipse.icons.table;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

public final class ColumnSelectionListener extends SelectionAdapter {
  private final TableColumn column;
  private final TableViewer viewer;

  public ColumnSelectionListener(final TableColumn column, final TableViewer viewer) {
    this.column = column;
    this.viewer = viewer;
  }

  @Override
  public void widgetSelected(final SelectionEvent e) {
    final Table table = this.column.getParent();
    final TableColumn sortColumn = table.getSortColumn();
    if (sortColumn == null || !sortColumn.equals(this.column)) {
      table.setSortColumn(this.column);
      table.setSortDirection(SWT.NONE);
    }
    switch (table.getSortDirection()) {
      case SWT.NONE: {
        table.setSortDirection(SWT.DOWN);
        break;
      }
      case SWT.DOWN: {
        table.setSortDirection(SWT.UP);
        break;
      }
      case SWT.UP: {
        table.setSortDirection(SWT.NONE);
        break;
      }
    }
    this.viewer.refresh();
  }
}