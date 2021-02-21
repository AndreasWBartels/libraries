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

import net.anwiba.commons.resource.annotation.Location;
import net.anwiba.commons.resource.reflaction.AbstractResourceFactory;

public class TopoJsonV01_0TestResources extends AbstractResourceFactory {

  static {
    initialize(TopoJsonV01_0TestResources.class, (c, r) -> c.getResource(r));
  }

  @Location("topologyObject.topojson")
  public static String topologyObject;
  @Location("quantiziedTopologyObject.topojson")
  public static String quantiziedTopologyObject;
  @Location("topologyObjects.topojson")
  public static String topologyObjects;

}
