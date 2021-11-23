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
package net.anwiba.commons.image.imagen;

import java.awt.RenderingHints;

import org.eclipse.imagen.PlanarImage;
import org.eclipse.imagen.RenderedOp;

import net.anwiba.commons.image.IImageMetadata;
import net.anwiba.commons.image.IImageMetadataAdjustor;
import net.anwiba.commons.image.awt.BufferedImageOperatorFactory;
import net.anwiba.commons.image.operation.IImageOperation;
import net.anwiba.commons.image.operation.ImageCropOperation;
import net.anwiba.commons.image.operation.ImageInvertOperation;
import net.anwiba.commons.image.operation.ImageMapBandsOperation;
import net.anwiba.commons.image.operation.ImageOpacityOperation;
import net.anwiba.commons.image.operation.ImageScaleOperation;
import net.anwiba.commons.image.operation.ImageToGrayScaleOperation;
import net.anwiba.commons.image.operation.ImageTransparencyColorOperation;
import net.anwiba.commons.lang.collection.IMutableObjectList;
import net.anwiba.commons.lang.collection.IObjectList;
import net.anwiba.commons.lang.collection.ObjectList;
import net.anwiba.commons.lang.exception.CanceledException;
import net.anwiba.commons.lang.object.ObjectPair;
import net.anwiba.commons.lang.optional.IOptional;
import net.anwiba.commons.lang.stream.Streams;
import net.anwiba.commons.thread.cancel.ICanceler;

class PlanarImageOperatorFactory {

  private static final class AggregatedPlanarImageOperator implements IPlanarImageOperator {
    private final IObjectList<IPlanarImageOperator> collection;

    public AggregatedPlanarImageOperator(final IObjectList<IPlanarImageOperator> collection) {
      this.collection = collection;
    }

