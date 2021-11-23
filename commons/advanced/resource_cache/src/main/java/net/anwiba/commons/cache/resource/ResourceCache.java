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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import net.anwiba.commons.cache.resource.properties.IProperties;
import net.anwiba.commons.lang.object.IObjectContainer;
import net.anwiba.commons.lang.object.ObjectContainer;
import net.anwiba.commons.lang.object.ObjectPair;
import net.anwiba.commons.lang.optional.IOptional;
import net.anwiba.commons.lang.optional.Optional;
import net.anwiba.commons.lang.stream.Streams;
import net.anwiba.commons.logging.ILevel;
import net.anwiba.commons.message.IMessageCollector;
import net.anwiba.commons.reference.IResourceReference;
import net.anwiba.commons.reference.IResourceReferenceFactory;
import net.anwiba.commons.reference.IResourceReferenceHandler;
import net.anwiba.commons.reference.IResourceReferenceVisitor;
import net.anwiba.commons.reference.IResourceReferenceWrapper;
import net.anwiba.commons.reference.utilities.ContentType;
import net.anwiba.commons.reference.utilities.IContentType;
import net.anwiba.commons.reference.utilities.IPrimaryType;
import net.anwiba.commons.reference.utilities.IoUtilities;
import net.anwiba.commons.reference.utilities.PrimaryType;
import net.anwiba.commons.thread.process.IProcessManager;
import net.anwiba.commons.thread.process.ProcessBuilder;
import net.anwiba.commons.thread.queue.IQueueNameConstans;

public class ResourceCache implements IResourceCache {

  private static net.anwiba.commons.logging.ILogger logger =
      net.anwiba.commons.logging.Logging.getLogger(ResourceCache.class);
  private final Map<Object, List<IResourceCacheObject>> cachedObjects = new ConcurrentHashMap<>();
  private final IResourceReferenceHandler resourceReferenceHandler;
  private final IResourceReferenceFactory resourceReferenceFactory;
  private final IProcessManager processManager;
  private final List<IResourceCacheConfiguration> configurations;

  private static class ResourceAge {

    private FileTime lastAccessed;
    private FileTime lastModified;
    private FileTime creationTime;
    private final IResourceReferenceHandler resourceReferenceHandler;
    private final IResourceReference resourceReference;

    public ResourceAge(
        final IResourceReferenceHandler resourceReferenceHandler,
        final IResourceReference resourceReference) {
      this.resourceReferenceHandler = resourceReferenceHandler;
      this.resourceReference = resourceReference;
    }

    public FileTime creationTime() {
      if (this.creationTime == null) {
        try {
          this.creationTime = this.resourceReferenceHandler.created(this.resourceReference);
        } catch (IOException exception) {
          this.creationTime = FileTime.from(0, TimeUnit.SECONDS);
        }
      }
      return this.creationTime;
    }

    public FileTime lastModified() {
      if (this.lastModified == null) {
        try {
          this.lastModified = this.resourceReferenceHandler.created(this.resourceReference);
        } catch (IOException exception) {
          this.lastModified = FileTime.from(0, TimeUnit.SECONDS);
        }
      }
      return this.lastModified;
    }

    public FileTime lastAccessed() {
      if (this.lastAccessed == null) {
        try {
          this.lastAccessed = this.resourceReferenceHandler.lastAccessed(this.resourceReference);
        } catch (IOException exception) {
          this.lastAccessed = FileTime.from(0, TimeUnit.SECONDS);
        }
      }
      return this.lastAccessed;
    }

    public Duration getAge(final Instant now, final ResourceAccessEvent event) {
      return Duration.between(getTime(event).toInstant(), now);
    }

    private FileTime getTime(final ResourceAccessEvent event) {
      switch (event) {
        case CREATED: {
          return creationTime();
        }
        case LAST_MODIFIED: {
          return lastModified();
        }
        case LAST_ACCESS: {
          return lastAccessed();
        }
      }
      return FileTime.from(0, TimeUnit.SECONDS);
    }
  }

  private static class ResourceReferenceWrapper implements IResourceReferenceWrapper {

