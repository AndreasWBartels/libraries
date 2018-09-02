/*
 * #%L
 * 
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
package net.anwiba.spatial.swing.ckan.search;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import net.anwiba.commons.lang.optional.If;
import net.anwiba.spatial.ckan.request.IFormatsNameConverter;

public class FormatsNameConverter implements IFormatsNameConverter {

  @Override
  public List<String> convert(final String string) {
    final String lowerCase = string.toLowerCase();
    final String upperCase = string.toUpperCase();
    final ArrayList<String> values = new ArrayList<>();
    If.isTrue(!Objects.equals(lowerCase, string)).excecute(() -> values.add(lowerCase));
    If.isTrue(!Objects.equals(upperCase, string)).excecute(() -> values.add(upperCase));
    values.add(string);
    switch (lowerCase) {
      case "geojson": { //$NON-NLS-1$
        If.isTrue(!Objects.equals("GeoJSON", string)).excecute(() -> values.add("GeoJSON")); //$NON-NLS-1$ //$NON-NLS-2$
        return values;
      }
      case "shape": { //$NON-NLS-1$
        If.isTrue(!Objects.equals("Shape", string)).excecute(() -> values.add("Shape")); //$NON-NLS-1$ //$NON-NLS-2$
        return values;
      }
      case "shapefile": { //$NON-NLS-1$
        If.isTrue(!Objects.equals("Shapefile", string)).excecute(() -> values.add("Shapefile")); //$NON-NLS-1$ //$NON-NLS-2$
        return values;
      }
      case "shapefiles": { //$NON-NLS-1$
        If.isTrue(!Objects.equals("Shapefiles", string)).excecute(() -> values.add("Shapefiles")); //$NON-NLS-1$ //$NON-NLS-2$
        return values;
      }
      case "esri shape": { //$NON-NLS-1$
        If.isTrue(!Objects.equals("ESRI Shape", string)).excecute(() -> values.add("ESRI Shape")); //$NON-NLS-1$ //$NON-NLS-2$
        If.isTrue(!Objects.equals("Esri Shape", string)).excecute(() -> values.add("Esri Shape")); //$NON-NLS-1$ //$NON-NLS-2$
        return values;
      }
      case "esri rest": { //$NON-NLS-1$
        If.isTrue(!Objects.equals("Esri REST", string)).excecute(() -> values.add("Esri REST")); //$NON-NLS-1$ //$NON-NLS-2$
        return values;
      }
      case "arcgis": { //$NON-NLS-1$
        If.isTrue(!Objects.equals("ArcGIS", string)).excecute(() -> values.add("ArcGIS")); //$NON-NLS-1$ //$NON-NLS-2$
        return values;
      }
      case "arcgis mapservice": { //$NON-NLS-1$
        If.isTrue(!Objects.equals("ArcGIS mapservice", string)).excecute(() -> values.add("ArcGIS mapservice")); //$NON-NLS-1$ //$NON-NLS-2$
        return values;
      }
      case "arcgis featureservice": { //$NON-NLS-1$
        If.isTrue(!Objects.equals("ArcGIS featureservice", string)).excecute( //$NON-NLS-1$
            () -> values.add("ArcGIS featureservice")); //$NON-NLS-1$
        return values;
      }
    }
    return values;
  }
} 
