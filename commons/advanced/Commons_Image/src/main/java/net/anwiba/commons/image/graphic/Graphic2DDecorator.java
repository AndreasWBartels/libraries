/*
 * #%L
 * *
 * %%
 * Copyright (C) 2007 - 2017 Andreas W. Bartels
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
package net.anwiba.commons.image.graphic;

import java.awt.Graphics2D;
import java.awt.RenderingHints;

public class Graphic2DDecorator extends AbstractGraphics {

  public Graphic2DDecorator(final Graphics2D graphics) {
    this(graphics, new RenderingHints(null));
  }

  public Graphic2DDecorator(final Graphics2D graphics, final RenderingHints hints) {
    super(graphics);
    hints.forEach((k, v) -> graphics.setRenderingHint((RenderingHints.Key) k, v));
  }

}
