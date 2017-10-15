// Copyright (c) 2012 by Andreas W. Bartels (bartels@anwiba.net)
package net.anwiba.spatial.ckan.marshaller;

import net.anwiba.commons.json.JsonObjectMarshaller;

public class CkanJsonObjectMarshaller<T> extends JsonObjectMarshaller<T> {

  public CkanJsonObjectMarshaller(final Class<T> clazz, final boolean isPrittyPrintEnabled) {
    super(clazz, isPrittyPrintEnabled);
  }

}
