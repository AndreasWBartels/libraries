// Copyright (c) 2017 by Andreas W. Bartels (bartels@anwiba.net)
package net.anwiba.spatial.osm.overpass.marshaller;

import java.util.HashMap;

import net.anwiba.commons.json.AbstractJsonObjectUnmarshaller;
import net.anwiba.spatial.osm.overpass.schema.v00_6.ErrorResponse;

public class OverpassJsonObjectUnmarshaller<T>
    extends
    AbstractJsonObjectUnmarshaller<T, ErrorResponse, OverpassJsonMapperException> {

  public OverpassJsonObjectUnmarshaller(final Class<T> clazz) {
    super(clazz, ErrorResponse.class, new HashMap<>(), new OverpassJsonMapperExceptionFactory());
  }

}
