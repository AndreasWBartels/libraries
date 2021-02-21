/*
 * #%L
 * anwiba commons
 * %%
 * Copyright (C) 2007 - 2021 Andreas W. Bartels
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
package net.anwiba.testing.demo.component;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.Icon;
import javax.swing.JComponent;

public class IconComponent extends JComponent {

  private final Icon icon;

  public IconComponent(final Icon icon) {
    this.icon = icon;
  }

  @Override
  public Dimension getPreferredSize() {
    return getIconSize();
  }

  private Dimension getIconSize() {
    return new Dimension(this.icon.getIconWidth(), this.icon.getIconHeight());
  }

  @Override
  public Dimension getMinimumSize() {
    return getPreferredSize();
  }

  @Override
  public Dimension getMaximumSize() {
    return getPreferredSize();
  }

  @Override
  public void paint(final Graphics g) {
    g.setColor(Color.white);
    g.fillRect(0, 0, getIconSize().width, getIconSize().height);
    this.icon.paintIcon(this, g, 0, 0);
  }
}
