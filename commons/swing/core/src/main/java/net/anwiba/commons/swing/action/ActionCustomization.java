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
package net.anwiba.commons.swing.action;

import javax.swing.Icon;

import net.anwiba.commons.swing.icon.IGuiIcon;
import net.anwiba.commons.utilities.string.StringUtilities;

public final class ActionCustomization implements IActionCustomization {
  private final String tooltipText;
  private final String name;
  private final IGuiIcon icon;

  public ActionCustomization(final String name, final IGuiIcon icon, final String tooltipText) {
    this.name = name;
    this.icon = icon;
    this.tooltipText = tooltipText;
  }

  @Override
  public String getTooltipText() {
    return StringUtilities.isNullOrTrimmedEmpty(this.tooltipText) ? null : this.tooltipText;
  }

  @Override
  public String getName() {
    return this.name;
  }

  @Override
  public Icon getIcon() {
    if (this.icon == null) {
      return null;
    }
    return this.icon.getSmallIcon();
  }
}