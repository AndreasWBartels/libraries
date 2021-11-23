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
package net.anwiba.commons.cache.resource;

import java.io.IOException;
import java.nio.file.attribute.FileTime;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.anwiba.commons.cache.resource.properties.IProperties;
import net.anwiba.commons.lang.optional.IOptional;
import net.anwiba.commons.lang.optional.Optional;
import net.anwiba.commons.lang.stream.Streams;
import net.anwiba.commons.reference.IResourceReference;
import net.anwiba.commons.reference.IResourceReferenceFactory;
import net.anwiba.commons.reference.IResourceReferenceHandler;

public class WeakInMemoryResourceCache implements IResourceCache {

  private final Map<Object, List<IResourceCacheObject>> objects = new ConcurrentHashMap<>();
  private final ICachingRule cachingRule;
  private final IResourceReferenceHandler resourceReferenceHandler;
  private final IResourceReferenceFactory resourceReferenceFactory;

  public WeakInMemoryResourceCache(
      final IResourceReferenceFactory resourceReferenceFactory,
      final IResourceReferenceHandler resourceReferenceHandler,
      final long lifeTime) {
    this.resourceReferenceFactory = resourceReferenceFactory;
    this.resourceReferenceHandler = resourceReferenceHandler;
    this.cachingRule = CachingRule.of(LifeTime.of(lifeTime));
  }

  @Override
  public IResourceReference put(final Object key,
      final byte[] data,
      final String contentType,
      final String charset,
      final IProperties properties) {
    return put(this.cachingRule, key, data, contentType, charset, properties);
  }

  @Override
  public IResourceReference put(final ICachingRule cachingRule,
      final Object key,
      final byte[] data,
      final String contentType,
      final String charset,
      final IProperties properties) {
    final IResourceReference resourceReference = this.resourceReferenceFactory
        .create(data, contentType, charset);
    if (data == null || data.length == 0) {
      return resourceReference;
    }
    if (contentType == null || contentType.isBlank()) {
      return resourceReference;
    }
    synchronized (this.objects) {
      this.objects.put(key, new LinkedList<IResourceCacheObject>());
      this.objects.get(key)
          .add(new WeakResourceCacheObject(key, cachingRule, resourceReference, contentType, charset, properties));
      return resourceReference;
    }
  }

  @Override
  public IResourceReference add(
      final Object key,
      final byte[] data,
      final String contentType,
      final String charset,
      final IProperties properties) {
    return add(this.cachingRule, key, data, contentType, charset, properties);
  }

  @Override
  public IResourceReference add(
      final ICachingRule cachingRule,
      final Object key,
      final byte[] data,
      final String contentType,
      final String charset,
      final IProperties properties) {
    final IResourceReference resourceReference = this.resourceReferenceFactory
        .create(data, contentType, charset);
    if (data == null || data.length == 0) {
      return resourceReference;
    }
    if (contentType == null || contentType.isBlank()) {
      return resourceReference;
    }
    synchronized (this.objects) {
      this.objects.putIfAbsent(key, new LinkedList<IResourceCacheObject>());
      this.objects.get(key)
          .add(new WeakResourceCacheObject(key, cachingRule, resourceReference, contentType, charset, properties));
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
    return Streams.of(getObjects(key))
        .convert(o -> o.getResourceReference())
        .asList();
  }

  @Override
  public IResourceCacheResult getObject(final Object key) {
    IResourceCacheListResult objects = getObjects(key);
    if (objects.isEmpty()) {
      return ResourceCacheResult.empty();
    }
    return ResourceCacheResult.of(objects.toStream().first().get(), false);
  }

  @Override
  public IResourceCacheListResult getObjects(final Object key) {
    try {
      final List<IResourceCacheObject> list = this.objects.get(key);
      if (list == null || list.isEmpty()) {
        return ResourceCacheListResult.empty();
      }
      for (IResourceCacheObject object : list) {
        final Duration objectLifeTime = object.getCachingRule().getPreferedLifeTime().getDuration();
        final IResourceReference resourceReference = object.getResourceReference();
        if (isExpired(resourceReference, objectLifeTime)) {
          remove(key);
          return ResourceCacheListResult.empty();
        }
      }
      return ResourceCacheListResult.of(list, false);
    } catch (NullPointerException exception) {
      remove(key);
      return ResourceCacheListResult.empty();
    }
  }

  private boolean isExpired(final IResourceReference resourceReference, final Duration objectLifeTime) {
    try {
      if (resourceReference == null) {
        return true;
      }
      if (!this.resourceReferenceHandler.exists(resourceReference)) {
        return true;
      }
      FileTime lastModified = this.resourceReferenceHandler.lastModified(resourceReference);
      final Instant currentTime = Instant.now(Clock.systemDefaultZone());
      final Duration age = Duration.between(lastModified.toInstant(), currentTime);
      return age.toMillis() > objectLifeTime.toMillis();
    } catch (IOException exception) {
      return false;
    }
  }

  @Override
  public void clear() {
    this.objects.clear();
  }

  @Override
  public void remove(final Object key) {
    this.objects.remove(key);
  }
}
