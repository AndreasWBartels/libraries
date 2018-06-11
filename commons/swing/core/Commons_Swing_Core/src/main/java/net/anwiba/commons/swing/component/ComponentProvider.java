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
package net.anwiba.commons.swing.component;

import java.awt.BorderLayout;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

import net.anwiba.commons.swing.utilities.SpringLayoutUtilities;

@SuppressWarnings("serial")
public final class ComponentProvider implements IComponentProvider {
  private final IComponentContainer container;
  private JPanel panel;

  public ComponentProvider(final IComponentContainer container) {
    this.container = container;
  }

  @Override
  public JComponent getComponent() {
    if (this.panel == null) {
      @SuppressWarnings("hiding")
      final IComponentContainer container = this.container;
      final JPanel component = new JPanel(new SpringLayout());
      this.panel = new JPanel(new BorderLayout()) {

        @Override
        public void setEnabled(final boolean enabled) {
          super.setEnabled(enabled);
          container.setEnabled(enabled);
        }
      };
      this.panel.add(component, BorderLayout.NORTH);
      this.container.addTo(component);
      SpringLayoutUtilities.makeCompactGrid(component, container.getColumnCount(), container.getRowCount(), 4, 4, 6, 6);
    }
    return this.panel;
  }
}
