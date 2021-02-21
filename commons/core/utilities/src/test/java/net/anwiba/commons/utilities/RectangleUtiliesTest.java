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

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

import org.junit.jupiter.api.Test;

public class RectangleUtiliesTest {

  @Test
  public void test() {
    final Rectangle rectangle = new Rectangle(10, 10, 210, 280);
    final Dimension dimension = new Dimension(300, 400);
    Rectangle2D relativRectangle = RectangleUtilies.toRelativRectangle(dimension, rectangle);
    Rectangle absoluteRectangle = RectangleUtilies.toAbsoluteRectangle(dimension, relativRectangle);
    assertEquals(rectangle, absoluteRectangle);
  }
}
