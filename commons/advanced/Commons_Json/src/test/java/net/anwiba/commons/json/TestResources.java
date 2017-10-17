package net.anwiba.commons.json;

import net.anwiba.commons.resource.annotation.Location;
import net.anwiba.commons.resource.reflaction.AbstractResourceFactory;

public class TestResources extends AbstractResourceFactory {

  static {
    initialize(TestResources.class);
  }

  @Location("object.json")
  public static String object;

  @Location("objects.json")
  public static String objects;

}