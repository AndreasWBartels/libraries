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
package net.anwiba.commons.image.operation;

import java.util.Arrays;

import net.anwiba.commons.lang.collection.IObjectList;
import net.anwiba.commons.lang.optional.IOptional;
import net.anwiba.commons.lang.optional.Optional;

public class ImageMapBandsOperation implements IImageOperation {

  private final int[] mapping;

  public ImageMapBandsOperation(final int[] bandMapping) {
    this.mapping = bandMapping;
  }

  public int[] getBandMapping() {
    return this.mapping;
  }

  public int getMappingSize() {
    return this.mapping.length;
  }

  public boolean isIdentity() {
    for (int i = 0; i < this.mapping.length; i++) {
      if (i >= this.mapping.length || i != this.mapping[i]) {
        return false;
      }
    }
    return true;
  }

  public boolean hasDuplicate() {
    for (int i = 0; i < this.mapping.length; i++) {
      int value = this.mapping[i];
      for (int j = i + 1; j < this.mapping.length; j++) {
        if (value == this.mapping[j]) {
          return true;
        }
      }
    }
    return false;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + Arrays.hashCode(this.mapping);
    return result;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    ImageMapBandsOperation other = (ImageMapBandsOperation) obj;
    if (!Arrays.equals(this.mapping, other.mapping)) {
      return false;
    }
    return true;
  }

  public static IOptional<ImageMapBandsOperation, RuntimeException>
      aggregate(final IObjectList<IImageOperation> imageOperations) {
    ImageMapBandsOperation mapBandsOperation = null;
    for (IImageOperation operation : imageOperations) {
      if (operation instanceof ImageMapBandsOperation) {
        if (mapBandsOperation == null) {
          mapBandsOperation = (ImageMapBandsOperation) operation;
        } else {
          return Optional.empty();
        }
      }
    }
    return Optional.of(mapBandsOperation);
  }
}
