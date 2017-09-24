// Copyright (c) 2012 by Andreas W. Bartels (bartels@anwiba.net)
package net.anwiba.spatial.ors.marshaller;

import java.util.HashMap;

import net.anwiba.commons.json.AbstractJsonObjectsUnmarshaller;
import net.anwiba.spatial.ors.schema.v04_0.ErrorResponse;

public class OpenRouteServiceJsonObjectUnmarshaller<T>
    extends
    AbstractJsonObjectsUnmarshaller<T, ErrorResponse, OpenRouteServiceJsonMapperException> {

  public OpenRouteServiceJsonObjectUnmarshaller(final Class<T> clazz) {
    super(clazz, ErrorResponse.class, new HashMap<>(), new OpenRouteServiceJsonMapperExceptionFactory());
  }

}
