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

import javax.swing.Icon;

import net.anwiba.commons.swing.icon.GuiIconSize;
import net.anwiba.commons.swing.ui.IObjectUi;

public class ChooserPanelConfigurationUi<T> implements IObjectUi<IChooserPanelConfiguration<T>> {

  private final GuiIconSize size;

  public ChooserPanelConfigurationUi(final GuiIconSize size) {
    this.size = size;
  }

  @Override
  public Icon getIcon(final IChooserPanelConfiguration<T> configuration) {
    return configuration.getGuiIcon().getIcon(this.size);
  }

  @Override
  public String getText(final IChooserPanelConfiguration<T> configuration) {
    return configuration.getName();
  }

  @Override
  public String getToolTipText(final IChooserPanelConfiguration<T> configuration) {
    return configuration.getToolTipText();
  }

}
