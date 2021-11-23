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

import net.anwiba.commons.image.IImageMetadata;
import net.anwiba.commons.image.IImageMetadataAdjustor;
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
import net.anwiba.commons.lang.exception.CanceledException;
import net.anwiba.commons.lang.object.ObjectPair;
import net.anwiba.commons.lang.optional.IOptional;
import net.anwiba.commons.lang.stream.Streams;
import net.anwiba.commons.thread.cancel.ICanceler;

public class BufferedImageOperatorFactory {

  private interface IBufferedImageFactory {

    BufferedImage create(BufferedImage image);

  }

  private static final class AggregatedBufferedImageOperator implements IBufferedImageOperator {
    private final IObjectList<IBufferedImageOperator> collection;

    public AggregatedBufferedImageOperator(final IObjectList<IBufferedImageOperator> collection) {
      this.collection = collection;
    }

    @Override
    public BufferedImage execute(final ICanceler canceler, final BufferedImage source) throws CanceledException {
      return Streams
          .of(CanceledException.class, this.collection)
          .aggregate(source, (s, o) -> {
            if (canceler.isCanceled()) {
              return null;
            }
            return s == null ? null : o.execute(canceler, s);
          })
          .get();
    }
  }

  private static final class BufferedImageOpOperator implements IBufferedImageOperator {

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
    public BufferedImage execute(final ICanceler canceler, final BufferedImage source) {
      return this.bufferedImageOp.filter(source, this.bufferedImageFactory.create(source));
    }

  }

  private static IBufferedImageOperator doNothingOperation = new IBufferedImageOperator() {

    @Override
    public BufferedImage execute(final ICanceler canceler, final BufferedImage source) {
      return source;
    }
  };

  private final IImageMetadataAdjustor metadataAdjustor;

  public BufferedImageOperatorFactory() {
    this(new BufferedImageMetadataAdjustor());
  }

  public BufferedImageOperatorFactory(final IImageMetadataAdjustor metadataAdjustor) {
    this.metadataAdjustor = metadataAdjustor;
  }

  public ObjectPair<IBufferedImageOperator, BufferedImageMetadata> create(
      final BufferedImageMetadata metadata,
      final IImageOperation operation,
      final RenderingHints hints) {
    if (operation instanceof ImageScaleOperation) {
      final ImageScaleOperation o = (ImageScaleOperation) operation;
      return ObjectPair.of(createImageScaleOperation(hints, o), adapt(metadata, operation));
    } else if (operation instanceof ImageCropOperation) {
      final ImageCropOperation o = (ImageCropOperation) operation;
      return ObjectPair.of(createImageCropOperation(o), adapt(metadata, operation));
    } else if (operation instanceof ImageMapBandsOperation) {
      final ImageMapBandsOperation o = (ImageMapBandsOperation) operation;
      return ObjectPair.of(createImageMapBandsOperation(metadata, hints, o), adapt(metadata, operation));
    } else if (operation instanceof ImageOpacityOperation) {
      final ImageOpacityOperation o = (ImageOpacityOperation) operation;
      if (o.getFactor() >= 1f) {
        return ObjectPair.of(doNothingOperation, metadata);
      }
      //      if (metadata.getNumberOfComponents() == 3) {
      //        return (canceler, source) -> Optional.of(ImagenImageContainerUtilities.toOpacity(hints, source, o.getFactor()))
      //            .convert(p -> p.getAsBufferedImage())
      //            .get();
      //      }
      return ObjectPair.of(createImageOpacityOperation(metadata, hints, o), adapt(metadata, operation));
    } else if (operation instanceof ImageInvertOperation) {
      //      return (canceler, source) -> Optional.of(ImagenImageContainerUtilities.toInverted(hints, source))
      //          .convert(p -> p.getAsBufferedImage())
      //          .get();
      return ObjectPair.of(createImageInvertOperation(metadata, hints), metadata);
    } else if (operation instanceof ImageToGrayScaleOperation) {
      return ObjectPair.of(createImageToGrayScaleOperation(hints), adapt(metadata, operation));
    }
    return ObjectPair.of(doNothingOperation, metadata);
  }

  private BufferedImageMetadata adapt(final IImageMetadata metadata, final IImageOperation operation) {
    return (BufferedImageMetadata) this.metadataAdjustor.adjust(metadata, operation);
  }

  public static IBufferedImageOperator createImageScaleOperation(final RenderingHints hints,
      final ImageScaleOperation o) {
    return new BufferedImageOpOperator(
        new AffineTransformOp(AffineTransform.getScaleInstance(o.getWidthFactor(), o.getHeightFactor()), hints));
  }

