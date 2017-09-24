// Copyright (c) 2012 by Andreas W. Bartels (bartels@anwiba.net)
package net.anwiba.spatial.topo.json.marshal;

import java.util.HashMap;

import net.anwiba.commons.json.AbstractJsonObjectUnmarshaller;
import net.anwiba.spatial.topo.json.v01_0.ErrorResponse;

public class TopoJsonObjectUnmarshaller<T>
    extends
    AbstractJsonObjectUnmarshaller<T, ErrorResponse, TopoJsonMarshallingException> {

  public TopoJsonObjectUnmarshaller(final Class<T> clazz) {
    super(clazz, ErrorResponse.class, new HashMap<>(), new TopoJsonMarshallingExceptionFactory());
  }

}
