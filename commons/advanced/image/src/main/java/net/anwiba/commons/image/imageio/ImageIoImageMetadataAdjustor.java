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
package net.anwiba.commons.image.imageio;

import net.anwiba.commons.image.IImageMetadata;
import net.anwiba.commons.image.IImageMetadataAdjustor;

final class ImageIoImageMetadataAdjustor implements IImageMetadataAdjustor {
  @Override
  public IImageMetadata doAdjust(final IImageMetadata metadata,
      final int numberOfComponents,
      final int numberOfBands,
      final int colorSpaceType) {
    ImageIoImageMetadata imageMetadata = (ImageIoImageMetadata) metadata;
    return new ImageIoImageMetadata(
        imageMetadata.getIndex(),
        imageMetadata.getWidth(),
        imageMetadata.getHeight(),
        numberOfComponents,
        numberOfBands,
        colorSpaceType,
        imageMetadata.getDataType(),
        imageMetadata.getTransparency(),
        imageMetadata.getImageType(), 
        imageMetadata.isIndexed());
  }

  @Override
  public IImageMetadata doAdjust(final IImageMetadata metadata, final float width, final float height) {
    ImageIoImageMetadata imageMetadata = (ImageIoImageMetadata) metadata;
    return new ImageIoImageMetadata(
        imageMetadata.getIndex(),
        width,
        height,
        imageMetadata.getNumberOfColorComponents(),
        imageMetadata.getNumberOfBands(),
        imageMetadata.getColorSpaceType(),
        imageMetadata.getDataType(),
        imageMetadata.getTransparency(),
        imageMetadata.getImageType(), 
        imageMetadata.isIndexed());
  }

  @Override
  public IImageMetadata
      doAdjust(final IImageMetadata metadata,
          final int numberOfComponents,
          final int numberOfBands,
          final int colorSpaceType,
          final int dataType,
          final int transparency) {
    ImageIoImageMetadata imageMetadata = (ImageIoImageMetadata) metadata;
    return new ImageIoImageMetadata(
        imageMetadata.getIndex(),
        imageMetadata.getWidth(),
        imageMetadata.getHeight(),
        numberOfComponents,
        numberOfBands,
        colorSpaceType,
        dataType,
        transparency,
        imageMetadata.getImageType(), 
        imageMetadata.isIndexed());
  }

  @Override
  public IImageMetadata doCopy(final IImageMetadata metadata) {
    ImageIoImageMetadata imageMetadata = (ImageIoImageMetadata) metadata;
    return new ImageIoImageMetadata(
        imageMetadata.getIndex(),
        imageMetadata.getWidth(),
        imageMetadata.getHeight(),
        imageMetadata.getNumberOfColorComponents(),
        imageMetadata.getNumberOfBands(),
        imageMetadata.getColorSpaceType(),
        imageMetadata.getDataType(),
        imageMetadata.getTransparency(),
        imageMetadata.getImageType(), 
        imageMetadata.isIndexed());
  }
}
