package net.anwiba.spatial.topo.json.marshal;

import net.anwiba.commons.resource.annotation.Location;
import net.anwiba.commons.resource.reflaction.AbstractResourceFactory;

public class TopoJsonV01_0TestResources extends AbstractResourceFactory {

  static {
    initialize(TopoJsonV01_0TestResources.class);
  }

  @Location("topologyObject.topojson")
  public static String topologyObject;
  @Location("quantiziedTopologyObject.topojson")
  public static String quantiziedTopologyObject;
  @Location("topologyObjects.topojson")
  public static String topologyObjects;

}
