/*
 * #%L
 * anwiba commons core
 * %%
 * Copyright (C) 2007 - 2018 Andreas W. Bartels
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

import java.io.IOException;

import net.anwiba.spatial.topo.json.v01_0.ErrorResponse;

public class TopoJsonMarshallingException extends IOException {

  private static final long serialVersionUID = -3150576485675908525L;
  private final ErrorResponse error;

  public TopoJsonMarshallingException(final ErrorResponse response) {
    this.error = response;
  }

  public ErrorResponse getError() {
    return this.error;
  }

}
