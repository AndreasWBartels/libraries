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

import net.anwiba.commons.cache.resource.ILifeTime;
import net.anwiba.commons.lang.functional.IClosure;
import net.anwiba.commons.lang.functional.IConsumer;
import net.anwiba.commons.lang.functional.IConverter;
import net.anwiba.commons.lang.optional.IOptional;
import net.anwiba.commons.lang.optional.Optional;
import net.anwiba.commons.lang.parameter.IParameters;

public interface IRequest {

  HttpMethodType getMethodType();

  String getUriString();

  IAuthentication getAuthentication();

  IParameters getParameters();

  long getContentLength();

  IClosure<InputStream, IOException> getContent();

  default <T> T getContent(IConverter<InputStream, T, IOException> converter) throws IOException {
    try (InputStream stream = getContent().execute()) {
      return converter.convert(stream);
    }
  }

  default void getContent(IConsumer<InputStream, IOException> converter) throws IOException {
    try (InputStream stream = getContent().execute()) {
      converter.consume(stream);
    }
  }

  String getContentMimeType();

  String getContentCharset();

  IParameters getProperties();

  String getHost();

  int getPort();
  
  default IOptional<ILifeTime, RuntimeException> getCacheTime() {
    return Optional.empty();
  }

}
