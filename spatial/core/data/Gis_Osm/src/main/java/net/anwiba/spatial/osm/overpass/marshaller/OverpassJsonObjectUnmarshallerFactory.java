// Copyright (c) 2017 by Andreas W. Bartels (bartels@anwiba.net)
package net.anwiba.spatial.osm.overpass.marshaller;

public class OverpassJsonObjectUnmarshallerFactory {

  public <T> OverpassJsonObjectUnmarshaller<T> create(final Class<T> clazz) {
    return new OverpassJsonObjectUnmarshaller<>(clazz);
  }

}
