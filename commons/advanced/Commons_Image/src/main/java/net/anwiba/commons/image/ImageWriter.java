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
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import net.anwiba.commons.image.codec.IImageCodecVisitor;
import net.anwiba.commons.image.codec.ImageCodec;
import net.anwiba.commons.image.encoder.IEncoder;
import net.anwiba.commons.image.encoder.JpegEncoder;
import net.anwiba.commons.image.encoder.PngEncoder;
import net.anwiba.commons.resource.utilities.FileUtilities;

public class ImageWriter {

  public void write(final BufferedImage image, final File file) throws IOException {
    final IEncoder encoder = getEncoder(FileUtilities.getExtension(file));
    write(image, file, encoder);
  }

  private IEncoder getEncoder(final String extension) {
    final IImageCodecVisitor<IEncoder, RuntimeException> visitor = new ImageCodecVisitor();
    return ImageCodec.getByExtension(extension).accept(visitor);
  }

  public void writeAsPng(final BufferedImage image, final File file) throws IOException {
    write(image, file, new PngEncoder());
  }

  public void writeAsJpeg(final BufferedImage image, final File file) throws IOException {
    write(image, file, new JpegEncoder());
  }

  private void write(final BufferedImage image, final File file, final IEncoder encoder)
      throws IOException,
      FileNotFoundException {
    try (OutputStream out = new ByteArrayOutputStream();) {
      encoder.encode(image, out);
      try (FileOutputStream fos = new FileOutputStream(file);) {
        fos.write(((ByteArrayOutputStream) out).toByteArray());
      }
    }
  }
}
