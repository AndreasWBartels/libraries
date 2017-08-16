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

public class HttpRequestException extends IOException {

  private static final long serialVersionUID = 1L;
  private final int statusCode;
  private final String statusText;
  private final byte[] content;
  private final String contentType;
  private final String contentEncoding;

  public HttpRequestException(
      final String message,
      final int statusCode,
      final String statusText,
      final byte[] content,
      final String contentType,
      final String contentEncoding) {
    super(message);
    this.statusCode = statusCode;
    this.statusText = statusText;
    this.content = content;
    this.contentType = contentType;
    this.contentEncoding = contentEncoding;
  }

  public HttpRequestException(
      final String message,
      final int statusCode,
      final String statusText,
      final byte[] content,
      final String contentType,
      final String contentEncoding,
      final Throwable throwable) {
    super(message, throwable);
    this.statusCode = statusCode;
    this.statusText = statusText;
    this.content = content;
    this.contentType = contentType;
    this.contentEncoding = contentEncoding;
  }

  public byte[] getContent() {
    return this.content;
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

}
