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
package net.anwiba.commons.swing.utilities;

import net.anwiba.commons.lang.exception.UnreachableCodeReachedException;

import java.awt.Component;
import java.awt.Container;

import javax.swing.Spring;
import javax.swing.SpringLayout;

/**
 * A 1.4 file that provides utility methods for creating form- or grid-style layouts with SpringLayout. These utilities
 * are used by several programs, such as SpringBox and SpringCompactGrid.
 */
public class SpringLayoutUtilities {

  /**
   * Aligns the first <code>rows</code> * <code>cols</code> components of <code>parent</code> in a grid. Each component
   * is as big as the maximum preferred width and height of the components. The parent is made just big enough to fit
   * them all.
   * @param cols
   *          number of columns
   * @param rows
   *          number of rows
   * @param initialX
   *          x location to start the grid at
   * @param initialY
   *          y location to start the grid at
   * @param xPad
   *          x padding between cells
   * @param yPad
   *          y padding between cells
   */
  public static void makeGrid(
      final Container parent,
      final int cols,
      final int rows,
      final int initialX,
      final int initialY,
      final int xPad,
      final int yPad) {
    SpringLayout layout;
    try {
      layout = (SpringLayout) parent.getLayout();
    } catch (final ClassCastException exc) {
      throw new RuntimeException("The first argument to makeGrid must use SpringLayout."); //$NON-NLS-1$
    }

    final Spring xPadSpring = Spring.constant(xPad);
    final Spring yPadSpring = Spring.constant(yPad);
    final Spring initialXSpring = Spring.constant(initialX);
    final Spring initialYSpring = Spring.constant(initialY);
    final int max = rows * cols;

    // Calculate Springs that are the max of the width/height so that all
    // cells have the same size.
    Spring maxWidthSpring = layout.getConstraints(parent.getComponent(0)).getWidth();
    Spring maxHeightSpring = layout.getConstraints(parent.getComponent(0)).getWidth();
    for (int i = 1; i < max; i++) {
      final SpringLayout.Constraints cons = layout.getConstraints(parent.getComponent(i));

      maxWidthSpring = Spring.max(maxWidthSpring, cons.getWidth());
      maxHeightSpring = Spring.max(maxHeightSpring, cons.getHeight());
    }

    // Apply the new width/height Spring. This forces all the
    // components to have the same size.
    for (int i = 0; i < max; i++) {
      final SpringLayout.Constraints cons = layout.getConstraints(parent.getComponent(i));

      cons.setWidth(maxWidthSpring);
      cons.setHeight(maxHeightSpring);
    }

    // Then adjust the x/y constraints of all the cells so that they
    // are aligned in a grid.
    SpringLayout.Constraints lastCons = null;
    SpringLayout.Constraints lastRowCons = null;
    for (int i = 0; i < max; i++) {
      final SpringLayout.Constraints cons = layout.getConstraints(parent.getComponent(i));
      if (i % cols == 0) { // start of new row
        lastRowCons = lastCons;
        cons.setX(initialXSpring);
      } else if (lastCons != null) { // x position depends on previous component
        cons.setX(Spring.sum(lastCons.getConstraint(SpringLayout.EAST), xPadSpring));
      } else {
        throw new UnreachableCodeReachedException();
      }

      if (i / cols == 0) { // first row
        cons.setY(initialYSpring);
      } else if (lastRowCons != null) { // y position depends on previous row
        cons.setY(Spring.sum(lastRowCons.getConstraint(SpringLayout.SOUTH), yPadSpring));
      } else {
        throw new UnreachableCodeReachedException();
      }
      lastCons = cons;
    }

    if (lastCons == null) {
      throw new UnreachableCodeReachedException();
    }

    // Set the parent's size.
    final SpringLayout.Constraints pCons = layout.getConstraints(parent);
    pCons.setConstraint(
        SpringLayout.SOUTH,
        Spring.sum(Spring.constant(initialX), lastCons.getConstraint(SpringLayout.SOUTH)));
    pCons.setConstraint(
        SpringLayout.EAST,
        Spring.sum(Spring.constant(initialY), lastCons.getConstraint(SpringLayout.EAST)));
  }

  /* Used by makeCompactGrid. */
  private static SpringLayout.Constraints getConstraintsForCell(
      final int col,
      final int row,
      final Container parent,
      final int cols) {
    final SpringLayout layout = (SpringLayout) parent.getLayout();
    final Component c = parent.getComponent(row * cols + col);
    return layout.getConstraints(c);
  }

  /**
   * Aligns the first <code>rows</code> * <code>cols</code> components of <code>parent</code> in a grid. Each component
   * in a column is as wide as the maximum preferred width of the components in that column; height is similarly
   * determined for each row. The parent is made just big enough to fit them all.
   * @param cols
   *          number of columns
   * @param rows
   *          number of rows
   * @param initialX
   *          x location to start the grid at
   * @param initialY
   *          y location to start the grid at
   * @param xPad
   *          x padding between cells
   * @param yPad
   *          y padding between cells
   */
  public static void makeCompactGrid(
      final Container parent,
      final int cols,
      final int rows,
      final int initialX,
      final int initialY,
      final int xPad,
      final int yPad) {
    SpringLayout layout;
    try {
      layout = (SpringLayout) parent.getLayout();
    } catch (final ClassCastException exc) {
      throw new RuntimeException("The first argument to makeCompactGrid must use SpringLayout."); //$NON-NLS-1$
    }

    // Align all cells in each column and make them the same width.
    Spring x = Spring.constant(initialX);
    for (int c = 0; c < cols; c++) {
      Spring width = Spring.constant(0);
      for (int r = 0; r < rows; r++) {
        width = Spring.max(width, getConstraintsForCell(c, r, parent, cols).getWidth());
      }
      for (int r = 0; r < rows; r++) {
        final SpringLayout.Constraints constraints = getConstraintsForCell(c, r, parent, cols);
        constraints.setX(x);
        constraints.setWidth(width);
      }
      x = Spring.sum(x, Spring.sum(width, Spring.constant(xPad)));
    }
    x = Spring.sum(x, Spring.sum(Spring.constant(-xPad), Spring.constant(initialX)));

    // Align all cells in each row and make them the same height.
    Spring y = Spring.constant(initialY);
    for (int row = 0; row < rows; row++) {
      Spring height = Spring.constant(0);
      for (int c = 0; c < cols; c++) {
        height = Spring.max(height, getConstraintsForCell(c, row, parent, cols).getHeight());
      }
      for (int col = 0; col < cols; col++) {
        final SpringLayout.Constraints constraints = getConstraintsForCell(col, row, parent, cols);
        constraints.setY(y);
        constraints.setHeight(height);
      }
      y = Spring.sum(y, Spring.sum(height, Spring.constant(yPad)));
    }
    y = Spring.sum(y, Spring.sum(Spring.constant(-yPad), Spring.constant(initialY)));

    // Set the parent's size.
    final SpringLayout.Constraints pCons = layout.getConstraints(parent);
    pCons.setConstraint(SpringLayout.SOUTH, y);
    pCons.setConstraint(SpringLayout.EAST, x);
  }
}
