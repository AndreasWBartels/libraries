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

import net.anwiba.commons.swing.action.IActionContainerProvider;

import java.awt.Container;

import javax.swing.AbstractButton;
import javax.swing.JPopupMenu;

public final class PopupMenuItemButtomFactory implements IMenuItemTypeVisitor<AbstractButton> {
  private final JPopupMenu menu;
  private final AbstractMenuItemConfiguration<?> menuItemConfiguration;

  public PopupMenuItemButtomFactory(final JPopupMenu menu, final AbstractMenuItemConfiguration<?> menuItemConfiguration) {
    this.menu = menu;
    this.menuItemConfiguration = menuItemConfiguration;
  }

  @Override
  public AbstractButton visitAction() {
    final PopupMenuActionItemConfiguration menuActionItemConfiguration =
        (PopupMenuActionItemConfiguration) this.menuItemConfiguration;
    if (menuActionItemConfiguration.hasActionContainerProvider()) {
      final IActionContainerProvider<? extends Container> actionContainerProvider =
          menuActionItemConfiguration.getActionContainerProvider();
      this.menu.add(actionContainerProvider.getContainer());
      return actionContainerProvider.getButton();
    }
    return this.menu.add(menuActionItemConfiguration.getAction());
  }

  @Override
  public AbstractButton visitMenu() {
    return this.menu.add(((MenuMenuItemConfiguration) this.menuItemConfiguration).getJMenu());
  }
}