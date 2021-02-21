/*
 * #%L
 *
 * %%
 * Copyright (C) 2007 - 2018 Andreas W. Bartels
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
package net.anwiba.commons.image.apache;

import java.awt.RenderingHints;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.imaging.ImageFormat;
import org.apache.commons.imaging.ImageFormats;
import org.apache.commons.imaging.ImageParser;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.Imaging;
import org.apache.commons.imaging.common.bytesource.ByteSourceInputStream;

import net.anwiba.commons.image.IImageContainer;

public class ApacheImageContainerFactory {

  private final RenderingHints hints;

  public ApacheImageContainerFactory(final RenderingHints hints) {
    this.hints = hints;
  }

  public IImageContainer create(final InputStream inputStream) {
    return null;
  }

  public boolean isSupported(final InputStream inputStream) {
    try {
      final ImageFormat format = Imaging.guessFormat(new ByteSourceInputStream(inputStream, "foo"));
      if (!format.equals(ImageFormats.UNKNOWN)) {

        final ImageParser[] imageParsers = ImageParser.getAllImageParsers();

        for (final ImageParser imageParser : imageParsers) {
          if (imageParser.canAcceptType(format)) {
            return true;
          }
        }
      }
      return false;
    } catch (IOException | ImageReadException exception) {
      return false;
    }
  }

}
