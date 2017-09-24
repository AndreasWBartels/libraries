// Copyright (c) 2012 by Andreas W. Bartels (bartels@anwiba.net)
package net.anwiba.spatial.topo.json.marshal;

public class TopoJsonObjectUnmarshallerFactory {

  public <T> TopoJsonObjectUnmarshaller<T> create(final Class<T> clazz) {
    return new TopoJsonObjectUnmarshaller<>(clazz);
  }

}
