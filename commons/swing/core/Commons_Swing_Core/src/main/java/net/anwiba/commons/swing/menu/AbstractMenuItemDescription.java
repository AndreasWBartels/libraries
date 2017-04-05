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

import net.anwiba.commons.ensure.Ensure;
import net.anwiba.commons.swing.action.IWeightDescription;

public abstract class AbstractMenuItemDescription implements IWeightDescription {

  protected final int weight;
  protected final MenuDescription menuDescription;
  protected final MenuItemGroupDescription menuGroupDescription;

  public AbstractMenuItemDescription(
    final MenuDescription menuDescription,
    final MenuItemGroupDescription menuGroupDescription,
    final int weight) {
    Ensure.ensureArgumentNotNull(menuGroupDescription);
    this.menuDescription = menuDescription;
    this.menuGroupDescription = menuGroupDescription;
    this.weight = weight;
  }

  public MenuDescription getMenuDescription() {
    return this.menuDescription;
  }

  public MenuItemGroupDescription getMenuGroupDescription() {
    return this.menuGroupDescription;
  }

  public abstract MenuItemType getMenuItemType();

  @Override
  public int getWeight() {
    return this.weight;
  }
}