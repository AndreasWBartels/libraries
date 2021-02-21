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

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import javax.activation.MimeType;
import javax.activation.MimeTypeParameterList;
import javax.activation.MimeTypeParseException;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;

import net.anwiba.commons.lang.optional.Optional;
import net.anwiba.commons.reference.utilities.IoUtilities;
import net.anwiba.commons.thread.cancel.ICanceler;
import net.anwiba.commons.utilities.string.StringUtilities;

public final class Response implements IResponse {

  private final HttpResponse response;
  private final ICanceler cancelable;
  private final HttpUriRequest request;
  private final Closeable client;

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
  public URI getUri() {
    return this.request.getURI();
  }

  @Override
  public String getBody() throws IOException {
    try (InputStream inputStream = getInputStream()) {
      return IoUtilities.toString(inputStream, getContentEncoding());
    }
  }

  @Override
  public int getStatusCode() {
    return Optional.of(this.response).convert(r -> r.getStatusLine()).convert(l -> l.getStatusCode()).getOr(() -> 404);
  }

  @Override
  public String getStatusText() {
    return Optional.of(this.response).convert(r -> r.getStatusLine()).convert(l -> l.getReasonPhrase()).getOr(() -> ""); //$NON-NLS-1$
  }

  @Override
  public InputStream getInputStream() throws IOException {
    return new CancelableInputStream(
        this.cancelable,
        Optional
            .of(IOException.class, this.response)
            .convert(r -> r.getEntity())
            .convert(e -> e.getContent())
            .getOrThrow(() -> new IOException()));
  }

  @Override
  public long getContentLength() {
    return Optional.of(this.response).convert(r -> r.getEntity()).convert(e -> e.getContentLength()).getOr(() -> 0l);
  }

  @Override
  public String getContentType() {
    return Optional
        .of(this.response)
        .convert(r -> r.getEntity())
        .convert(e -> e.getContentType())
        .convert(h -> h.getValue())
        .get();
  }

  @Override
  public String getContentEncoding() {
    return Optional
        .of(this.response)
        .convert(r -> r.getEntity())
        .convert(e -> e.getContentEncoding())
        .convert(h -> h.getValue())
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
//    try {
//      close();
//    } catch (IOException exception) {
//      // TODO Auto-generated catch block
//      exception.printStackTrace();
//    }
  }
}
