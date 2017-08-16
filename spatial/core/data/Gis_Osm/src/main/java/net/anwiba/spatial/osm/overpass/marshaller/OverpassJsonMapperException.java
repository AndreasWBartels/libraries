// Copyright (c) 2012 by Andreas W. Bartels (bartels@anwiba.net)
package net.anwiba.spatial.osm.overpass.marshaller;

import java.io.IOException;

import net.anwiba.spatial.osm.overpass.schema.v00_6.ErrorResponse;

public class OverpassJsonMapperException extends IOException {

  private static final long serialVersionUID = -3150576485675908525L;
  private final ErrorResponse error;

  public OverpassJsonMapperException(final ErrorResponse response) {
    this.error = response;
  }

  public ErrorResponse getError() {
    return this.error;
  }

}
