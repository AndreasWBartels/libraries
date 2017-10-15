// Copyright (c) 2012 by Andreas W. Bartels (bartels@anwiba.net)
package net.anwiba.spatial.ckan.marshaller;

public class CkanJsonObjectMarshallerFactory {

  private final boolean isPrittyPrintEnabled;

  public CkanJsonObjectMarshallerFactory(final boolean isPrittyPrintEnabled) {
    this.isPrittyPrintEnabled = isPrittyPrintEnabled;
  }

  public <T> CkanJsonObjectMarshaller<T> create(final Class<T> clazz) {
    return new CkanJsonObjectMarshaller<>(clazz, this.isPrittyPrintEnabled);
  }

}
