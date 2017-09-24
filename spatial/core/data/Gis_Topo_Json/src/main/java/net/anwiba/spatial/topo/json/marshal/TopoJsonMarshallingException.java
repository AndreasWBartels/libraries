// Copyright (c) 2012 by Andreas W. Bartels (bartels@anwiba.net)
package net.anwiba.spatial.topo.json.marshal;

import java.io.IOException;

import net.anwiba.spatial.topo.json.v01_0.ErrorResponse;

public class TopoJsonMarshallingException extends IOException {

  private static final long serialVersionUID = -3150576485675908525L;
  private final ErrorResponse error;

  public TopoJsonMarshallingException(final ErrorResponse response) {
    this.error = response;
  }

  public ErrorResponse getError() {
    return this.error;
  }

}
