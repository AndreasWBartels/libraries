/*
 * #%L
 * anwiba commons
 * %%
 * Copyright (C) 2007 - 2022 Andreas W. Bartels
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.anwiba.commons.lang.stream.IStream;
import net.anwiba.commons.lang.stream.Streams;

public class ResourceCacheListResult implements IResourceCacheListResult {

  private List<IResourceCacheObject> objects;
  private boolean isExpired;

  public ResourceCacheListResult(List<IResourceCacheObject> objects, boolean isExpired) {
    this.objects = objects;
    this.isExpired = isExpired;
  }

  public static IResourceCacheListResult empty() {
    return new ResourceCacheListResult(List.of(), true);
  }

  public static IResourceCacheListResult of(List<IResourceCacheObject> objects, boolean isExpired) {
    return new ResourceCacheListResult(objects, isExpired);
  }

  @Override
  public Iterator<IResourceCacheObject> iterator() {
    return new ArrayList<>(objects).iterator();
  }

  @Override
  public boolean isEmpty() {
    return objects.isEmpty();
  }

  @Override
  public boolean isExpired() {
    return isExpired;
  }

  @Override
  public IStream<IResourceCacheObject, RuntimeException> toStream() {
    return Streams.of(objects);
  }

  @Override
  public int size() {
    return objects.size();
  }

}
