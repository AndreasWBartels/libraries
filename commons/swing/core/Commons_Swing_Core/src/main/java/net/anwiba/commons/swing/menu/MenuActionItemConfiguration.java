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

import java.awt.Container;

import javax.swing.Action;
import javax.swing.JMenuItem;

import net.anwiba.commons.ensure.Ensure;
import net.anwiba.commons.swing.action.IActionContainerProvider;

public class MenuActionItemConfiguration extends AbstractMenuItemConfiguration<MenuActionItemDescription> {

  private final Action action;
  private final IActionContainerProvider<? extends Container> actionContainerProvider;

  public MenuActionItemConfiguration(final MenuActionItemDescription description, final Action action) {
    this(description, null, action);
  }

  public MenuActionItemConfiguration(
    final MenuActionItemDescription description,
    final AbstractMenuItemProvider<? extends JMenuItem> provider) {
    this(description, provider, provider.getAction());
  }

  public MenuActionItemConfiguration(
    final MenuActionItemDescription description,
    final AbstractMenuItemProvider<? extends JMenuItem> actionContainerProvider,
    final Action action) {
    super(description);
    Ensure.ensureArgumentNotNull(action);
    this.actionContainerProvider = actionContainerProvider;
    this.action = action;
  }

  public MenuItemType getMenuItemType() {
    return getDescription().getMenuItemType();
  }

  public boolean hasActionContainerProvider() {
    return this.actionContainerProvider != null;
  }

  public IActionContainerProvider<? extends Container> getActionContainerProvider() {
    return this.actionContainerProvider;
  }

  public Action getAction() {
    return this.action;
  }
}