/*
 * #%L
 * *
 * %%
 * Copyright (C) 2007 - 2017 Andreas W. Bartels
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
package net.anwiba.spatial.ckan.json.types;

import java.io.IOException;
import java.util.Locale;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

public class DateStringDeserializer extends StdDeserializer<DateString> {

  final String language = Locale.getDefault().getLanguage();

  protected DateStringDeserializer() {
    super(DateString.class);
  }

  private static final long serialVersionUID = 1L;

  @Override
  public DateString deserialize(final JsonParser p, final DeserializationContext ctxt)
      throws IOException,
      JsonProcessingException {
    final JsonNode node = p.getCodec().readTree(p);
    if (node.isTextual()) {
      return new DateString(node.asText());
    }
    return null;
  }

}
