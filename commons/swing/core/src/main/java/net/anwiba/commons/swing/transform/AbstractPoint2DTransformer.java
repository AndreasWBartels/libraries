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
package net.anwiba.commons.swing.transform;

import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;

public abstract class AbstractPoint2DTransformer implements IPoint2DTransformer {

  public static final class Point2DTransformer extends AbstractPoint2DTransformer {
    private final AffineTransform scaled;

    public Point2DTransformer(AffineTransform scaled) {
      this.scaled = scaled;
    }

    @Override
    protected AffineTransform getAffinTransform() {
      return this.scaled;
    }
  }

  @Override
  public Point2D transform(final Point2D point) throws NoninvertibleTransformException {
    final Point2D transformedPoint = new Point2D.Double();
    getAffinTransform().transform(point, transformedPoint);
    return transformedPoint;
  }

  @Override
  public Point2D inverseTransform(final Point2D point) throws NoninvertibleTransformException {
    final Point2D transformedPoint = new Point2D.Double();
    getAffinTransform().inverseTransform(point, transformedPoint);
    return transformedPoint;
  }

  @Override
  public IPoint2DTransformer getScaledInstance(final double scale) {
    final AffineTransform affinTransform = getAffinTransform();
    final AffineTransform scaled = new AffineTransform(affinTransform);
    scaled.scale(scale, -scale);
    return new Point2DTransformer(scaled);
  }

  protected abstract AffineTransform getAffinTransform();

}
