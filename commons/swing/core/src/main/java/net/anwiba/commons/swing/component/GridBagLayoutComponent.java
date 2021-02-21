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

import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.JComponent;

public class GridBagLayoutComponent implements IGridBagLayoutComponent {

  private final JComponent component;
  private final int column;
  private final int row;
  private final int with;
  private final int anchor;
  private final int height;

  public GridBagLayoutComponent(
      final JComponent component,
      final int column,
      final int row,
      final int with,
      final int height,
      final int anchor) {
    this.component = component;
    this.column = column;
    this.row = row;
    this.with = with;
    this.height = height;
    this.anchor = anchor;
  }

  @Override
  public JComponent getComponent() {
    return this.component;
  }

  @Override
  public GridBagConstraints getConstraints(final Insets insets) {
    final Insets componentInsets = this.column == 0 && this.row == 0 //
        ? new Insets(0, 0, 0, 0)
        : //
        this.row == 0 //
            ? new Insets(0, insets.left, 0, 0)
            : this.column == 0 //
                ? new Insets(insets.top, 0, 0, 0)
                : insets;
    final GridBagConstraints constraints = new GridBagConstraints();
    constraints.fill = GridBagConstraints.HORIZONTAL;
    constraints.insets = componentInsets;
    constraints.gridx = this.column;
    constraints.gridy = this.row;
    constraints.gridwidth = this.with;
    constraints.gridheight = this.height;
    if (this.anchor > 0) {
      if (this.column == 0 && this.row == 0) {
        constraints.anchor = GridBagConstraints.FIRST_LINE_END;
      } else if (this.column == 0) {
        constraints.anchor = GridBagConstraints.LINE_START;
      } else {
        constraints.anchor = this.anchor;
      }
    }
    return constraints;
  }

}
