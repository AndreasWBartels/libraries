package net.anwiba.spatial.ckan.marshaller;

import net.anwiba.commons.resource.annotation.Location;
import net.anwiba.commons.resource.reflaction.AbstractResourceFactory;

public class CkanV01_0TestResources extends AbstractResourceFactory {

  static {
    initialize(CkanV01_0TestResources.class);
  }

  @Location("packageSearchResponse.json")
  public static String packageSearchResponse;

  @Location("resourceSearchResponse.json")
  public static String resourceSearchResponse;

  @Location("contacts.json")
  public static String contacts;

  @Location("license.json")
  public static String license;

  @Location("resource.json")
  public static String resource;

  @Location("shapeInfo.json")
  public static String shapeInfo;

}
