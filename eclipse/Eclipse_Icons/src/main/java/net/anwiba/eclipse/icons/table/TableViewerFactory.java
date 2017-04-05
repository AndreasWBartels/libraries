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
// Copyright (c) 2010 by Andreas W. Bartels (bartels@anwiba.de)
package net.anwiba.eclipse.icons.table;

import net.anwiba.commons.model.IChangeableObjectListener;
import net.anwiba.commons.model.IObjectModel;
import net.anwiba.commons.utilities.string.StringUtilities;
import net.anwiba.eclipse.icons.description.IGuiIconDescription;

import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

public class TableViewerFactory {

  private final class GuiIconDescriptionViewerFilter extends ViewerFilter {

    private boolean isActive = false;
    private String searchText = null;

    @Override
    public boolean select(final Viewer viewer, final Object parentElement, final Object element) {
      if (!this.isActive) {
        return true;
      }
      final IGuiIconDescription description = (IGuiIconDescription) element;
      if (description.getConstant().getConstantName().toLowerCase().contains(this.searchText.toLowerCase())) {
        return true;
      }
      return false;
    }

    public void setSearchText(final String searchText) {
      this.isActive = !StringUtilities.isNullOrTrimmedEmpty(searchText);
      this.searchText = searchText;
    }
  }

  private void createColumns(final TableViewer tableViewer) {
    final String[] titles = { "Icon", "Name", "Small", "Medium", "Large", "Source", "Class" };
    final int[] bounds = { 40, 200, 60, 60, 60, 200, 200 };
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

  public TableViewer createTable(
      final Composite parent,
      final WritableList<IGuiIconDescription> descriptions,
      final IObjectModel<String> searchStringModel) {
    final GuiIconDescriptionViewerFilter viewerFilter = new GuiIconDescriptionViewerFilter();
    final TableViewer table = new TableViewer(parent, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
    createColumns(table);
    table.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
    table.setContentProvider(new ObservableListContentProvider());
    table.setInput(descriptions);
    table.setLabelProvider(new TableLabelProvider());
    table.setFilters(viewerFilter);
    searchStringModel.addChangeListener(new IChangeableObjectListener() {

      @Override
      public void objectChanged() {
        viewerFilter.setSearchText(searchStringModel.get());
        table.refresh();
      }
    });
    return table;
  }

}