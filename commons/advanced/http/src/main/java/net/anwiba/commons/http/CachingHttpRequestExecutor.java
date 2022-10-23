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

import net.anwiba.commons.cache.resource.CachingRule;
import net.anwiba.commons.cache.resource.IResourceCache;
import net.anwiba.commons.lang.exception.CanceledException;
import net.anwiba.commons.lang.exception.CreationException;
import net.anwiba.commons.lang.functional.IBlock;
import net.anwiba.commons.lang.functional.IObserver;
import net.anwiba.commons.lang.optional.IOptional;
import net.anwiba.commons.lang.parameter.IParameters;
import net.anwiba.commons.logging.ILogger;
import net.anwiba.commons.logging.Logging;
import net.anwiba.commons.reference.IResourceReference;
import net.anwiba.commons.reference.IResourceReferenceHandler;
import net.anwiba.commons.reference.ResourceReferenceFactory;
import net.anwiba.commons.reference.url.IUrl;
import net.anwiba.commons.reference.url.builder.UrlBuilder;
import net.anwiba.commons.reference.url.parser.UrlParser;
import net.anwiba.commons.reference.utilities.IoUtilities;
import net.anwiba.commons.thread.cancel.ICanceler;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

public class CachingHttpRequestExecutor implements IHttpRequestExecutor {

  private static ILogger logger = Logging.getLogger(CachingHttpRequestExecutor.class);

  public static final class StaticResponse implements IResponse {
    private final int statusCode;
    private final IResourceReference resourceReference;
    private final String url;
    private final String statusTest;
    private final IResourceReferenceHandler resourceReferenceHandler;
    private final IBlock<RuntimeException> abortBlock;

    public StaticResponse(final IResourceReferenceHandler resourceReferenceHandler,
        final IBlock<RuntimeException> abortBlock,
        final String url,
        final IResourceReference resourceReference,
        final int statusCode,
        final String statusTest) {
      this.resourceReferenceHandler = resourceReferenceHandler;
      this.abortBlock = abortBlock;
      this.statusCode = statusCode;
      this.resourceReference = resourceReference;
      this.url = url;
      this.statusTest = statusTest;
    }

    public IResourceReference getResourceReference() {
      return this.resourceReference;
    }

    @Override
    public String getUri() {
      return this.url;
    }

    @Override
    public String getStatusText() {
      return this.statusTest;
    }

    @Override
    public int getStatusCode() {
      return this.statusCode;
    }

    @Override
    public InputStream getInputStream() throws IOException {
      return this.resourceReferenceHandler.openInputStream(this.resourceReference);
    }

    @Override
    public String getContentType() {
      return this.resourceReferenceHandler.getContentType(this.resourceReference);
    }

    @Override
    public long getContentLength() {
      return this.resourceReferenceHandler.getContentLength(this.resourceReference);
    }

    @Override
    public String getContentEncoding() {
      return "UTF-8";
    }

    @Override
    public String getBody() throws IOException {
      return this.resourceReferenceHandler.toString(this.resourceReference);
    }

    @Override
    public void close() throws IOException {
      // nothing todo
    }

    @Override
    public void abort() {
      this.abortBlock.execute();
    }
  }

  private final IHttpRequestExecutor httpRequestExecutor;
  private final IResourceCache cache;
  private final IResourceReferenceHandler resourceReferenceHandler;
  private final Map<HttpRequestCacheKey, Future<StaticResponse>> registeredRequests;

  public CachingHttpRequestExecutor(
      final IResourceCache cache,
      final Map<HttpRequestCacheKey, Future<StaticResponse>> registeredRequests,
      final IResourceReferenceHandler resourceReferenceHandler,
      final IHttpRequestExecutor httpRequestExecutor) {
    this.cache = cache;
    this.registeredRequests = registeredRequests;
    this.resourceReferenceHandler = resourceReferenceHandler;
    this.httpRequestExecutor = httpRequestExecutor;
  }

