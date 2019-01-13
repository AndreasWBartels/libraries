
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

import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;

import net.anwiba.commons.image.IImageContainer;
import net.anwiba.commons.image.operation.IImageOperation;
import net.anwiba.commons.image.operation.ImageCropOperation;
import net.anwiba.commons.image.operation.ImageInvertOperation;
import net.anwiba.commons.image.operation.ImageMapBandsOperation;
import net.anwiba.commons.image.operation.ImageScaleOperation;
import net.anwiba.commons.image.operation.ImageToGrayScaleOperation;
import net.anwiba.commons.lang.collection.IMutableObjectList;
import net.anwiba.commons.lang.collection.IObjectList;
import net.anwiba.commons.lang.collection.ObjectList;
import net.anwiba.commons.lang.optional.Optional;
import net.anwiba.commons.logging.ILevel;

public class BufferedImageContainer implements IImageContainer {

  private static net.anwiba.commons.logging.ILogger logger = net.anwiba.commons.logging.Logging
      .getLogger(BufferedImageContainer.class);
  private final BufferedImage image;
  private final IMutableObjectList<IImageOperation> operations = new ObjectList<>();
  private final BufferedImageMetadata metadata;
  private final RenderingHints hints;

  public BufferedImageContainer(
      final RenderingHints hints,
      final BufferedImageMetadata metadata,
      final BufferedImage image,
      final IObjectList<IImageOperation> operations) {
    this.hints = hints;
    this.image = image;
    this.metadata = metadata;
    this.operations.add(operations);
  }

  @Override
  public void dispose() {
    // nothing to do
  }

  @Override
  public int getWidth() {
    return (int) Math.ceil(this.metadata.getWidth());
  }

  @Override
  public int getHeight() {
    return (int) Math.ceil(this.metadata.getHeight());
  }

  @Override
  public int getNumberOfBands() {
    return this.metadata.getNumberOfBands();
  }

  @Override
  public int getColorSpaceType() {
    return this.metadata.getColorSpaceType();
  }

  @Override
  public IImageContainer crop(final Rectangle rectangle) {
    return crop(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
  }

  @SuppressWarnings("hiding")
  @Override
  public IImageContainer crop(final float x, final float y, final float width, final float height) {
    final IMutableObjectList<IImageOperation> operations = new ObjectList<>(this.operations);
    final ImageCropOperation operation = operations.stream().instanceOf(ImageCropOperation.class).first().get();
    Optional.of(operation).consume(o -> {
      operations.remove(o);
      operations.add(new ImageCropOperation(o.getX() + x, o.getY() + y, width, height));
    }).or(() -> operations.add(new ImageCropOperation(x, y, width, height)));
    return new BufferedImageContainer(
        this.hints,
        new BufferedImageMetadata(
            this.metadata.getIndex(),
            width,
            height,
            this.metadata.getNumberOfComponents(),
            this.metadata.getNumberOfBands(),
            this.metadata.getColorSpaceType()),
        this.image,
        operations);
  }

  @Override
  public IImageContainer fit(final int width, final int height) {
    final float withFactor = factor(width, getWidth());
    final float heightfactor = factor(height, getHeight());
    return scale(Math.max(withFactor, heightfactor));
  }

  private float factor(final int numerator, final int denominator) {
    return (float) numerator / (float) denominator;
  }

  @SuppressWarnings("hiding")
  @Override
  public IImageContainer scale(final float widthFactor, final float heightFactor) {
    final IMutableObjectList<IImageOperation> operations = new ObjectList<>(this.operations);
    final ImageScaleOperation operation = operations.stream().instanceOf(ImageScaleOperation.class).first().get();
    Optional.of(operation).consume(o -> {
      operations.remove(o);
      operations.add(new ImageScaleOperation(o.getWidthFactor() * widthFactor, o.getHeightFactor() * heightFactor));
    }).or(() -> operations.add(new ImageScaleOperation(widthFactor, heightFactor)));
    return new BufferedImageContainer(
        this.hints,
        new BufferedImageMetadata(
            this.metadata.getIndex(),
            this.metadata.getWidth() * widthFactor,
            this.metadata.getHeight() * heightFactor,
            this.metadata.getNumberOfComponents(),
            this.metadata.getNumberOfBands(),
            this.metadata.getColorSpaceType()),
        this.image,
        operations);
  }

  @Override
  public IImageContainer scale(final float factor) {
    return scale(factor, factor);
  }

  @SuppressWarnings("hiding")
  @Override
  public IImageContainer invert() {
    final IMutableObjectList<IImageOperation> operations = new ObjectList<>(this.operations);
    final ImageInvertOperation operation = operations.stream().instanceOf(ImageInvertOperation.class).first().get();
    Optional.of(operation).consume(o -> {
      operations.remove(o);
    }).or(() -> operations.add(new ImageInvertOperation()));
    return new BufferedImageContainer(
        this.hints,
        new BufferedImageMetadata(
            this.metadata.getIndex(),
            this.metadata.getWidth(),
            this.metadata.getHeight(),
            this.metadata.getNumberOfComponents(),
            this.metadata.getNumberOfBands(),
            this.metadata.getColorSpaceType()),
        this.image,
        operations);
  }

  @SuppressWarnings("hiding")
  @Override
  public IImageContainer toGrayScale() {
    final IMutableObjectList<IImageOperation> operations = new ObjectList<>(this.operations);
    final ImageToGrayScaleOperation operation = operations
        .stream()
        .instanceOf(ImageToGrayScaleOperation.class)
        .first()
        .get();
    Optional.of(operation).or(() -> operations.add(new ImageToGrayScaleOperation()));
    return new BufferedImageContainer(
        this.hints,
        new BufferedImageMetadata(
            this.metadata.getIndex(),
            this.metadata.getWidth(),
            this.metadata.getHeight(),
            1,
            1,
            ColorSpace.TYPE_GRAY),
        this.image,
        operations);
  }

  @SuppressWarnings("hiding")
  @Override
  public IImageContainer mapBands(final int[] bandMapping) {
    if (bandMapping == null || bandMapping.length == 0) {
      return this;
    }
    if (bandMapping.length < this.metadata.getNumberOfBands()) {
      throw new IllegalArgumentException();
    }
    final IMutableObjectList<IImageOperation> operations = new ObjectList<>(this.operations);
    final ImageCropOperation operation = operations.stream().instanceOf(ImageCropOperation.class).first().get();
    Optional.of(operation).consume(o -> {
      operations.remove(o);
    });
    operations.add(new ImageMapBandsOperation(bandMapping));
    return new BufferedImageContainer(
        this.hints,
        new BufferedImageMetadata(
            this.metadata.getIndex(),
            this.metadata.getWidth(),
            this.metadata.getHeight(),
            this.metadata.getNumberOfComponents(),
            this.metadata.getNumberOfBands(),
            this.metadata.getColorSpaceType()),
        this.image,
        operations);
  }

  @Override
  public BufferedImage asBufferImage() {
    final long size = (long) getWidth() * (long) getHeight();
    if (size >= Integer.MAX_VALUE) {
      logger
          .log(ILevel.WARNING, "image dimensions (width=" + getWidth() + " height=" + getHeight() + ") are too large"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
      return null;
    }
    final IBufferedImageOperator bufferedImageOperator = new BufferedImageOperatorFactory()
        .create(new BufferedImageMetadataFactory().create(this.image), this.operations, this.hints);
    return bufferedImageOperator.execute(this.image);
  }

  @Override
  public int getNumberOfComponents() {
    return this.metadata.getNumberOfComponents();
  }
}