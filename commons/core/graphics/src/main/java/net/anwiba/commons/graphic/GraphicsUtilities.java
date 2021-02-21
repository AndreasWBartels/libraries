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

import java.awt.Color;
import java.awt.geom.Rectangle2D;

public class GraphicsUtilities {

  public static void paintTextBackground(final IGraphics graphics,
      final String string,
      final double x,
      final double y) {
    Rectangle2D bounds = graphics.getFont().getStringBounds(string, graphics.getFontRenderContext());
    graphics.setColor(new Color(1f, 1f, 1f, 0.7f));
    graphics.fill(new Rectangle2D.Double(x + 3,
        (y + bounds.getHeight() - 8),
        bounds.getWidth() + 4,
        bounds.getHeight() + 4));
  }

}
