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
