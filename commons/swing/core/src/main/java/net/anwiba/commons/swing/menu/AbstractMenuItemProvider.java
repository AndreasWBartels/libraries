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
package net.anwiba.commons.swing.menu;

import javax.swing.Action;
import javax.swing.JMenuItem;

import net.anwiba.commons.swing.action.IActionContainerProvider;

public abstract class AbstractMenuItemProvider<T extends JMenuItem> implements IActionContainerProvider<T> {

  private final Action action;

  public AbstractMenuItemProvider(final Action action) {
    this.action = action;
  }

  @Override
  public Action getAction() {
    return this.action;
  }
}