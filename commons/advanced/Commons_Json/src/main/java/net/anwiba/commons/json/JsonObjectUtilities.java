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
package net.anwiba.commons.json;

import java.io.IOException;
import java.io.StringWriter;

public class JsonObjectUtilities {

  public static <T> T unmarshall(final Class<T> clazz, final String string) throws IOException {
    final JsonObjectUnmarshaller<T> unmarshaller = new JsonObjectUnmarshaller<>(clazz);
    return unmarshaller.unmarshal(string);
  }

  public static <T> String marshall(final T bean) {
    try {
      @SuppressWarnings("unchecked")
      final JsonObjectMarshaller<T> marshaller = new JsonObjectMarshaller<>((Class<T>) bean.getClass(), false);
      final StringWriter outputStream = new StringWriter();
      marshaller.marshall(outputStream, bean);
      return outputStream.toString();
    } catch (final IOException exception) {
      throw new RuntimeException("Unreachable code reached");
    }
  }
}
