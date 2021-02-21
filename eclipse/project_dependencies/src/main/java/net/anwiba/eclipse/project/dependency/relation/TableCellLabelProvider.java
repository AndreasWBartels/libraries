// Copyright (c) 2010 by Andreas W. Bartels (bartels@anwiba.de)
package net.anwiba.eclipse.project.dependency.relation;

import net.anwiba.eclipse.project.dependency.object.IDependencyRelation;

import java.text.MessageFormat;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.graphics.Point;

public final class TableCellLabelProvider extends ColumnLabelProvider {

  @Override
  public String getToolTipText(final Object element) {
    if (element == null) {
      return null;
    }
    final IDependencyRelation relation = (IDependencyRelation) element;
    return MessageFormat.format("{1} {0} ", relation.getItem().getName(), relation.getRelationType()); //$NON-NLS-1$
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