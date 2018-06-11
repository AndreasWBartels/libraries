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

import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.JToolBar;

import net.anwiba.commons.lang.optional.Optional;
import net.anwiba.commons.swing.action.IActionContainerProvider;
import net.anwiba.commons.utilities.registry.KeyValueRegistry;

public class ToolBarConfiguration {
  public static final class ToolBarItemGroupDescriptionComparator
      implements
      Comparator<ToolBarItemGroupDescription>,
      Serializable {
    private static final long serialVersionUID = 1L;

    @Override
    public int compare(final ToolBarItemGroupDescription o1, final ToolBarItemGroupDescription o2) {
      return Integer.valueOf(o1.getWeight()).compareTo(Integer.valueOf(o2.getWeight()));
    }
  }

  public static final class ActionDisabler implements ActionListener {
    private final List<Action> actions;

    public ActionDisabler(final List<Action> actions) {
      this.actions = actions;
    }

    @Override
    public void actionPerformed(final ActionEvent event) {
      for (final Action action : this.actions) {
        action.setEnabled(true);
      }
      final Object source = event.getSource();
      if (source instanceof AbstractButton) {
        ((AbstractButton) source).getAction().setEnabled(false);
      }
    }
  }

  private final ToolBarDescription description;

  private final KeyValueRegistry<ToolBarItemGroupDescription, ToolBarItemGroupConfiguration> registry = new KeyValueRegistry<>();
  final Comparator<ToolBarItemGroupDescription> comparator = new ToolBarItemGroupDescriptionComparator();

  public ToolBarConfiguration(final ToolBarDescription description) {
    this.description = description;
  }

  public ToolBarDescription getDescription() {
    return this.description;
  }

  public ToolBarItemGroupConfiguration get(final ToolBarItemGroupDescription toolBarGroupDescription) {
    return this.registry.get(toolBarGroupDescription);
  }

  public void add(final ToolBarItemGroupConfiguration toolBarGroup) {
    this.registry.register(toolBarGroup.getDescription(), toolBarGroup);
  }

  public void remove(final ToolBarItemGroupConfiguration toolBarGroup) {
    this.registry.remove(toolBarGroup.getDescription());
  }

  public JToolBar getJToolBar() {
    final ToolBarItemGroupConfiguration[] toolBarGroups = this.registry
        .getItems(this.comparator, ToolBarItemGroupDescription.class, ToolBarItemGroupConfiguration.class);
    final JToolBar toolBar = new JToolBar();
    toolBar.setFloatable(isFloatable());
    for (int i = 0; i < toolBarGroups.length; i++) {
      if (i > 0) {
        toolBar.addSeparator(new Dimension(3, 1));
      }
      final ToolBarItemConfiguration[] toolBarItemConfigurations = toolBarGroups[i].getToolBarItemConfigurations();
      if (toolBarGroups[i].getDescription().isToggelGroup()) {
        final List<Action> actions = new ArrayList<>();
        final ActionListener listener = new ActionDisabler(actions);
        for (final ToolBarItemConfiguration toolBarItemConfiguration : toolBarItemConfigurations) {
          Optional.of(addToToolBar(toolBar, toolBarItemConfiguration)).consume(
              button -> button.addActionListener(listener));
          Optional.of(toolBarItemConfiguration.getAction()).consume(a -> actions.add(a));
        }
      } else {
        for (final ToolBarItemConfiguration toolBarItemConfiguration : toolBarItemConfigurations) {
          addToToolBar(toolBar, toolBarItemConfiguration);
        }
      }
    }
    toolBar.revalidate();
    return toolBar;
  }

  private boolean isFloatable() {
    return this.description.isFloatable();
  }

  private AbstractButton addToToolBar(final JToolBar toolBar, final ToolBarItemConfiguration toolBarItemConfiguration) {
    if (toolBarItemConfiguration.hasActionContainerProvider()) {
      final IActionContainerProvider<? extends Container> actionContainerProvider = toolBarItemConfiguration
          .getActionContainerProvider();
      toolBar.add(actionContainerProvider.getContainer());
      return actionContainerProvider.getButton();
    }
    return toolBar.add(toolBarItemConfiguration.getAction());
  }

  public boolean isEmpty() {
    return this.registry.isEmpty();
  }
}
