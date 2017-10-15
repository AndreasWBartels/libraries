// Copyright (c) 2012 by Andreas W. Bartels (bartels@anwiba.net)
package net.anwiba.spatial.ckan.marshaller;

import net.anwiba.commons.json.AbstractJsonObjectUnmarshaller;
import net.anwiba.commons.lang.map.HasMapBuilder;
import net.anwiba.spatial.ckan.json.factory.ExtraValueFactory;

public class CkanJsonResponseUnmarshaller<T> extends AbstractJsonObjectUnmarshaller<T, Void, CkanJsonMapperException> {

  public CkanJsonResponseUnmarshaller(final Class<T> clazz) {
    super(
        clazz,
        Void.class,
        new HasMapBuilder<String, Object>().put("extravaluefactory", new ExtraValueFactory()).build(), //$NON-NLS-1$
        new CkanJsonResponeMapperExceptionFactory());
  }

}
