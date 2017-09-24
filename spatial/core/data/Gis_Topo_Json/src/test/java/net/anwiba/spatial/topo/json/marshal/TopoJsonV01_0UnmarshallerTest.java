// Copyright (c) 2012 by Andreas W. Bartels (bartels@anwiba.net)
package net.anwiba.spatial.topo.json.marshal;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

import net.anwiba.spatial.topo.json.v01_0.Topologie;

public class TopoJsonV01_0UnmarshallerTest {

  TopoJsonObjectUnmarshallerFactory factory = new TopoJsonObjectUnmarshallerFactory();

  @Test
  public void topologyObject() throws IOException, TopoJsonMarshallingException {
    final Topologie response = this.factory
        .create(Topologie.class)
        .unmarshal(TopoJsonV01_0TestResources.topologyObject);
    assertThat(response, notNullValue());
  }

  @Test
  public void topologyObjects() throws IOException, TopoJsonMarshallingException {
    final Topologie response = this.factory
        .create(Topologie.class)
        .unmarshal(TopoJsonV01_0TestResources.topologyObjects);
    assertThat(response, notNullValue());
  }

  @Test
  public void quantiziezedtopologyObjects() throws IOException, TopoJsonMarshallingException {
    final Topologie response = this.factory
        .create(Topologie.class)
        .unmarshal(TopoJsonV01_0TestResources.quantiziedTopologyObject);
    assertThat(response, notNullValue());
  }

}
