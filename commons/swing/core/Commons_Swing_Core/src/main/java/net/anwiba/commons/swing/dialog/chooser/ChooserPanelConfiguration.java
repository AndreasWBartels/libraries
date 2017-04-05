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
package net.anwiba.commons.swing.dialog.chooser;

import net.anwiba.commons.swing.icon.IGuiIcon;

public class ChooserPanelConfiguration<T> implements IChooserPanelConfiguration<T> {
  private final IGuiIcon icon;
  private final String name;
  private final IChooserPanelFactory<T> factory;
  private final String toolTipText;
  private final ITryTaskFactory<T> tryTaskFactory;
  private final int order;

  public ChooserPanelConfiguration(
      final String name,
      final IGuiIcon icon,
      final String toolTipText,
      final IChooserPanelFactory<T> panelFactory,
      final ITryTaskFactory<T> tryTaskFactory,
      final int order) {
    this.name = name;
    this.icon = icon;
    this.toolTipText = toolTipText;
    this.factory = panelFactory;
    this.tryTaskFactory = tryTaskFactory;
    this.order = order;
  }

  @Override
  public String getName() {
    return this.name;
  }

  @Override
  public IGuiIcon getGuiIcon() {
    return this.icon;
  }

  @Override
  public IChooserPanelFactory<T> getOptionPanelFactory() {
    return this.factory;
  }

  @Override
  public String getToolTipText() {
    return this.toolTipText;
  }

  @Override
  public ITryTaskFactory<T> getTryTaskFactory() {
    return this.tryTaskFactory;
  }

  @Override
  public int order() {
    return this.order;
  }

}