    @Override
    public PlanarImage execute(final ICanceler canceler, final PlanarImage source)
        throws CanceledException {
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

  private IImageMetadataAdjustor metadataAdjustor;

  public PlanarImageOperatorFactory() {
    this(new ImagenImageMetadataAdjustor());
  }

  public PlanarImageOperatorFactory(IImageMetadataAdjustor metadataAdjustor) {
    this.metadataAdjustor = metadataAdjustor;
  }

  public ObjectPair<IPlanarImageOperator, ImagenImageMetadata> create(
      final ImagenImageMetadata metadata,
      final IImageOperation operation,
      final RenderingHints hints) {
    if (operation instanceof ImageScaleOperation) {
      final ImageScaleOperation o = (ImageScaleOperation) operation;
      return ObjectPair.of(createImageScaleOperation(hints, o), adapt(metadata, operation));
    } else if (operation instanceof ImageCropOperation) {
      final ImageCropOperation o = (ImageCropOperation) operation;
      return ObjectPair.of(createImageCropOperation(hints, o), adapt(metadata, operation));
    } else if (operation instanceof ImageMapBandsOperation) {
      final ImageMapBandsOperation o = (ImageMapBandsOperation) operation;
      return ObjectPair.of(createImageMapBandsOperation(hints, o), adapt(metadata, operation));
    } else if (operation instanceof ImageOpacityOperation) {
      final ImageOpacityOperation o = (ImageOpacityOperation) operation;
      return ObjectPair.of(createImageOpacityOperation(hints, metadata, o), adapt(metadata, operation));
    } else if (operation instanceof ImageTransparencyColorOperation) {
      final ImageTransparencyColorOperation o = (ImageTransparencyColorOperation) operation;
      return ObjectPair.of(createImageTransparencyOperation(hints, o), adapt(metadata, operation));
    } else if (operation instanceof ImageInvertOperation) {
      return ObjectPair.of(createImageInvertOperation(metadata, hints), adapt(metadata, operation));
    } else if (operation instanceof ImageToGrayScaleOperation) {
      return ObjectPair.of(createImageToGrayScaleOperation(hints), adapt(metadata, operation));
    }
    return ObjectPair.of((canceler, source) -> source, metadata);
  }

  private ImagenImageMetadata adapt(IImageMetadata metadata, IImageOperation operation) {
    return (ImagenImageMetadata) metadataAdjustor.adjust(metadata, operation);
  }

  private IPlanarImageOperator createImageScaleOperation(
      final RenderingHints hints,
      final ImageScaleOperation o) {
    return (canceler, source) -> ImagenImageContainerUtilities
        .scale(
            hints,
            source,
            o.getWidthFactor(),
            o.getHeightFactor(),
            ImagenImageContainerUtilities.getInterpolation(hints));
  }

  private IPlanarImageOperator createImageCropOperation(
      final RenderingHints hints,
      final ImageCropOperation o) {
    return (canceler, source) -> {
      float x = o.getX();
      float y = o.getY();
      float width = o.getWidth();
      float height = o.getHeight();
      final RenderedOp cropedRenderOp = ImagenImageContainerUtilities
          .crop(hints, source, x, y, width, height);
      if (cropedRenderOp == null || canceler.isCanceled()) {
        return null;
      }
      return ImagenImageContainerUtilities
          .translate(
              hints,
              cropedRenderOp,
              -x,
              -y,
              ImagenImageContainerUtilities.getInterpolation(hints));
    };
  }

  private IPlanarImageOperator createImageMapBandsOperation(
      final RenderingHints hints,
      final ImageMapBandsOperation o) {
    int[] mapping = o.getBandMapping();
    return (canceler, source) -> ImagenImageContainerUtilities.toMapped(hints, source, mapping);
  }

  private IPlanarImageOperator createImageOpacityOperation(
      final RenderingHints hints,
      final IImageMetadata metadata,
      final ImageOpacityOperation o) {
    if (metadata.isIndexed()) {
      return (canceler, source) -> ImagenImageContainerUtilities.toOpacity(hints, source, o.getFactor());
    }
    return (canceler, source) -> ImagenImageContainerUtilities.toPlanarImage(
        BufferedImageOperatorFactory.createImageOpacityOperation(metadata, hints, o)
            .execute(canceler, source.getAsBufferedImage()));
  }

  private IPlanarImageOperator createImageTransparencyOperation(
      final RenderingHints hints,
      final ImageTransparencyColorOperation o) {
    return (canceler, source) -> ImagenImageContainerUtilities
        .toTransparent(hints, source, o.getColor());
  }

  private IPlanarImageOperator createImageInvertOperation(
      final IImageMetadata metadata,
      final RenderingHints hints) {
    if (metadata.isIndexed()) {
      return (canceler, source) -> ImagenImageContainerUtilities.toInverted(hints, source);
    }
    return (canceler, source) -> ImagenImageContainerUtilities.toPlanarImage(
        BufferedImageOperatorFactory.createImageInvertOperation(metadata, hints)
            .execute(canceler, source.getAsBufferedImage()));
  }

  private IPlanarImageOperator createImageToGrayScaleOperation(final RenderingHints hints) {
    return (canceler, source) -> ImagenImageContainerUtilities.toGrayScale(hints, source);
  }

  IPlanarImageOperator create(
      final ImagenImageMetadata metadata,
      final IObjectList<IImageOperation> imageOperations,
      final RenderingHints hints) {
    if (imageOperations.isEmpty()) {
      return new AggregatedPlanarImageOperator(new ObjectList<IPlanarImageOperator>());
    }
    IOptional<ImageScaleOperation, RuntimeException> scaleOperation = ImageScaleOperation
        .aggregate(imageOperations);
    IOptional<ImageCropOperation, RuntimeException> cropOperation = ImageCropOperation
        .aggregate(imageOperations);
    final IMutableObjectList<IImageOperation> operations = new ObjectList<>();
    if (cropOperation.isAccepted()) {
      operations.add(cropOperation.get());
    }
    if (scaleOperation
        .accept(s -> s.getWidthFactor() < 1 && s.getHeightFactor() < 1)
        .isAccepted()) {
      operations.add(scaleOperation.get());
    }
    for (final IImageOperation operation : imageOperations) {
      if (operation instanceof ImageScaleOperation || operation instanceof ImageCropOperation) {
        continue;
      }
      if (operation instanceof ImageMapBandsOperation
          && ((ImageMapBandsOperation) operation).getMappingSize() == 0) {
        continue;
      }
      operations.add(operation);
    }
    if (scaleOperation
        .accept(s -> s.getWidthFactor() > 1 || s.getHeightFactor() > 1)
        .isAccepted()) {
      operations.add(scaleOperation.get());
    }
    final IMutableObjectList<IPlanarImageOperator> collection = new ObjectList<>();
    ImagenImageMetadata imageMetadata = metadata;
    for (IImageOperation operation : operations) {
      ObjectPair<IPlanarImageOperator, ImagenImageMetadata> pair = create(imageMetadata, operation, hints);
      collection.add(pair.getFirstObject());
      imageMetadata = pair.getSecondObject();
    }
    return new AggregatedPlanarImageOperator(collection);
  }
}