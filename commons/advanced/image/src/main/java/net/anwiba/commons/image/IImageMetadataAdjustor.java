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

import java.awt.color.ColorSpace;

import net.anwiba.commons.image.operation.IImageOperation;
import net.anwiba.commons.image.operation.ImageCropOperation;
import net.anwiba.commons.image.operation.ImageInvertOperation;
import net.anwiba.commons.image.operation.ImageMapBandsOperation;
import net.anwiba.commons.image.operation.ImageOpacityOperation;
import net.anwiba.commons.image.operation.ImageScaleOperation;
import net.anwiba.commons.image.operation.ImageToGrayScaleOperation;
import net.anwiba.commons.message.MessageBuilder;

public interface IImageMetadataAdjustor {

  default IImageMetadata adjust(IImageMetadata metadata, IImageOperation operation) {
    if (operation instanceof ImageScaleOperation) {
      final ImageScaleOperation o = (ImageScaleOperation) operation;
      return adjust(
          metadata,
          metadata.getWidth() * o.getWidthFactor(),
          metadata.getHeight() * o.getHeightFactor());
    } else if (operation instanceof ImageCropOperation) {
      final ImageCropOperation o = (ImageCropOperation) operation;
      float width = adjust(metadata.getWidth(), o.getX(), o.getWidth());
      float height = adjust(metadata.getHeight(), o.getY(), o.getHeight());
      return adjust(metadata, width, height);
    } else if (operation instanceof ImageMapBandsOperation) {
      final ImageMapBandsOperation o = (ImageMapBandsOperation) operation;
      return adjust(metadata, o.getMappingSize(), o.getMappingSize(), o.getMappingSize() < 3 ? ColorSpace.TYPE_GRAY : ColorSpace.TYPE_RGB);
    } else if (operation instanceof ImageOpacityOperation) {
      final ImageOpacityOperation o = (ImageOpacityOperation) operation;
      if (o.getFactor() >= 1f) {
        return metadata;
      }
      return adjust(metadata, 4, 4, ColorSpace.TYPE_RGB);
    } else if (operation instanceof ImageInvertOperation) {
      return metadata;
    } else if (operation instanceof ImageToGrayScaleOperation) {
      int numberOfComponents = adjustToGrayScale(metadata.getNumberOfColorComponents());
      int numberOfBands = adjustToGrayScale(metadata.getNumberOfBands());
      return adjust(metadata, numberOfComponents, numberOfBands, ColorSpace.TYPE_GRAY);
    }
    return metadata;
  }

  default int adjustToGrayScale(int compoments) {
    if (compoments == 4) {
      return 2;
    }
    if (compoments >= 3) {
      return 1;
    }
    return compoments;
  }

  private float adjust(final float currentValue, final float offset, final float value) {
    return offset >= 0
        ? offset > currentValue
            ? 0
            : offset + value <= currentValue
                ? value
            : currentValue - offset
        : offset + value < 0
            ? 0
        : offset + value <= currentValue
            ? offset + value
        : currentValue;
  }

  default IImageMetadata adjust(final IImageMetadata metadata, final float width, final float height) {
    if (metadata == null || metadata instanceof InvalidImageMetadata) {
      return metadata;
    }
    if (metadata.getWidth() <= 0) {
      return new InvalidImageMetadata(new MessageBuilder().setText("width <= 0").setError().build());
    }
    if (metadata.getHeight() <= 0) {
      return new InvalidImageMetadata(new MessageBuilder().setText("height <= 0").setError().build());
    }
    return doAdjust(metadata, width, height);
  }

  default IImageMetadata
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
    if (metadata.getNumberOfColorComponents() <= 0) {
      return new InvalidImageMetadata(new MessageBuilder().setText("number of components <= 0").setError().build());
    }
    if (metadata.getColorSpaceType() < 0 || metadata.getColorSpaceType() > 25) {
      return new InvalidImageMetadata(new MessageBuilder().setText("number of components <= 0").setError().build());
    }

    return doAdjust(metadata, numberOfComponents, numberOfBands, colorSpaceType);
  }

  default IImageMetadata copy(final IImageMetadata metadata) {
    if (metadata == null || metadata instanceof InvalidImageMetadata) {
      return metadata;
    }
    return doCopy(metadata);
  }

  default IImageMetadata adjust(IImageMetadata metadata,
      final int numberOfComponents,
      final int numberOfBands,
      final int colorSpaceType,
      final int dataType,
      final int transparency) {
    if (metadata == null || metadata instanceof InvalidImageMetadata) {
      return metadata;
    }
    return doAdjust(metadata, numberOfComponents, numberOfBands, colorSpaceType, dataType, transparency);
  }

  IImageMetadata doAdjust(IImageMetadata metadata,
      final int numberOfComponents,
      final int numberOfBands,
      final int colorSpaceType,
      final int dataType,
      final int transparency);

  IImageMetadata doAdjust(IImageMetadata metadata, float width, float height);

  IImageMetadata doAdjust(IImageMetadata metadata,
      final int numberOfComponents,
      final int numberOfBands,
      final int colorSpaceType);

  IImageMetadata doCopy(IImageMetadata metadata);
}
