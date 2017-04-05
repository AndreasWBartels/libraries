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
package net.anwiba.commons.swing.component.search.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

import net.anwiba.commons.swing.component.search.ISearchEngine;
import net.anwiba.commons.swing.icon.GuiIcons;

public final class PreviousAction<C, R> extends AbstractAction {
  private final ISearchEngine<C, R> engine;
  private static final long serialVersionUID = 1L;

  public PreviousAction(final ISearchEngine<C, R> engine) {
    super(null, GuiIcons.PREVIOUS_ICON.getSmallIcon());
    putValue(Action.SHORT_DESCRIPTION, "zoom to previous selected feature");
    this.engine = engine;
  }

  @Override
  public void actionPerformed(final ActionEvent e) {
    this.engine.previous();
  }
}