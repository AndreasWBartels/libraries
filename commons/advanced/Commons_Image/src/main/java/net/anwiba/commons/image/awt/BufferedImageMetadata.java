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
package net.anwiba.commons.image.awt;

public class BufferedImageMetadata {

  private final int index;
  private final float width;
  private final float height;
  private final int numberOfBands;
  private final int colorSpaceType;
  private final int numberOfComponents;

  public BufferedImageMetadata(
      final int index,
      final float width,
      final float height,
      final int numberOfComponents,
      final int numberOfBands,
      final int colorSpaceType) {
    this.index = index;
    this.width = width;
    this.height = height;
    this.numberOfComponents = numberOfComponents;
    this.numberOfBands = numberOfBands;
    this.colorSpaceType = colorSpaceType;
  }

  public float getWidth() {
    return this.width;
  }

  public float getHeight() {
    return this.height;
  }

  public int getIndex() {
    return this.index;
  }

  public int getNumberOfComponents() {
    return this.numberOfComponents;
  }

  public int getNumberOfBands() {
    return this.numberOfBands;
  }

  public int getColorSpaceType() {
    return this.colorSpaceType;
  }

}
