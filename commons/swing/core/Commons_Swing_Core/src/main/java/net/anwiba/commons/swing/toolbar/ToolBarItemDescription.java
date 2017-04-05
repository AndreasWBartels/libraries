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

import net.anwiba.commons.swing.action.AbstractActionDescription;

public class ToolBarItemDescription extends AbstractActionDescription {

  private final ToolBarDescription toolBarDescription;
  private final ToolBarItemGroupDescription toolBarGroupDescription;

  public ToolBarItemDescription(final ToolBarDescription toolBarDescription, final int weight) {
    this(toolBarDescription, new ToolBarItemGroupDescription("DEFAULT", 0), weight); //$NON-NLS-1$
  }

  public ToolBarItemDescription(
    final ToolBarDescription toolBarDescription,
    final ToolBarItemGroupDescription toolBarGroupDescription,
    final int weight) {
    super(weight);
    this.toolBarDescription = toolBarDescription;
    this.toolBarGroupDescription = toolBarGroupDescription;
  }

  public ToolBarDescription getToolBarDescription() {
    return this.toolBarDescription;
  }

  public ToolBarItemGroupDescription getToolBarGroupDescription() {
    return this.toolBarGroupDescription;
  }
}
