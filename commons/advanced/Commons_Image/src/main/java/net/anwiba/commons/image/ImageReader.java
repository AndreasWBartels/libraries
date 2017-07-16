/*
 * #%L
 * *
 * %%
 * Copyright (C) 2007 - 2016 Andreas W. Bartels (bartels@anwiba.de)
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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

import javax.media.jai.JAI;
import javax.media.jai.RenderedOp;

import com.sun.media.jai.codec.MemoryCacheSeekableStream;

import net.anwiba.commons.lang.exception.UnreachableCodeReachedException;
import net.anwiba.commons.process.cancel.ICanceler;
import net.anwiba.commons.resource.reference.IResourceReference;
import net.anwiba.commons.resource.reference.IResourceReferenceFactory;
import net.anwiba.commons.resource.reference.IResourceReferenceHandler;
import net.anwiba.commons.resource.utilities.IoUtilities;

public final class ImageReader {

  private final IResourceReferenceFactory factory;
  private final IResourceReferenceHandler handler;

  public ImageReader(final IResourceReferenceFactory factory, final IResourceReferenceHandler handler) {
    super();
    this.factory = factory;
    this.handler = handler;
  }

  public BufferedImage scale(final URL resource, final float factor) throws IOException {
    final IResourceReference resourceReference = this.factory.create(resource);
    try (InputStream stream = this.handler.openInputStream(resourceReference)) {
      final RenderedOp scaledRenderedOp = ImageContainerUtilities.scale(createRenderOp(stream), factor);
      try {
        final BufferedImage image = scaledRenderedOp.getAsBufferedImage();
        return image;
      } finally {
        scaledRenderedOp.dispose();
      }
    }
  }

  public BufferedImage readBufferedImage(final File file) throws IOException {
    try {
      final IResourceReference resourceReference = this.factory.create(file);
      return readBufferedImage(ICanceler.DummyCancler, resourceReference);
    } catch (final InterruptedException exception) {
      throw new UnreachableCodeReachedException(exception);
    }
  }

  public BufferedImage readBufferedImage(final URI uri) throws IOException {
    try {
      final IResourceReference resourceReference = this.factory.create(uri);
      return readBufferedImage(ICanceler.DummyCancler, resourceReference);
    } catch (final InterruptedException exception) {
      throw new UnreachableCodeReachedException(exception);
    }
  }

  public BufferedImage readBufferedImage(final ICanceler canceler, final URL resource)
      throws InterruptedException,
      IOException {
    final IResourceReference resourceReference = this.factory.create(resource);
    return readBufferedImage(canceler, resourceReference);
  }

  public BufferedImage readBufferedImage(final ICanceler canceler, final IResourceReference resourceReference)
      throws InterruptedException,
      IOException {
    canceler.check();
    try (InputStream stream = this.handler.openInputStream(resourceReference)) {
      return readBufferedImage(stream);
    }
  }

  public BufferedImage readBufferedImage(final ICanceler canceler, final InputStream inputStream)
      throws InterruptedException,
      IOException {
    canceler.check();
    return readBufferedImage(inputStream);
  }

  public BufferedImage readBufferedImage(final InputStream inputStream) throws IOException {
    try {
      final RenderedOp renderedOp = createRenderOp(inputStream);
      try {
        return renderedOp.getAsBufferedImage();
      } finally {
        renderedOp.dispose();
      }
    } catch (final RuntimeException exception) {
      throw new IOException(exception);
    }
  }

  public IImageContainer read(final ICanceler canceler, final URL resource) throws InterruptedException, IOException {
    return read(canceler, this.factory.create(resource));
  }

  public IImageContainer read(final ICanceler canceler, final IResourceReference resourceReference)
      throws InterruptedException,
      IOException {
    canceler.check();
    if (this.handler.isFileSystemResource(resourceReference)) {
      return read(canceler, this.handler.openInputStream(resourceReference));
    }
    try (final InputStream stream = this.handler.openInputStream(resourceReference)) {
      return read(canceler, IoUtilities.copy(stream));
    }
  }

  public IImageContainer read(final ICanceler canceler, final InputStream inputStream) throws InterruptedException {
    canceler.check();
    return new PlanarImageContainer(createRenderOp(inputStream));
  }

  @SuppressWarnings("resource")
  private RenderedOp createRenderOp(final InputStream inputStream) {
    final MemoryCacheSeekableStream memoryCacheSeekableStream = new MemoryCacheSeekableStream(inputStream);
    return JAI.create("Stream", memoryCacheSeekableStream); //$NON-NLS-1$

  }

  @SuppressWarnings("resource")
  public IImageContainer createImageContainer(final File file) throws IOException {
    return new PlanarImageContainer(createRenderOp(new FileInputStream(file)));
  }
}
