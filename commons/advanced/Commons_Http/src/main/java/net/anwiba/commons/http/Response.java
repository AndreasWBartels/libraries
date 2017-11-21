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

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;

import net.anwiba.commons.resource.utilities.IoUtilities;
import net.anwiba.commons.thread.cancel.ICanceler;

public final class Response implements IResponse {
  private final HttpClient client;
  private final HttpResponse response;
  private final ICanceler cancelable;

  public Response(final ICanceler cancelable, final HttpClient client, final HttpResponse response) {
    this.cancelable = cancelable;
    this.client = client;
    this.response = response;
  }

  @Override
  public String getBody() throws IOException {
    try (InputStream inputStream = getInputStream();) {
      return IoUtilities.toString(inputStream, getContentEncoding());
    }
  }

  @Override
  public int getStatusCode() {
    return this.response.getStatusLine().getStatusCode();
  }

  @Override
  public String getStatusText() {
    return this.response.getStatusLine().getReasonPhrase();
  }

  @Override
  public InputStream getInputStream() throws IOException {
    return new CancelableInputStream(this.cancelable, this.response.getEntity().getContent());
  }

  @Override
  public long getContentLength() {
    return this.response.getEntity().getContentLength();
  }

  @Override
  public String getContentType() {
    final Header contentType = this.response.getEntity().getContentType();
    if (contentType == null) {
      return null;
    }
    return contentType.getValue();
  }

  @Override
  public String getContentEncoding() {
    final Header contentEncoding = this.response.getEntity().getContentEncoding();
    if (contentEncoding == null) {
      return "UTF-8"; //$NON-NLS-1$
    }
    return contentEncoding.getValue();
  }

  @Override
  public void close() throws IOException {
    if (this.response instanceof CloseableHttpResponse) {
      ((CloseableHttpResponse) this.response).close();
    }
  }
}
