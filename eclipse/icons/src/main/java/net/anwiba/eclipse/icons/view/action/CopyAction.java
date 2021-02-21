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
