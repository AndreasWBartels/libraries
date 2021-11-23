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

import java.util.List;

import net.anwiba.commons.cache.resource.properties.IProperties;
import net.anwiba.commons.lang.optional.IOptional;
import net.anwiba.commons.lang.optional.Optional;
import net.anwiba.commons.reference.IResourceReference;
import net.anwiba.commons.reference.IResourceReferenceFactory;
import net.anwiba.commons.reference.ResourceReferenceFactory;

public class DummyResourceCache implements IResourceCache {

  private final IResourceReferenceFactory resourceReferenceFactory = new ResourceReferenceFactory();

  @Override
  public IResourceReference put(final Object key,
      final byte[] data,
      final String contentType,
      final String charset,
      final IProperties properties) {
    return put(CachingRule.of(), key, data, contentType, charset, properties);
  }

  @Override
  public IResourceReference put(final ICachingRule cachingRule,
      final Object key,
      final byte[] data,
      final String contentType,
      final String charset,
      final IProperties properties) {
    return this.resourceReferenceFactory.create(data, contentType, charset);
  }

  @Override
  public IResourceReference add(
      final Object key,
      final byte[] data,
      final String contentType,
      final String charset,
      final IProperties properties) {
    return add(CachingRule.of(), key, data, contentType, charset, properties);
  }

  @Override
  public IResourceReference add(
      final ICachingRule cachingRule,
      final Object key,
      final byte[] data,
      final String contentType,
      final String charset,
      final IProperties properties) {
    return this.resourceReferenceFactory.create(data, contentType, charset);
  }

  @Override
  public IOptional<IResourceReference, RuntimeException> getResourceReference(final Object key) {
    return Optional.empty();
  }

  @Override
  public List<IResourceReference> getResourceReferences(final Object key) {
    return List.of();
  }

  @Override
  public void clear() {
  }

  @Override
  public IResourceCacheResult getObject(final Object key) {
    return ResourceCacheResult.empty();
  }

  @Override
  public IResourceCacheListResult getObjects(final Object key) {
    return ResourceCacheListResult.empty();
  }
}
