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

package net.anwiba.eclipse.project.dependency.type;

import net.anwiba.eclipse.project.dependency.java.IType;

import java.text.MessageFormat;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

public final class TableLabelProvider extends LabelProvider implements ITableLabelProvider {

  @Override
  public Image getColumnImage(final Object element, final int columnIndex) {
    if (columnIndex == 0) {
      return getImage(element);
    }
    return null;
  }

  @Override
  public Image getImage(final Object element) {
    if (element == null) {
      return null;
    }
    return null;
  }

  @Override
  public String getColumnText(final Object element, final int columnIndex) {
    if (element == null) {
      return null;
    }
    final IType type = (IType) element;
    return CellValueFactory.create(type, columnIndex);
  }

  @Override
  public String getText(final Object element) {
    if (element == null) {
      return null;
    }
    final IType type = (IType) element;
    return MessageFormat.format("{0} ", type.getName()); //$NON-NLS-1$
  }

}