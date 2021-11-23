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
package net.anwiba.commons.image.imagen;

import net.anwiba.commons.image.IImageMetadata;

class ImagenImageMetadata implements IImageMetadata {

  private final float width;
  private final float height;
  private final int numberOfBands;
  private final int numberOfComponents;
  private final int colorSpaceType;
  private final int dataType;
  private final int transparency;
  private final boolean isIndexed;

  public ImagenImageMetadata(
      final float width,
      final float height,
      final int numberOfComponents,
      final int numberOfBands,
      final int colorSpaceType,
      final int dataType,
      final int transparency,
      final boolean isIndexed) {
    this.width = width;
    this.height = height;
    this.numberOfComponents = numberOfComponents;
    this.numberOfBands = numberOfBands;
    this.colorSpaceType = colorSpaceType;
    this.dataType = dataType;
    this.transparency = transparency;
    this.isIndexed = isIndexed;
  }

  @Override
  public float getWidth() {
    return this.width;
  }

  @Override
  public float getHeight() {
    return this.height;
  }

  @Override
  public int getNumberOfColorComponents() {
    return this.numberOfComponents;
  }

  @Override
  public int getNumberOfBands() {
    return this.numberOfBands;
  }

  @Override
  public int getColorSpaceType() {
    return this.colorSpaceType;
  }

  @Override
  public int getDataType() {
    return this.dataType;
  }

  @Override
  public int getTransparency() {
    return this.transparency;
  }
  
  @Override
  public boolean isIndexed() {
    return isIndexed;
  }
}