  @Override
  public IResponse execute(final ICanceler cancelable, final IRequest request) throws CanceledException, IOException {
    String url = createUrl(request.getUriString(), request.getParameters());
    IOptional<HttpRequestCacheKey, RuntimeException> key = HttpRequestCacheKey.of(request);
    IOptional<IResourceReference, RuntimeException> optional = get(key);
    if (optional.isEmpty()) {
      try {
        Callable<StaticResponse> callable = () -> {
          try (IResponse response = CachingHttpRequestExecutor.this.httpRequestExecutor.execute(cancelable, request)) {
            try (IObserver watcher = cancelable.observerFactory().create(() -> {
              response.abort();
            })) {
              final int statusCode = response.getStatusCode();
              final String statusText = response.getStatusText();
              if (!(statusCode >= 200 && statusCode < 300) || key.isEmpty()) {
                return create(url, reference(response), () -> {}, statusCode, statusText);
              }
              if (!response.cacheControl().isEmpty()) {
                // https://developer.mozilla.org/de/docs/Web/HTTP/Headers/Cache-Control
                // https://developer.mozilla.org/de/docs/Web/HTTP/Caching
                // [private,no-cache,no-store,no-transform,max-age=0]
                String string = response.cacheControl().get().toString();
                string.isBlank();
              } else if (!response.pragma().isEmpty()) {
                // [private,no-cache,no-store,no-transform,max-age=0]
                String string = response.pragma().get().toString();
                string.isBlank();
              }
              if (!response.expires().isEmpty()) {
                String string = response.expires().get();
                string.isBlank();
              }
              try (InputStream stream = response.getInputStream()) {
                byte[] byteArray = IoUtilities.toByteArray(stream);
                IResourceReference reference =
                    request.getCacheTime()
                        .convert(cacheTime -> this.cache.put(
                            CachingRule.builder()
                                .preferdLifeTime(cacheTime)
                                .minimumLifeTime(cacheTime)
                                .maximumLifeTime(cacheTime)
                                .build(),
                            key.get(),
                            byteArray,
                            response.getContentType(),
                            response.getContentEncoding()))
                        .getOr(() -> this.cache.put(
                            key.get(),
                            byteArray,
                            response.getContentType(),
                            response.getContentEncoding()));
                return create(url, reference, () -> {}, statusCode, statusText);
              }
            }
          }
        };
        FutureTask<StaticResponse> future = new FutureTask<>(callable);
        this.registeredRequests.put(key.get(), future);
        future.run();
        return future.get();
      } catch (CancellationException | InterruptedException exception) {
        throw new CanceledException(exception.getMessage(), exception);
      } catch (ExecutionException exception) {
        if (exception.getCause() instanceof CancellationException ce) {
          throw new CanceledException(ce.getMessage(), ce);
        }
        if (exception.getCause() instanceof IOException ioe) {
          throw ioe;
        }
        throw new IOException(exception.getMessage(), exception);
      } finally {
        Future<StaticResponse> future = this.registeredRequests.remove(key.get());
        if (future != null && !future.isDone()) {
          future.cancel(true);
        }
      }
    } else {
      logger.debug("got cached response " + url);
      return create(url, optional.get(), () -> {}, 200, "OK");
    }
  }

  private synchronized IOptional<IResourceReference, RuntimeException>
      get(final IOptional<HttpRequestCacheKey, RuntimeException> key) {
    return key
        .convert(this.cache::getResourceReference)
        .getOr(() -> key
            .convert(this.registeredRequests::get)
            .convert(f -> {
              try {
                final IResourceReference resourceReference = f.get().getResourceReference();
                logger.debug("got reponse from registered future: " + key.get().getUrl());
                return resourceReference;
              } catch (InterruptedException | ExecutionException exception) {
                return null;
              }
            }));
  }

  private IResourceReference reference(final IResponse response) throws IOException {
    if (response.getContentLength() == 0) {
      return new ResourceReferenceFactory()
          .create(new byte[] {}, response.getContentType(), response.getContentEncoding());
    }
    try (InputStream inputStream = response.getInputStream()) {
      return new ResourceReferenceFactory()
          .create(IoUtilities.toByteArray(inputStream), response.getContentType(), response.getContentEncoding());
    }
  }

  private StaticResponse create(final String url,
      final IResourceReference resourceReference,
      final IBlock<RuntimeException> abortBlock,
      final int statusCode,
      final String statusTest) {
    return new StaticResponse(this.resourceReferenceHandler,
        abortBlock,
        url,
        resourceReference,
        statusCode,
        statusTest);
  }

  private String createUrl(final String uriString, final IParameters parameters) {
    try {
      IUrl url = new UrlParser().parse(uriString);
      UrlBuilder builder = new UrlBuilder(url);
      parameters.forEach(p -> builder.addQueryParameter(p));
      return builder.build().toString();
    } catch (CreationException e) {
      return uriString;
    }
  }

  @Override
  public void close() throws IOException {
    this.httpRequestExecutor.close();
  }
}
