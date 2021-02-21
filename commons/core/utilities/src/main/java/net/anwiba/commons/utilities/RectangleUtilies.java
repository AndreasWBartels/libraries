/*
 * #%L
 * anwiba commons
 * %%
 * Copyright (C) 2007 - 2019 Andreas W. Bartels
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
package net.anwiba.commons.utilities;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

public class RectangleUtilies {

  public static Rectangle2D toRelativRectangle(final Dimension dimension, final Rectangle rectangle) {
    if (rectangle == null) {
      return null;
    }
    final double withRelation = 100 / dimension.getWidth();
    final double heightRelation = 100 / dimension.getHeight();
    double x = withRelation * rectangle.getX();
    double width = withRelation * rectangle.getWidth();
    double y = heightRelation * rectangle.getY();
    double height = heightRelation * rectangle.getHeight();
    return new Rectangle2D.Double(x, y, width, height);
  }

  public static Rectangle toAbsoluteRectangle(final Dimension dimension, final Rectangle2D rectangle) {
    if (rectangle == null) {
      return null;
    }
    int x = (int) Math.round(dimension.getWidth() * (rectangle.getX() / 100));
    int width = (int) Math.round(dimension.getWidth() * (rectangle.getWidth() / 100.));
    int y = (int) Math.round(dimension.getHeight() * (rectangle.getY() / 100.));
    int height = (int) Math.round(dimension.getHeight() * (rectangle.getHeight() / 100.));
    return new Rectangle(x, y, width, height);
  }
}
