// Copyright (c) 2010 by Andreas W. Bartels (bartels@anwiba.de)
package net.anwiba.eclipse.project.dependency.relation;

import net.anwiba.eclipse.project.dependency.object.IDependencyRelation;

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
    final IDependencyRelation description = (IDependencyRelation) element;
    return CellValueFactory.create(description, columnIndex);
  }

  @Override
  public String getText(final Object element) {
    if (element == null) {
      return null;
    }
    final IDependencyRelation relation = (IDependencyRelation) element;
    return MessageFormat.format("{1} {0} ", relation.getItem().getName(), relation.getRelationType()); //$NON-NLS-1$
  }

}