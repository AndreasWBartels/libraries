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
package net.anwiba.commons.image.imageio;

import java.awt.RenderingHints;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.ImageInputStream;

import net.anwiba.commons.image.IImageContainer;
import net.anwiba.commons.lang.collection.IObjectList;
import net.anwiba.commons.lang.io.NoneClosingInputStream;
import net.anwiba.commons.lang.optional.Optional;
import net.anwiba.commons.lang.stream.Streams;
import net.anwiba.commons.logging.ILevel;

public class ImageIoImageContainerFactory {

  private static net.anwiba.commons.logging.ILogger logger = net.anwiba.commons.logging.Logging
      .getLogger(ImageIoImageContainerFactory.class);
  private final RenderingHints hints;

  public ImageIoImageContainerFactory(final RenderingHints hints) {
    this.hints = hints;
  }

  @SuppressWarnings("resource")
  public IImageContainer create(final InputStream inputStream) throws IOException {
    final NoneClosingInputStream noneClosingInputStream = new NoneClosingInputStream(inputStream);
    noneClosingInputStream.mark(Integer.MAX_VALUE);
    ImageInputStream imageInputStream = null;
    try {
      imageInputStream = ImageIO.createImageInputStream(noneClosingInputStream);
      final IObjectList<ImageReader> imageReaders = Streams
          .of(IOException.class, ImageIO.getImageReaders(imageInputStream))
          .asObjectList();
      if (imageReaders.isEmpty()) {
        imageInputStream.close();
        return null;
      }
      final ImageReader imageReader = imageReaders.stream().first().get();
      imageReader.setInput(imageInputStream);
      final int index = imageReader.getMinIndex();
      final int width = imageReader.getWidth(index);
      final int height = imageReader.getHeight(index);
      final ImageTypeSpecifier imageType = Streams
          .of(IOException.class, imageReader.getImageTypes(index))
          .first()
          .get();
      final IIOMetadata imageMetadata = imageReader.getImageMetadata(index);
      final ImageIoImageMetadata metadata = new ImageIoImageMetadata(
          index,
          width,
          height,
          imageType.getColorModel().getNumColorComponents(),
          imageType.getNumBands(),
          imageType.getBufferedImageType(),
          imageType,
          imageMetadata);
      return new ImageIoImageContainer(this.hints, metadata, imageReader);
    } catch (final IOException exception) {
      Optional.of(IOException.class, imageInputStream).consume(s -> s.close());
      logger.log(ILevel.DEBUG, exception.getMessage(), exception);
      noneClosingInputStream.reset();
      return null;
    }
  }

}
