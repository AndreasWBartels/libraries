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

public class OverlayConstraintsBuilder {

  private final Set<Anchor> anchors = new HashSet<>();
  private int top = 0;
  private int left = 0;
  private int bottom = 0;
  private int right = 0;
  private double topFactor = Double.NaN;
  private double leftFactor = Double.NaN;
  private double bottomFactor = Double.NaN;
  private double rightFactor = Double.NaN;
  private double widthFactor = Double.NaN;
  private double heightFactor = Double.NaN;

  public OverlayConstraintsBuilder addAnchorToLeft() {
    anchors.add(Anchor.LEFT);
    return this;
  }

  public OverlayConstraintsBuilder addAnchorToLeft(final int distance) {
    setLeftBorder(distance);
    anchors.add(Anchor.LEFT);
    return this;
  }

  public OverlayConstraintsBuilder addAnchorToLeft(final double distanceFactor) {
    setLeftFactor(distanceFactor);
    anchors.add(Anchor.LEFT);
    return this;
  }

  public OverlayConstraintsBuilder addAnchorToRight() {
    anchors.add(Anchor.RIGHT);
    return this;
  }

  public OverlayConstraintsBuilder addAnchorToRight(final int distance) {
    setRightBorder(distance);
    anchors.add(Anchor.RIGHT);
    return this;
  }

  public OverlayConstraintsBuilder addAnchorToRight(final double distanceFactor) {
    setRightBorderFactor(distanceFactor);
    anchors.add(Anchor.RIGHT);
    return this;
  }

  public OverlayConstraintsBuilder addAnchorToTop() {
    anchors.add(Anchor.TOP);
    return this;
  }

  public OverlayConstraintsBuilder addAnchorToTop(final int distance) {
    setTopBorder(distance);
    anchors.add(Anchor.TOP);
    return this;
  }

  public OverlayConstraintsBuilder addAnchorToTop(final double distanceFactor) {
    setTopFactor(distanceFactor);
    anchors.add(Anchor.TOP);
    return this;
  }

  public OverlayConstraintsBuilder addAnchorToBottom() {
    anchors.add(Anchor.BUTTOM);
    return this;
  }

  public OverlayConstraintsBuilder addAnchorToBottom(final int distance) {
    setButtomBorder(distance);
    anchors.add(Anchor.BUTTOM);
    return this;
  }

  public OverlayConstraintsBuilder addAnchorToBottom(final double distanceFactor) {
    setBottomFactor(distanceFactor);
    anchors.add(Anchor.BUTTOM);
    return this;
  }

  public OverlayConstraintsBuilder setHorizontelFilling() {
    addAnchorToLeft();
    addAnchorToRight();
    return this;
  }

  public OverlayConstraintsBuilder setVerticalFilling() {
    addAnchorToTop();
    addAnchorToBottom();
    return this;
  }

  public OverlayConstraintsBuilder setFullFilling() {
    addAnchorToTop();
    addAnchorToBottom();
    addAnchorToLeft();
    addAnchorToRight();
    return this;
  }

  public OverlayConstraintsBuilder setRightBorder(final int width) {
    if (width < 0) {
      throw new IllegalArgumentException();
    }
    right = width;
    return this;
  }

  public OverlayConstraintsBuilder setLeftBorder(final int width) {
    if (width < 0) {
      throw new IllegalArgumentException();
    }
    left = width;
    return this;
  }

  public OverlayConstraintsBuilder setTopBorder(final int width) {
    if (width < 0) {
      throw new IllegalArgumentException();
    }
    top = width;
    return this;
  }

  public OverlayConstraintsBuilder setButtomBorder(final int width) {
    if (width < 0) {
      throw new IllegalArgumentException();
    }
    bottom = width;
    return this;
  }

  public OverlayConstraintsBuilder setRightBorderFactor(final double width) {
    if (!Double.isNaN(width) && (width < 0 || width > 1)) {
      throw new IllegalArgumentException();
    }
    rightFactor = width;
    return this;
  }

  public OverlayConstraintsBuilder setLeftFactor(final double width) {
    if (!Double.isNaN(width) && (width < 0 || width > 1)) {
      throw new IllegalArgumentException();
    }
    leftFactor = width;
    return this;
  }

  public OverlayConstraintsBuilder setTopFactor(final double width) {
    if (!Double.isNaN(width) && (width < 0 || width > 1)) {
      throw new IllegalArgumentException();
    }
    topFactor = width;
    return this;
  }

  public OverlayConstraintsBuilder setBottomFactor(final double width) {
    if (!Double.isNaN(width) && (width < 0 || width > 1)) {
      throw new IllegalArgumentException();
    }
    bottomFactor = width;
    return this;
  }

  public OverlayConstraintsBuilder setBorder(
      final int top,
      final int left,
      final int bottom,
      final int right) {
    this.top = top;
    this.left = left;
    this.bottom = bottom;
    this.right = right;
    return this;
  }

  public OverlayConstraints build() {
    return new OverlayConstraints(
        anchors,
        new Insets(top, left, bottom, right),
        topFactor,
        leftFactor,
        bottomFactor,
        rightFactor,
        widthFactor,
        heightFactor);
  }

  public OverlayConstraintsBuilder setWidthFactor(final double widthFactor) {
    this.widthFactor = widthFactor;
    return this;
  }

  public OverlayConstraintsBuilder setHeightFactor(final double heightFactor) {
    this.heightFactor = heightFactor;
    return this;
  }

}
