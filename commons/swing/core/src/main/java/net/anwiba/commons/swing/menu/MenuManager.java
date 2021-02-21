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

import net.anwiba.commons.utilities.registry.KeyValueRegistry;

import java.text.MessageFormat;
import java.util.Comparator;

import javax.swing.Box;
import javax.swing.JMenuBar;

public class MenuManager implements IMenuRegistry {

  final private KeyValueRegistry<MenuDescription, MenuConfiguration> registry = new KeyValueRegistry<>();
  static final Comparator<MenuDescription> comparator = new Comparator<MenuDescription>() {

    @Override
    public int compare(final MenuDescription o1, final MenuDescription o2) {
      return Integer.valueOf(o1.getWeight()).compareTo(Integer.valueOf(o2.getWeight()));
    }
  };

  private synchronized void addConfiguration(
      final AbstractMenuItemConfiguration<? extends AbstractMenuItemDescription> menuItem) {
    final MenuDescription menuDescription = menuItem.getDescription().getMenuDescription();
    MenuConfiguration menu = this.registry.get(menuDescription);
    if (menu == null) {
      menu = new MenuConfiguration(menuDescription);
      this.registry.register(menuDescription, menu);
    }
    if (MenuItemType.MENU.equals(menuItem.getDescription().getMenuItemType())) {
      if (!(menuItem instanceof MenuMenuItemConfiguration)) {
        throw new IllegalArgumentException(MessageFormat.format("Illegal type MENU and class {0} combination" //$NON-NLS-1$
            ,
            menuItem.getClass()));
      }
      final MenuConfiguration subMenu = ((MenuMenuItemConfiguration) menuItem).getMenuConfiguration();
      if (!this.registry.contains(subMenu.getDescription())) {
        this.registry.register(subMenu.getDescription(), subMenu);
      }
    }
    final MenuItemGroupDescription menuGroupDescription = menuItem.getDescription().getMenuGroupDescription();
    MenuItemGroupConfiguration menuGroup = menu.get(menuGroupDescription);
    if (menuGroup == null) {
      menuGroup = new MenuItemGroupConfiguration(menuGroupDescription);
      menu.add(menuGroup);
    }
    menuGroup.add(menuItem);
  }

  @Override
  public void add(
      @SuppressWarnings("unchecked") final AbstractMenuItemConfiguration<? extends AbstractMenuItemDescription>... configurations) {
    for (final AbstractMenuItemConfiguration<? extends AbstractMenuItemDescription> confguration : configurations) {
      addConfiguration(confguration);
    }
  }

  private synchronized void remove(final AbstractMenuItemConfiguration<? extends AbstractMenuItemDescription> menuItem) {
    final MenuDescription menuDescription = menuItem.getDescription().getMenuDescription();
    final MenuConfiguration menu = this.registry.get(menuDescription);
    if (menu == null) {
      return;
    }
    final MenuItemGroupDescription menuGroupDescription = menuItem.getDescription().getMenuGroupDescription();
    final MenuItemGroupConfiguration menuGroup = menu.get(menuGroupDescription);
    if (menuGroup == null) {
      return;
    }
    menuGroup.remove(menuItem);
    if (menuGroup.isEmpty()) {
      menu.remove(menuGroup);
    }
    if (menu.isEmpty()) {
      this.registry.remove(menuDescription);
    }
  }

  public void remove(final AbstractMenuItemConfiguration<? extends AbstractMenuItemDescription>[] menuItems) {
    for (final AbstractMenuItemConfiguration<? extends AbstractMenuItemDescription> menuItem : menuItems) {
      remove(menuItem);
    }
  }

  public JMenuBar getMenuBar() {
    final MenuConfiguration[] menus =
        this.registry.getItems(comparator, MenuDescription.class, MenuConfiguration.class);
    final JMenuBar menuBar = new JMenuBar();
    boolean isBoxAdded = false;
    for (final MenuConfiguration menu : menus) {
      if (menu.getMenuType() == MenuType.MENU) {
        if (menu.getDescription().getWeight() == Integer.MAX_VALUE && !isBoxAdded) {
          menuBar.add(Box.createHorizontalGlue());
          isBoxAdded = true;
        }
        menuBar.add(menu.getJMenu());
      }
    }
    return menuBar;
  }
}
