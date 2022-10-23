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
package net.anwiba.commons.http;

import net.anwiba.commons.cache.resource.IResourceCache;
import net.anwiba.commons.http.CachingHttpRequestExecutor.StaticResponse;
import net.anwiba.commons.reference.IResourceReferenceHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

public class CachingHttpResquestExecutorFactory implements IHttpRequestExecutorFactory {

  private final Map<HttpRequestCacheKey, Future<StaticResponse>> registeredRequests = new ConcurrentHashMap<>();
  private final IHttpRequestExecutorFactory httpRequestExecutorFactory;
  private final IResourceReferenceHandler resourceReferenceHandler;
  private final IResourceCache cache;

  public CachingHttpResquestExecutorFactory(
      final IResourceCache cache,
      final IResourceReferenceHandler resourceReferenceHandler,
      final IHttpRequestExecutorFactory httpRequestExecutorFactory) {
    this.cache = cache;
    this.resourceReferenceHandler = resourceReferenceHandler;
    this.httpRequestExecutorFactory = httpRequestExecutorFactory;
  }

  @Override
  public IHttpRequestExecutor create() {
    return new CachingHttpRequestExecutor(
        this.cache,
        this.registeredRequests,
        this.resourceReferenceHandler,
        this.httpRequestExecutorFactory.create());
  }

}
