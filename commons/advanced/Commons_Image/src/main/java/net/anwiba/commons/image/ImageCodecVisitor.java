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

import net.anwiba.commons.image.codec.IImageCodecVisitor;
import net.anwiba.commons.image.encoder.BmpEncoder;
import net.anwiba.commons.image.encoder.GifEncoder;
import net.anwiba.commons.image.encoder.IEncoder;
import net.anwiba.commons.image.encoder.JpegEncoder;
import net.anwiba.commons.image.encoder.PngEncoder;
import net.anwiba.commons.image.encoder.TiffEncoder;

public final class ImageCodecVisitor implements IImageCodecVisitor<IEncoder, RuntimeException> {
  @Override
  public IEncoder visitJpeg() throws RuntimeException {
    return new JpegEncoder();
  }

  @Override
  public IEncoder visitPng() throws RuntimeException {
    return new PngEncoder();
  }

  @Override
  public IEncoder visitUnknown() throws RuntimeException {
    return new PngEncoder();
  }

  @Override
  public IEncoder visitBmp() throws RuntimeException {
    return new BmpEncoder();
  }

  @Override
  public IEncoder visitTiff() throws RuntimeException {
    return new TiffEncoder();
  }

  @Override
  public IEncoder visitGif() throws RuntimeException {
    return new GifEncoder();
  }
} 
