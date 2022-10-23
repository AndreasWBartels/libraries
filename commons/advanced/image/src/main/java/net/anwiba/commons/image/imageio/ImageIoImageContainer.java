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
package net.anwiba.commons.image.imageio;

import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.Raster;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.stream.ImageInputStream;

import net.anwiba.commons.image.AbstractImageContainer;
import net.anwiba.commons.image.IImageContainer;
import net.anwiba.commons.image.IImageContainerSettings;
import net.anwiba.commons.image.IImageMetadata;
import net.anwiba.commons.image.IImageMetadataAdjustor;
import net.anwiba.commons.image.ImageUtilities;
import net.anwiba.commons.image.imagen.ImagenImageMetadataAdjustor;
import net.anwiba.commons.image.imagen.RenderedImageContainer;
import net.anwiba.commons.image.operation.IImageOperation;
import net.anwiba.commons.image.operation.ImageCropOperation;
import net.anwiba.commons.image.operation.ImageMapBandsOperation;
import net.anwiba.commons.lang.collection.IMutableObjectList;
import net.anwiba.commons.lang.collection.IObjectList;
import net.anwiba.commons.lang.collection.ObjectList;
import net.anwiba.commons.lang.exception.CanceledException;
import net.anwiba.commons.lang.optional.IOptional;
import net.anwiba.commons.lang.stream.Streams;
import net.anwiba.commons.logging.ILevel;
import net.anwiba.commons.message.IMessageCollector;
import net.anwiba.commons.message.MessageType;
import net.anwiba.commons.thread.cancel.ICanceler;
import net.anwiba.commons.thread.cancel.ICancelerListener;

class ImageIoImageContainer extends AbstractImageContainer {

  private static net.anwiba.commons.logging.ILogger logger = net.anwiba.commons.logging.Logging
      .getLogger(ImageIoImageContainer.class);
  private final IImageInputStreamConnector imageInputStreamConnector;

  public ImageIoImageContainer(
      final RenderingHints hints,
      final IImageMetadata metadata,
      final IImageInputStreamConnector imageInputStream,
      final IImageMetadataAdjustor metadataAdjustor) {
    this(hints, metadata, imageInputStream, new ObjectList<>(), metadataAdjustor);
  }

  private ImageIoImageContainer(
      final RenderingHints hints,
      final IImageMetadata metadata,
      final IImageInputStreamConnector imageInputStream,
      final IObjectList<IImageOperation> operations,
      final IImageMetadataAdjustor metadataAdjustor) {
    super(hints, metadata, operations, metadataAdjustor);
    this.imageInputStreamConnector = imageInputStream;
  }

  @Override
  protected IImageContainer
      adapt(final RenderingHints hints,
          final IImageMetadata metadata,
          final IObjectList<IImageOperation> operations,
          final IImageMetadataAdjustor metadataAdjustor) {
    return new ImageIoImageContainer(hints, metadata, this.imageInputStreamConnector, operations, metadataAdjustor);
  }

  @Override
  protected ImageIoImageMetadata read(final ICanceler canceler, final RenderingHints hints) throws CanceledException,
      IOException {
    ImageReader imageReader = null;
    try (ImageInputStream inputStream = this.imageInputStreamConnector.connect()) {
      imageReader = getImageReader(inputStream, hints);
      return read(canceler, imageReader);
    } finally {
      if (imageReader == null) {
        return null;
      }
      imageReader.setInput(null);
      imageReader.dispose();
    }
  }

  private ImageReader getImageReader(final ImageInputStream inputStream, final RenderingHints hints)
      throws IOException {
    IImageIoImageContainerSettings imageIoSettings = IImageIoImageContainerSettings.getSettings(hints);
    IImageContainerSettings settings = IImageContainerSettings.getSettings(hints);
    final IObjectList<ImageReader> imageReaders = Streams
        .of(IOException.class, ImageIO.getImageReaders(inputStream))
        .asObjectList();
    if (imageReaders.isEmpty()) {
      logger.log(ILevel.WARNING, "missing reader", new IOException("missing reader"));
      settings
          .getImageContainerListener()
          .eventOccurred("missing imageio reader", null, MessageType.WARNING);
      return null;
    }
    final ImageReader imageReader = imageIoSettings.getImageReader(imageReaders);
    imageReader.setInput(inputStream);
    return imageReader;
  }

  private ImageIoImageMetadata
      read(final ICanceler canceler, final ImageReader imageReader)
          throws IOException {
    final int index = imageReader.getMinIndex();
    final int width = imageReader.getWidth(index);
    final int height = imageReader.getHeight(index);
    final ImageTypeSpecifier imageType =
        Streams.of(IOException.class, imageReader.getImageTypes(index)).first().get();
    ColorModel colorModel = imageType.getColorModel();
    int numColorComponents = colorModel.getNumColorComponents();
    int numBands = colorModel.getNumComponents();
    boolean isIndexed = colorModel instanceof IndexColorModel;
    return new ImageIoImageMetadata(
        index,
        width,
        height,
        numColorComponents,
        numBands,
        colorModel.getColorSpace().getType(),
        colorModel.getTransferType(),
        colorModel.getTransparency(),
        imageType,
        isIndexed,
        isIndexed
            ? ImageUtilities.getColors((IndexColorModel) colorModel)
            : List.of());
  }

