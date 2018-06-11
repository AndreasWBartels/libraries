/*
 * #%L
 * anwiba commons advanced
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
package net.anwiba.commons.image;

import java.awt.image.BufferedImage;

public class BufferedImageContainer extends AbstractRenderedImageContainer {

  private final BufferedImage image;

  public BufferedImageContainer(final BufferedImage image) {
    super(image);
    this.image = image;
  }

  @Override
  public BufferedImage asBufferImage() {
    return this.image;
  }

  @Override
  public BufferedImage asBufferImage(final int x, final int y, final int w, final int h) {
    return this.image.getSubimage(x, y, w, h);
  }

  @Override
  public void dispose() {
    // nothing to do
  }

  @Override
  public int getNumberOfBands() {
    return this.image.getColorModel().getColorSpace().getNumComponents();
  }

}
