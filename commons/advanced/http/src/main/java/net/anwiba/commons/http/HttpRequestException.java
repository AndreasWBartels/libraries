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

import net.anwiba.commons.reference.utilities.IoUtilities;

public class HttpRequestException extends IOException {

  private static final long serialVersionUID = 1L;
  private final int statusCode;
  private final String statusText;
  private final byte[] content;
  private final String contentType;
  private final String contentEncoding;
  private String url;

  public HttpRequestException(
      final String message,
      final String url,
      final int statusCode,
      final String statusText,
      final byte[] content,
      final String contentType,
      final String contentEncoding) {
    super(message);
    this.url = url;
    this.statusCode = statusCode;
    this.statusText = statusText;
    this.content = content;
    this.contentType = contentType;
    this.contentEncoding = contentEncoding;
  }

  public HttpRequestException(
      final String message,
      final String url,
      final int statusCode,
      final String statusText,
      final byte[] content,
      final String contentType,
      final String contentEncoding,
      final Throwable throwable) {
    super(message, throwable);
    this.url = url;
    this.statusCode = statusCode;
    this.statusText = statusText;
    this.content = content;
    this.contentType = contentType;
    this.contentEncoding = contentEncoding;
  }

  public String getUrl() {
    return this.url;
  }

  public byte[] getContent() {
    return this.content;
  }


  public int getContentLength() {
    return this.content.length;
  }

  public String getContentAsString() {
    try {
      return new String(this.content, this.contentEncoding);
    } catch (final IOException exception) {
      return new String(this.content);
    }
  }

  public String getContentType() {
    return this.contentType;
  }

  public String getContentEncoding() {
    return this.contentEncoding;
  }

  public int getStatusCode() {
    return this.statusCode;
  }

  public String getStatusText() {
    return this.statusText;
  }

  public static HttpRequestException create(final String message, final IResponse response, final byte[] body) {
    return new HttpRequestException(
        message,
        response.getUri(),
        response.getStatusCode(),
        response.getStatusText(),
        body,
        response.getContentType(),
        response.getContentEncoding());
  }

  public static HttpRequestException create(final String message, final IResponse response) throws IOException {
    try (InputStream inputStream = response.getInputStream()) {
      return new HttpRequestException(
          message,
          response.getUri(),
          response.getStatusCode(),
          response.getStatusText(),
          IoUtilities.toByteArray(inputStream),
          response.getContentType(),
          response.getContentEncoding());
    }
  }
}
