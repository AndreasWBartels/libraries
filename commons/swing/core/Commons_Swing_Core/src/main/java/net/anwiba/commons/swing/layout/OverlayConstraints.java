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

import java.awt.Insets;
import java.util.HashSet;
import java.util.Set;

public class OverlayConstraints {

  private final Set<Anchor> anchors = new HashSet<>();
  private final double widthFactor;
  private final double heightFactor;
  private final double topFactor;
  private final double rightFactor;
  private final double bottomFactor;
  private final double leftFactor;
  private final Insets insets;

  public OverlayConstraints(
      final Set<Anchor> anchors,
      final Insets insets,
      final double topFactor,
      final double leftFactor,
      final double bottomFactor,
      final double rightFactor,
      final double widthFactor,
      final double heightFactor) {
    this.anchors.addAll(anchors);
    this.insets = insets;
    this.topFactor = topFactor;
    this.leftFactor = leftFactor;
    this.bottomFactor = bottomFactor;
    this.rightFactor = rightFactor;
    this.heightFactor = heightFactor;
    this.widthFactor = widthFactor;
  }

  public Set<Anchor> getAnchors() {
    return anchors;
  }

  public double getWidthFactor() {
    return this.widthFactor;
  }

  public double getHeightFactor() {
    return this.heightFactor;
  }

  public Insets getInsets() {
    return this.insets;
  }

  public double getTopFactor() {
    return this.topFactor;
  }

  public double getLeftFactor() {
    return this.leftFactor;
  }

  public double getBottomFactor() {
    return this.bottomFactor;
  }

  public double getRightFactor() {
    return this.rightFactor;
  }
}
