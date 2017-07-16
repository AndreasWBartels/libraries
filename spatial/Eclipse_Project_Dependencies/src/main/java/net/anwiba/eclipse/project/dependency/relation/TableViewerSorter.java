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
package net.anwiba.eclipse.project.dependency.relation;

import net.anwiba.eclipse.project.dependency.object.IDependencyRelation;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

public final class TableViewerSorter extends ViewerSorter {
  private final String[] titles;

  public TableViewerSorter(final String[] titles) {
    this.titles = titles;
  }

  @Override
  public int compare(final Viewer viewer, final Object object, final Object other) {
    final TableViewer tableViewer = (TableViewer) viewer;
    final Table table = tableViewer.getTable();
    final int sortDirection = table.getSortDirection();
    if (sortDirection == SWT.NONE) {
      return 1;
    }
    if (object == null && other == null) {
      return 0;
    }
    if (object == null) {
      return -1;
    }
    if (other == null) {
      return 1;
    }
    final int columnIndex = getSortColumnIndex(table);
    final IDependencyRelation description = (IDependencyRelation) object;
    final IDependencyRelation otherDescription = (IDependencyRelation) other;
    final int compare = compare(columnIndex, description, otherDescription);
    if (sortDirection == SWT.UP) {
      return compare * -1;
    }
    return compare;
  }

  private int compare(final int columnIndex, final IDependencyRelation value, final IDependencyRelation other) {
    return String.CASE_INSENSITIVE_ORDER.compare(
        CellValueFactory.create(value, columnIndex),
        CellValueFactory.create(other, columnIndex));
  }

  private int getSortColumnIndex(final Table table) {
    final TableColumn sortColumn = table.getSortColumn();
    if (sortColumn == null) {
      return -1;
    }
    return getColumnIndex(sortColumn.getText());
  }

  private int getColumnIndex(final String text) {
    for (int i = 0; i < this.titles.length; i++) {
      if (this.titles[i].equals(text)) {
        return i;
      }
    }
    return -1;
  }
}