    private final IObjectContainer<IResourceReference> resourceReferenceContainer;

    public ResourceReferenceWrapper(final IObjectContainer<IResourceReference> resourceReferenceContainer) {
      this.resourceReferenceContainer = resourceReferenceContainer;
    }

    @Override
    public <O, E extends Exception> O accept(final IResourceReferenceVisitor<O, E> visitor) throws E {
      return visitor.visitWrappedReference(this);
    }

    @Override
    public IResourceReference getWrappedResourceReference() {
      return this.resourceReferenceContainer.get();
    }
  }

  private final IResourceCacheConfiguration defaultInMemoryCacheConfiguration =
      ResourceCacheConfigurationBuilder
          .builder("memory")
          .acceptor(new BiPredicate<Object, IOptional<IContentType, RuntimeException>>() {

            private final Set<IPrimaryType> supportedPrimaryTypes = Set.of(
                PrimaryType.TEXT,
                PrimaryType.APPLICATION);
            private final Set<IContentType> notSupportedContentTypes = Set.of(
                ContentType.APPLICATION_OGC_SE_XML // OGC exception type
            );

            @Override
            public boolean test(final Object key, final IOptional<IContentType, RuntimeException> contentType) {
              if (contentType.isEmpty()) {
                return false;
              }
              if (!this.supportedPrimaryTypes.contains(contentType.get().getPrimaryType())) {
                return false;
              }
              if (this.notSupportedContentTypes.contains(contentType.get())) {
                return false;
              }
              return true;
            }
          })
          .lifeTime(Duration.ofMinutes(5))
          .creationTimeAsStartPointForExpire()
          .weakMemoryAsPreferedCacheStorage()
          .build();

