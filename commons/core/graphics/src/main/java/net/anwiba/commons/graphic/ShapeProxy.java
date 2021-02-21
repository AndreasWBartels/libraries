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
package net.anwiba.commons.graphic;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class ShapeProxy implements Shape {

  public static final class PathIteratorProxy implements PathIterator {
    private final PathIterator pathIterator;

    public PathIteratorProxy(final PathIterator pathIterator) {
      this.pathIterator = pathIterator;
    }

    @Override
    public void next() {
      this.pathIterator.next();
    }

    @Override
    public boolean isDone() {
      return this.pathIterator.isDone();
    }

    @Override
    public int getWindingRule() {
      return this.pathIterator.getWindingRule();
    }

    @Override
    public int currentSegment(final double[] coords) {
      return this.pathIterator.currentSegment(coords);
    }

    @Override
    public int currentSegment(final float[] coords) {
      return this.pathIterator.currentSegment(coords);
    }
  }

  private final Shape shape;

  public ShapeProxy(final Shape shape) {
    this.shape = shape;
  }

  @Override
  public Rectangle getBounds() {
    return this.shape.getBounds();
  }

  @Override
  public Rectangle2D getBounds2D() {
    return this.shape.getBounds2D();
  }

  @Override
  public boolean contains(final double x, final double y) {
    return this.shape.contains(x, y);
  }

  @Override
  public boolean contains(final Point2D p) {
    return this.shape.contains(p);
  }

  @Override
  public boolean intersects(final double x, final double y, final double w, final double h) {
    return this.shape.intersects(x, y, w, h);
  }

  @Override
  public boolean intersects(final Rectangle2D r) {
    return this.shape.intersects(r);
  }

  @Override
  public boolean contains(final double x, final double y, final double w, final double h) {
    return this.shape.contains(x, y, w, h);
  }

  @Override
  public boolean contains(final Rectangle2D r) {
    return this.shape.contains(r);
  }

  @Override
  public PathIterator getPathIterator(final AffineTransform at) {
    return new PathIteratorProxy(this.shape.getPathIterator(at));
  }

  @Override
  public PathIterator getPathIterator(final AffineTransform at, final double flatness) {
    return new PathIteratorProxy(this.shape.getPathIterator(at, flatness));
  }

}
