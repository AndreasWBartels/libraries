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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program. If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */
package net.anwiba.commons.image.imagen.encoder;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.MemoryCacheImageOutputStream;

import org.eclipse.imagen.media.codec.ImageEncoder;

import net.anwiba.commons.image.ImageUtilities;
import net.anwiba.commons.image.encoder.IEncoder;
import net.anwiba.commons.lang.stream.Streams;

public abstract class AbstractEncoder implements IEncoder {

  private final String mimetype;

  public AbstractEncoder(final String mimetype) {
    this.mimetype = mimetype;
  }

  @Override
  public void encode(final BufferedImage image, final OutputStream out) throws IOException {
    if (Objects.equals(this.mimetype, "image/gif")) {
      ImageIO.write(image, "GIF", new MemoryCacheImageOutputStream(out));
      return;
    }
//    if (Objects.equals(this.mimetype, "image/bmp")) {
//      ImageIO.write(image, "BMP", new MemoryCacheImageOutputStream(out));
//      return;
//    }
    final ImageEncoder encoder = getEncoder(image, out);
    if (encoder != null) {
      encoder.encode(image);
      return;
    }
    final ImageWriter writer = getWriter(out);
    try {
      if (writer != null) {
        if (Objects.equals(writer.getClass().getName(), "com.sun.imageio.plugins.jpeg.JPEGImageWriter")) {
          JPEGImageWriteParam jpegParams = new JPEGImageWriteParam(null); // locale is irrelevant here
          jpegParams.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
          jpegParams.setCompressionQuality(.9f);
          writer.setOutput(new MemoryCacheImageOutputStream(out));
          BufferedImage opaqueBufferedImage = ImageUtilities.createOpaqueImage(image);
          writer.write(null, new IIOImage(opaqueBufferedImage, null, null), jpegParams);
          return;
        }
        writer.write(image);
        return;
      }
    } catch (IOException e) {
      writer.abort();
      throw e;
    } finally {
//      writer.setOutput(null);
      writer.dispose();
      out.flush();
    }
    throw new IOException();
  }

  private ImageWriter getWriter(final OutputStream out) throws IOException {
    return Streams
        .of(IOException.class, ImageIO.getImageWritersByMIMEType(this.mimetype))
        .first(w -> isAccaptable(w))
        .consume(w -> w.setOutput(new MemoryCacheImageOutputStream(out)))
        .get();
  }

  protected boolean isAccaptable(final ImageWriter writer) {
    return writer.getClass().getName().startsWith("com.sun.imageio.plugins");
  }

  protected abstract ImageEncoder getEncoder(RenderedImage image, OutputStream out);

}