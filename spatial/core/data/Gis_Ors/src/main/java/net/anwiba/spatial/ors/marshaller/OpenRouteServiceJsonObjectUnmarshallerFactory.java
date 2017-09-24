// Copyright (c) 2012 by Andreas W. Bartels (bartels@anwiba.net)
package net.anwiba.spatial.ors.marshaller;

public class OpenRouteServiceJsonObjectUnmarshallerFactory {

  public <T> OpenRouteServiceJsonObjectUnmarshaller<T> create(final Class<T> clazz) {
    return new OpenRouteServiceJsonObjectUnmarshaller<>(clazz);
  }

}
