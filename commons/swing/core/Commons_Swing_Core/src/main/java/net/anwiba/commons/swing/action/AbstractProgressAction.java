/*
 * #%L
 * anwiba commons swing
 * %%
 * Copyright (C) 2007 - 2016 Andreas Bartels
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
package net.anwiba.commons.swing.action;

import net.anwiba.commons.lang.object.IObjectProvider;
import net.anwiba.commons.swing.dialog.exception.ExceptionDialog;
import net.anwiba.commons.swing.dialog.progress.ProgressDialog;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.lang.reflect.InvocationTargetException;

import javax.swing.AbstractAction;
import javax.swing.Icon;

public abstract class AbstractProgressAction extends AbstractAction {

  private static final long serialVersionUID = 1L;
  private final IObjectProvider<Window> ownerProvider;

  public AbstractProgressAction(final IObjectProvider<Window> ownerProvider, final String title, final Icon icon) {
    super(title, icon);
    this.ownerProvider = ownerProvider;
  }

  protected Window getOwner() {
    return this.ownerProvider.get();
  }

  protected abstract IProgressActionConfiguration getProgressActionConfiguration();

  @Override
  public final void actionPerformed(final ActionEvent e) {
    final IProgressActionConfiguration configuration = getProgressActionConfiguration();
    if (configuration == null) {
      return;
    }
    try {
      ProgressDialog.show(getOwner(), configuration.getMessage(), configuration.getProgressTask());
    } catch (final InterruptedException exception) {
      // nothing to do
    } catch (final InvocationTargetException exception) {
      ExceptionDialog.show(getOwner(), exception.getCause());
    }
  }
}