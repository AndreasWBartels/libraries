// Copyright (c) 2012 by Andreas W. Bartels (bartels@anwiba.net)
package net.anwiba.spatial.ckan.marshaller;

import net.anwiba.commons.json.IJsonObjectMarshallingExceptionFactory;
import net.anwiba.spatial.ckan.json.schema.v1_0.Response;

public class CkanJsonMapperExceptionFactory
    implements
    IJsonObjectMarshallingExceptionFactory<Response, CkanJsonMapperException> {

  @Override
  public CkanJsonMapperException create(final Response response) {
    return new CkanJsonMapperException(response.getError());
  }

}
