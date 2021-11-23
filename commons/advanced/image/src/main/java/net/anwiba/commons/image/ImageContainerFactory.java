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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program. If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */
package net.anwiba.commons.image;

import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Map;

import org.eclipse.imagen.media.codec.SeekableStream;

import net.anwiba.commons.image.apache.ApacheImageContainerFactory;
import net.anwiba.commons.image.awt.BufferedImageContainerFactory;
import net.anwiba.commons.image.imageio.IImageInputStreamConnector;
import net.anwiba.commons.image.imageio.ImageIoImageContainerFactory;
import net.anwiba.commons.image.imageio.SeekableImageInputStream;
import net.anwiba.commons.image.imagen.ISeekableStreamConnector;
import net.anwiba.commons.image.imagen.ImagenImageContainerFactory;
import net.anwiba.commons.lang.optional.Optional;
import net.anwiba.commons.reference.IResourceReference;
import net.anwiba.commons.reference.IResourceReferenceHandler;

public class ImageContainerFactory implements IImageContainerFactory {

  private static net.anwiba.commons.logging.ILogger logger = net.anwiba.commons.logging.Logging
      .getLogger(ImageContainerFactory.class);
  final ImagenImageContainerFactory imagenImageContainerFactory;
  final ImageIoImageContainerFactory imageIoImageContainerFactory;
  final BufferedImageContainerFactory bufferedImageContainerFactory;
  private final ApacheImageContainerFactory apacheImageContainerFactory;

  public static ImageContainerFactory of(
      final RenderingHints hints,
      final IResourceReferenceHandler resourceReferenceHandler) {
    RenderingHints renderingHints = Optional.of(hints).getOr(() -> new RenderingHints(Map.of()));
    return new ImageContainerFactory(
        new BufferedImageContainerFactory(renderingHints),
        new ApacheImageContainerFactory(renderingHints, resourceReferenceHandler),
        new ImageIoImageContainerFactory(renderingHints, resourceReferenceHandler),
        new ImagenImageContainerFactory(renderingHints, resourceReferenceHandler));
  }

  public ImageContainerFactory(
      final BufferedImageContainerFactory bufferedImageContainerFactory,
      final ApacheImageContainerFactory apacheImageContainerFactory,
      final ImageIoImageContainerFactory imageIoImageContainerFactory,
      final ImagenImageContainerFactory imagenImageContainerFactory) {
    this.bufferedImageContainerFactory = bufferedImageContainerFactory;
    this.apacheImageContainerFactory = apacheImageContainerFactory;
    this.imageIoImageContainerFactory = imageIoImageContainerFactory;
    this.imagenImageContainerFactory = imagenImageContainerFactory;
  }

  @Override
  public IImageContainer create(final BufferedImage image) {
    return this.bufferedImageContainerFactory.create(image);
  }

  @Override
  public IImageContainer create(final IResourceReference resourceReference) throws IOException {
    ISeekableStreamConnector connector = this.imagenImageContainerFactory
        .createInputStreamConnector(resourceReference);
    try (final SeekableStream seekableStream = connector.connect()) {
      if (this.imagenImageContainerFactory.isSupported(seekableStream)) {
        seekableStream.seek(0);
        return this.imagenImageContainerFactory.create(connector);
      }
      seekableStream.seek(0);
      final IImageInputStreamConnector imageInputStreamConnector = () -> {
        seekableStream.seek(0);
        return new SeekableImageInputStream(seekableStream);
      };
      if (this.imageIoImageContainerFactory.isSupported(imageInputStreamConnector)) {
        return this.imageIoImageContainerFactory.create(resourceReference);
      }
      seekableStream.seek(0);
      if (this.apacheImageContainerFactory.isSupported(seekableStream)) {
        return this.apacheImageContainerFactory.create(resourceReference);
      }
    }
    throw new IOException("Unsupported image format");
  }
}