  private IBufferedImageOperator createImageCropOperation(final ImageCropOperation o) {
    return (canceler, source) -> {
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

  public static IBufferedImageOperator createImageMapBandsOperation(
      final IImageMetadata metadata,
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

  public static IBufferedImageOperator createImageOpacityOperation(
      final IImageMetadata metadata,
      final RenderingHints hints,
      final ImageOpacityOperation o) {
    final float factor = o.getFactor();
    if (metadata.getNumberOfBands() == 1 || metadata.getNumberOfBands() == 3) {
      return new IBufferedImageOperator() {

        @Override
        public BufferedImage execute(final ICanceler canceler, final BufferedImage source) {
          BufferedImage image = new ColorConvertOp(hints).filter(source,
              new BufferedImage(source.getWidth(), source.getHeight(), BufferedImage.TYPE_INT_ARGB));
          BufferedImage result = new LookupOp(new LookupTable(0, 4) {

            @Override
            public int[] lookupPixel(final int[] src, final int[] dest) {
              dest[0] = src[0];
              dest[1] = src[1];
              dest[2] = src[2];
              dest[3] = Math.round(src[3] * factor);
              return dest;
            }
          }, hints).filter(image, null);
          return result;
        }
      };
    }
    final LookupTable lookupTable = createColorOpacityLookupTable(metadata, factor);
    return new BufferedImageOpOperator(new LookupOp(lookupTable, hints));
  }

  private static LookupTable createColorOpacityLookupTable(final IImageMetadata metadata, final float factor) {
    return new LookupTable(0, metadata.getNumberOfBands()) {

      @Override
      public int[] lookupPixel(final int[] src, final int[] dest) {
        for (int i = 0; i < dest.length - 1; i++) {
          dest[i] = src[i];
        }
        dest[dest.length - 1] = Math.round(src[dest.length - 1] * factor);
        return dest;
      }
    };
  }

  public static IBufferedImageOperator createImageInvertOperation(final IImageMetadata metadata,
      final RenderingHints hints) {
    final LookupTable lookupTable = createColorInvertLookupTable(metadata);
    return new BufferedImageOpOperator(new LookupOp(lookupTable, hints));
  }

  private static LookupTable createColorInvertLookupTable(final IImageMetadata metadata) {
    if (ColorSpace.TYPE_GRAY == metadata.getColorSpaceType() && metadata.getNumberOfBands() == 1) {
      return new LookupTable(0, metadata.getNumberOfBands()) {

        @Override
        public int[] lookupPixel(final int[] src, final int[] dest) {
          dest[0] = 255 - src[0];
          return dest;
        }
      };
    }
    if (ColorSpace.TYPE_GRAY == metadata.getColorSpaceType() && metadata.getNumberOfBands() == 2) {
      return new LookupTable(0, metadata.getNumberOfBands()) {

        @Override
        public int[] lookupPixel(final int[] src, final int[] dest) {
          dest[0] = 255 - src[0];
          dest[1] = src[1];
          return dest;
        }
      };
    }
    if (metadata.getNumberOfColorComponents() == 3) {
      return new LookupTable(0, metadata.getNumberOfBands()) {

        @Override
        public int[] lookupPixel(final int[] src, final int[] dest) {
          dest[0] = 255 - src[0];
          dest[1] = 255 - src[1];
          dest[2] = 255 - src[2];
          return dest;
        }
      };
    }
    if (metadata.getNumberOfColorComponents() == 4) {
      return new LookupTable(0, metadata.getNumberOfBands()) {

        @Override
        public int[] lookupPixel(final int[] src, final int[] dest) {
          dest[0] = 255 - src[0];
          dest[1] = 255 - src[1];
          dest[2] = 255 - src[2];
          dest[3] = src[3];
          return dest;
        }
      };
    }
    return new LookupTable(0, metadata.getNumberOfBands()) {

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
  }

  public static IBufferedImageOperator createImageToGrayScaleOperation(final RenderingHints hints) {
    return new BufferedImageOpOperator(new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY), hints));
  }

  IBufferedImageOperator create(
      final BufferedImageMetadata metadata,
      final IObjectList<IImageOperation> imageOperations,
      final RenderingHints hints) {

    if (imageOperations.isEmpty()) {
      return new AggregatedBufferedImageOperator(new ObjectList<IBufferedImageOperator>());
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

    IMutableObjectList<IBufferedImageOperator> collection = new ObjectList<>();
    BufferedImageMetadata imageMetadata = metadata;
    for (IImageOperation operation : operations) {
      ObjectPair<IBufferedImageOperator, BufferedImageMetadata> pair = create(imageMetadata, operation, hints);
      collection.add(pair.getFirstObject());
      imageMetadata = pair.getSecondObject();
    }
    return new AggregatedBufferedImageOperator(collection);
  }
}