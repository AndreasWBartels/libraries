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
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

import net.anwiba.commons.resource.reference.IResourceReference;
import net.anwiba.commons.resource.reference.ResourceReferenceFactory;
import net.anwiba.commons.resource.reference.ResourceReferenceHandler;
import net.anwiba.commons.thread.cancel.ICanceler;

public class ImageReaderUtilities {

  private final static ImageReader imageReader = new ImageReader(new ResourceReferenceFactory(), new ResourceReferenceHandler());

  static public BufferedImage scale(final URL resource, final float factor) throws IOException {
    return imageReader.scale(resource, factor);
  }

  public static BufferedImage readBufferedImage(final File file) throws IOException {
    return imageReader.readBufferedImage(file);
  }

  public static BufferedImage readBufferedImage(final URI uri) throws IOException {
    return imageReader.readBufferedImage(uri);
  }

  public static BufferedImage readBufferedImage(final ICanceler canceler, final URL resource)
      throws InterruptedException,
      IOException {
    return imageReader.readBufferedImage(canceler, resource);
  }

  public static BufferedImage readBufferedImage(final ICanceler canceler, final IResourceReference resourceReference)
      throws InterruptedException,
      IOException {
    return imageReader.readBufferedImage(canceler, resourceReference);
  }

  public static BufferedImage readBufferedImage(final InputStream inputStream) throws IOException {
    return imageReader.readBufferedImage(inputStream);
  }

  public static BufferedImage readBufferedImage(final ICanceler canceler, final InputStream inputStream)
      throws InterruptedException,
      IOException {
    return imageReader.readBufferedImage(canceler, inputStream);
  }

  public static IImageContainer read(final ICanceler canceler, final URL resource)
      throws InterruptedException,
      IOException {
    return imageReader.read(canceler, resource);
  }

  public static IImageContainer read(final ICanceler canceler, final IResourceReference resourceReference)
      throws InterruptedException,
      IOException {
    return imageReader.read(canceler, resourceReference);
  }

  public static IImageContainer read(final ICanceler canceler, final InputStream inputStream)
      throws InterruptedException {
    return imageReader.read(canceler, inputStream);
  }

  public static IImageContainer createImageContainer(final File file) throws IOException {
    return imageReader.createImageContainer(file);
  }
}
