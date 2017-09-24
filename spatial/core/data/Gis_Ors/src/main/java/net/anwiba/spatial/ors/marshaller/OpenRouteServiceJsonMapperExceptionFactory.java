// Copyright (c) 2012 by Andreas W. Bartels (bartels@anwiba.net)
package net.anwiba.spatial.ors.marshaller;

import net.anwiba.commons.json.IJsonObjectMarshallingExceptionFactory;
import net.anwiba.spatial.ors.schema.v04_0.ErrorResponse;

public class OpenRouteServiceJsonMapperExceptionFactory
    implements
    IJsonObjectMarshallingExceptionFactory<ErrorResponse, OpenRouteServiceJsonMapperException> {

  @Override
  public OpenRouteServiceJsonMapperException create(final ErrorResponse response) {
    return new OpenRouteServiceJsonMapperException(response);
  }

}
