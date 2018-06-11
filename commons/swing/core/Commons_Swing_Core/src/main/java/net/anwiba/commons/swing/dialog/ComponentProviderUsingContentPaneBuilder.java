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

import javax.swing.JComponent;

import net.anwiba.commons.preferences.IPreferences;
import net.anwiba.commons.swing.component.IComponentProvider;
import net.anwiba.commons.swing.dialog.pane.AbstractContentPane;
import net.anwiba.commons.swing.dialog.pane.IContentPanel;

public final class ComponentProviderUsingContentPaneBuilder extends AbstractContentPaneBuilder {
  final IComponentProvider componentProvider;

  public ComponentProviderUsingContentPaneBuilder(final IComponentProvider componentProvider) {
    this.componentProvider = componentProvider;
  }

  @Override
  protected IContentPanel create(final Window owner, final IPreferences preferences) {
    @SuppressWarnings("hiding")
    final IComponentProvider componentProvider = this.componentProvider;
    return new AbstractContentPane() {

      @Override
      public JComponent getComponent() {
        return componentProvider.getComponent();
      }
    };
  }
}
