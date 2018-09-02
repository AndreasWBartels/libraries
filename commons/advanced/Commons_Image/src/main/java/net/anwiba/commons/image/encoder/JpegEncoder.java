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
package net.anwiba.commons.image.encoder;

import java.awt.image.RenderedImage;
import java.io.OutputStream;

import com.sun.media.jai.codec.ImageEncoder;
import com.sun.media.jai.codec.JPEGEncodeParam;

public final class JpegEncoder extends AbstractEncoder {

  public JpegEncoder() {
    super("image/jpeg"); //$NON-NLS-1$
  }

  @Override
  protected ImageEncoder getEncoder(final RenderedImage image, final OutputStream out) {
    final JPEGEncodeParam param = new JPEGEncodeParam();
    param.setQuality(1f);
    return com.sun.media.jai.codec.ImageCodec.createImageEncoder("JPEG", out, param); //$NON-NLS-1$
  }
}