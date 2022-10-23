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
package net.anwiba.eclipse.icons.table;

import java.text.MessageFormat;

import net.anwiba.eclipse.icons.description.IConstant;
import net.anwiba.eclipse.icons.description.IGuiIconDescription;

import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.graphics.Point;

public final class TableCellLabelProvider extends CellLabelProvider {

  @Override
  public String getToolTipText(final Object element) {
    if (element == null) {
      return null;
    }
    final IGuiIconDescription description = (IGuiIconDescription) element;
    final IConstant constant = description.getConstant();
    return MessageFormat.format("{0}.{1}.{2}", constant.getPackageName(), constant.getClassName(), constant //$NON-NLS-1$
        .getConstantName());
  }

  @Override
  public Point getToolTipShift(final Object object) {
    return new Point(5, 5);
  }

  @Override
  public int getToolTipDisplayDelayTime(final Object object) {
    return 2000;
  }

  @Override
  public int getToolTipTimeDisplayed(final Object object) {
    return 5000;
  }

  @Override
  public void update(final ViewerCell cell) {
    cell.setText(cell.getElement().toString());
  }
}
