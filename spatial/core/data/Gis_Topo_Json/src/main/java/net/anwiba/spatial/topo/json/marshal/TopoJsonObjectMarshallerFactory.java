// Copyright (c) 2012 by Andreas W. Bartels (bartels@anwiba.net)
package net.anwiba.spatial.topo.json.marshal;

public class TopoJsonObjectMarshallerFactory {

  private final boolean isPrittyPrintEnabled;

  public TopoJsonObjectMarshallerFactory(final boolean isPrittyPrintEnabled) {
    this.isPrittyPrintEnabled = isPrittyPrintEnabled;
  }

  public <T> TopoJsonObjectMarshaller<T> create(final Class<T> clazz) {
    return new TopoJsonObjectMarshaller<>(clazz, this.isPrittyPrintEnabled);
  }

}
