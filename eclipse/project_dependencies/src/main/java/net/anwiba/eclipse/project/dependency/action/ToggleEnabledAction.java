package net.anwiba.eclipse.project.dependency.action;

import net.anwiba.commons.model.IBooleanModel;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.PlatformUI;

public class ToggleEnabledAction extends Action {

  private final IBooleanModel model;

  public ToggleEnabledAction(final String icon, final String name, final IBooleanModel model) {
    this.model = model;
    setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(icon));
    setToolTipText(name);
    setChecked(model.isTrue());
  }

  @Override
  public void run() {
    this.model.set(isChecked());
  }
}