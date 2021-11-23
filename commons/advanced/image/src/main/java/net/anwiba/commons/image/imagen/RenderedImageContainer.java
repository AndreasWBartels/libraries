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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program. If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */
package net.anwiba.commons.image.imagen;

import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.RenderedImage;
import java.io.IOException;

import org.eclipse.imagen.PlanarImage;
import org.eclipse.imagen.RenderedImageAdapter;

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

public class RenderedImageContainer extends AbstractImageContainer {

  private final RenderedImage renderedImage;

  public RenderedImageContainer(final RenderingHints hints, final RenderedImage renderedImage,IImageMetadataAdjustor metadataAdjustor) {
    this(null, renderedImage, new ObjectList<IImageOperation>(), hints, metadataAdjustor);
  }

  public RenderedImageContainer(final IImageMetadata metadata,
      final RenderedImage renderedImage,
      final IObjectList<IImageOperation> operations,
      final RenderingHints hints,IImageMetadataAdjustor metadataAdjustor) {
    super(hints, metadata, operations, metadataAdjustor);
    this.renderedImage = renderedImage;
  }

  @Override
  protected IImageMetadata read(final ICanceler canceler, final RenderingHints hints) throws CanceledException,
      IOException {
    final ColorModel colorModel = this.renderedImage.getColorModel();
    return new ImagenImageMetadata(this.renderedImage.getWidth(),
        this.renderedImage.getHeight(),
        colorModel.getNumColorComponents(),
        colorModel.getNumComponents(),
        colorModel.getColorSpace().getType(),
        colorModel.getTransferType(),
        colorModel.getTransparency(),
        colorModel instanceof IndexColorModel);
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
    IImageMetadata metadata = read(canceler, hints);
    final PlanarImage planarImage = new PlanarImageOperatorFactory(metadataAdjustor)
        .create((ImagenImageMetadata)metadata, operations, hints)
        .execute(canceler, new RenderedImageAdapter(this.renderedImage));
    return planarImage == null ? null : planarImage.getAsBufferedImage();
  }

  @Override
  protected IImageContainer
      adapt(final RenderingHints hints, final IImageMetadata metadata, final IObjectList<IImageOperation> operations,IImageMetadataAdjustor metadataAdjustor) {
    return new RenderedImageContainer(metadata, this.renderedImage, operations, hints,metadataAdjustor);
  }

}