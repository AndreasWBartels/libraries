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

package net.anwiba.spatial.geo.json.marshal;

import java.util.HashMap;

import net.anwiba.commons.json.AbstractJsonObjectUnmarshaller;
import net.anwiba.spatial.geo.json.v01_0.ErrorResponse;

public class GeoJsonObjectUnmarshaller<T>
    extends
    AbstractJsonObjectUnmarshaller<T, ErrorResponse, GeoJsonMarshallingException> {

  public GeoJsonObjectUnmarshaller(final Class<T> clazz) {
    super(clazz, ErrorResponse.class, new HashMap<>(), new GeoJsonMarshallingExceptionFactory());
  }

}
