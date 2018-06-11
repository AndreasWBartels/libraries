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

import java.awt.Window;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

import net.anwiba.commons.thread.process.IProcessManager;

public abstract class AbstractProcessAction extends AbstractAction {

  private static final long serialVersionUID = 1L;
  private final Window owner;
  private final IProcessManager processManager;

  public AbstractProcessAction(
      final String text,
      final ImageIcon icon,
      final Window owner,
      final IProcessManager processManager) {
    super(text, icon);
    this.owner = owner;
    this.processManager = processManager;
  }

  @SuppressWarnings("hiding")
  public abstract void execute(final Window owner, final IProcessManager processManager);

  @Override
  public void actionPerformed(final ActionEvent e) {
    execute(this.owner, this.processManager);
  }
}
