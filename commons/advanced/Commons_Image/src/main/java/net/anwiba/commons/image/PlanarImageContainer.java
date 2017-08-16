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

import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import javax.media.jai.PlanarImage;

import net.anwiba.commons.logging.ILevel;

public class PlanarImageContainer extends AbstractRenderedImageContainer {

  private static net.anwiba.commons.logging.ILogger logger = net.anwiba.commons.logging.Logging
      .getLogger(PlanarImageContainer.class);
  final PlanarImage image;

  public PlanarImageContainer(final PlanarImage image) {
    super(image);
    this.image = image;
  }

  @Override
  public BufferedImage asBufferImage() {
    try {
      final long size = (long) getWidth() * getHeight();
      if (size >= Integer.MAX_VALUE) {
        logger.log(
            ILevel.WARNING,
            "image dimensions (width=" + getWidth() + " height=" + getHeight() + ") are too large"); //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
        return null;
      }
      return this.image.getAsBufferedImage();
    } catch (final RuntimeException exception) {
      logger.log(ILevel.WARNING, exception.getMessage(), exception);
      return null;
    }
  }

  @Override
  public void dispose() {
    this.image.dispose();
  }

  @Override
  public BufferedImage asBufferImage(final int x, final int y, final int w, final int h) {
    return this.image.getAsBufferedImage(new Rectangle(x, y, w, h), null);
  }
}
