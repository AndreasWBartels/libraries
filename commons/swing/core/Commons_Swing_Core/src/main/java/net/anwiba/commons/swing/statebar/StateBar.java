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
package net.anwiba.commons.swing.statebar;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

public class StateBar extends JPanel {

  private static final long serialVersionUID = 1L;

  final JPanel leftContainer = new JPanel();
  final JPanel rightContainer = new JPanel();
  {
    setLayout(new BorderLayout());
    this.leftContainer.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
    this.leftContainer.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
    add(BorderLayout.WEST, this.leftContainer);
    this.rightContainer.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
    this.rightContainer.setLayout(new FlowLayout(FlowLayout.RIGHT, 0, 0));
    add(BorderLayout.EAST, this.rightContainer);
  }

  public void add(final Side side, final Component component) {
    final ISideVisitor visitor = new ISideVisitor() {

      @Override
      public void visitLeft() {
        StateBar.this.leftContainer.add(component);
      }

      @Override
      public void visitRight() {
        StateBar.this.rightContainer.add(component);
      }

    };
    side.accept(visitor);
  }
}
