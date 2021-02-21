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

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager2;
import java.awt.Rectangle;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class OverlayAnchorLayout implements LayoutManager2 {

  public static interface Function {

    public int execute(int i, int j);

  }

  public static interface Size {

    public Dimension get(Component component);

  }

  private final Map<Component, OverlayConstraints> constraintsMap = new HashMap<>();
  private final Map<Component, Rectangle> boundsMap = new HashMap<>();

  @Override
  public void layoutContainer(final Container target) {
    if (!this.boundsMap.isEmpty()) {
      for (final Component c : target.getComponents()) {
        if (this.boundsMap.containsKey(c)) {
          c.setBounds(this.boundsMap.get(c));
          continue;
        }
        c.setBounds(0, 0, c.getWidth(), c.getHeight());
      }
      return;
    }
    for (final Component c : target.getComponents()) {
      if (this.constraintsMap.containsKey(c)) {
        final OverlayConstraints constraints = this.constraintsMap.get(c);
        final Rectangle rectangle = new Rectangle( //
            calculateX(constraints, target, c), //
            calculateY(constraints, target, c),
            calculateWidth(constraints, target, c),
            calculateHeight(constraints, target, c));
        c.setBounds(rectangle);
        this.boundsMap.put(c, rectangle);
        continue;
      }
      c.setBounds(0, 0, c.getWidth(), c.getHeight());
    }
  }

  private int calculateX(final OverlayConstraints constraints, final Container target, final Component component) {
    final Set<Anchor> anchors = constraints.getAnchors();
    final Insets insets = constraints.getInsets();
    if (anchors.contains(Anchor.LEFT)) {
      return (Double.isNaN(constraints.getLeftFactor()) //
          ? insets.left //
          : (int) (constraints.getLeftFactor() * target.getWidth()));
    }
    if (anchors.contains(Anchor.RIGHT)) {
      return target.getWidth()
          - (componentWidth(constraints, target, component) + (Double.isNaN(constraints.getRightFactor())
              ? insets.right
              : (int) (constraints.getRightFactor() * target.getWidth())));
    }
    return component.getX();
  }

  private int calculateY(final OverlayConstraints constraints, final Container target, final Component component) {
    final Set<Anchor> anchors = constraints.getAnchors();
    final Insets insets = constraints.getInsets();
    if (anchors.contains(Anchor.TOP)) {
      return (Double.isNaN(constraints.getTopFactor()) //
          ? insets.top
          : (int) (constraints.getTopFactor() * target.getHeight()));
    }
    if (anchors.contains(Anchor.BUTTOM)) {
      return target.getHeight()
          - (componentHeight(constraints, target, component) + (Double.isNaN(constraints.getBottomFactor())
              ? insets.bottom
              : (int) (constraints.getBottomFactor() * target.getHeight())));
    }
    return component.getY();
  }

  private int calculateWidth(final OverlayConstraints constraints, final Container target, final Component component) {
    final Set<Anchor> anchors = constraints.getAnchors();
    final Insets insets = constraints.getInsets();
    if (anchors.contains(Anchor.LEFT) && anchors.contains(Anchor.RIGHT)) {
      return target.getWidth()
          - (Double.isNaN(constraints.getLeftFactor()) ? insets.left : (int) (constraints.getLeftFactor() * target
              .getWidth()))
          - (Double.isNaN(constraints.getRightFactor()) ? insets.right : (int) (constraints.getRightFactor() * target
              .getWidth()));
    }
    return componentWidth(constraints, target, component);
  }

  private int calculateHeight(final OverlayConstraints constraints, final Container target, final Component component) {
    final Set<Anchor> anchors = constraints.getAnchors();
    if (anchors.contains(Anchor.TOP) && anchors.contains(Anchor.BUTTOM)) {
      final Insets insets = constraints.getInsets();
      return target.getHeight()
          - (Double.isNaN(constraints.getTopFactor()) ? insets.top : (int) (constraints.getTopFactor() * target
              .getHeight()))
          - (Double.isNaN(constraints.getBottomFactor())
              ? insets.bottom
              : (int) (constraints.getBottomFactor() * target.getHeight()));
    }
    return componentHeight(constraints, target, component);
  }

  private int componentWidth(final OverlayConstraints constraints, final Container target, final Component component) {
    return Math.min( //
        Math.max(Double.isNaN(constraints.getWidthFactor()) //
            ? component.getWidth()
            : (int) (target.getWidth() * constraints.getWidthFactor()), component.getMinimumSize() == null //
            ? 0
            : component.getMinimumSize().width),
        component.getMaximumSize() == null //
            ? Integer.MAX_VALUE
            : component.getMaximumSize().width);
  }

  private int componentHeight(final OverlayConstraints constraints, final Container target, final Component component) {
    return Math.min( //
        Math.max(Double.isNaN(constraints.getHeightFactor()) //
            ? component.getHeight()
            : (int) (target.getHeight() * constraints.getHeightFactor()), component.getMinimumSize() == null //
            ? 0
            : component.getMinimumSize().height),
        component.getMaximumSize() == null //
            ? Integer.MAX_VALUE
            : component.getMaximumSize().height);
  }

  @Override
  public Dimension preferredLayoutSize(final Container target) {
    if (target.isPreferredSizeSet()) {
      return target.getPreferredSize();
    }
    return validateSize((i, j) -> Math.max(i, j), c -> c.getPreferredSize(), target, new Dimension());
  }

  @Override
  public Dimension minimumLayoutSize(final Container target) {
    if (target.isMinimumSizeSet()) {
      return target.getMinimumSize();
    }
    return validateSize((i, j) -> Math.max(i, j), c -> c.getMinimumSize(), target, new Dimension());
  }

  @Override
  public Dimension maximumLayoutSize(final Container target) {
    if (target.isMaximumSizeSet()) {
      return target.getMaximumSize();
    }
    return validateSize((i, j) -> Math.max(i, j), c -> c.getMaximumSize(), target, new Dimension());
  }

  private Dimension validateSize(
      final Function function,
      final Size sizeProvider,
      final Container target,
      final Dimension size) {
    final Component[] components = target.getComponents();
    for (final Component component : components) {
      final Dimension componentSize = sizeProvider.get(component);
      size.width = function.execute(size.width, componentSize.width);
      size.height = function.execute(size.height, componentSize.height);
    }
    final Insets insets = target.getInsets();
    size.width += insets.left + insets.right;
    size.height += insets.top + insets.bottom;
    return size;
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
  public void addLayoutComponent(final Component comp, final Object constraints) {
    if (constraints instanceof OverlayConstraints) {
      addLayoutComponent(comp, (OverlayConstraints) constraints);
      return;
    }
  }

  public void addLayoutComponent(final Component comp, final OverlayConstraints constraints) {
    this.constraintsMap.put(comp, constraints);
  }

  @Override
  public void addLayoutComponent(final String name, final Component comp) {
    // nothing to do
  }

  @Override
  public void removeLayoutComponent(final Component comp) {
    if (this.constraintsMap.containsKey(comp)) {
      this.constraintsMap.remove(comp);
    }
  }

  @Override
  public void invalidateLayout(final Container target) {
    this.boundsMap.clear();
  }
}
