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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.util.Objects;
import java.util.function.BiPredicate;

import net.anwiba.commons.lang.optional.IOptional;
import net.anwiba.commons.lang.optional.Optional;
import net.anwiba.commons.reference.utilities.ContentType;
import net.anwiba.commons.reference.utilities.IContentType;

public class ResourceCacheConfigurationBuilder {

  public static ResourceCacheConfigurationBuilder builder(String name) {
    return new ResourceCacheConfigurationBuilder(name);
  }

  private String name;
  private BiPredicate<Object, IOptional<IContentType, RuntimeException>> acceptor = (key, contentType) -> true;
  private File cachingFolder = null;
  private Duration lifeTime = Duration.ofMinutes(5);
  private ResourceAccessEvent pointOfTimeToExpire = ResourceAccessEvent.LAST_ACCESS;
  private CacheStorage preferedCacheStorage = null;

  public ResourceCacheConfigurationBuilder(String name) {
    this.name = name;
  }

  public ResourceCacheConfigurationBuilder acceptor(BiPredicate<Object, IOptional<IContentType, RuntimeException>> acceptor) {
    this.acceptor = acceptor;
    return this;
  }

  public ResourceCacheConfigurationBuilder lifeTime(Duration lifeTime) {
    this.lifeTime = lifeTime;
    return this;
  }

  public ResourceCacheConfigurationBuilder creationTimeAsStartPointForExpire() {
    this.pointOfTimeToExpire = ResourceAccessEvent.CREATED;
    return this;
  }

  public ResourceCacheConfigurationBuilder startPointForExpire(ResourceAccessEvent pointOfFileTime) {
    this.pointOfTimeToExpire = pointOfFileTime;
    return this;
  }

  public ResourceCacheConfigurationBuilder lastModifiedAsStartPointForExpire() {
    this.pointOfTimeToExpire = ResourceAccessEvent.LAST_MODIFIED;
    return this;
  }

  public ResourceCacheConfigurationBuilder lastAccessAsStartPointForExpire() {
    this.pointOfTimeToExpire = ResourceAccessEvent.LAST_ACCESS;
    return this;
  }

  public ResourceCacheConfigurationBuilder preferedCacheStorage(CacheStorage preferedCacheStorage) {
    this.preferedCacheStorage = preferedCacheStorage;
    return this;
  }

  public ResourceCacheConfigurationBuilder weakMemoryAsPreferedCacheStorage() {
    this.preferedCacheStorage = CacheStorage.MEMORY_WEAK;
    return this;
  }

  public ResourceCacheConfigurationBuilder staticMemoryAsPreferedCacheStorage() {
    this.preferedCacheStorage = CacheStorage.MEMORY_STATIC;
    return this;
  }

  public ResourceCacheConfigurationBuilder fileSystemAsPreferedCacheStorage() {
    this.preferedCacheStorage = CacheStorage.FILE_SYSTEM;
    return this;
  }

  public ResourceCacheConfigurationBuilder cachingFolder(File cachingFolder) {
    this.cachingFolder = cachingFolder;
    return this;
  }

  private ICachingRule createCachingRule() {
    return CachingRule.of(LifeTime.of(lifeTime, pointOfTimeToExpire), preferedCacheStorage);
  }

  private IOptional<File, IOException> cachingFolder() {
    if (Objects.equals(preferedCacheStorage, CacheStorage.FILE_SYSTEM)) {
      try {
        return Optional.of(IOException.class, Optional.of(IOException.class, cachingFolder).getOr(() -> {
            return new File(Files.createTempDirectory("resourcecache").toFile(), name);
        }));
      } catch (IOException exception) {
        return Optional.failed(IOException.class, exception);
      }
    }
    return Optional.of(IOException.class, cachingFolder);
  }

  public IResourceCacheConfiguration build() {
    final ICachingRule cachingRule = createCachingRule();
    final IOptional<File, IOException> cachingFolder = cachingFolder();
    return new IResourceCacheConfiguration() {

      @Override
      public boolean isApplicable(Object key, String contentType) {
        return acceptor.test(key, ContentType.from(contentType));
      }

      @Override
      public String getName() {
        return name;
      }

      @Override
      public ICachingRule getCachingRule() {
          return cachingRule;
      }

      @Override
      public IOptional<File, IOException> getCachingFolder() {
        return cachingFolder;
      }
    };
  }
}
