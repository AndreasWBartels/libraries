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

import javax.swing.Action;

import net.anwiba.commons.swing.action.AbstractActionConfiguration;
import net.anwiba.commons.swing.action.IActionContainerProvider;

public class ToolBarItemConfiguration extends AbstractActionConfiguration<ToolBarItemDescription> {

  private final IActionContainerProvider<? extends Container> actionContainerProvider;

  public ToolBarItemConfiguration(final ToolBarItemDescription toolBarItemDescription, final Action action) {
    this(toolBarItemDescription, null, action);
  }

  public ToolBarItemConfiguration(
      final ToolBarItemDescription toolBarItemDescription,
      final IActionContainerProvider<? extends Container> actionContainerProvider,
      final Action action) {
    super(toolBarItemDescription, action);
    this.actionContainerProvider = actionContainerProvider;
  }

  public ToolBarItemConfiguration(
      final ToolBarItemDescription toolBarItemDescription,
      final IActionContainerProvider<? extends Container> actionContainerProvider) {
    super(toolBarItemDescription, null);
    this.actionContainerProvider = actionContainerProvider;
  }

  public boolean hasActionContainerProvider() {
    return this.actionContainerProvider != null;
  }

  public IActionContainerProvider<? extends Container> getActionContainerProvider() {
    return this.actionContainerProvider;
  }
}
