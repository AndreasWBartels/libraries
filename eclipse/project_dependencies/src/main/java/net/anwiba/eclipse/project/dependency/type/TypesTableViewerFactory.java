/*
 * #%L
 * anwiba eclipse java tools
 * %%
 * Copyright (C) 2007 - 2022 Andreas Bartels
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
// Copyright (c) 2010 by Andreas W. Bartels (bartels@anwiba.de)
package net.anwiba.eclipse.project.dependency.type;

import net.anwiba.eclipse.project.dependency.java.IType;

import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

public class TypesTableViewerFactory {

  private void createColumns(final TableViewer tableViewer) {
    final String[] titles = { "Class" };
    final int[] bounds = { 120, 100, 200 };
    for (int i = 0; i < titles.length; i++) {
      final TableViewerColumn viewerColumn = new TableViewerColumn(tableViewer, SWT.NONE);
      tableViewer.setSorter(new TableViewerSorter(titles));
      viewerColumn.setLabelProvider(new TableCellLabelProvider());
      final TableColumn column = viewerColumn.getColumn();
      column.addSelectionListener(new ColumnSelectionListener(column, tableViewer));
      column.setText(titles[i]);
      column.setWidth(bounds[i]);
      column.setResizable(true);
      column.setMoveable(false);
    }
    final Table table = tableViewer.getTable();
    table.setHeaderVisible(true);
    table.setLinesVisible(true);
    ColumnViewerToolTipSupport.enableFor(tableViewer, ToolTip.RECREATE);
  }

  public TableViewer createTable(final Composite parent, final WritableList<IType> descriptions) {
    final TableViewer table = new TableViewer(parent, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
    createColumns(table);
    table.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
    table.setContentProvider(new ObservableListContentProvider<>());
    table.setInput(descriptions);
    table.setLabelProvider(new TableLabelProvider());
    ColumnViewerToolTipSupport.enableFor(table);
    return table;
  }
}
