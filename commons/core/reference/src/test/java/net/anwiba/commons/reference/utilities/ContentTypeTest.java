/*
 * #%L
 * anwiba commons
 * %%
 * Copyright (C) 2007 - 2022 Andreas W. Bartels
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
package net.anwiba.commons.reference.utilities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import net.anwiba.commons.lang.exception.CreationException;
import net.anwiba.commons.lang.optional.IOptional;

public class ContentTypeTest {

  @Test
  public void parseError() throws CreationException {
    IOptional<IContentType, CreationException> optional = ContentType.parse("application;;charset=UTF-8");

    assertFalse(optional.isSuccessful());
    assertInstanceOf(CreationException.class, optional.getCause());

    assertTrue(optional.isEmpty());
    assertFalse(optional.isAccepted());

    assertThrows(CreationException.class, () -> optional.get());

  }

  @Test
  public void fromFaild() throws CreationException {
    IOptional<IContentType, RuntimeException> optional = ContentType.from("application;;charset=UTF-8");

    assertTrue(optional.isSuccessful());
    assertThrows(IllegalStateException.class, () -> optional.getCause());

    assertTrue(optional.isEmpty());
    assertFalse(optional.isAccepted());

    assertEquals(optional.get(), null);
  }

  @Test
  public void parse() throws CreationException {
    IOptional<IContentType, CreationException> optional = ContentType.parse("application/json;charset=UTF-8");

    assertTrue(optional.isSuccessful());
    assertTrue(optional.isAccepted());
    IContentType contentType = optional.get();

    assertNotEquals(contentType, ContentType.APPLICATION_JSON);

    assertFalse(contentType.getParameters().isEmpty());
    assertTrue(contentType.getParameters().contains("charset"));
    assertEquals(contentType.getParameters().getValue("charset"), "UTF-8");
    assertTrue(contentType.getParameters().contains("charset"));

    assertTrue(ContentType.hasFileExtension(contentType, "json"));
    assertFalse(((ContentType) contentType).hasFileExtension("json"));

    assertEquals(contentType.toString(), "application/json; charset=UTF-8");
  }

  @Test
  public void fromWithCharset() {
    IOptional<IContentType, RuntimeException> optional = ContentType.from("application/json;charset=UTF-8");

    assertTrue(optional.isSuccessful());
    assertTrue(optional.isAccepted());
    IContentType contentType = optional.get();

    assertEquals(contentType, ContentType.APPLICATION_JSON);
    assertTrue(contentType.getParameters().isEmpty());

    assertTrue(ContentType.hasFileExtension(contentType, "json"));
    assertTrue(((ContentType) contentType).hasFileExtension("json"));

    assertEquals(contentType.toString(), "application/json");
  }

  @Test
  public void fromWithoutCharset() {
    IOptional<IContentType, RuntimeException> optional = ContentType.from("application/json");

    assertTrue(optional.isSuccessful());
    assertTrue(optional.isAccepted());
    IContentType contentType = optional.get();

    assertEquals(contentType, ContentType.APPLICATION_JSON);
    assertTrue(contentType.getParameters().isEmpty());

    assertTrue(ContentType.hasFileExtension(contentType, "json"));
    assertTrue(((ContentType) contentType).hasFileExtension("json"));

    assertEquals(contentType.toString(), "application/json");
  }

}
