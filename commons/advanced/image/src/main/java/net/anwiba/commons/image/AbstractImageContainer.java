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
package net.anwiba.commons.image;

import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;

import net.anwiba.commons.image.operation.IImageOperation;
import net.anwiba.commons.image.operation.ImageCropOperation;
import net.anwiba.commons.image.operation.ImageInvertOperation;
import net.anwiba.commons.image.operation.ImageMapBandsOperation;
import net.anwiba.commons.image.operation.ImageOpacityOperation;
import net.anwiba.commons.image.operation.ImageScaleOperation;
import net.anwiba.commons.image.operation.ImageToGrayScaleOperation;
import net.anwiba.commons.lang.collection.IMutableObjectList;
import net.anwiba.commons.lang.collection.IObjectList;
import net.anwiba.commons.lang.collection.ObjectList;
import net.anwiba.commons.lang.collection.ObjectListBuilder;
import net.anwiba.commons.lang.exception.CanceledException;
import net.anwiba.commons.logging.ILevel;
import net.anwiba.commons.message.MessageBuilder;
import net.anwiba.commons.thread.cancel.ICanceler;

public abstract class AbstractImageContainer implements IImageContainer {

  final IImageMetadataAdjustor metadataAdjustor = new IImageMetadataAdjustor() {

    @Override
    public IImageMetadata adjust(final IImageMetadata metadata, final float width, final float height) {
      if (metadata == null || metadata instanceof InvalidImageMetadata) {
        return metadata;
      }
      if (metadata.getWidth() <= 0) {
        return new InvalidImageMetadata(new MessageBuilder().setText("width <= 0").setError().build());
      }
      if (metadata.getHeight() <= 0) {
        return new InvalidImageMetadata(new MessageBuilder().setText("height <= 0").setError().build());
      }
      return AbstractImageContainer.this.adjust(metadata, width, height);
    }

    @Override
    public IImageMetadata
        adjust(final IImageMetadata metadata,
            final int numberOfComponents,
            final int numberOfBands,
            final int colorSpaceType) {
      if (metadata == null || metadata instanceof InvalidImageMetadata) {
        return metadata;
      }
      if (metadata.getNumberOfBands() <= 0) {
        return new InvalidImageMetadata(new MessageBuilder().setText("number of bands <= 0").setError().build());
      }
      if (metadata.getNumberOfComponents() <= 0) {
        return new InvalidImageMetadata(new MessageBuilder().setText("number of components <= 0").setError().build());
      }
      if (metadata.getColorSpaceType() < 0 || metadata.getColorSpaceType() > 25) {
        return new InvalidImageMetadata(new MessageBuilder().setText("number of components <= 0").setError().build());
      }

      return AbstractImageContainer.this.adjust(metadata, numberOfComponents, numberOfBands, colorSpaceType);
    }

    @Override
    public IImageMetadata copy(final IImageMetadata metadata) {
      if (metadata == null || metadata instanceof InvalidImageMetadata) {
        return metadata;
      }
      return AbstractImageContainer.this.copy(metadata);
    }
  };

  private static net.anwiba.commons.logging.ILogger logger = net.anwiba.commons.logging.Logging
      .getLogger(AbstractImageContainer.class);
  private final IMutableObjectList<IImageOperation> operations = new ObjectList<>();
  private IImageMetadata metadata;
  private final RenderingHints hints;

  public AbstractImageContainer(final RenderingHints hints,
      final IImageMetadata metadata,
      final IObjectList<IImageOperation> operations) {
    this.hints = hints;
    this.metadata = metadata;
    this.operations.add(operations);
  }

  protected abstract IImageMetadata
      adjust(IImageMetadata metadata2, int numberOfComponents, int numberOfBands, int colorSpaceType);

  @Override
  public final BufferedImage asBufferImage(final ICanceler canceler) throws CanceledException {
    try {
      return read(canceler, this.hints, this.operations);
    } catch (IOException exception) {
      logger.log(ILevel.DEBUG, exception.getMessage(), exception);
      return null;
    }
  }

  @Override
  public void dispose() {
    // nothing to do
  }

