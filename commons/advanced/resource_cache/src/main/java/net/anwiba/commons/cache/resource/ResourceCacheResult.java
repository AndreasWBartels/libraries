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
package net.anwiba.commons.cache.resource;

import net.anwiba.commons.lang.optional.IOptional;
import net.anwiba.commons.lang.optional.Optional;

public class ResourceCacheResult implements IResourceCacheResult {

  private boolean isExpired;
  private IResourceCacheObject object;

  public static IResourceCacheResult empty() {
    return new ResourceCacheResult(null, true);
  }

  public static IResourceCacheResult of(IResourceCacheObject object) {
    return of(object, false);
  }

  public static IResourceCacheResult of(IResourceCacheObject object , boolean isExpired) {
    return new ResourceCacheResult(object, isExpired);
  }
  
  public ResourceCacheResult(IResourceCacheObject object , boolean isExpired) {
    this.object = object;
    this.isExpired = isExpired;
  }
  
  @Override
  public boolean isEmpty() {
    return object == null;
  }

  @Override
  public boolean isExpired() {
    return isExpired;
  }

  @Override
  public IOptional<IResourceCacheObject, RuntimeException> toOptional() {
    return Optional.of(object);
  }

}
