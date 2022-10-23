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

import net.anwiba.eclipse.icons.description.IConstant;
import net.anwiba.eclipse.icons.description.IGuiIconDescription;

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
    final IGuiIconDescription description = (IGuiIconDescription) element;
    return description.getImage();
  }

  @Override
  public String getColumnText(final Object element, final int columnIndex) {
    if (element == null) {
      return null;
    }
    final IGuiIconDescription description = (IGuiIconDescription) element;
    final IConstant constant = description.getConstant();
    switch (columnIndex) {
      case 0: {
        return null;
      }
      case 1: {
        return constant.getConstantName();
      }
      case 2: {
        return description.getSmallIcon() != null
            ? "yes" //$NON-NLS-1$
            : "no"; //$NON-NLS-1$
      }
      case 3: {
        return description.getMediumIcon() != null
            ? "yes" //$NON-NLS-1$
            : "no"; //$NON-NLS-1$
      }
      case 4: {
        return description.getLargeIcon() != null
            ? "yes" //$NON-NLS-1$
            : "no"; //$NON-NLS-1$
      }
      case 5: {
        return description.getSource();
      }
      case 6: {
        return MessageFormat.format("{0}.{1}", constant.getPackageName(), constant.getClassName()); //$NON-NLS-1$
      }
    }
    return null;
  }

  @Override
  public String getText(final Object element) {
    if (element == null) {
      return null;
    }
    final IGuiIconDescription description = (IGuiIconDescription) element;
    final IConstant constant = description.getConstant();
    return MessageFormat.format("{0}.{1}.{2}", constant.getPackageName(), constant.getClassName(), constant //$NON-NLS-1$
        .getConstantName());
  }

}
