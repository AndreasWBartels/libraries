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

import java.util.List;

import net.anwiba.commons.cache.resource.properties.IProperties;
import net.anwiba.commons.cache.resource.properties.Properties;
import net.anwiba.commons.lang.optional.IOptional;
import net.anwiba.commons.reference.IResourceReference;

public interface IResourceCache {

  default IResourceReference put(final Object key, final byte[] data, final String contentType, final String charset) {
    return put(key, data, contentType, charset, Properties.empty());
  }

  IResourceReference put(Object key, byte[] data, String contentType, String charset, IProperties properties);

  default IResourceReference put(final ICachingRule cachingRule,
      final Object key,
      final byte[] data,
      final String contentType,
      final String charset) {
    return put(cachingRule, key, data, contentType, charset, Properties.empty());
  }

  IResourceReference put(ICachingRule cachingRule,
      Object key,
      byte[] data,
      String contentType,
      String charset,
      IProperties properties);

  default IResourceReference add(final Object key, final byte[] data, final String contentType, final String charset) {
    return add(key, data, contentType, charset, Properties.empty());
  }

  IResourceReference add(Object key, byte[] data, String contentType, String charset, IProperties properties);

  default IResourceReference add(final ICachingRule cachingRule,
      final Object key,
      final byte[] data,
      final String contentType,
      final String charset) {
    return add(cachingRule, key, data, contentType, charset, Properties.empty());
  }

  IResourceReference add(ICachingRule cachingRule,
      Object key,
      byte[] data,
      String contentType,
      String charset,
      IProperties properties);

  IOptional<IResourceReference, RuntimeException> getResourceReference(Object key);

  List<IResourceReference> getResourceReferences(Object key);

  IResourceCacheResult getObject(Object key);

  IResourceCacheListResult getObjects(Object key);

  default void remove(final Object key) {

  }

  void clear();

}
