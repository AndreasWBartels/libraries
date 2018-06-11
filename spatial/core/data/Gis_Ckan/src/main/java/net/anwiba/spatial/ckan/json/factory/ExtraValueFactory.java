/*
 * #%L
 * *
 * %%
 * Copyright (C) 2007 - 2017 Andreas W. Bartels
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
package net.anwiba.spatial.ckan.json.factory;

import java.io.IOException;
import java.util.Objects;

import net.anwiba.commons.lang.functional.IFactory;
import net.anwiba.commons.logging.ILevel;
import net.anwiba.spatial.ckan.json.schema.v1_0.ContactString;
import net.anwiba.spatial.ckan.json.schema.v1_0.Extra;
import net.anwiba.spatial.ckan.json.schema.v1_0.ExtraContacts;
import net.anwiba.spatial.ckan.json.schema.v1_0.ExtraDates;
import net.anwiba.spatial.ckan.json.schema.v1_0.ExtraGeometry;
import net.anwiba.spatial.ckan.json.schema.v1_0.ExtraLicense;
import net.anwiba.spatial.ckan.json.schema.v1_0.ExtraSpatialReference;
import net.anwiba.spatial.ckan.json.schema.v1_0.ExtraString;
import net.anwiba.spatial.ckan.json.schema.v1_0.License;
import net.anwiba.spatial.ckan.json.schema.v1_0.SpatialGeometryString;
import net.anwiba.spatial.ckan.json.schema.v1_0.SpatialReference;
import net.anwiba.spatial.ckan.json.types.DateString;
import net.anwiba.spatial.ckan.marshaller.CkanJsonObjectUnmarshallerFactory;
import net.anwiba.spatial.ckan.marshaller.CkanJsonObjectsUnmarshallerFactory;
import net.anwiba.spatial.geo.json.marshal.GeoJsonObjectUnmarshallerFactory;

public class ExtraValueFactory {

  //  {

  private static net.anwiba.commons.logging.ILogger logger = net.anwiba.commons.logging.Logging
      .getLogger(ExtraValueFactory.class);
  final CkanJsonObjectUnmarshallerFactory objectUnmarshallerFactory = new CkanJsonObjectUnmarshallerFactory();
  final CkanJsonObjectsUnmarshallerFactory objectsUnmarshallerFactory = new CkanJsonObjectsUnmarshallerFactory();
  final GeoJsonObjectUnmarshallerFactory geometryUnmarshallerFactory = new GeoJsonObjectUnmarshallerFactory();

  public Extra create(final String key, final Object objectValue) {
    if (objectValue instanceof String) {
      final String value = (String) objectValue;
      final IFactory<String, Extra, RuntimeException> factory = type -> {
        try {
          switch (type) {
            case "contacts": { //$NON-NLS-1$
              final ExtraContacts object = new ExtraContacts();
              object.setValue(new ContactString(value));
              return object;
            }
            case "terms_of_use": { //$NON-NLS-1$
              final ExtraLicense object = new ExtraLicense();
              final License result = this.objectUnmarshallerFactory.create(License.class).unmarshal(value);
              object.setValue(result);
              return object;
            }
            case "dates": { //$NON-NLS-1$
              final ExtraDates object = new ExtraDates();
              object.setValue(new DateString(value));
              return object;
            }
            case "spatial_reference": { //$NON-NLS-1$
              final ExtraSpatialReference object = new ExtraSpatialReference();
              if (Objects.equals(value.trim(), "{}")) { //$NON-NLS-1$
                return object;
              }
              final SpatialReference result = this.objectUnmarshallerFactory.create(SpatialReference.class).unmarshal(
                  value);
              object.setValue(result);
              return object;
            }
            case "spatial": { //$NON-NLS-1$
              final ExtraGeometry object = new ExtraGeometry();
              object.setValue(new SpatialGeometryString(value));
              return object;
            }
            default: {
              final ExtraString object = new ExtraString();
              object.setValue(value);
              return object;
            }
          }
        } catch (final IOException exception) {
          logger.log(ILevel.DEBUG, "key '" + key + "' value '" + value + "', " + exception.getMessage(), exception); //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
          final ExtraString object = new ExtraString();
          object.setValue(value);
          return object;
        }
      };
      final Extra object = factory.create(key);
      object.setKey(key);
      return object;
    }
    final Extra object = new Extra();
    object.setKey(key);
    object.getValue();
    return object;
  }

}
