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

import net.anwiba.commons.utilities.registry.KeyValueRegistry;

public class MenuItemGroupConfiguration {
  public static final class MenuItemDescriptionComparator implements
      Comparator<AbstractMenuItemDescription>,
      Serializable {
    private static final long serialVersionUID = 1L;

    @Override
    public int compare(final AbstractMenuItemDescription o1, final AbstractMenuItemDescription o2) {
      return Integer.valueOf(o1.getWeight()).compareTo(Integer.valueOf(o2.getWeight()));
    }
  }

  private final MenuItemGroupDescription description;

  @SuppressWarnings("rawtypes")
  private final KeyValueRegistry<AbstractMenuItemDescription, AbstractMenuItemConfiguration> registry = new KeyValueRegistry<>();
  final Comparator<AbstractMenuItemDescription> comparator = new MenuItemDescriptionComparator();

  public MenuItemGroupConfiguration(final MenuItemGroupDescription menuGroupDescription) {
    this.description = menuGroupDescription;
  }

  public MenuItemGroupDescription getDescription() {
    return this.description;
  }

  public void add(final AbstractMenuItemConfiguration<? extends AbstractMenuItemDescription> menuItem) {
    this.registry.register(menuItem.getDescription(), menuItem);
  }

  public void remove(final AbstractMenuItemConfiguration<? extends AbstractMenuItemDescription> menuItem) {
    this.registry.remove(menuItem.getDescription());
  }

  public boolean isEmpty() {
    return this.registry.isEmpty();
  }

  @SuppressWarnings("unchecked")
  public AbstractMenuItemConfiguration<? extends AbstractMenuItemDescription>[] getMenuItemConfiguration() {
    return this.registry.getItems(
        this.comparator,
        AbstractMenuItemDescription.class,
        AbstractMenuItemConfiguration.class);
  }
}