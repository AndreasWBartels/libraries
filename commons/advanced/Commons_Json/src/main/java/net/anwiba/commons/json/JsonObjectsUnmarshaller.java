// Copyright (c) 2012 by Andreas W. Bartels (bartels@anwiba.net)
package net.anwiba.commons.json;

import java.io.IOException;
import java.util.Map;

import net.anwiba.commons.lang.map.HasMapBuilder;

public class JsonObjectsUnmarshaller<T> extends AbstractJsonObjectsUnmarshaller<T, Void, IOException> {

  public JsonObjectsUnmarshaller(final Class<T> clazz) {
    this(clazz, new HasMapBuilder<String, Object>().build());
  }

  public JsonObjectsUnmarshaller(final Class<T> clazz, final Map<String, Object> injectionValues) {
    super(clazz, Void.class, injectionValues, response -> {
      throw new RuntimeException("Unreachable code reached"); //$NON-NLS-1$
    });
  }

}
