// Copyright (c) 2010 by Andreas W. Bartels (bartels@anwiba.de)
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