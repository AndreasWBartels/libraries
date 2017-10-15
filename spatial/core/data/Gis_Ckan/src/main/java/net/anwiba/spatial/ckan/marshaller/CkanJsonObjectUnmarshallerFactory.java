// Copyright (c) 2012 by Andreas W. Bartels (bartels@anwiba.net)
package net.anwiba.spatial.ckan.marshaller;

public class CkanJsonObjectUnmarshallerFactory {

  public <T> CkanJsonObjectUnmarshaller<T> create(final Class<T> clazz) {
    return new CkanJsonObjectUnmarshaller<>(clazz);
  }

}
