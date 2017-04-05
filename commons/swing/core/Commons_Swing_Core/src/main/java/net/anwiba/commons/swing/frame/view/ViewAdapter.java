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
package net.anwiba.commons.swing.frame.view;

import net.anwiba.commons.ensure.Ensure;
import net.anwiba.commons.swing.frame.IKeyActionConfiguration;
import net.anwiba.commons.swing.menu.AbstractMenuItemConfiguration;
import net.anwiba.commons.swing.menu.AbstractMenuItemDescription;
import net.anwiba.commons.swing.statebar.StateBarComponentConfiguration;
import net.anwiba.commons.swing.toolbar.ToolBarItemConfiguration;

import java.util.Arrays;
import java.util.List;

import javax.swing.JComponent;

public class ViewAdapter extends AbstractView {

  private final JComponent component;
  private final AbstractMenuItemConfiguration<? extends AbstractMenuItemDescription>[] menuItemConfigurations;
  private final List<ToolBarItemConfiguration> toolBarItemConfigurations;
  private final List<StateBarComponentConfiguration> stateBarComponentConfigurations;
  private final List<IKeyActionConfiguration> keyActionConfigurations;

  public ViewAdapter(
    final JComponent component,
    final AbstractMenuItemConfiguration<? extends AbstractMenuItemDescription>[] menuItemConfigurations,
    final ToolBarItemConfiguration[] toolBarItemConfigurations,
    final StateBarComponentConfiguration[] stateBarComponentConfigurations,
    final IKeyActionConfiguration[] keyActionConfigurations) {
    Ensure.ensureArgumentNotNull(menuItemConfigurations);
    Ensure.ensureArgumentNotNull(toolBarItemConfigurations);
    Ensure.ensureArgumentNotNull(component);
    Ensure.ensureArgumentNotNull(stateBarComponentConfigurations);
    Ensure.ensureArgumentNotNull(keyActionConfigurations);
    this.component = component;
    this.menuItemConfigurations = Arrays.copyOf(menuItemConfigurations, menuItemConfigurations.length);
    this.toolBarItemConfigurations = Arrays.asList(toolBarItemConfigurations);
    this.stateBarComponentConfigurations = Arrays.asList(stateBarComponentConfigurations);
    this.keyActionConfigurations = Arrays.asList(keyActionConfigurations);
  }

  @Override
  public JComponent getComponent() {
    return this.component;
  }

  @Override
  public ToolBarItemConfiguration[] getToolBarItemConfigurations() {
    return this.toolBarItemConfigurations.toArray(new ToolBarItemConfiguration[this.toolBarItemConfigurations.size()]);
  }

  @Override
  public AbstractMenuItemConfiguration<? extends AbstractMenuItemDescription>[] getMenuItemConfigurations() {
    return this.menuItemConfigurations;
  }

  @Override
  public StateBarComponentConfiguration[] getStateBarComponentConfigurations() {
    return this.stateBarComponentConfigurations
        .toArray(new StateBarComponentConfiguration[this.stateBarComponentConfigurations.size()]);
  }

  @Override
  public IKeyActionConfiguration[] getKeyActionConfigurations() {
    return this.keyActionConfigurations.toArray(new IKeyActionConfiguration[this.keyActionConfigurations.size()]);
  }

}
