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
package net.anwiba.commons.image.awt;

import java.awt.RenderingHints;
import java.awt.color.ColorSpace;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ColorConvertOp;
import java.awt.image.LookupOp;
import java.awt.image.LookupTable;
import java.awt.image.RasterFormatException;

import net.anwiba.commons.image.IImageOperator;
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
import net.anwiba.commons.lang.optional.IOptional;

class BufferedImageOperatorFactory {

  private interface IBufferedImageFactory {

    BufferedImage create(BufferedImage image);

  }

  private static final class AggregatedBufferedImageOperator implements IImageOperator {
    private final IObjectList<IImageOperator> collection;

    public AggregatedBufferedImageOperator(final IObjectList<IImageOperator> collection) {
      this.collection = collection;
    }

    @Override
    public BufferedImage execute(final BufferedImage source) {
      return this.collection.stream().aggregate(source, (s, o) -> o.execute(s)).get();
    }
  }

  private static final class BufferedImageOpOperator implements IImageOperator {

    private final IBufferedImageFactory bufferedImageFactory;
    private final BufferedImageOp bufferedImageOp;

    public BufferedImageOpOperator(final BufferedImageOp bufferedImageOp) {
      this(b -> null, bufferedImageOp);
    }

    public BufferedImageOpOperator(
        final IBufferedImageFactory bufferedImageFactory,
        final BufferedImageOp bufferedImageOp) {
      this.bufferedImageFactory = bufferedImageFactory;
      this.bufferedImageOp = bufferedImageOp;
    }

    @Override
    public BufferedImage execute(final BufferedImage source) {
      return this.bufferedImageOp.filter(source, this.bufferedImageFactory.create(source));
    }

  }

  public IImageOperator create(
      final BufferedImageMetadata metadata,
      final IImageOperation operation,
      final RenderingHints hints) {
    if (operation instanceof ImageScaleOperation) {
      final ImageScaleOperation o = (ImageScaleOperation) operation;
      return createImageScaleOperation(hints, o);
    } else if (operation instanceof ImageCropOperation) {
      final ImageCropOperation o = (ImageCropOperation) operation;
      return createImageCropOperation(o);
    } else if (operation instanceof ImageMapBandsOperation) {
      final ImageMapBandsOperation o = (ImageMapBandsOperation) operation;
      return createImageMapBandsOperation(metadata, hints, o);
    } else if (operation instanceof ImageOpacityOperation) {
      final ImageOpacityOperation o = (ImageOpacityOperation) operation;
      return createImageOpacityOperation(metadata, hints, o);
    } else if (operation instanceof ImageInvertOperation) {
      return createImageInvertOperation(metadata, hints);
    } else if (operation instanceof ImageToGrayScaleOperation) {
      return createImageToGrayScaleOperation(hints);
    }
    return new IImageOperator() {

      @Override
      public BufferedImage execute(final BufferedImage source) {
        return source;
      }
    };
  }

  private IImageOperator createImageScaleOperation(final RenderingHints hints, final ImageScaleOperation o) {
    return new BufferedImageOpOperator(
        new AffineTransformOp(AffineTransform.getScaleInstance(o.getWidthFactor(), o.getHeightFactor()), hints));
  }

  private IImageOperator createImageCropOperation(final ImageCropOperation o) {
    return source -> {
      try {
        final int x = Math.round(o.getX());
        final int y = Math.round(o.getY());
        final int width = Math.round(o.getWidth());
        final int height = Math.round(o.getHeight());
        return source
            .getSubimage(x,
                y,
                (x + width > source.getWidth() ? source.getWidth() - x : width),
                (y + height > source.getHeight() ? source.getHeight() - y : height));
      } catch (RasterFormatException exception) {
        throw exception;
      }
    };
  }

