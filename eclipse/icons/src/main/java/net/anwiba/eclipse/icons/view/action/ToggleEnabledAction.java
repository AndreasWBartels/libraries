package net.anwiba.eclipse.icons.view.action;

import net.anwiba.eclipse.icons.view.listener.ViewSiteListener;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

public class ToggleEnabledAction extends Action {

  private final ViewSiteListener listener;

  public ToggleEnabledAction(final ViewSiteListener listener) {
    this.listener = listener;
    setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_ELCL_SYNCED));
    setToolTipText("switch enable/disable");
    setChecked(listener.isEnabled());
  }

  @Override
  public void run() {
    this.listener.setEnabled(isChecked());
    if (!isChecked()) {
      return;
    }
  }
}