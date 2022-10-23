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
package net.anwiba.eclipse.icons.view.action;

import net.anwiba.commons.lang.object.IObjectText;
import net.anwiba.eclipse.icons.description.IGuiIconDescription;

import java.text.MessageFormat;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

public class CopyAction extends Action {

  private final Display display;
  private final IObjectText<IGuiIconDescription> descriptionText = new IObjectText<IGuiIconDescription>() {

    @Override
    public String getText(final IGuiIconDescription description) {
      return description == null ? "" : MessageFormat.format("{0}.{1}", //$NON-NLS-1$ //$NON-NLS-2$
          description.getConstant().getClassName(),
          description.getConstant().getConstantName());
    }
  };
  private final ISelectionProvider selectionProvider;

  public CopyAction(final Display display, final ISelectionProvider selectionProvider) {
    this.display = display;
    this.selectionProvider = selectionProvider;
    setToolTipText("Copy");
    setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_TOOL_COPY));
    setDisabledImageDescriptor(PlatformUI
        .getWorkbench()
        .getSharedImages()
        .getImageDescriptor(ISharedImages.IMG_TOOL_COPY_DISABLED));
    selectionProvider.addSelectionChangedListener(new ISelectionChangedListener() {

      @Override
      public void selectionChanged(final SelectionChangedEvent event) {
        setEnabled(!event.getSelection().isEmpty());
      }
    });
    setEnabled(!selectionProvider.getSelection().isEmpty());
  }

  @Override
  public void run() {
    final IGuiIconDescription description = getGuiIconDescription();
    final Clipboard clipboard = new Clipboard(this.display);
    final TextTransfer textTransfer = TextTransfer.getInstance();
    clipboard.setContents(new Object[] { this.descriptionText.getText(description) }, new Transfer[] { textTransfer });
  }

  private IGuiIconDescription getGuiIconDescription() {
    return (IGuiIconDescription) ((IStructuredSelection) this.selectionProvider.getSelection()).getFirstElement();
  }
}
