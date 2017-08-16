// Copyright (c) 2012 by Andreas W. Bartels (bartels@anwiba.net)
package net.anwiba.spatial.osm.nominatim.marshaller;

public class NominatimJsonObjectsUnmarshallerFactory {

  public <T> NominatimJsonObjectsUnmarshaller<T> create(final Class<T> clazz) {
    return new NominatimJsonObjectsUnmarshaller<>(clazz);
  }

}
