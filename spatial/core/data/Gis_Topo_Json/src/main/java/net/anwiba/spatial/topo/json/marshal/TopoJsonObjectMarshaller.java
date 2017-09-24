// Copyright (c) 2012 by Andreas W. Bartels (bartels@anwiba.net)
package net.anwiba.spatial.topo.json.marshal;

import net.anwiba.commons.json.JsonObjectMarshaller;

public class TopoJsonObjectMarshaller<T> extends JsonObjectMarshaller<T> {

  public TopoJsonObjectMarshaller(final Class<T> clazz, final boolean isPrittyPrintEnabled) {
    super(clazz, isPrittyPrintEnabled);
  }

}
