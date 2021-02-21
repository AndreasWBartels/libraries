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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program. If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */
package net.anwiba.commons.utilities.cache;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.anwiba.commons.lang.optional.IOptional;
import net.anwiba.commons.lang.optional.Optional;
import net.anwiba.commons.reference.IResourceReference;
import net.anwiba.commons.reference.IResourceReferenceFactory;
import net.anwiba.commons.reference.IResourceReferenceHandler;

public class WeakInMemoryCache implements ICache {

  private final Map<Object, List<ICacheObject>> objects = new ConcurrentHashMap<>();
  private final long lifeTime;
  private final IResourceReferenceHandler resourceReferenceHandler;
  private final IResourceReferenceFactory resourceReferenceFactory;

  public WeakInMemoryCache(
      final IResourceReferenceFactory resourceReferenceFactory,
      final IResourceReferenceHandler resourceReferenceHandler,
      final long lifeTime) {
    this.resourceReferenceFactory = resourceReferenceFactory;
    this.resourceReferenceHandler = resourceReferenceHandler;
    this.lifeTime = lifeTime;
  }

  @Override
  public IResourceReference add(
      final Object key,
      final byte[] data,
      final String mimeType,
      final String encoding) {
    return add(key, data, mimeType, encoding, this.lifeTime);
  }

  @Override
  public IResourceReference add(
      final Object key,
      final byte[] data,
      final String mimeType,
      final String encoding,
      final long lifeTime) {
    final IResourceReference resourceReference = this.resourceReferenceFactory
        .create(data, mimeType, encoding);
    if (data == null || data.length == 0) {
      return resourceReference;
    }
    if (mimeType == null || mimeType.isBlank()) {
      return resourceReference;
    }
    synchronized (this.objects) {
      this.objects.putIfAbsent(key, new LinkedList<ICacheObject>());
      this.objects.get(key).add(new CacheObject(key, lifeTime, resourceReference));
      return resourceReference;
    }
  }

  @Override
  public IOptional<IResourceReference, RuntimeException> getResourceReference(final Object key) {
    List<IResourceReference> references = getResourceReferences(key);
    if (references.isEmpty()) {
      return Optional.empty();
    }
    return Optional.of(references.get(0));
  }

  @Override
  public List<IResourceReference> getResourceReferences(final Object key) {
    synchronized (this.objects) {
      try {
        final List<ICacheObject> list = this.objects.get(key);
        if (list == null || list.isEmpty()) {
          return List.of();
        }
        List<IResourceReference> references = new LinkedList<>();
        for (ICacheObject object : list) {
          final IResourceReference resourceReference = object.getResourceReference();
          if (resourceReference == null) {
            this.objects.remove(key);
            return List.of();
          }
          try {
            final long lastModified = this.resourceReferenceHandler.lastModified(resourceReference);
            final long currentTimeMillis = System.currentTimeMillis();
            final long age = currentTimeMillis - lastModified;
            final long objectLifeTime = object.getLifeTime();
            if (age > objectLifeTime) {
              this.objects.remove(key);
              return List.of();
            }
            references.add(resourceReference);
          } catch (IOException e) {
            this.objects.remove(key);
            return List.of();
          }
        }
        return references;
      } catch (NullPointerException exception) {
        this.objects.remove(key);
        return List.of();
      }
    }
  }

  @Override
  public void clear() {
    this.objects.clear();
  }

}
