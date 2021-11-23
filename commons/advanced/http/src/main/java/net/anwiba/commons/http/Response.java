/*
 * #%L
 * anwiba commons advanced
 * %%
 * Copyright (C) 2007 - 2016 Andreas Bartels
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

import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.hc.client5.http.classic.methods.HttpUriRequest;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpEntityContainer;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.URIScheme;
import org.apache.hc.core5.net.URIAuthority;

import jakarta.activation.MimeType;
import jakarta.activation.MimeTypeParameterList;
import jakarta.activation.MimeTypeParseException;
import net.anwiba.commons.lang.optional.IOptional;
import net.anwiba.commons.lang.optional.Optional;
import net.anwiba.commons.lang.stream.Streams;
import net.anwiba.commons.reference.utilities.IoUtilities;
import net.anwiba.commons.thread.cancel.ICanceler;
import net.anwiba.commons.utilities.string.StringUtilities;

public final class Response implements IResponse {

  private final HttpResponse response;
  private final ICanceler cancelable;
  private final HttpUriRequest request;
  private final Closeable client;
  private byte[] body;

  public Response(
      final ICanceler cancelable,
      final Closeable client,
      final HttpUriRequest request,
      final HttpResponse response) {
    this.cancelable = cancelable;
    this.client = client;
    this.request = request;
    this.response = response;
  }

  @Override
  public IOptional<List<String>, RuntimeException> cacheControl() {
    final Header[] headers = this.response.getHeaders("Cache-control");
    return headers == null || headers.length == 0
        ? Optional.empty()
        : Optional.of(Streams.of(headers).convert(h -> h.getValue()).asList());
  }

  @Override
  public IOptional<String, RuntimeException> expires() {
    final Header header = this.response.getFirstHeader("Expires");
    return header == null
        ? Optional.empty()
        : Optional.of(header.getValue());
  }

  @Override
  public IOptional<String, RuntimeException> pragma() {
    final Header header = this.response.getFirstHeader("Pragma");
    return header == null
        ? Optional.empty()
        : Optional.of(header.getValue());
  }

  @Override
  public String getUri() {
    final StringBuilder builder = new StringBuilder();
    final URIAuthority authority = this.request.getAuthority();
    if (authority != null) {
      final String scheme = this.request.getScheme();
      builder.append(scheme != null ? scheme : URIScheme.HTTP.id).append("://");
      builder.append(authority.getHostName());
      if (authority.getPort() >= 0) {
        builder.append(":").append(authority.getPort());
      }
    }
    final String path = this.request.getPath();
    if (path == null) {
      builder.append("/");
    } else {
      if (builder.length() > 0 && !path.startsWith("/")) {
        builder.append("/");
      }
      builder.append(path);
    }
    return builder.toString();
  }

  @Override
  public String getBody() throws IOException {
    if (body != null) {
      return new String(body, getContentEncoding());
    }
    try (InputStream inputStream = getInputStream()) {
      body = IoUtilities.toByteArray(inputStream);
      return new String(body, getContentEncoding());
    }
  }

  @Override
  public int getStatusCode() {
    return Optional.of(this.response)
        .convert(r -> r.getCode())
        .getOr(() -> 404).intValue();
  }

  @Override
  public String getStatusText() {
    return Optional.of(this.response)
        .convert(r -> r.getReasonPhrase())
        .getOr(() -> StatusCodes.getPhrase(getStatusCode())); //$NON-NLS-1$
  }

  @Override
  public InputStream getInputStream() throws IOException {
    if (body != null) {
      return new CancelableInputStream(
          this.cancelable,
          new ByteArrayInputStream(body));
    }
    return new CancelableInputStream(
        this.cancelable,
        Optional
            .of(IOException.class, this.response)
            .instanceOf(HttpEntityContainer.class)
            .convert(r -> r.getEntity())
            .convert(e -> e.getContent())
            .getOrThrow(() -> new IOException()));
  }

  @Override
  public long getContentLength() {
    return Optional
        .of(this.response)
        .instanceOf(HttpEntityContainer.class)
        .convert(r -> r.getEntity())
        .convert(e -> e.getContentLength())
        .getOr(() -> 0l)
        .longValue();
  }

  @Override
  public String getContentType() {
    return Optional
        .of(this.response)
        .instanceOf(HttpEntityContainer.class)
        .convert(r -> r.getEntity())
        .convert(e -> e.getContentType())
        .get();
  }

  @Override
  public String getContentEncoding() {
    return Optional
        .of(this.response)
        .instanceOf(HttpEntityContainer.class)
        .convert(r -> r.getEntity())
        .convert(e -> e.getContentEncoding())
        .getOr(() -> getContentEncodingfromContentType());
  }

  private String getContentEncodingfromContentType() {
    try {
      final String contentType = getContentType();
      if (StringUtilities.isNullOrTrimmedEmpty(contentType)) {
        return "UTF-8"; //$NON-NLS-1$
      }
      final MimeType mimeType = new MimeType(contentType);
      final MimeTypeParameterList parameters = mimeType.getParameters();
      final String charset = parameters.get("charset"); //$NON-NLS-1$
      if (StringUtilities.isNullOrTrimmedEmpty(charset)) {
        return "UTF-8"; //$NON-NLS-1$
      }
      return charset;
    } catch (final MimeTypeParseException exception) {
      return "UTF-8"; //$NON-NLS-1$
    }
  }

  @Override
  public void close() throws IOException {
    IOException exception = null;
    if (this.response instanceof CloseableHttpResponse) {
      exception = IoUtilities.close(() -> ((CloseableHttpResponse) this.response).close(), exception);
    }
    exception = IoUtilities.close(this.client, exception);
    IoUtilities.toss(exception);
  }

  @Override
  public void abort() {
    this.request.abort();
  }
}
