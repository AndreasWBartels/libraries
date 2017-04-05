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

import com.sun.media.jai.codec.BMPEncodeParam;
import com.sun.media.jai.codec.ImageEncoder;

import java.awt.image.RenderedImage;
import java.io.OutputStream;

public final class BmpEncoder extends AbstractEncoder {

  @Override
  protected ImageEncoder getEncoder(final RenderedImage image, final OutputStream out) {
    final BMPEncodeParam param = new BMPEncodeParam();
    return com.sun.media.jai.codec.ImageCodec.createImageEncoder("BMP", out, param); //$NON-NLS-1$
  }
}