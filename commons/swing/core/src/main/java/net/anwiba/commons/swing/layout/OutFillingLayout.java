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

package net.anwiba.commons.swing.layout;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager2;

import javax.swing.BorderFactory;
import javax.swing.JComponent;

public final class OutFillingLayout implements LayoutManager2 {

  private final Color borderColor = Color.BLACK;
  //UIManager.getLookAndFeelDefaults().getColor("Panel.background"); //$NON-NLS-1$
  private final int numberOfRows;

  public OutFillingLayout(final int numberOfRows) {
    this.numberOfRows = numberOfRows;
  }

  @Override
  public void removeLayoutComponent(final Component comp) {
    // nothing to do
  }

  @Override
  public Dimension preferredLayoutSize(final Container parent) {
    return getSize(parent);
  }

  @Override
  public Dimension minimumLayoutSize(final Container parent) {
    return getSize(parent);
  }

  @Override
  public Dimension maximumLayoutSize(final Container parent) {
    return getSize(parent);
  }

  private Dimension getSize(final Container parent) {
    return new Dimension(parent.getWidth(), parent.getHeight());
  }

  @Override
  public void layoutContainer(final Container target) {
    int counter = 0;
    final int width = target.getWidth();
    final int height = target.getHeight() / this.numberOfRows;
    for (final Component c : target.getComponents()) {
      c.setBounds(0, height * counter++, width, height);
      if (c instanceof JComponent) {
        ((JComponent) c).setBorder(BorderFactory.createLineBorder(this.borderColor, 1));
      }
    }
  }

  @Override
  public void addLayoutComponent(final String name, final Component comp) {
    // nothing to do
  }

  @Override
  public void addLayoutComponent(final Component comp, final Object constraints) {
    // nothing to do
  }

  @Override
  public float getLayoutAlignmentX(final Container target) {
    return 0;
  }

  @Override
  public float getLayoutAlignmentY(final Container target) {
    return 0;
  }

  @Override
  public void invalidateLayout(final Container target) {
    // nothing to do
  }
}
