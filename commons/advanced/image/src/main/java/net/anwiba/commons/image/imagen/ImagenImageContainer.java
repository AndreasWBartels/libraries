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

import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.io.IOException;

import org.eclipse.imagen.PlanarImage;
import org.eclipse.imagen.RenderedOp;
import org.eclipse.imagen.media.codec.SeekableStream;
import org.eclipse.imagen.operator.StreamDescriptor;

import net.anwiba.commons.image.AbstractImageContainer;
import net.anwiba.commons.image.IImageContainer;
import net.anwiba.commons.image.IImageMetadata;
import net.anwiba.commons.image.IImageMetadataAdjustor;
import net.anwiba.commons.image.operation.IImageOperation;
import net.anwiba.commons.lang.collection.IObjectList;
import net.anwiba.commons.lang.collection.ObjectList;
import net.anwiba.commons.lang.exception.CanceledException;
import net.anwiba.commons.message.IMessageCollector;
import net.anwiba.commons.thread.cancel.ICanceler;

class ImagenImageContainer extends AbstractImageContainer {

  private final ISeekableStreamConnector seekableStreamConnector;

  public ImagenImageContainer(
      final RenderingHints hints,
      final IImageMetadata metadata,
      final ISeekableStreamConnector seekableStreamConnector,IImageMetadataAdjustor metadataAdjustor) {
    this(hints, metadata, new ObjectList<IImageOperation>(), seekableStreamConnector, metadataAdjustor);
  }

  public ImagenImageContainer(
      final RenderingHints hints,
      final IImageMetadata metadata,
      final IObjectList<IImageOperation> operations,
      final ISeekableStreamConnector seekableStreamConnector,IImageMetadataAdjustor metadataAdjustor) {
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
      return new ImagenImageMetadata(
          renderedOp.getWidth(),
          renderedOp.getHeight(),
          colorModel.getNumColorComponents(),
          colorModel.getNumComponents(),
          colorModel.getColorSpace().getType(),
          colorModel.getTransferType(),
          colorModel.getTransparency(),
          colorModel instanceof IndexColorModel);
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
      final IObjectList<IImageOperation> operations,IImageMetadataAdjustor metadataAdjustor) {
    return new ImagenImageContainer(hints, metadata, operations, this.seekableStreamConnector, metadataAdjustor);
  }

  @Override
  protected BufferedImage read(
      final IMessageCollector messageCollector,
      final ICanceler canceler,
      final RenderingHints hints,
      final IObjectList<IImageOperation> imageOperations,
      final IImageMetadataAdjustor metadataAdjustor)
      throws CanceledException,
      IOException {
    IImageMetadata metadata = read(canceler, hints);
    PlanarImage planarImage = null;
    try (final SeekableStream inputStream = this.seekableStreamConnector.connect()) {
      RenderedOp renderedOp = StreamDescriptor.create(inputStream, null, hints);
      planarImage = new PlanarImageOperatorFactory(metadataAdjustor)
          .create((ImagenImageMetadata)metadata, imageOperations, hints)
          .execute(canceler, renderedOp);
      return planarImage == null ? null : planarImage.getAsBufferedImage();
    } finally {
      if (planarImage != null) {
        planarImage.dispose();
      }
    }
  }
}
