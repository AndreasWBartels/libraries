/*
 * #%L
 * anwiba commons
 * %%
 * Copyright (C) 2007 - 2022 Andreas W. Bartels
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
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

import org.apache.commons.imaging.ImageFormat;
import org.apache.commons.imaging.ImageInfo;
import org.apache.commons.imaging.ImageInfo.ColorType;
import org.apache.commons.imaging.ImageParser;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.Imaging;
import org.apache.commons.imaging.common.bytesource.ByteSource;

import net.anwiba.commons.image.AbstractImageContainer;
import net.anwiba.commons.image.IImageContainer;
import net.anwiba.commons.image.IImageMetadata;
import net.anwiba.commons.image.IImageMetadataAdjustor;
import net.anwiba.commons.image.InvalidImageMetadata;
import net.anwiba.commons.image.awt.BufferedImageContainerFactory;
import net.anwiba.commons.image.operation.IImageOperation;
import net.anwiba.commons.lang.collection.IObjectList;
import net.anwiba.commons.lang.collection.ObjectList;
import net.anwiba.commons.lang.exception.CanceledException;
import net.anwiba.commons.lang.optional.IOptional;
import net.anwiba.commons.lang.optional.Optional;
import net.anwiba.commons.message.IMessageCollector;
import net.anwiba.commons.message.Message;
import net.anwiba.commons.thread.cancel.ICanceler;

public class ApacheImageContainer extends AbstractImageContainer implements IImageContainer {

  private final IByteSourceConnector byteSourceConnector;

  public ApacheImageContainer(final RenderingHints hints,
      final ApacheImageMetadata imageMetadata,
      final IByteSourceConnector byteSourceConnector,
      final IImageMetadataAdjustor metadataAdjustor) {
    this(hints, imageMetadata, new ObjectList<IImageOperation>(), byteSourceConnector, metadataAdjustor);
  }

  public ApacheImageContainer(final RenderingHints hints,
      final ApacheImageMetadata imageMetadata,
      final IObjectList<IImageOperation> imageOperations,
      final IByteSourceConnector byteSourceConnector,
      final IImageMetadataAdjustor metadataAdjustor) {
    super(hints, imageMetadata, imageOperations, metadataAdjustor);
    this.byteSourceConnector = byteSourceConnector;
  }

  @Override
  protected IImageContainer
      adapt(final RenderingHints hints,
          final IImageMetadata metadata,
          final IObjectList<IImageOperation> operations,
          final IImageMetadataAdjustor metadataAdjustor) {
    return new ApacheImageContainer(hints,
        (ApacheImageMetadata) metadata,
        operations,
        this.byteSourceConnector,
        metadataAdjustor);
  }

  @Override
  protected BufferedImage
      read(final IMessageCollector messageCollector,
          final ICanceler canceler,
          final RenderingHints hints,
          final IObjectList<IImageOperation> operations,
          final IImageMetadataAdjustor metadataAdjustor)
          throws CanceledException,
          IOException {
    ByteSource byteSource = this.byteSourceConnector.connect();
    IOptional<ImageParser, RuntimeException> imageParser = getImageParser(byteSource);
    if (imageParser.isEmpty()) {
      throw new IOException("Unsupported image format");
    }
    try {
      BufferedImage bufferedImage = imageParser.get().getBufferedImage(byteSource, Map.of());
      final IImageContainer containter = new BufferedImageContainerFactory(hints)
          .create(bufferedImage);
      for (IImageOperation operation : operations) {
        containter.operation(operation);
      }
      return containter.asBufferImage();
    } catch (ImageReadException | IOException | RuntimeException exception) {
      throw new IOException("Unsupported image format");
    }
  }

  @Override
  protected IImageMetadata read(final ICanceler canceler, final RenderingHints hints) throws CanceledException,
      IOException {
    ByteSource byteSource = this.byteSourceConnector.connect();
    IOptional<ImageParser, RuntimeException> imageParser = getImageParser(byteSource);
    if (imageParser.isEmpty()) {
      return new InvalidImageMetadata(
          Message.error("Unsupported image format")
              .throwable(new UnsupportedOperationException("Unsupported image format"))
              .build());
    }
    try {
      final ImageParser parser = imageParser.get();
      ImageInfo imageInfo = parser.getImageInfo(byteSource);
      return new ApacheImageMetadata(
          imageInfo.getWidth(),
          imageInfo.getHeight(),
          guessNumberOfComponents(imageInfo),
          guessNumberOfBands(imageInfo),
          guessColorSpaceType(imageInfo),
          guessDataType(imageInfo),
          guessTransparency(imageInfo),
          false);
    } catch (ImageReadException | IOException | RuntimeException exception) {
      return new InvalidImageMetadata(
          Message.error(exception).build());
    }
  }

  private int guessNumberOfComponents(final ImageInfo imageInfo) {
    final ColorType colorType = imageInfo.getColorType();
    return Set.of(colorType.BW, colorType.GRAYSCALE).contains(colorType)
        ? 1
        : 3;
  }

  private int guessNumberOfBands(final ImageInfo imageInfo) {
    return guessNumberOfComponents(imageInfo) +
        (imageInfo.isTransparent() ? 1 : 0);
  }

  private int guessColorSpaceType(final ImageInfo imageInfo) {
    final ColorType colorType = imageInfo.getColorType();
    return switch (colorType) {
      case BW -> ColorSpace.TYPE_GRAY;
      case CMYK -> ColorSpace.TYPE_CMYK;
      case GRAYSCALE -> ColorSpace.TYPE_GRAY;
      case RGB -> ColorSpace.TYPE_RGB;
      case YCC -> ColorSpace.TYPE_YCbCr;
      case YCCK -> ColorSpace.TYPE_YCbCr;
      case YCbCr -> ColorSpace.TYPE_YCbCr;
      case OTHER -> ColorSpace.TYPE_RGB;
      case UNKNOWN -> ColorSpace.TYPE_RGB;
    };
  }

  private int guessTransparency(final ImageInfo imageInfo) {
    return imageInfo.isTransparent()
        ? 3
        : 0;
  }

  private int guessDataType(final ImageInfo imageInfo) {
    return DataBuffer.TYPE_BYTE;
  }

  private static IOptional<ImageParser, RuntimeException>
      getImageParser(final ByteSource byteSource) throws IOException {
    try {
      return getImageParser(Imaging.guessFormat(byteSource));
    } catch (ImageReadException exception) {
      throw new IOException(exception.getMessage(), exception);
    }
  }

  private static IOptional<ImageParser, RuntimeException> getImageParser(
      final ImageFormat... formats) {
    for (ImageFormat format : formats) {
      final ImageParser[] imageParsers = ImageParser.getAllImageParsers();
      for (final ImageParser imageParser : imageParsers) {
        if (imageParser.canAcceptType(format)) {
          return Optional.of(imageParser);
        }
      }
    }
    return Optional.empty();
  }
}
