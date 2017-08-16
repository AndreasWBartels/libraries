// Copyright (c) 2012 by Andreas W. Bartels (bartels@anwiba.net)
package net.anwiba.spatial.osm.nominatim.marshaller;

import net.anwiba.commons.json.IJsonObjectMarshallingExceptionFactory;
import net.anwiba.spatial.osm.nominatim.schema.v01_0.ErrorResponse;

public class NominatimJsonMapperExceptionFactory
    implements
    IJsonObjectMarshallingExceptionFactory<ErrorResponse, NominatimJsonMapperException> {

  @Override
  public NominatimJsonMapperException create(final ErrorResponse response) {
    return new NominatimJsonMapperException(response);
  }

}
