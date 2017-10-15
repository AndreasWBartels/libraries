// Copyright (c) 2012 by Andreas W. Bartels (bartels@anwiba.net)
package net.anwiba.spatial.ckan.marshaller;

import net.anwiba.commons.json.AbstractJsonObjectsUnmarshaller;
import net.anwiba.commons.lang.map.HasMapBuilder;
import net.anwiba.spatial.ckan.json.factory.ExtraValueFactory;
import net.anwiba.spatial.ckan.json.schema.v1_0.Response;

public class CkanJsonObjectsUnmarshaller<T>
    extends
    AbstractJsonObjectsUnmarshaller<T, Response, CkanJsonMapperException> {

  public CkanJsonObjectsUnmarshaller(final Class<T> clazz) {
    super(
        clazz,
        Response.class,
        new HasMapBuilder<String, Object>().put("extravaluefactory", new ExtraValueFactory()).build(), //$NON-NLS-1$
        new CkanJsonMapperExceptionFactory());
  }

}
