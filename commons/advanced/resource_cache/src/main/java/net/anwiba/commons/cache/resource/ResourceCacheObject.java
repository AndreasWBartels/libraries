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
package net.anwiba.commons.cache.resource;

import java.io.Serializable;

import net.anwiba.commons.cache.resource.properties.IProperties;
import net.anwiba.commons.reference.IResourceReference;

public final class ResourceCacheObject implements IResourceCacheObject {
  private final Object key;
  private final ICachingRule cachingRule;
  private final IResourceReference resourceReference;
  private final String contentType;
  private final String charset;
  private IProperties properties;

  public ResourceCacheObject(final Object key,
      final ICachingRule lifeTime,
      final IResourceReference resourceReference,
      final String contentType,
      final String charset,
      final IProperties properties) {
    this.key = key;
    this.cachingRule = lifeTime;
    this.resourceReference = resourceReference;
    this.properties = properties;
    this.contentType = contentType;
    this.charset = charset;

  }

  @Override
  public ICachingRule getCachingRule() {
    return this.cachingRule;
  }

  @Override
  public Object getKey() {
    return this.key;
  }

  @Override
  public IResourceReference getResourceReference() {
    return this.resourceReference;
  }

  @SuppressWarnings("unchecked")
  public <T extends Serializable> T getPropertyValue(String name) {
    return (T) properties.getValue(name);
  }

  @Override
  public IProperties getProperties() {
    return properties;
  }

  @Override
  public String getContentType() {
    return contentType;
  }

  @Override
  public String getCharset() {
    return charset;
  }
}
