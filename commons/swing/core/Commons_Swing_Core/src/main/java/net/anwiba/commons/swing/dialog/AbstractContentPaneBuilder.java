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
package net.anwiba.commons.swing.dialog;

import java.awt.Window;

import net.anwiba.commons.preferences.DummyPreferences;
import net.anwiba.commons.preferences.IPreferences;
import net.anwiba.commons.swing.dialog.pane.IContentPaneBuilder;
import net.anwiba.commons.swing.dialog.pane.IContentPanel;

public abstract class AbstractContentPaneBuilder implements IContentPaneBuilder {

  private Window owner;
  private IPreferences preferences = new DummyPreferences();

  public AbstractContentPaneBuilder() {
    super();
  }

  @Override
  public IContentPaneBuilder setOwner(final Window owner) {
    this.owner = owner;
    return this;
  }

  @Override
  public IContentPaneBuilder setPreferences(final IPreferences preferences) {
    this.preferences = preferences;
    return this;
  }

  @Override
  public IContentPanel build() {
    return create(this.owner, this.preferences);
  }

  @SuppressWarnings("hiding")
  protected abstract IContentPanel create(Window owner, IPreferences preferences);

}
