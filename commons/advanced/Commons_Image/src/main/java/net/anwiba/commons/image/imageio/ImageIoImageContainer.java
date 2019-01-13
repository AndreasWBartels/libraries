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

import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.color.ColorSpace;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import net.anwiba.commons.image.IImageContainer;
import net.anwiba.commons.image.awt.BufferedImageContainerFactory;
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

public class ImageIoImageContainer implements IImageContainer {

  private static net.anwiba.commons.logging.ILogger logger = net.anwiba.commons.logging.Logging
      .getLogger(ImageIoImageContainer.class);
  private final ImageIoImageMetadata metadata;
  private final ImageReader imageReader;
  private final IMutableObjectList<IImageOperation> operations = new ObjectList<>();
  private final RenderingHints hints;
  private final BufferedImageContainerFactory bufferedImageContainerFactory;

  public ImageIoImageContainer(
      final RenderingHints hints,
      final ImageIoImageMetadata metadata,
      final ImageReader imageReader) {
    this(hints, metadata, imageReader, new ObjectList<>());
  }

  private ImageIoImageContainer(
      final RenderingHints hints,
      final ImageIoImageMetadata metadata,
      final ImageReader imageReader,
      final IObjectList<IImageOperation> operations) {
    this.bufferedImageContainerFactory = new BufferedImageContainerFactory(hints);
    this.hints = hints;
    this.metadata = metadata;
    this.imageReader = imageReader;
    this.operations.add(operations);
  }

