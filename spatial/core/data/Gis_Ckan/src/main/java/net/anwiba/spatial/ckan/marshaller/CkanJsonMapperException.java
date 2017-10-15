// Copyright (c) 2012 by Andreas W. Bartels (bartels@anwiba.net)
package net.anwiba.spatial.ckan.marshaller;

import java.io.IOException;

import net.anwiba.spatial.ckan.json.schema.v1_0.Error;

public class CkanJsonMapperException extends IOException {

  private static final long serialVersionUID = -3150576485675908525L;
  private final Error error;

  public CkanJsonMapperException(final Error response) {
    super(response == null ? "" : response.getMessage()); //$NON-NLS-1$
    this.error = response;
  }

  public Error getError() {
    return this.error;
  }

}
