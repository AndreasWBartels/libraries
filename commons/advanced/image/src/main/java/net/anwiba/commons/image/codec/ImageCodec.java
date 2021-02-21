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
package net.anwiba.commons.image.codec;

import net.anwiba.commons.reference.utilities.IFileExtensions;
import net.anwiba.commons.utilities.string.StringUtilities;

public enum ImageCodec {
  BMP(false, IFileExtensions.BMP) {
    @Override
    public <T, E extends Exception> T accept(final IImageCodecVisitor<T, E> visitor) throws E {
      return visitor.visitBmp();
    }
  },
  GIF(true, IFileExtensions.GIF) {
    @Override
    public <T, E extends Exception> T accept(final IImageCodecVisitor<T, E> visitor) throws E {
      return visitor.visitGif();
    }
  },
  JPEG(false, IFileExtensions.JPG, IFileExtensions.JPEG) {
    @Override
    public <T, E extends Exception> T accept(final IImageCodecVisitor<T, E> visitor) throws E {
      return visitor.visitJpeg();
    }
  },
  PNG(true, IFileExtensions.PNG) {
    @Override
    public <T, E extends Exception> T accept(final IImageCodecVisitor<T, E> visitor) throws E {
      return visitor.visitPng();
    }
  },
  TIFF(false, IFileExtensions.TIF, IFileExtensions.TIFF) {
    @Override
    public <T, E extends Exception> T accept(final IImageCodecVisitor<T, E> visitor) throws E {
      return visitor.visitTiff();
    }
  },
  UNKNOWN(true) {
    @Override
    public <T, E extends Exception> T accept(final IImageCodecVisitor<T, E> visitor) throws E {
      return visitor.visitUnknown();
    }
  };

  private final String[] extensions;
  private final boolean isTransparentSupported;

  private ImageCodec(final boolean isTransparentSupported, final String... extension) {
    this.isTransparentSupported = isTransparentSupported;
    this.extensions = extension;
  }

  public static ImageCodec getByExtension(final String extension) {
    for (final ImageCodec imageCodec : values()) {
      if (StringUtilities.containsIgnoreCase(extension, imageCodec.extensions)) {
        return imageCodec;
      }
    }
    return UNKNOWN;
  }

  public abstract <T, E extends Exception> T accept(IImageCodecVisitor<T, E> visitor) throws E;

  public String getName() {
    return name();
  }

  public boolean isTransparentSupported() {
    return this.isTransparentSupported;
  }

  public String getExtension() {
    if (this.extensions.length == 0) {
      return "*"; //$NON-NLS-1$
    }
    return this.extensions[0];
  }

  public String[] getExtensions() {
    return this.extensions;
  }
}
