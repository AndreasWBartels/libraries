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