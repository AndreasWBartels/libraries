// Copyright (c) 2012 by Andreas W. Bartels (bartels@anwiba.net)
package net.anwiba.spatial.osm.overpass.marshaller;

import net.anwiba.commons.json.IJsonObjectMarshallingExceptionFactory;
import net.anwiba.spatial.osm.overpass.schema.v00_6.ErrorResponse;

public class OverpassJsonMapperExceptionFactory
    implements
    IJsonObjectMarshallingExceptionFactory<ErrorResponse, OverpassJsonMapperException> {

  @Override
  public OverpassJsonMapperException create(final ErrorResponse response) {
    return new OverpassJsonMapperException(response);
  }

}
