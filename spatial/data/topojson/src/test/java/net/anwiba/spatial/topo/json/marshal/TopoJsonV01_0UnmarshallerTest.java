/*
 * #%L
 * anwiba spatial
 * %%
 * Copyright (C) 2007 - 2019 Andreas Bartels
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */

package net.anwiba.spatial.topo.json.marshal;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;

import java.io.IOException;

import org.junit.jupiter.api.Test;

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
