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
package net.anwiba.commons.image.awt;

import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;

import net.anwiba.commons.image.AbstractImageContainer;
import net.anwiba.commons.image.IImageContainer;
import net.anwiba.commons.image.IImageMetadata;
import net.anwiba.commons.image.IImageOperator;
import net.anwiba.commons.image.operation.IImageOperation;
import net.anwiba.commons.lang.collection.IObjectList;
import net.anwiba.commons.lang.exception.CanceledException;
import net.anwiba.commons.logging.ILevel;
import net.anwiba.commons.thread.cancel.ICanceler;

class BufferedImageContainer extends AbstractImageContainer {

  private static net.anwiba.commons.logging.ILogger logger =
      net.anwiba.commons.logging.Logging.getLogger(BufferedImageContainer.class);
  private final BufferedImage image;

  public BufferedImageContainer(
      final RenderingHints hints,
      final BufferedImageMetadata metadata,
      final BufferedImage image,
      final IObjectList<IImageOperation> operations) {
    super(hints, metadata, operations);
    this.image = image;
  }

  @Override
  public int getWidth() {
    return (int) Math.ceil(getMetadata().getWidth());
  }

  @Override
  public int getHeight() {
    return (int) Math.ceil(getMetadata().getHeight());
  }

  @Override
  public void dispose() {
    // nothing to do
  }

  @Override
  protected IImageMetadata read(final ICanceler canceler, final RenderingHints hints) throws CanceledException,
      IOException {
    return new BufferedImageMetadata(
        this.image.getWidth(),
        this.image.getHeight(),
        this.image.getColorModel().getNumColorComponents(),
        this.image.getColorModel().getNumComponents(),
        this.image.getColorModel().getColorSpace().getType(),
        this.image.getColorModel().getTransferType(),
        this.image.getColorModel().getTransparency());
  }

  @Override
  protected BufferedImage
      read(final ICanceler canceler,
          final RenderingHints hints,
          final IObjectList<IImageOperation> operations)
          throws CanceledException,
          IOException {
    final long size = (long) getWidth() * (long) getHeight();
    if (size >= Integer.MAX_VALUE) {
      logger
          .log(ILevel.WARNING, "image dimensions (width=" + getWidth() + " height=" + getHeight() + ") are too large"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
      return null;
    }
    canceler.check();
    final IImageOperator bufferedImageOperator = new BufferedImageOperatorFactory()
        .create(new BufferedImageMetadataFactory().create(this.image), operations, hints);
    return bufferedImageOperator.execute(this.image);
  }

  @Override
  protected IImageMetadata adjust(final IImageMetadata imageMetadata, final float width, final float height) {
    return new BufferedImageMetadata(
        width,
        height,
        imageMetadata.getNumberOfComponents(),
        imageMetadata.getNumberOfBands(),
        imageMetadata.getColorSpaceType(),
        imageMetadata.getDataType(),
        imageMetadata.getTransparency());
  }

  @Override
  public IImageMetadata adjust(final IImageMetadata imageMetadata,
      final int numberOfComponents,
      final int numberOfBands,
      final int colorSpaceType) {
    return new BufferedImageMetadata(
        imageMetadata.getWidth(),
        imageMetadata.getHeight(),
        numberOfComponents,
        numberOfBands,
        colorSpaceType,
        imageMetadata.getDataType(),
        imageMetadata.getTransparency());
  }

  @Override
  protected IImageMetadata
      adjust(final IImageMetadata imageMetadata,
          final int numberOfComponents,
          final int numberOfBands,
          final int colorSpaceType,
          final int dataType,
          final int transparency) {
    return new BufferedImageMetadata(
        imageMetadata.getWidth(),
        imageMetadata.getHeight(),
        numberOfComponents,
        numberOfBands,
        colorSpaceType,
        dataType,
        transparency);
  }

  @Override
  protected IImageMetadata copy(final IImageMetadata imageMetadata) {
    return new BufferedImageMetadata(
        imageMetadata.getWidth(),
        imageMetadata.getHeight(),
        imageMetadata.getNumberOfComponents(),
        imageMetadata.getNumberOfBands(),
        imageMetadata.getColorSpaceType(),
        imageMetadata.getDataType(),
        imageMetadata.getTransparency());
  }

  @Override
  protected IImageContainer
      adapt(final RenderingHints hints, final IImageMetadata metadata, final IObjectList<IImageOperation> operations) {
    BufferedImageMetadata imageMetadata = (BufferedImageMetadata) metadata;
    return new BufferedImageContainer(
        hints,
        imageMetadata,
        this.image,
        operations);
  }
}