/*
 * #%L
 * anwiba commons
 * %%
 * Copyright (C) 2007 - 2019 Andreas W. Bartels
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

import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.stream.ImageInputStream;

import net.anwiba.commons.image.IImageMetaDataReader;
import net.anwiba.commons.image.IImageMetadata;
import net.anwiba.commons.image.ImageMetadata;
import net.anwiba.commons.image.InvalidImageMetadata;
import net.anwiba.commons.lang.collection.IObjectList;
import net.anwiba.commons.lang.optional.Optional;
import net.anwiba.commons.lang.stream.Streams;
import net.anwiba.commons.logging.ILevel;
import net.anwiba.commons.message.MessageBuilder;

public class ImageIoImageMetaDataReader implements IImageMetaDataReader {

  private static net.anwiba.commons.logging.ILogger logger = net.anwiba.commons.logging.Logging
      .getLogger(ImageIoImageMetaDataReader.class);

  @Override
  public IImageMetadata read(final InputStream inputStream) throws IOException {
    @SuppressWarnings("resource")
    ImageInputStream imageInputStream = ImageIO.createImageInputStream(inputStream);
    IObjectList<ImageReader> imageReaders =
        Streams.of(IOException.class, ImageIO.getImageReaders(imageInputStream)).asObjectList();
    if (imageReaders.isEmpty()) {
      imageInputStream.close();
      logger.log(ILevel.WARNING, "missing reader");
      final IOException exception = new IOException("missing reader");
      logger.log(ILevel.DEBUG, "missing reader", exception);
      return new InvalidImageMetadata(
          new MessageBuilder().setError().setText(exception.getMessage()).setThrowable(exception).build());
    }
    ImageReader imageReader = null;
    try {
      imageReader = imageReaders.stream().first().get();
      imageReader.setInput(imageInputStream);
      final int index = imageReader.getMinIndex();
      final int width = imageReader.getWidth(index);
      final int height = imageReader.getHeight(index);
      final ImageTypeSpecifier imageType =
          Streams.of(IOException.class, imageReader.getImageTypes(index)).first().get();
      final ColorModel colorModel = imageType.getColorModel();
      int numColorComponents = colorModel.getNumColorComponents();
      int numBands = colorModel.getNumComponents();
      final ImageMetadata metadata = new ImageMetadata(
          width,
          height,
          numColorComponents,
          numBands,
          colorModel.getColorSpace().getType(),
          colorModel.getTransferType(),
          colorModel.getTransparency(),
          colorModel instanceof IndexColorModel);
      return metadata;
    } finally {
      imageReader.setInput(null);
      imageReader.dispose();
    }
  }

  @SuppressWarnings("resource")
  public boolean isSupported(final InputStream inputStream) {
    ImageInputStream imageInputStream = null;
    try {
      imageInputStream = ImageIO.createImageInputStream(inputStream);
      final IObjectList<ImageReader> imageReaders =
          Streams.of(IOException.class, ImageIO.getImageReaders(imageInputStream)).asObjectList();
      imageInputStream.close();
      return !imageReaders.isEmpty();
    } catch (final IOException exception) {
      Optional.of(IOException.class, imageInputStream).consume(s -> s.close());
      logger.log(ILevel.DEBUG, exception.getMessage(), exception);
      return false;
    }
  }
}