  @SuppressWarnings("nls")
  @Override
  public void dispose() {
    logger.log(ILevel.DEBUG, "dispose");
    if (this.imageReader.getInput() != null) {
      this.imageReader.dispose();
      try {
        ((ImageInputStream) this.imageReader.getInput()).close();
      } catch (final IOException exception) {
        logger.log(ILevel.DEBUG, exception.getMessage(), exception);
      }
    }
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
  public int getNumberOfComponents() {
    return this.metadata.getNumberOfComponents();
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
    return new ImageIoImageContainer(
        this.hints,
        new ImageIoImageMetadata(
            this.metadata.getIndex(),
            width,
            height,
            this.metadata.getNumberOfComponents(),
            this.metadata.getNumberOfBands(),
            this.metadata.getColorSpaceType(),
            this.metadata.getImageType(),
            this.metadata.getImageMetadata()),
        this.imageReader,
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
    return new ImageIoImageContainer(
        this.hints,
        new ImageIoImageMetadata(
            this.metadata.getIndex(),
            this.metadata.getWidth() * widthFactor,
            this.metadata.getHeight() * heightFactor,
            this.metadata.getNumberOfComponents(),
            this.metadata.getNumberOfBands(),
            this.metadata.getColorSpaceType(),
            this.metadata.getImageType(),
            this.metadata.getImageMetadata()),
        this.imageReader,
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
    return new ImageIoImageContainer(
        this.hints,
        new ImageIoImageMetadata(
            this.metadata.getIndex(),
            this.metadata.getWidth(),
            this.metadata.getHeight(),
            this.metadata.getNumberOfComponents(),
            this.metadata.getNumberOfBands(),
            this.metadata.getColorSpaceType(),
            this.metadata.getImageType(),
            this.metadata.getImageMetadata()),
        this.imageReader,
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
    return new ImageIoImageContainer(
        this.hints,
        new ImageIoImageMetadata(
            this.metadata.getIndex(),
            this.metadata.getWidth(),
            this.metadata.getHeight(),
            1,
            1,
            ColorSpace.TYPE_GRAY,
            this.metadata.getImageType(),
            this.metadata.getImageMetadata()),
        this.imageReader,
        operations);
  }

  @SuppressWarnings("hiding")
  @Override
  public IImageContainer mapBands(final int[] bandMapping) {
    if (bandMapping == null || bandMapping.length == 0) {
      return this;
    }
    if (bandMapping.length < this.metadata.getNumberOfComponents()) {
      throw new IllegalArgumentException();
    }
    final IMutableObjectList<IImageOperation> operations = new ObjectList<>(this.operations);
    final ImageCropOperation operation = operations.stream().instanceOf(ImageCropOperation.class).first().get();
    Optional.of(operation).consume(o -> {
      operations.remove(o);
    });
    operations.add(new ImageMapBandsOperation(bandMapping));
    return new ImageIoImageContainer(
        this.hints,
        new ImageIoImageMetadata(
            this.metadata.getIndex(),
            this.metadata.getWidth(),
            this.metadata.getHeight(),
            this.metadata.getNumberOfComponents(),
            this.metadata.getNumberOfBands(),
            this.metadata.getColorSpaceType(),
            this.metadata.getImageType(),
            this.metadata.getImageMetadata()),
        this.imageReader,
        operations);
  }

  @SuppressWarnings("hiding")
  @Override
  public BufferedImage asBufferImage() {
    try {
      final long size = (long) getWidth() * (long) getHeight();
      if (size >= Integer.MAX_VALUE) {
        logger.log(
            ILevel.WARNING,
            "image dimensions (width=" + getWidth() + " height=" + getHeight() + ") are too large"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        return null;
      }
      final ImageReadParam imageReadParameter = this.imageReader.getDefaultReadParam();
      final IMutableObjectList<IImageOperation> operations = new ObjectList<>();
      ImageScaleOperation imageScaleOperation = null;
      for (final IImageOperation operation : this.operations) {
        if (operation instanceof ImageScaleOperation) {
          imageScaleOperation = (ImageScaleOperation) operation;
          operations.add(operation);
        } else if (operation instanceof ImageCropOperation) {
          final ImageCropOperation o = (ImageCropOperation) operation;
          final Rectangle.Float rectangle = new Rectangle.Float(o.getX(), o.getY(), o.getWidth(), o.getHeight());
          if (imageScaleOperation != null) {
            final AffineTransform transform = AffineTransform
                .getScaleInstance(imageScaleOperation.getWidthFactor(), imageScaleOperation.getHeightFactor());
            final Shape shape = transform.createTransformedShape(rectangle);
            imageReadParameter.setSourceRegion(shape.getBounds());
            imageScaleOperation = null;
          } else {
            imageReadParameter.setSourceRegion(rectangle.getBounds());
          }
        } else if (operation instanceof ImageMapBandsOperation) {
          final ImageMapBandsOperation o = (ImageMapBandsOperation) operation;
          imageReadParameter.setSourceBands(o.getBandMapping());
        } else {
          operations.add(operation);
        }
      }
      final BufferedImage image = this.imageReader.read(this.metadata.getIndex(), imageReadParameter);
      if (operations.isEmpty()) {
        return image;
      }
      IImageContainer bufferedImageContainer = this.bufferedImageContainerFactory.create(image);
      try {
        for (final IImageOperation operation : operations) {
          if (operation instanceof ImageScaleOperation) {
            final ImageScaleOperation o = (ImageScaleOperation) operation;
            bufferedImageContainer = bufferedImageContainer.scale(o.getWidthFactor(), o.getHeightFactor());
          } else if (operation instanceof ImageCropOperation) {
            final ImageCropOperation o = (ImageCropOperation) operation;
            bufferedImageContainer = bufferedImageContainer.crop(o.getX(), o.getX(), o.getWidth(), o.getHeight());
          } else if (operation instanceof ImageMapBandsOperation) {
            final ImageMapBandsOperation o = (ImageMapBandsOperation) operation;
            bufferedImageContainer = bufferedImageContainer.mapBands(o.getBandMapping());
          } else if (operation instanceof ImageInvertOperation) {
            bufferedImageContainer = bufferedImageContainer.invert();
          } else if (operation instanceof ImageToGrayScaleOperation) {
            bufferedImageContainer = bufferedImageContainer.toGrayScale();
          }
        }
        return bufferedImageContainer.asBufferImage();
      } finally {
        bufferedImageContainer.dispose();
      }
    } catch (final IOException exception) {
      logger.log(ILevel.WARNING, exception.getMessage(), exception);
      return new BufferedImage(getWidth(), getHeight(), getColorSpaceType());
    }
  }
}
