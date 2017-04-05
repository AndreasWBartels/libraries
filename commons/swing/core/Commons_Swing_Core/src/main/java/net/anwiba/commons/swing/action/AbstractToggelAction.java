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

import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;

import net.anwiba.commons.ensure.Ensure;

public abstract class AbstractToggelAction extends AbstractAction {

  private static final long serialVersionUID = 1L;
  private final List<AbstractToggelAction> actions;

  public AbstractToggelAction(
    final List<AbstractToggelAction> actions,
    final String text,
    final String tooltip,
    final Icon icon) {
    super(text, icon);
    Ensure.ensureArgumentNotNull(actions);
    putValue(SHORT_DESCRIPTION, tooltip);
    this.actions = actions;
    this.actions.add(this);
  }

  @Override
  public final void actionPerformed(final ActionEvent event) {
    for (final Action action : this.actions) {
      action.setEnabled(true);
    }
    setEnabled(false);
    execute(event);
  }

  protected abstract void execute(ActionEvent event);
}
