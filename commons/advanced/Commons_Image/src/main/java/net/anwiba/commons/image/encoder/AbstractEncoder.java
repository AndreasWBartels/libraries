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

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.io.OutputStream;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;

import com.sun.media.jai.codec.ImageEncoder;

import net.anwiba.commons.lang.stream.Streams;

public abstract class AbstractEncoder implements IEncoder {

  private final String mimetype;

  public AbstractEncoder(final String mimetype) {
    this.mimetype = mimetype;
  }

  @Override
  public void encode(final BufferedImage image, final OutputStream out) throws IOException {
    final ImageWriter writer = getWriter(out);
    if (writer != null) {
      writer.write(image);
      return;
    }
    final ImageEncoder encoder = getEncoder(image, out);
    if (encoder != null) {
      encoder.encode(image);
      return;
    }
    throw new IOException();
  }

  private ImageWriter getWriter(final OutputStream out) throws IOException {
    return Streams
        .of(IOException.class, ImageIO.getImageWritersByMIMEType(this.mimetype))
        .first()
        .consume(w -> w.setOutput(ImageIO.createImageOutputStream(out)))
        .get();
  }

  protected abstract ImageEncoder getEncoder(RenderedImage image, OutputStream out);

}