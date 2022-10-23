/*
 * #%L
 * anwiba commons
 * %%
 * Copyright (C) 2007 - 2021 Andreas W. Bartels
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
package net.anwiba.commons.image.imagen;

import java.awt.Dimension;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.Raster;
import java.awt.image.renderable.ParameterBlock;
import java.io.IOException;
import java.util.List;

import org.eclipse.imagen.Histogram;
import org.eclipse.imagen.JAI;
import org.eclipse.imagen.PlanarImage;
import org.eclipse.imagen.RenderedOp;
import org.eclipse.imagen.media.codec.SeekableStream;
import org.eclipse.imagen.operator.StreamDescriptor;

import net.anwiba.commons.image.AbstractImageContainer;
import net.anwiba.commons.image.IImageContainer;
import net.anwiba.commons.image.IImageMetadata;
import net.anwiba.commons.image.IImageMetadataAdjustor;
import net.anwiba.commons.image.ImageUtilities;
import net.anwiba.commons.image.operation.IImageOperation;
import net.anwiba.commons.lang.collection.IObjectList;
import net.anwiba.commons.lang.collection.ObjectList;
import net.anwiba.commons.lang.exception.CanceledException;
import net.anwiba.commons.lang.functional.IConverter;
import net.anwiba.commons.message.IMessageCollector;
import net.anwiba.commons.thread.cancel.ICanceler;

class ImagenImageContainer extends AbstractImageContainer {

  private final ISeekableStreamConnector seekableStreamConnector;

  public ImagenImageContainer(
      final RenderingHints hints,
      final IImageMetadata metadata,
      final ISeekableStreamConnector seekableStreamConnector,
      final IImageMetadataAdjustor metadataAdjustor) {
    this(hints, metadata, new ObjectList<IImageOperation>(), seekableStreamConnector, metadataAdjustor);
  }

  public ImagenImageContainer(
      final RenderingHints hints,
      final IImageMetadata metadata,
      final IObjectList<IImageOperation> operations,
      final ISeekableStreamConnector seekableStreamConnector,
      final IImageMetadataAdjustor metadataAdjustor) {
    super(hints, metadata, operations, metadataAdjustor);
    this.seekableStreamConnector = seekableStreamConnector;
  }

  @Override
  protected IImageMetadata read(final ICanceler canceler, final RenderingHints hints)
      throws CanceledException,
      IOException {
    RenderedOp renderedOp = null;
    try (SeekableStream inputStream = this.seekableStreamConnector.connect()) {
      renderedOp = StreamDescriptor.create(inputStream, null, hints);
      final ColorModel colorModel = renderedOp.getColorModel();
      boolean isIndexed = colorModel instanceof IndexColorModel;
      return new ImagenImageMetadata(
          renderedOp.getWidth(),
          renderedOp.getHeight(),
          colorModel.getNumColorComponents(),
          colorModel.getNumComponents(),
          colorModel.getColorSpace().getType(),
          colorModel.getTransferType(),
          colorModel.getTransparency(),
          isIndexed,
          isIndexed
              ? ImageUtilities.getColors((IndexColorModel) colorModel)
              : List.of());
    } finally {
      if (renderedOp != null) {
        renderedOp.dispose();
      }
    }
  }

  @Override
  protected IImageContainer adapt(
      final RenderingHints hints,
      final IImageMetadata metadata,
      final IObjectList<IImageOperation> operations,
      final IImageMetadataAdjustor metadataAdjustor) {
    return new ImagenImageContainer(hints, metadata, operations, this.seekableStreamConnector, metadataAdjustor);
  }

  @Override
  protected BufferedImage read(
      final IMessageCollector messageCollector,
      final ICanceler canceler,
      final RenderingHints hints,
      final IObjectList<IImageOperation> operations,
      final IImageMetadataAdjustor metadataAdjustor)
      throws CanceledException,
      IOException {
    return read(messageCollector,
        canceler,
        hints,
        operations,
        metadataAdjustor,
        image -> image.getAsBufferedImage());
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
      final int height)
      throws CanceledException,
      IOException {
    final Raster raster = read(messageCollector,
        canceler,
        hints,
        operations,
        metadataAdjustor,
        image -> ImageUtilities
            .getIntersection(new Dimension(image.getWidth(), image.getHeight()), x, y, width, height)
            .convert(intersection -> {
              RenderedOp cropped = ImagenImageContainerUtilities
                  .crop(hints, image, x, y, Math.max(width, 10), Math.max(height, 10));
              if (cropped == null) {
                return null;
              }
              if (cropped.getWidth() == 0 || cropped.getHeight() == 0) {
                return image.getData(intersection);
              }
              return cropped.getData(intersection);
            })
            .getOr(() -> null));
    if (raster == null) {
      return null;
    }
    return ImageUtilities.getValues(raster);
  }

  private <O> O read(final IMessageCollector messageCollector,
      final ICanceler canceler,
      final RenderingHints hints,
      final IObjectList<IImageOperation> operations,
      final IImageMetadataAdjustor metadataAdjustor,
      final IConverter<PlanarImage, O, IOException> converter)
      throws CanceledException,
      IOException {
    PlanarImage planarImage = null;
    if (operations.isEmpty()) {
      try (final SeekableStream inputStream = this.seekableStreamConnector.connect()) {
        planarImage = StreamDescriptor.create(inputStream, null, hints);
        return planarImage == null ? null : converter.convert(planarImage);
      } finally {
        if (planarImage != null) {
          planarImage.dispose();
        }
      }
    }
    final IImageMetadata metadata = read(canceler, hints);
    try (final SeekableStream inputStream = this.seekableStreamConnector.connect()) {
      final RenderedOp renderedOp = StreamDescriptor.create(inputStream, null, hints);
      planarImage = new PlanarImageOperatorFactory(metadataAdjustor)
          .create((ImagenImageMetadata) metadata, operations, hints)
          .execute(canceler, renderedOp);
      return planarImage == null ? null : converter.convert(planarImage);
    } finally {
      if (planarImage != null) {
        planarImage.dispose();
      }
    }
  }

  @Override
  protected Histogram readHistogram(final IMessageCollector messageCollector,
      final ICanceler canceler,
      final RenderingHints hints,
      final IObjectList<IImageOperation> operations,
      final IImageMetadataAdjustor metadataAdjustor) throws CanceledException, IOException {
    return read(messageCollector,
        canceler,
        hints,
        operations,
        metadataAdjustor,
        image -> {
          ParameterBlock pb = new ParameterBlock();
          pb.addSource(image);
          RenderedOp op = JAI.create("histogram", pb, null);
          Histogram histogram = (Histogram) op.getProperty("histogram");
          return histogram;
        });
  }
  
}
