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

import java.io.Serializable;
import java.util.Comparator;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;

import net.anwiba.commons.utilities.registry.KeyValueRegistry;

public class PopMenuManager {

  public static final class MenuItemGroupDescriptionComarator
      implements
      Comparator<MenuItemGroupDescription>,
      Serializable {

    private static final long serialVersionUID = 1L;

    @Override
    public int compare(final MenuItemGroupDescription o1, final MenuItemGroupDescription o2) {
      return Integer.valueOf(o1.getWeight()).compareTo(Integer.valueOf(o2.getWeight()));
    }
  }

  private final KeyValueRegistry<MenuItemGroupDescription, MenuItemGroupConfiguration> registry = new KeyValueRegistry<>();
  final Comparator<MenuItemGroupDescription> comparator = new MenuItemGroupDescriptionComarator();

  public synchronized void addConfiguration(final PopupMenuActionItemConfiguration menuItem) {

    final MenuItemGroupDescription menuGroupDescription = menuItem.getDescription().getMenuGroupDescription();
    if (!this.registry.contains(menuGroupDescription)) {
      this.registry.register(menuGroupDescription, new MenuItemGroupConfiguration(menuGroupDescription));
    }
    final MenuItemGroupConfiguration groupConfiguration = this.registry.get(menuGroupDescription);
    groupConfiguration.add(menuItem);
  }

  public JPopupMenu getMenu() {
    final JPopupMenu menu = new JPopupMenu();
    return fillMenu(menu);
  }

  private AbstractButton addToMenu(final JPopupMenu menu, final AbstractMenuItemConfiguration<?> menuItemConfiguration) {
    final IMenuItemTypeVisitor<AbstractButton> visitor = new PopupMenuItemButtomFactory(menu, menuItemConfiguration);
    return menuItemConfiguration.getDescription().getMenuItemType().accept(visitor);
  }

  public boolean isEmpty() {
    return this.registry.isEmpty();
  }

  public JPopupMenu fillMenu(final JPopupMenu menu) {
    final MenuItemGroupConfiguration[] menuGroups = this.registry.getItems(
        this.comparator,
        MenuItemGroupDescription.class,
        MenuItemGroupConfiguration.class);
    for (int i = 0; i < menuGroups.length; i++) {
      if (i > 0) {
        menu.addSeparator();
      }
      final AbstractMenuItemConfiguration<?>[] menuItemConfigurations = menuGroups[i].getMenuItemConfiguration();
      if (menuGroups[i].getDescription().isToggelGroup()) {
        final ButtonGroup group = new ButtonGroup();
        for (final AbstractMenuItemConfiguration<?> menuItemConfiguration : menuItemConfigurations) {
          final AbstractButton button = addToMenu(menu, menuItemConfiguration);
          group.add(button);
        }
      } else {
        for (final AbstractMenuItemConfiguration<?> menuItemConfiguration : menuItemConfigurations) {
          addToMenu(menu, menuItemConfiguration);
        }
      }
    }
    return menu;
  }

  public JMenu fillMenu(final JMenu menu) {
    final MenuItemGroupConfiguration[] menuGroups = this.registry.getItems(
        this.comparator,
        MenuItemGroupDescription.class,
        MenuItemGroupConfiguration.class);
    for (int i = 0; i < menuGroups.length; i++) {
      if (i > 0) {
        menu.addSeparator();
      }
      final AbstractMenuItemConfiguration<?>[] menuItemConfigurations = menuGroups[i].getMenuItemConfiguration();
      if (menuGroups[i].getDescription().isToggelGroup()) {
        final ButtonGroup group = new ButtonGroup();
        for (final AbstractMenuItemConfiguration<?> menuItemConfiguration : menuItemConfigurations) {
          final AbstractButton button = addToMenu(menu, menuItemConfiguration);
          group.add(button);
        }
      } else {
        for (final AbstractMenuItemConfiguration<?> menuItemConfiguration : menuItemConfigurations) {
          addToMenu(menu, menuItemConfiguration);
        }
      }
    }
    return menu;
  }

  private AbstractButton addToMenu(final JMenu menu, final AbstractMenuItemConfiguration<?> menuItemConfiguration) {
    final IMenuItemTypeVisitor<AbstractButton> visitor = new MenuItemButtomFactory(menu, menuItemConfiguration);
    return menuItemConfiguration.getDescription().getMenuItemType().accept(visitor);
  }

}
