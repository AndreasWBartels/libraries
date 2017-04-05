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

import net.anwiba.commons.lang.object.ObjectUtilities;
import net.anwiba.commons.swing.action.AbstractIdBasedWeightDescription;
import net.anwiba.commons.utilities.factory.IFactory;

import javax.swing.JMenu;

public class MenuDescription extends AbstractIdBasedWeightDescription {

  private final String title;
  private final IFactory<String, JMenu, RuntimeException> factory;

  public MenuDescription(
    final String id,
    final String title,
    final int weight,
    final IFactory<String, JMenu, RuntimeException> factory) {
    super(id, weight);
    this.title = title;
    this.factory = factory;
  }

  public MenuDescription(final String id, final String title, final int weight) {
    this(id, title, weight, new IFactory<String, JMenu, RuntimeException>() {

      @Override
      public JMenu create(final String title) {
        return new JMenu(title);
      }
    });
  }

  public String getTitle() {
    return this.title;
  }

  @Override
  public int hashCode() {
    return this.getId().hashCode();
  }

  @Override
  public boolean equals(final Object obj) {
    if (!(obj instanceof MenuDescription)) {
      return false;
    }
    final MenuDescription other = (MenuDescription) obj;
    return ObjectUtilities.equals(getId(), other.getId());
  }

  public IFactory<String, JMenu, RuntimeException> getMenuFactory() {
    return this.factory;
  }
}