  private IImageOperator createImageMapBandsOperation(
      final BufferedImageMetadata metadata,
      final RenderingHints hints,
      final ImageMapBandsOperation o) {
    int[] mappings = o.getBandMapping();
    final LookupTable lookupTable = new LookupTable(0, metadata.getNumberOfBands()) {

      @Override
      public int[] lookupPixel(final int[] src, final int[] dest) {
        for (int i = 0; i < dest.length; i++) {
          if (i < mappings.length) {
            dest[i] = src[mappings[i]];
            continue;
          }
          dest[i] = 255;
        }
        return dest;
      }
    };
    return new BufferedImageOpOperator(new LookupOp(lookupTable, hints));
  }

  private IImageOperator createImageOpacityOperation(
      final BufferedImageMetadata metadata,
      final RenderingHints hints,
      final ImageOpacityOperation o) {
    final float factor = o.getFactor();
    final LookupTable lookupTable = new LookupTable(0, metadata.getNumberOfBands()) {

      @Override
      public int[] lookupPixel(final int[] src, final int[] dest) {
        for (int i = 0; i < dest.length; i++) {
          if (i == 3) {
            if (src.length < 3) {
              dest[i] = Math.round(255 * factor);
              continue;
            }
            dest[i] = Math.round(src[i] * factor);
            continue;
          }
          dest[i] = src[i];
        }
        return dest;
      }
    };
    return new BufferedImageOpOperator(new IBufferedImageFactory() {

      @Override
      public BufferedImage create(final BufferedImage image) {
        if (image.getType() != BufferedImage.TYPE_INT_ARGB) {
          return null;
        }
        return new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
      }
    }, new LookupOp(lookupTable, hints));
  }

  private IImageOperator createImageInvertOperation(final BufferedImageMetadata metadata, final RenderingHints hints) {
    final LookupTable lookupTable = new LookupTable(0, metadata.getNumberOfBands()) {

      @Override
      public int[] lookupPixel(final int[] src, final int[] dest) {
        for (int i = 0; i < dest.length; i++) {
          if (i == 3) {
            dest[i] = src[i];
            continue;
          }
          dest[i] = 255 - src[i];
        }
        return dest;
      }
    };
    return new BufferedImageOpOperator(new LookupOp(lookupTable, hints));
  }

  private IImageOperator createImageToGrayScaleOperation(final RenderingHints hints) {
    return new BufferedImageOpOperator(new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY), hints));
  }

  IImageOperator create(
      final BufferedImageMetadata metadata,
      final IObjectList<IImageOperation> imageOperations,
      final RenderingHints hints) {

    if (imageOperations.isEmpty()) {
      return new AggregatedBufferedImageOperator(new ObjectList<IImageOperator>());
    }
    IOptional<ImageScaleOperation, RuntimeException> scaleOperation = ImageScaleOperation.aggregate(imageOperations);
    IOptional<ImageCropOperation, RuntimeException> cropOperation = ImageCropOperation.aggregate(imageOperations);
    final IMutableObjectList<IImageOperation> operations = new ObjectList<>();
    if (cropOperation.isAccepted()) {
      operations.add(cropOperation.get());
    }
    if (scaleOperation.accept(s -> s.getWidthFactor() < 1 && s.getHeightFactor() < 1).isAccepted()) {
      operations.add(scaleOperation.get());
    }
    for (final IImageOperation operation : imageOperations) {
      if (operation instanceof ImageScaleOperation || operation instanceof ImageCropOperation) {
        continue;
      }
      if (operation instanceof ImageMapBandsOperation && ((ImageMapBandsOperation) operation).getMappingSize() == 0) {
        continue;
      }
      operations.add(operation);
    }
    if (scaleOperation.accept(s -> s.getWidthFactor() > 1 || s.getHeightFactor() > 1).isAccepted()) {
      operations.add(scaleOperation.get());
    }

    final IObjectList<IImageOperator> collection =
        operations.stream().convert(o -> create(metadata, o, hints)).asObjectList();
    return new AggregatedBufferedImageOperator(collection);
  }
}