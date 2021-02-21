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

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

import net.anwiba.commons.lang.exception.CanceledException;
import net.anwiba.commons.lang.exception.CreationException;
import net.anwiba.commons.lang.exception.UnreachableCodeReachedException;
import net.anwiba.commons.lang.functional.IBlock;
import net.anwiba.commons.lang.functional.IWatcher;
import net.anwiba.commons.lang.optional.IOptional;
import net.anwiba.commons.reference.IResourceReference;
import net.anwiba.commons.reference.IResourceReferenceHandler;
import net.anwiba.commons.reference.utilities.IoUtilities;
import net.anwiba.commons.thread.cancel.ICanceler;
import net.anwiba.commons.utilities.cache.ICache;
import net.anwiba.commons.utilities.io.url.IUrl;
import net.anwiba.commons.utilities.io.url.UrlBuilder;
import net.anwiba.commons.utilities.io.url.parser.UrlParser;
import net.anwiba.commons.utilities.parameter.IParameters;

public class CachingHttpRequestExecutor implements IHttpRequestExecutor {

  private final IHttpRequestExecutor httpRequestExecutor;
  private final ICache cache;
  private final IResourceReferenceHandler resourceReferenceHandler;

  public CachingHttpRequestExecutor(
      final ICache cache,
      final IResourceReferenceHandler resourceReferenceHandler,
      final IHttpRequestExecutor httpRequestExecutor) {
    this.cache = cache;
    this.resourceReferenceHandler = resourceReferenceHandler;
    this.httpRequestExecutor = httpRequestExecutor;
  }

  @Override
  public IResponse execute(final ICanceler cancelable, final IRequest request) throws CanceledException, IOException {
    String url = createUrl(request.getUriString(), request.getParameters());
    IOptional<IResourceReference, RuntimeException> optional = this.cache.getResourceReference(url);
    if (optional.isEmpty()) {
      try (IResponse response = this.httpRequestExecutor.execute(cancelable, request)) {
        try (IWatcher watcher = cancelable.watcherFactory().create(() -> {
          response.abort();
        })) {
          if (!(response.getStatusCode() >= 200 && response.getStatusCode() < 300)) {
            return response;
          }
          try (InputStream stream = response.getInputStream()) {
            IResourceReference reference =
                this.cache
                    .add(url,
                        IoUtilities.toByteArray(stream),
                        response.getContentType(),
                        response.getContentEncoding());
            return create(url, reference, () -> {
              response.abort();
            });
          }
        }
      }
    } else {
      return create(url, optional.get(), () -> {});
    }
  }

  private IResponse
      create(final String url, final IResourceReference resourceReference, final IBlock<RuntimeException> abortBlock) {
    return new IResponse() {

      @Override
      public URI getUri() {
        try {
          return new URI(url);
        } catch (URISyntaxException e) {
          throw new UnreachableCodeReachedException(e);
        }
      }

      @Override
      public String getStatusText() {
        return "OK";
      }

      @Override
      public int getStatusCode() {
        return 200;
      }

      @Override
      public InputStream getInputStream() throws IOException {
        return CachingHttpRequestExecutor.this.resourceReferenceHandler.openInputStream(resourceReference);
      }

      @Override
      public String getContentType() {
        return CachingHttpRequestExecutor.this.resourceReferenceHandler.getContentType(resourceReference);
      }

      @Override
      public long getContentLength() {
        return CachingHttpRequestExecutor.this.resourceReferenceHandler.getContentLength(resourceReference);
      }

      @Override
      public String getContentEncoding() {
        return "UTF-8";
      }

      @Override
      public String getBody() throws IOException {
        return CachingHttpRequestExecutor.this.resourceReferenceHandler.toString(resourceReference);
      }

      @Override
      public void close() throws IOException {
        // nothing todo
      }

      @Override
      public void abort() {
        abortBlock.execute();
      }
    };
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
