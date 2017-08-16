// Copyright (c) 2012 by Andreas W. Bartels (bartels@anwiba.net)
package net.anwiba.spatial.osm.overpass.marshaller;

import java.util.HashMap;

import net.anwiba.commons.json.AbstractJsonObjectsUnmarshaller;
import net.anwiba.spatial.osm.overpass.schema.v00_6.ErrorResponse;

public class OverpassJsonObjectsUnmarshaller<T>
    extends
    AbstractJsonObjectsUnmarshaller<T, ErrorResponse, OverpassJsonMapperException> {

  public OverpassJsonObjectsUnmarshaller(final Class<T> clazz) {
    super(clazz, ErrorResponse.class, new HashMap<>(), new OverpassJsonMapperExceptionFactory());
  }

}
