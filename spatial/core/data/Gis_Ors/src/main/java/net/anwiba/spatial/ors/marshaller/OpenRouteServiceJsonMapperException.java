// Copyright (c) 2012 by Andreas W. Bartels (bartels@anwiba.net)
package net.anwiba.spatial.ors.marshaller;

import java.io.IOException;

import net.anwiba.spatial.ors.schema.v04_0.ErrorResponse;

public class OpenRouteServiceJsonMapperException extends IOException {

  private static final long serialVersionUID = 1L;
  private final ErrorResponse error;

  public OpenRouteServiceJsonMapperException(final ErrorResponse response) {
    this.error = response;
  }

  public ErrorResponse getError() {
    return this.error;
  }

}
