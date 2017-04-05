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

import javax.swing.JToolBar;

public class ToolBarManager implements IToolBarComponentRegistry {

  public static final class ToolBarDescriptionComparator implements Comparator<ToolBarDescription>, Serializable {
    private static final long serialVersionUID = 1L;

    @Override
    public int compare(final ToolBarDescription o1, final ToolBarDescription o2) {
      return Integer.valueOf(o1.getWeight()).compareTo(Integer.valueOf(o2.getWeight()));
    }
  }

  final private KeyValueRegistry<ToolBarDescription, ToolBarConfiguration> registry = new KeyValueRegistry<>();
  final Comparator<ToolBarDescription> comparator = new ToolBarDescriptionComparator();

  public synchronized JToolBar[] getJToolBars() {
    final ToolBarConfiguration[] toolBars =
        this.registry.getItems(this.comparator, ToolBarDescription.class, ToolBarConfiguration.class);
    final JToolBar[] jtoolBars = new JToolBar[toolBars.length];
    for (int i = 0; i < jtoolBars.length; i++) {
      jtoolBars[i] = toolBars[i].getJToolBar();
    }
    return jtoolBars;
  }

  private synchronized void addConfiguration(final ToolBarItemConfiguration toolBarItemConfiguration) {
    final ToolBarDescription toolBarDescription = toolBarItemConfiguration.getDescription().getToolBarDescription();
    ToolBarConfiguration toolBarConfiguration = this.registry.get(toolBarDescription);
    if (toolBarConfiguration == null) {
      toolBarConfiguration = new ToolBarConfiguration(toolBarDescription);
      this.registry.register(toolBarDescription, toolBarConfiguration);
    }
    final ToolBarItemGroupDescription toolBarGroupDescription =
        toolBarItemConfiguration.getDescription().getToolBarGroupDescription();
    ToolBarItemGroupConfiguration toolBarGroup = toolBarConfiguration.get(toolBarGroupDescription);
    if (toolBarGroup == null) {
      toolBarGroup = new ToolBarItemGroupConfiguration(toolBarGroupDescription);
      toolBarConfiguration.add(toolBarGroup);
    }
    toolBarGroup.add(toolBarItemConfiguration);
  }

  @Override
  public synchronized void add(final ToolBarItemConfiguration... toolBarItems) {
    for (final ToolBarItemConfiguration toolBarItem : toolBarItems) {
      addConfiguration(toolBarItem);
    }
  }

  private synchronized void remove(final ToolBarItemConfiguration toolBarItem) {
    final ToolBarDescription toolBarDescription = toolBarItem.getDescription().getToolBarDescription();
    final ToolBarConfiguration toolBar = this.registry.get(toolBarDescription);
    if (toolBar == null) {
      return;
    }
    final ToolBarItemGroupDescription toolBarGroupDescription =
        toolBarItem.getDescription().getToolBarGroupDescription();
    final ToolBarItemGroupConfiguration toolBarGroup = toolBar.get(toolBarGroupDescription);
    if (toolBarGroup == null) {
      return;
    }
    toolBarGroup.remove(toolBarItem);
    if (toolBarGroup.isEmpty()) {
      toolBar.remove(toolBarGroup);
    }
    if (toolBar.isEmpty()) {
      this.registry.remove(toolBarDescription);
    }
  }

  public synchronized void remove(final ToolBarItemConfiguration[] toolBarItems) {
    for (final ToolBarItemConfiguration toolBarItem : toolBarItems) {
      remove(toolBarItem);
    }
  }
}
