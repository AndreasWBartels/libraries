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
package net.anwiba.commons.utilities.cache;

import java.lang.ref.WeakReference;

import net.anwiba.commons.reference.IResourceReference;

public final class CacheObject implements ICacheObject {
  private final Object key;
  private final long lifeTime;
  private final IResourceReference resourceReference;
  private final WeakReference<IResourceReference> reference;

  public CacheObject(final Object key, final long lifeTime, final IResourceReference resourceReference) {
    this.key = key;
    this.lifeTime = lifeTime;
    this.resourceReference = resourceReference;
    this.reference = new WeakReference<>(this.resourceReference);
  }

  @Override
  public long getLifeTime() {
    return this.lifeTime;
  }

  @Override
  public Object getKey() {
    return this.key;
  }

  @Override
  public IResourceReference getResourceReference() {
    return this.reference.get();
  }
}
