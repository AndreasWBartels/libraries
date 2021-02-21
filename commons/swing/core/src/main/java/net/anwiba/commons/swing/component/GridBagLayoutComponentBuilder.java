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

import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

public class GridBagLayoutComponentBuilder {

  private static final int UNDEFIND_ANCHOR = -1;
  private final List<IGridBagLayoutComponent> components = new ArrayList<>(10);
  private int row = 0;
  private int column = -1;
  private int maximunsNumberOfColumns = -1;
  private Border border = BorderFactory.createEmptyBorder(4, 4, 4, 4);
  private final Insets insets = new Insets(4, 4, 0, 0);

  public GridBagLayoutComponentBuilder header(final String text) {
    newline();
    final JLabel label = new JLabel(text + (text.isEmpty() ? "" : ":")); //$NON-NLS-1$ //$NON-NLS-2$
    label.setVerticalTextPosition(JLabel.TOP);
    label.setVerticalAlignment(JLabel.TOP);
    label.setHorizontalTextPosition(JLabel.LEFT);
    label.setHorizontalAlignment(JLabel.LEFT);
    label.setFont(label.getFont().deriveFont(Font.BOLD, label.getFont().getSize() + 2));
    this.components.add(new GridBagLayoutComponent(label, ++this.column, this.row, 1, 1, UNDEFIND_ANCHOR));
    return this;
  }

  public GridBagLayoutComponentBuilder label(final String text) {
    newline();
    final JLabel label = new JLabel(text + (text.isEmpty() ? "" : ":")); //$NON-NLS-1$ //$NON-NLS-2$
    label.setVerticalTextPosition(JLabel.TOP);
    label.setVerticalAlignment(JLabel.TOP);
    label.setHorizontalTextPosition(JLabel.LEFT);
    label.setHorizontalAlignment(JLabel.LEFT);
    this.components.add(new GridBagLayoutComponent(label, ++this.column, this.row, 1, 1, UNDEFIND_ANCHOR));
    return this;
  }

  public GridBagLayoutComponentBuilder add(final JComponent component) {
    this.components.add(new GridBagLayoutComponent(component, ++this.column, this.row, 1, 1, UNDEFIND_ANCHOR));
    return this;
  }

  public GridBagLayoutComponentBuilder add(final JComponent component, final int with) {
    final int componentColumn = this.column + 1;
    this.column += with;
    this.components.add(new GridBagLayoutComponent(component, componentColumn, this.row, with, 1, UNDEFIND_ANCHOR));
    return this;
  }

  public GridBagLayoutComponentBuilder newline() {
    this.maximunsNumberOfColumns = Math.max(this.maximunsNumberOfColumns, this.column);
    this.column = -1;
    this.row += 1;
    return this;
  }

  public JComponent build() {
    final JPanel contentPanel = new JPanel(new GridBagLayout());
    contentPanel.setBorder(this.border);
    for (final IGridBagLayoutComponent component : this.components) {
      contentPanel.add(component.getComponent(), component.getConstraints(this.insets));
    }
    return contentPanel;
  }

  public GridBagLayoutComponentBuilder emptyLine() {
    if (this.column > -1) {
      newline();
    }
    this.components.add(new GridBagLayoutComponent(new JLabel(" "), ++this.column, this.row, 1, 1, UNDEFIND_ANCHOR)); //$NON-NLS-1$
    newline();
    return this;
  }

  public GridBagLayoutComponentBuilder setBorder(final Border border) {
    this.border = border;
    return this;
  }
}
