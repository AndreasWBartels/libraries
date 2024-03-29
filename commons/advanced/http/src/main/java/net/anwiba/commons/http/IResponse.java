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
import java.util.List;

import net.anwiba.commons.lang.optional.IOptional;
import net.anwiba.commons.lang.optional.Optional;

public interface IResponse extends Closeable {

  default boolean isStatusCode2xx() {
    return getStatusCode() >= 200 && getStatusCode() < 300;
  }

  String getStatusText();

  int getStatusCode();

  String getBody() throws IOException;

  InputStream getInputStream() throws IOException;

  long getContentLength();

  String getContentType();

  String getContentEncoding();

  @Override
  public void close() throws IOException;

  String getUri();

  void abort();

  default IOptional<List<String>, RuntimeException> cacheControl() {
    return Optional.empty();
  }

  default IOptional<String, RuntimeException> expires() {
    return Optional.empty();
  }

  default IOptional<String, RuntimeException> pragma() {
    return Optional.empty();
  }
}
