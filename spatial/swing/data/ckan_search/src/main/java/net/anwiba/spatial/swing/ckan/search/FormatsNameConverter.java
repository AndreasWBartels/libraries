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

import net.anwiba.commons.lang.optional.If;
import net.anwiba.spatial.ckan.query.IFormatsNameConverter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FormatsNameConverter implements IFormatsNameConverter {

  @Override
  public List<String> convert(final String string) {
    final String lowerCase = string.toLowerCase();
    final String upperCase = string.toUpperCase();
    final ArrayList<String> values = new ArrayList<>();
    If.isTrue(!Objects.equals(lowerCase, string)).execute(() -> values.add(lowerCase));
    If.isTrue(!Objects.equals(upperCase, string)).execute(() -> values.add(upperCase));
    values.add(string);
    switch (lowerCase) {
      case "geojson": { //$NON-NLS-1$
        If.isTrue(!Objects.equals("GeoJSON", string)).execute(() -> values.add("GeoJSON")); //$NON-NLS-1$ //$NON-NLS-2$
        return values;
      }
      case "shape": { //$NON-NLS-1$
        If.isTrue(!Objects.equals("Shape", string)).execute(() -> values.add("Shape")); //$NON-NLS-1$ //$NON-NLS-2$
        return values;
      }
      case "shapefile": { //$NON-NLS-1$
        If.isTrue(!Objects.equals("Shapefile", string)).execute(() -> values.add("Shapefile")); //$NON-NLS-1$ //$NON-NLS-2$
        return values;
      }
      case "shapefiles": { //$NON-NLS-1$
        If.isTrue(!Objects.equals("Shapefiles", string)).execute(() -> values.add("Shapefiles")); //$NON-NLS-1$ //$NON-NLS-2$
        return values;
      }
      case "esri shape": { //$NON-NLS-1$
        If.isTrue(!Objects.equals("ESRI Shape", string)).execute(() -> values.add("ESRI Shape")); //$NON-NLS-1$ //$NON-NLS-2$
        If.isTrue(!Objects.equals("Esri Shape", string)).execute(() -> values.add("Esri Shape")); //$NON-NLS-1$ //$NON-NLS-2$
        return values;
      }
      case "esri rest": { //$NON-NLS-1$
        If.isTrue(!Objects.equals("Esri REST", string)).execute(() -> values.add("Esri REST")); //$NON-NLS-1$ //$NON-NLS-2$
        return values;
      }
      case "arcgis": { //$NON-NLS-1$
        If.isTrue(!Objects.equals("ArcGIS", string)).execute(() -> values.add("ArcGIS")); //$NON-NLS-1$ //$NON-NLS-2$
        return values;
      }
      case "arcgis mapservice": { //$NON-NLS-1$
        If.isTrue(!Objects.equals("ArcGIS mapservice", string)).execute(() -> values.add("ArcGIS mapservice")); //$NON-NLS-1$ //$NON-NLS-2$
        return values;
      }
      case "arcgis featureservice": { //$NON-NLS-1$
        If.isTrue(!Objects.equals("ArcGIS featureservice", string)) //$NON-NLS-1$
            .execute(
                () -> values.add("ArcGIS featureservice")); //$NON-NLS-1$
        return values;
      }
    }
    return values;
  }
}
