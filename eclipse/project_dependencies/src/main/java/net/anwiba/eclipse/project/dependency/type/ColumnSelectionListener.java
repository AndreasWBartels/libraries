// Copyright (c) 2010 by Andreas W. Bartels (bartels@anwiba.de)
package net.anwiba.eclipse.project.dependency.type;

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