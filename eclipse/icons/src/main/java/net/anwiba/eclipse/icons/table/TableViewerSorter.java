/*
 * #%L
 * anwiba eclipse java tools
 * %%
 * Copyright (C) 2007 - 2021 Andreas Bartels
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

import net.anwiba.eclipse.icons.description.IGuiIconDescription;

import java.io.File;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

public final class TableViewerSorter extends ViewerComparator {
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
    final IGuiIconDescription description = (IGuiIconDescription) object;
    final IGuiIconDescription otherDescription = (IGuiIconDescription) other;
    final int compare = compare(columnIndex, description, otherDescription);
    if (sortDirection == SWT.UP) {
      return compare * -1;
    }
    return compare;
  }

  private int compare(
      final int columnIndex,
      final IGuiIconDescription description,
      final IGuiIconDescription otherDescription) {
    switch (columnIndex) {
      case 0:
      case 1: {
        return compareConstantName(description, otherDescription);
      }
      case 2: {
        return compare(description, description.getSmallIcon(), otherDescription, otherDescription.getSmallIcon());
      }
      case 3: {
        return compare(description, description.getMediumIcon(), otherDescription, otherDescription.getMediumIcon());
      }
      case 4: {
        return compare(description, description.getLargeIcon(), otherDescription, otherDescription.getLargeIcon());
      }
      case 5: {
        return compareSource(description, description.getSource(), otherDescription, otherDescription.getSource());
      }
      case 6: {
        return String.CASE_INSENSITIVE_ORDER
            .compare(description.getConstant().getName(), otherDescription.getConstant().getName());
      }
    }
    return 1;
  }

  private int compareSource(
      final IGuiIconDescription description,
      final String source,
      final IGuiIconDescription otherDescription,
      final String otherSource) {
    final int compare = String.CASE_INSENSITIVE_ORDER.compare(source, otherSource);
    if (compare == 0) {
      return compareConstantName(description, otherDescription);
    }
    return compare;
  }

  private int compare(
      final IGuiIconDescription description,
      final File file,
      final IGuiIconDescription otherDescription,
      final File otherFile) {
    final int compare = compare(getBooleanValue(file), getBooleanValue(otherFile));
    if (compare == 0) {
      return compareConstantName(description, otherDescription);
    }
    return compare;
  }

  private int compareConstantName(final IGuiIconDescription description, final IGuiIconDescription otherDescription) {
    final int constantNameCompare = String.CASE_INSENSITIVE_ORDER
        .compare(description.getConstant().getConstantName(), otherDescription.getConstant().getConstantName());
    if (constantNameCompare == 0) {
      return String.CASE_INSENSITIVE_ORDER
          .compare(description.getConstant().getName(), otherDescription.getConstant().getName());
    }
    return constantNameCompare;
  }

  private int getSortColumnIndex(final Table table) {
    final TableColumn sortColumn = table.getSortColumn();
    if (sortColumn == null) {
      return -1;
    }
    return getColumnIndex(sortColumn.getText());
  }

  private int compare(final boolean value, final boolean otherValue) {
    return (value == otherValue
        ? 0
        : (otherValue
            ? 1
            : -1));
  }

  private boolean getBooleanValue(final File file) {
    return file != null;
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
