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

package net.anwiba.spatial.coordinatereferencesystem.coordinatesystem;

import net.anwiba.commons.nls.NLS;

public class CoordinateSystemMessages extends NLS {

  public static String PSEUDO_MERCATOR;
  public static String MERCATOR;
  public static String MERCATOR_SP1;
  public static String TRANSVERSE_MERCATOR;
  public static String UTM;
  public static String GAUSS_KRUGER;
  public static String LAMBERT_CONFORMAL_CONIC;
  public static String LAMBERT_CONFORMAL_CONIC_1SP;
  public static String LAMBERT_CONFORMAL_CONIC_2SP;
  public static String UNKOWN;
  public static String LAMBERT_AZIMUTHAL_EQUAL_AREA;
  public static String CASSINI_SOLDNER;
  public static String POLAR_STEREOGRAPHIC;
  public static String HOTINE_OBLIQUE_MERCATOR_AZIMUTH_CENTER;

  static {
    NLS.initializeMessages(CoordinateSystemMessages.class);
  }
}
