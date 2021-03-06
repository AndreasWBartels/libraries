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
import java.io.OutputStream;
import java.io.Writer;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;

public class JsonObjectMarshaller<T> {
  private final ObjectWriter writer;

  public JsonObjectMarshaller(final Class<T> clazz, final boolean isPrittyPrintEnabled) {
    final ObjectMapper mapper = new ObjectMapper();
    mapper.findAndRegisterModules();
    mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    if (isPrittyPrintEnabled) {
      this.writer = mapper.writerFor(clazz).withDefaultPrettyPrinter();
      return;
    }
    this.writer = mapper.writerFor(clazz);
  }

  public void marshall(final OutputStream outputStream, final T object) throws IOException {
    this.writer.writeValue(outputStream, object);
  }

  public void marshall(final Writer outputWriter, final T object) throws IOException {
    this.writer.writeValue(outputWriter, object);
  }
}