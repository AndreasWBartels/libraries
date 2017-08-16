// Copyright (c) 2012 by Andreas W. Bartels (bartels@anwiba.net)
package net.anwiba.spatial.osm.nominatim.marshaller;

import java.util.HashMap;

import net.anwiba.commons.json.AbstractJsonObjectsUnmarshaller;
import net.anwiba.spatial.osm.nominatim.schema.v01_0.ErrorResponse;

public class NominatimJsonObjectsUnmarshaller<T>
    extends
    AbstractJsonObjectsUnmarshaller<T, ErrorResponse, NominatimJsonMapperException> {

  public NominatimJsonObjectsUnmarshaller(final Class<T> clazz) {
    super(clazz, ErrorResponse.class, new HashMap<>(), new NominatimJsonMapperExceptionFactory());
  }

}
