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
package net.anwiba.eclipse.icons.view.listener;

import net.anwiba.eclipse.icons.description.GuiIconDescription;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.TextTransfer;

public class GuiIconDragListener implements DragSourceListener {

  private final TableViewer viewer;

  public GuiIconDragListener(final TableViewer viewer) {
    this.viewer = viewer;
  }

  @Override
  public void dragFinished(final DragSourceEvent event) {
    // nothing to do
  }

  @Override
  public void dragSetData(final DragSourceEvent event) {
    final IStructuredSelection selection = (IStructuredSelection) this.viewer.getSelection();
    final GuiIconDescription firstElement = (GuiIconDescription) selection.getFirstElement();
    if (TextTransfer.getInstance().isSupportedType(event.dataType)) {
      event.data = firstElement.getConstant().getName();
    }
  }

  @Override
  public void dragStart(final DragSourceEvent event) {
    // nothing to do
  }
}
