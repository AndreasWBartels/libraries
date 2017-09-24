// Copyright (c) 2012 by Andreas W. Bartels (bartels@anwiba.net)
package net.anwiba.spatial.topo.json.marshal;

import net.anwiba.commons.json.IJsonObjectMarshallingExceptionFactory;
import net.anwiba.spatial.topo.json.v01_0.ErrorResponse;

public class TopoJsonMarshallingExceptionFactory
    implements
    IJsonObjectMarshallingExceptionFactory<ErrorResponse, TopoJsonMarshallingException> {

  @Override
  public TopoJsonMarshallingException create(final ErrorResponse response) {
    return new TopoJsonMarshallingException(response);
  }

}