  public ResourceCache(
      final List<IResourceCacheConfiguration> configurations,
      final IProcessManager processManager,
      final IResourceReferenceFactory resourceReferenceFactory,
      final IResourceReferenceHandler resourceReferenceHandler) {
    this.configurations = configurations;
    this.processManager = processManager;
    this.resourceReferenceFactory = resourceReferenceFactory;
    this.resourceReferenceHandler = resourceReferenceHandler;
    addToQueue("clean up resource cache",
        messageCollector -> {
          for (IResourceCacheConfiguration configuration : configurations) {
            if (!configuration.isCleanUpOnStartEnabled()) {
              continue;
            }
            final Duration maximumAgeOnStartupTime = configuration.getMaximumAgeOnStartupTime();
            final ILifeTime lifeTime = configuration.getCachingRule().getPreferedLifeTime();
            final Instant currentTime = Instant.now(Clock.systemDefaultZone());
            final List<Path> filesToDelete = new ArrayList<>();
            configuration.getCachingFolder()
                .convert(folder -> folder.toPath())
                .consume(path -> {
                  try {
                    if (!Files.exists(path)) {
                      Files.createDirectories(path);
                      return;
                    }
                    Files.walkFileTree(path, new SimpleFileVisitor<Path>() {

                      @Override
                      public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs)
                          throws IOException {
                        final Duration age = getAge(lifeTime.getStartPointOfTimeMeasuring(), attrs, currentTime);
                        if (age.toMillis() > maximumAgeOnStartupTime.toMillis()) {
                          filesToDelete.add(file);
                        }
                        return FileVisitResult.CONTINUE;
                      }
                    });
                    filesToDelete.forEach(f -> {
                      try {
                        Files.delete(f);
                      } catch (IOException exception) {
                        logger.log(ILevel.DEBUG, exception.getMessage(), exception);
                      }
                    });
                  } catch (IOException exception) {
                    logger.log(ILevel.DEBUG, exception.getMessage(), exception);
                  }
                });
          }
        });
    addToQueue("clean up resource cache",
        messageCollector -> {
          final Set<Entry<Object, List<IResourceCacheObject>>> entrySet = this.cachedObjects.entrySet();
          for (final Entry<Object, List<IResourceCacheObject>> entry : entrySet) {
            final List<IResourceCacheObject> objects = entry.getValue();
            if (shouldBeRemoved(objects)) {
              this.cachedObjects.remove(entry.getKey());
              objects.forEach(o -> remove(o));
            }
          }
        },
        Duration.ofMinutes(1));
  }

  @Override
  public IResourceReference put(final Object key,
      final byte[] data,
      final String contentType,
      final String charset,
      final IProperties properties) {
    return getConfiguration(key, contentType)
        .convert(
            configuration -> put(configuration,
                configuration.getCachingRule(),
                key,
                data,
                contentType,
                charset,
                properties))
        .getOr(() -> this.resourceReferenceFactory.create(data, contentType, charset));
  }

  @Override
  public IResourceReference put(final ICachingRule cachingRule,
      final Object key,
      final byte[] data,
      final String contentType,
      final String charset,
      final IProperties properties) {
    return getConfiguration(cachingRule, key, contentType)
        .convert(configuration -> put(configuration, cachingRule, key, data, contentType, charset, properties))
        .getOr(() -> this.resourceReferenceFactory.create(data, contentType, charset));
  }

  private IResourceReference put(final IResourceCacheConfiguration configuration,
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
    CacheStorage cacheStorage = getCacheStorage(cachingRule, configuration);
    if (Objects.equals(CacheStorage.MEMORY_WEAK, cacheStorage)) {
      put(key,
          new WeakResourceCacheObject(key,
              cachingRule.adapt(cacheStorage),
              resourceReference,
              contentType,
              charset,
              properties));
      return resourceReference;
    }
    if (Objects.equals(CacheStorage.MEMORY_STATIC, cacheStorage)) {
      put(key,
          new ResourceCacheObject(key,
              cachingRule.adapt(cacheStorage),
              resourceReference,
              contentType,
              charset,
              properties));
      return resourceReference;
    }
    final ObjectContainer<IResourceReference> resourceReferenceContainer = new ObjectContainer<>(resourceReference);
    put(key,
        new ResourceCacheObject(key,
            cachingRule.adapt(cacheStorage),
            new ResourceReferenceWrapper(resourceReferenceContainer),
            contentType,
            charset,
            properties));
    store(configuration, data, contentType, resourceReferenceContainer);
    return resourceReference;
  }

  protected void store(final IResourceCacheConfiguration configuration,
      final byte[] data,
      final String contentType,
      final ObjectContainer<IResourceReference> resourceReferenceContainer) {
    addToQueue("Store resource into " + configuration.getName() + " cache",
        messageCollector -> {
          try {
            create(configuration, contentType).consume(targetFile -> {
              try (final ByteArrayInputStream in = new ByteArrayInputStream(data)) {
                try (final FileOutputStream out = new FileOutputStream(targetFile)) {
                  IoUtilities.pipe(in, out);
                }
                resourceReferenceContainer.set(this.resourceReferenceFactory.create(targetFile));
              }
            }).throwIfFaild();
          } catch (IOException exception) {
            logger.log(ILevel.DEBUG, exception.getMessage(), exception);
          }
        });
  }

  private IResourceCacheObject put(
      final Object key,
      final IResourceCacheObject object) {
    synchronized (this.cachedObjects) {
      this.cachedObjects.put(key, new LinkedList<IResourceCacheObject>());
      this.cachedObjects.get(key).add(object);
      return object;
    }
  }

  @Override
  public IResourceReference add(
      final Object key,
      final byte[] data,
      final String contentType,
      final String charset,
      final IProperties properties) {
    return getConfiguration(key, contentType)
        .convert(
            configuration -> add(configuration,
                configuration.getCachingRule(),
                key,
                data,
                contentType,
                charset,
                properties))
        .getOr(() -> this.resourceReferenceFactory.create(data, contentType, charset));
  }

  @Override
  public IResourceReference add(
      final ICachingRule cachingRule,
      final Object key,
      final byte[] data,
      final String contentType,
      final String charset,
      final IProperties properties) {
    return getConfiguration(cachingRule, key, contentType)
        .convert(configuration -> add(configuration, cachingRule, key, data, contentType, charset, properties))
        .getOr(() -> this.resourceReferenceFactory.create(data, contentType, charset));
  }

  private IResourceReference add(
      final IResourceCacheConfiguration configuration,
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
    CacheStorage cacheStorage = getCacheStorage(cachingRule, configuration);
    if (Objects.equals(CacheStorage.MEMORY_WEAK, cacheStorage)) {
      add(key,
          new WeakResourceCacheObject(key,
              cachingRule.adapt(cacheStorage),
              resourceReference,
              contentType,
              charset,
              properties));
      return resourceReference;
    }
    if (Objects.equals(CacheStorage.MEMORY_STATIC, cacheStorage)) {
      add(key,
          new ResourceCacheObject(key,
              cachingRule.adapt(cacheStorage),
              resourceReference,
              contentType,
              charset,
              properties));
      return resourceReference;
    }
    final ObjectContainer<IResourceReference> resourceReferenceContainer = new ObjectContainer<>(resourceReference);
    add(key,
        new ResourceCacheObject(key,
            cachingRule.adapt(cacheStorage),
            new ResourceReferenceWrapper(resourceReferenceContainer),
            contentType,
            charset,
            properties));
    store(configuration, data, contentType, resourceReferenceContainer);
    return resourceReference;
  }

  private CacheStorage getCacheStorage(final ICachingRule cachingRule,
      final IResourceCacheConfiguration configuration) {
    final CacheStorage configuredCacheStorage = configuration.getCachingRule().getCacheStorage();
    final CacheStorage cacheStorage = cachingRule.getCacheStorage();
    if ((Objects.equals(CacheStorage.FILE_SYSTEM, cacheStorage) ||
        Objects.equals(CacheStorage.FILE_SYSTEM, configuredCacheStorage))) {
      if (configuration.getCachingFolder().isAccepted()) {
        return CacheStorage.FILE_SYSTEM;
      } else {
        return CacheStorage.MEMORY_STATIC;
      }
    }
    return cachingRule.getMinimumLifeTime()
        .convert(t -> CacheStorage.MEMORY_STATIC)
        .getOr(() -> cacheStorage);
  }

  private IOptional<File, IOException> create(
      final IResourceCacheConfiguration configuration,
      final String contentType) {
    final String name = UUID.randomUUID().toString();
    final String extension = configuration.getExtensionFor(contentType);
    return configuration.getCachingFolder().convert(folder -> new File(folder, name + "." + extension));
  }

  private IResourceCacheObject add(
      final Object key,
      final IResourceCacheObject object) {
    synchronized (this.cachedObjects) {
      this.cachedObjects.putIfAbsent(key, new LinkedList<IResourceCacheObject>());
      this.cachedObjects.get(key).add(object);
      return object;
    }
  }

  @Override
  public IOptional<IResourceReference, RuntimeException> getResourceReference(final Object key) {
    final List<IResourceReference> references = getResourceReferences(key);
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
    final IResourceCacheListResult objects = getObjects(key);
    if (objects.isEmpty()) {
      return ResourceCacheResult.empty();
    }
    return ResourceCacheResult.of(objects.toStream().first().get(), objects.isExpired());
  }

  @Override
  public IResourceCacheListResult getObjects(final Object key) {
    try {
      final List<IResourceCacheObject> list = this.cachedObjects.get(key);
      if (list == null || list.isEmpty()) {
        return ResourceCacheListResult.empty();
      }
      final Instant currentTime = Instant.now();
      final List<IResourceCacheObject> result = new ArrayList<>();
      boolean isExpired = false;
      for (final IResourceCacheObject object : list) {
        final IProperties properties = object.getProperties();
        if (properties == null) {
          remove(key);
          return ResourceCacheListResult.empty();
        }
        final IResourceReference resourceReference = object.getResourceReference();
        final ICachingRule cachingRule = object.getCachingRule();
        final IResourceCacheConfiguration configuration =
            getConfiguration(cachingRule, key, object.getContentType()).get();
        final ObjectPair<Boolean, ResourceAge> expired = isExpired(
            configuration,
            resourceReference,
            cachingRule,
            currentTime);
        isExpired = isExpired && expired.getFirstObject();
        if (isExpired) {
          if (shouldBeRemoved(cachingRule, expired.getSecondObject(), currentTime)) {
            remove(key);
            return ResourceCacheListResult.empty();
          }
          if (!canBeUsed(cachingRule, expired.getSecondObject(), configuration, currentTime)) {
            return ResourceCacheListResult.empty();
          }
        }
        result.add(object instanceof WeakResourceCacheObject
            ? new ResourceCacheObject(key,
                cachingRule,
                resourceReference,
                object.getContentType(),
                object.getCharset(),
                properties)
            : object);
      }
      return ResourceCacheListResult.of(result, isExpired);
    } catch (NullPointerException exception) {
      remove(key);
      return ResourceCacheListResult.empty();
    }
  }

  private boolean
      canBeUsed(final ICachingRule cachingRule,
          final ResourceAge age,
          final IResourceCacheConfiguration configuration,
          final Instant currentTime) {
    return !(age == null
        || cachingRule.getMaximumLifeTime()
            .convert(lifeTime -> age.getAge(currentTime, lifeTime.getStartPointOfTimeMeasuring()).toMillis()
                > lifeTime.getDuration()
                    .minus(configuration.getLatencyTime())
                    .toMillis())
            .getOr(() -> false));
  }

  private boolean shouldBeRemoved(final List<IResourceCacheObject> list) {
    if (list == null || list.isEmpty()) {
      return true;
    }
    final Instant currentTime = Instant.now(Clock.systemDefaultZone());
    for (final IResourceCacheObject object : list) {
      if (object.getProperties() == null) {
        return true;
      }
      ObjectPair<Boolean, ResourceAge> expired =
          isExpired(getConfiguration(object.getCachingRule(), object.getKey(), object.getContentType()).get(),
              object,
              currentTime);
      if (shouldBeRemoved(object.getCachingRule(), expired.getSecondObject(), currentTime)) {
        return true;
      }
    }
    return false;
  }

  private boolean
      shouldBeRemoved(final ICachingRule cachingRule, final ResourceAge resourceAge, final Instant currentTime) {
    if (resourceAge == null) {
      return true;
    }
    final boolean isExpired = isExpired(resourceAge, cachingRule.getPreferedLifeTime(), currentTime);
    if (cachingRule.getMaximumLifeTime().isEmpty()
        && cachingRule.getMinimumLifeTime().isEmpty()
        && isExpired) {
      return true;
    }
    if (cachingRule.getMaximumLifeTime()
        .convert(lifeTime -> isExpired(resourceAge, lifeTime, currentTime))
        .getOr(() -> false)) {
      return true;
    }
    if (cachingRule.getMinimumLifeTime().isEmpty()
        && isExpired) {
      return true;
    }
    return (cachingRule.getMinimumLifeTime()
        .convert(lifeTime -> (isExpired(resourceAge, lifeTime, currentTime)
            && cachingRule.getCacheStorage().isMemory()))
        .getOr(() -> false)
        || isExpired);
  }

  protected boolean isExpired(final ResourceAge resourceAge, final ILifeTime lifeTime, final Instant currentTime) {
    return resourceAge.getAge(currentTime, lifeTime.getStartPointOfTimeMeasuring()).toMillis()
        > lifeTime.getDuration().toMillis();
  }

  private ObjectPair<Boolean, ResourceAge> isExpired(final IResourceCacheConfiguration configuration,
      final IResourceCacheObject object,
      final Instant currentTime) {
    return isExpired(configuration,
        object.getResourceReference(),
        object.getCachingRule(),
        currentTime);
  }

  private ObjectPair<Boolean, ResourceAge> isExpired(final IResourceCacheConfiguration configuration,
      final IResourceReference resourceReference,
      final ICachingRule cachingRule,
      final Instant currentTime) {
    if (resourceReference == null) {
      return ObjectPair.of(true, null);
    }
    if (!this.resourceReferenceHandler.exists(resourceReference)) {
      return ObjectPair.of(true, null);
    }
    final ResourceAge fileAge = new ResourceAge(this.resourceReferenceHandler, resourceReference);
    return cachingRule.getMaximumLifeTime()
        .convert(lifeTime -> isExpired(configuration, fileAge, lifeTime, currentTime))
        .getOr(() -> cachingRule.getMinimumLifeTime()
            .convert(lifeTime -> isExpired(configuration, fileAge, lifeTime, currentTime))
            .getOr(() -> isExpired(configuration, fileAge, cachingRule.getPreferedLifeTime(), currentTime)));
  }

  private ObjectPair<Boolean, ResourceAge> isExpired(final IResourceCacheConfiguration configuration,
      final ResourceAge fileAge,
      final ILifeTime lifeTime,
      final Instant currentTime) {
    final Duration age = fileAge.getAge(currentTime, lifeTime.getStartPointOfTimeMeasuring());
    return ObjectPair.of(
        age.toMillis() > lifeTime.getDuration().minus(configuration.getLatencyTime()).toMillis(),
        fileAge);
  }

  private void remove(final IResourceCacheObject object) {
    try {
      final IResourceReference resourceReference = object.getResourceReference();
      if (this.resourceReferenceHandler.canDelete(resourceReference)) {
        this.resourceReferenceHandler.delete(resourceReference);
      }
    } catch (IOException exception) {
      logger.log(ILevel.DEBUG, exception.getMessage(), exception);
    }
  }

  @Override
  public void remove(final Object key) {
    final List<IResourceCacheObject> objects = this.cachedObjects.get(key);
    this.cachedObjects.remove(key);
    if (objects == null || objects.isEmpty()) {
      return;
    }
    addToQueue("Remove resource from resource cache",
        messageCollector -> {
          objects.forEach(o -> remove(o));
        });
  }

  @Override
  public void clear() {
    final List<IResourceCacheObject> objects = this.cachedObjects
        .values()
        .stream()
        .flatMap(l -> l.stream())
        .collect(Collectors.toList());
    this.cachedObjects.clear();
    objects.forEach(o -> remove(o));
  }

  private Duration getAge(
      final ResourceAccessEvent pointOfTime,
      final BasicFileAttributes attrs,
      final Instant currentTime) {
    final FileTime time = getTime(pointOfTime, attrs);
    return Duration.between(time.toInstant(), currentTime);
  }

  private FileTime getTime(final ResourceAccessEvent pointOfTime, final BasicFileAttributes attributes) {
    switch (pointOfTime) {
      case CREATED: {
        return attributes.creationTime();
      }
      case LAST_MODIFIED: {
        return attributes.lastModifiedTime();
      }
      case LAST_ACCESS: {
        return attributes.lastAccessTime();
      }
    }
    return FileTime.from(0, TimeUnit.SECONDS);
  }

  private IOptional<IResourceCacheConfiguration, RuntimeException> getConfiguration(final ICachingRule cachingRule,
      final Object key,
      final String contentType) {
    return Optional
        .of(getConfiguration(key, contentType)
            .getOr(() -> !Objects.equals(cachingRule.getCacheStorage(), CacheStorage.FILE_SYSTEM)
                ? this.defaultInMemoryCacheConfiguration.isApplicable(key, contentType)
                    ? this.defaultInMemoryCacheConfiguration : null
                : null));
  }

  private IOptional<IResourceCacheConfiguration, RuntimeException> getConfiguration(final Object key,
      final String contentType) {
    return Streams
        .of(this.configurations)
        .first(c -> c.isApplicable(key, contentType));
  }

  private void addToQueue(final String description, final Consumer<IMessageCollector> executor) {
    addToQueue(description, executor, null);
  }

  private void addToQueue(final String description, final Consumer<IMessageCollector> executor, final Duration delay) {
    this.processManager.execute(new ProcessBuilder()
        .setQueueName(IQueueNameConstans.CACHE_WRITER_QUEUE)
        .setDescription(description)
        .setCancelable(false)
        .setDelay(delay)
        .setExecutable((monitor, canceler, processIdentfier) -> executor.accept(monitor))
        .build());
  }
}
