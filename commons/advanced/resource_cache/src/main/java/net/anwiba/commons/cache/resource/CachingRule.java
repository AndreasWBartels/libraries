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

import java.time.Duration;

import net.anwiba.commons.lang.optional.IOptional;
import net.anwiba.commons.lang.optional.Optional;

public class CachingRule implements ICachingRule {

  public static class Builder {

    private ILifeTime preferdLifeTime;
    private CacheStorage preferedCacheStorage;

    private ILifeTime minimumLifeTime = null;
    private ILifeTime maximumLifeTime = null;

    Builder() {
    }

    public Builder preferedCacheStorage(CacheStorage preferedCacheStorage) {
      this.preferedCacheStorage = preferedCacheStorage;
      return this;
    }

    public Builder preferdLifeTime(ILifeTime preferdLifeTime) {
      this.preferdLifeTime = preferdLifeTime;
      return this;
    }

    public Builder minimumLifeTime(ILifeTime minimumLifeTime) {
      this.minimumLifeTime = minimumLifeTime;
      return this;
    }

    public Builder maximumLifeTime(ILifeTime maximumLifeTime) {
      this.maximumLifeTime = maximumLifeTime;
      return this;
    }

    public ICachingRule build() {
      return new CachingRule(createPreferedLifeTime(),
          Optional.of(preferedCacheStorage)
              .getOr(() -> Optional.of(minimumLifeTime)
                  .convert(t -> CacheStorage.MEMORY_STATIC)
                  .getOr(() -> CacheStorage.MEMORY_WEAK)),
          minimumLifeTime,
          maximumLifeTime);
    }

    private ILifeTime createPreferedLifeTime() {
      return LifeTime.of(
          Optional.of(preferdLifeTime).convert(t -> t.getDuration()) .getOr(() -> Duration.ofMinutes(5)),
          Optional.of(preferdLifeTime).convert(t -> t.getStartPointOfTimeMeasuring()).getOr(() -> ResourceAccessEvent.CREATED));
    }
  }

  public static ICachingRule of() {
    return builder().build();
  }

  public static ICachingRule of(ILifeTime preferedLifeTime) {
    return of(preferedLifeTime, null, null, null);
  }

  public static ICachingRule of(ILifeTime preferedLifeTime, CacheStorage preferedCacheStorage) {
    return of(preferedLifeTime, preferedCacheStorage, null, null);
  }

  public static ICachingRule of(ILifeTime preferedLifeTime,
      CacheStorage preferedCacheStorage,
      ILifeTime minimumLifeTime,
      ILifeTime maximumLifeTime) {
    return builder()
        .preferdLifeTime(preferedLifeTime)
        .preferedCacheStorage(preferedCacheStorage)
        .minimumLifeTime(minimumLifeTime)
        .minimumLifeTime(maximumLifeTime)
        .build();
  }

  public static Builder builder() {
    return new Builder();
  }

  private ILifeTime preferedLifeTime;
  private ILifeTime minimumLifeTime;
  private ILifeTime maximumLifeTime;
  private CacheStorage cacheStorage;

  CachingRule(ILifeTime preferedLifeTime,
      CacheStorage cacheStorage,
      ILifeTime minimumLifeTime,
      ILifeTime maximumLifeTime) {
    this.preferedLifeTime = preferedLifeTime;
    this.minimumLifeTime = minimumLifeTime;
    this.maximumLifeTime = maximumLifeTime;
    this.cacheStorage = cacheStorage;
  }

  @Override
  public ILifeTime getPreferedLifeTime() {
    return this.preferedLifeTime;
  }

  @Override
  public CacheStorage getCacheStorage() {
    return this.cacheStorage;
  }

  @Override
  public IOptional<ILifeTime, RuntimeException> getMinimumLifeTime() {
    return Optional.of(this.minimumLifeTime);
  }

  @Override
  public IOptional<ILifeTime, RuntimeException> getMaximumLifeTime() {
    return Optional.of(this.maximumLifeTime);
  }

  @Override
  public ICachingRule adapt(CacheStorage storage) {
    return new CachingRule(preferedLifeTime, storage, minimumLifeTime, maximumLifeTime);
  }
}