  @Override
  protected BufferedImage
      read(final IMessageCollector messageCollector,
          final ICanceler canceler,
          final RenderingHints hints,
          final IObjectList<IImageOperation> imageOperations,
          final IImageMetadataAdjustor metadataAdjustor)
          throws CanceledException,
          IOException {
    final IImageContainerSettings settings = IImageContainerSettings.getSettings(hints);
    try (ImageInputStream inputStream = this.imageInputStreamConnector.connect()) {
      final long size = (long) getWidth() * (long) getHeight();
      if (size >= Integer.MAX_VALUE) {
        logger
            .log(
                ILevel.WARNING,
                "image dimensions (width=" //$NON-NLS-1$
                    + getWidth()
                    + " height=" //$NON-NLS-1$
                    + getHeight()
                    + ") are too large"); //$NON-NLS-1$
        return new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
      }
      inputStream.seek(0);
      canceler.check();
      final ImageReader imageReader = getImageReader(inputStream, hints);
      final ICancelerListener abortListenerlistener = () -> imageReader.abort();
      try {
        canceler.addCancelerListener(abortListenerlistener);
        canceler.check();
        imageReader
            .addIIOReadWarningListener(
                (source, warning) -> settings
                    .getImageContainerListener()
                    .eventOccurred(warning, null, MessageType.WARNING));
        final ImageIoImageMetadata metadata = read(canceler, imageReader);
        if (metadata == null) {
          return new BufferedImage(getWidth(), getHeight(), getColorSpaceType());
        }
        final IOptional<ImageCropOperation, RuntimeException> cropOperation =
            ImageCropOperation.aggregate(imageOperations);
        final ImageReadParam imageReadParameter = imageReader.getDefaultReadParam();
        final IMutableObjectList<IImageOperation> operations = new ObjectList<>();
        for (final IImageOperation operation : imageOperations) {
          if (operation instanceof ImageCropOperation) {
          } else if (operation instanceof ImageMapBandsOperation) {
            final ImageMapBandsOperation o = (ImageMapBandsOperation) operation;
            if (o.getMappingSize() == metadata.getNumberOfBands()
                && !o.isIdentity()
                && !o.hasDuplicate()) {
              imageReadParameter.setSourceBands(o.getBandMapping());
            } else if (o.getMappingSize() == metadata.getNumberOfBands()
                && o.isIdentity()) {
            } else {
              operations.add(operation);
            }
          } else {
            operations.add(operation);
          }
        }
        inputStream.seek(0);
        if (cropOperation.isAccepted()) {
          imageReadParameter.setSourceRegion(cropOperation.get().getBounds());
        }
        canceler.check();
        final BufferedImage image = imageReader.read(metadata.getIndex(), imageReadParameter);
        if (operations.isEmpty()) {
          return image;
        }
        canceler.check();
        IImageContainer bufferedImageContainer =
            new RenderedImageContainer(hints, image, new ImagenImageMetadataAdjustor());
        for (final IImageOperation operation : operations) {
          bufferedImageContainer = bufferedImageContainer.operation(operation);
        }
        return bufferedImageContainer.asBufferImage(canceler);
      } finally {
        canceler.removeCancelerListener(abortListenerlistener);
        imageReader.setInput(null);
        imageReader.dispose();
      }
    } catch (final RuntimeException | IOException exception) {
      logger.log(ILevel.WARNING, exception.getMessage(), exception);
      settings
          .getImageContainerListener()
          .eventOccurred(exception.getMessage(), exception, MessageType.ERROR);
      return null;
    }
  }

  @Override
  protected Number[][] read(final IMessageCollector messageCollector,
      final ICanceler canceler,
      final RenderingHints hints,
      final IObjectList<IImageOperation> operations,
      final IImageMetadataAdjustor metadataAdjustor,
      final int x,
      final int y,
      final int width,
      final int height) throws CanceledException,
      IOException {
    if (operations.isEmpty()) {
      try (ImageInputStream inputStream = this.imageInputStreamConnector.connect()) {
        final long size = (long) width * (long) height;
        if (size >= Integer.MAX_VALUE) {
          logger
              .log(
                  ILevel.WARNING,
                  "image dimensions (width=" //$NON-NLS-1$
                      + width
                      + " height=" //$NON-NLS-1$
                      + height
                      + ") are too large"); //$NON-NLS-1$
          return null;
        }
        inputStream.seek(0);
        canceler.check();
        final ImageReader imageReader = getImageReader(inputStream, hints);
        if (imageReader == null) {
          return null;
        }
        if (imageReader.canReadRaster()) {
          final ImageIoImageMetadata metadata = read(canceler, imageReader);
          if (metadata == null) {
            return null;
          }
          final ImageReadParam imageReadParameter = imageReader.getDefaultReadParam();
          return ImageUtilities.getIntersection(metadata, x, y, width, height)
              .convert(intersection -> {
                imageReadParameter.setSourceRegion(intersection);
                Raster raster = imageReader.readRaster(metadata.getIndex(), imageReadParameter);
                return ImageUtilities.getValues(raster);
              })
              .getOr(() -> null);
        }
      }
    }
    return super.read(messageCollector, canceler, hints, operations, metadataAdjustor, x, y, width, height);
  }
}