  @Override
  public final IImageContainer crop(final Rectangle rectangle) {
    return crop(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
  }

  @Override
  public final IImageContainer crop(final float x, final float y, final float width, final float height) {
    return create(this.metadata, new ImageCropOperation(x, y, width, height));
  }

  @Override
  public final IImageContainer fitTo(final int width, final int height) {
    final float withFactor = factor(width, getWidth());
    final float heightfactor = factor(height, getHeight());
    return scale(Math.max(withFactor, heightfactor));
  }

  @Override
  public final IImageContainer scaleTo(final int width, final int height) {
    final float withFactor = factor(width, getWidth());
    final float heightfactor = factor(height, getHeight());
    return scale(withFactor, heightfactor);
  }

  private float factor(final int numerator, final int denominator) {
    return (float) numerator / (float) denominator;
  }

  @Override
  public final IImageContainer scale(final float factor) {
    return scale(factor, factor);
  }

  @Override
  public final IImageContainer scale(final float widthFactor, final float heightFactor) {
    return create(this.metadata, new ImageScaleOperation(widthFactor, heightFactor));
  }

  @Override
  public final IImageContainer opacity(final float factor) {
    return create(this.metadata, new ImageOpacityOperation(factor));
  }

  @Override
  public final IImageContainer mapBands(final int[] bandMapping) {
    return create(this.metadata, new ImageMapBandsOperation(bandMapping));
  }

  @Override
  public final IImageContainer invert() {
    return create(this.metadata, new ImageInvertOperation());
  }

  @Override
  public final IImageContainer toGrayScale() {
    return create(this.metadata, new ImageToGrayScaleOperation());
  }

  @Override
  public IImageContainer operation(final IImageOperation operation) {
    return create(this.metadata, operation);
  }

  @Override
  public final IImageMetadata getMetadata() {
    try {
      if (this.metadata == null) {
        IImageMetadata metadata = read(ICanceler.DummyCanceler, this.hints);
        if (metadata == null) {
          return new InvalidImageMetadata(
              new MessageBuilder()
                  .setText("Couldn't read image metadata")
                  .setError()
                  .build());
        }
        for (IImageOperation operation : this.operations) {
          metadata = operation.adjust(metadata, this.metadataAdjustor);
        }
        this.metadata = metadata;
      }
      return this.metadata;
    } catch (IOException | CanceledException exception) {
      return new InvalidImageMetadata(
          new MessageBuilder()
              .setText(exception.getMessage())
              .setThrowable(exception)
              .setError()
              .build());
    }
  }

  @Override
  public final int getColorSpaceType() {
    return getMetadata().getColorSpaceType();
  }

  @Override
  public int getWidth() {
    return Math.round(getMetadata().getWidth());
  }

  @Override
  public int getHeight() {
    return Math.round(getMetadata().getHeight());
  }

  @Override
  public final int getNumberOfComponents() {
    return getMetadata().getNumberOfComponents();
  }

  @Override
  public final int getNumberOfBands() {
    return getMetadata().getNumberOfBands();
  }

  private IImageContainer create(final IImageMetadata metadata, final IImageOperation operation) {
    return adapt(this.hints,
        metadata == null ? null : operation.adjust(metadata, this.metadataAdjustor),
        addTo(this.operations, operation));
  }

  private IObjectList<IImageOperation> addTo(final IObjectList<IImageOperation> operations,
      final IImageOperation operation) {
    return new ObjectListBuilder<>(operations).add(operation).build();
  }

  protected abstract IImageMetadata read(final ICanceler canceler, final RenderingHints hints) throws CanceledException,
      IOException;

  protected abstract BufferedImage
      read(final ICanceler canceler,
          final RenderingHints hints,
          final IObjectList<IImageOperation> operations)
          throws CanceledException,
          IOException;

  protected abstract IImageMetadata adjust(IImageMetadata metadata, float width, float height);

  protected abstract IImageMetadata
      adjust(IImageMetadata metadata,
          final int numberOfComponents,
          final int numberOfBands,
          final int colorSpaceType,
          final int dataType,
          final int transparency);

  protected abstract IImageMetadata copy(IImageMetadata metadata);

  protected abstract IImageContainer
      adapt(final RenderingHints hints, final IImageMetadata metadata, final IObjectList<IImageOperation> operations);
}
