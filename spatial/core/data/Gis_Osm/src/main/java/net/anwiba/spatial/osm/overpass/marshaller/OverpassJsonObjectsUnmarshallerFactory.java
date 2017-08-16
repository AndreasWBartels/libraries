// Copyright (c) 2012 by Andreas W. Bartels (bartels@anwiba.net)
package net.anwiba.spatial.osm.overpass.marshaller;

public class OverpassJsonObjectsUnmarshallerFactory {

  public <T> OverpassJsonObjectsUnmarshaller<T> create(final Class<T> clazz) {
    return new OverpassJsonObjectsUnmarshaller<>(clazz);
  }

}
