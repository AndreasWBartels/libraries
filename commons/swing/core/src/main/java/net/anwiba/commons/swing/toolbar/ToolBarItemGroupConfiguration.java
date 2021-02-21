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
package net.anwiba.commons.swing.toolbar;

import net.anwiba.commons.utilities.registry.KeyValueRegistry;

import java.io.Serializable;
import java.util.Comparator;

public class ToolBarItemGroupConfiguration {
  public static final class ToolBarItemDescriptionComparator implements
      Comparator<ToolBarItemDescription>,
      Serializable {
    private static final long serialVersionUID = 1L;

    @Override
    public int compare(final ToolBarItemDescription o1, final ToolBarItemDescription o2) {
      return Integer.valueOf(o1.getWeight()).compareTo(Integer.valueOf(o2.getWeight()));
    }
  }

  private final ToolBarItemGroupDescription description;

  private final KeyValueRegistry<ToolBarItemDescription, ToolBarItemConfiguration> registry = new KeyValueRegistry<>();
  final Comparator<ToolBarItemDescription> comparator = new ToolBarItemDescriptionComparator();

  public ToolBarItemGroupConfiguration(final ToolBarItemGroupDescription toolBarGroupDescription) {
    this.description = toolBarGroupDescription;
  }

  public ToolBarItemGroupDescription getDescription() {
    return this.description;
  }

  public void add(final ToolBarItemConfiguration toolBarItem) {
    this.registry.register(toolBarItem.getDescription(), toolBarItem);
  }

  public void remove(final ToolBarItemConfiguration toolBarItem) {
    this.registry.remove(toolBarItem.getDescription());
  }

  public ToolBarItemConfiguration[] getToolBarItemConfigurations() {
    return this.registry.getItems(this.comparator, ToolBarItemDescription.class, ToolBarItemConfiguration.class);
  }

  public boolean isEmpty() {
    return this.registry.isEmpty();
  }
}
