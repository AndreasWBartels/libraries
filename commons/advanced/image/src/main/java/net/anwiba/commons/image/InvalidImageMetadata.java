/*
 * #%L
 * anwiba commons
 * %%
 * Copyright (C) 2007 - 2019 Andreas W. Bartels
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

import net.anwiba.commons.message.IMessage;

public final class InvalidImageMetadata implements IImageMetadata {

  private final IMessage message;

  public InvalidImageMetadata(final IMessage message) {
    this.message = message;
  }

  @Override
  public float getWidth() {
    return 0;
  }

  @Override
  public int getNumberOfColorComponents() {
    return 0;
  }

  @Override
  public int getNumberOfBands() {
    return 0;
  }

  @Override
  public float getHeight() {
    return 0;
  }

  @Override
  public int getColorSpaceType() {
    return -1;
  }

  @Override
  public int getDataType() {
    return -1;
  }

  @Override
  public int getTransparency() {
    return -1;
  }

  @Override
  public boolean isIndexed() {
    return false;
  }
}
