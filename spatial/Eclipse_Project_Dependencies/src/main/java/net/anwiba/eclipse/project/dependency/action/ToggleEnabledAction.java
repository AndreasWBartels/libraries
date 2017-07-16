/*
 * #%L
 * anwiba commons core
 * %%
 * Copyright (C) 2007 - 2017 Andreas Bartels
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
    setChecked(model.get());
  }

  @Override
  public void run() {
    this.model.set(isChecked());
  }
}