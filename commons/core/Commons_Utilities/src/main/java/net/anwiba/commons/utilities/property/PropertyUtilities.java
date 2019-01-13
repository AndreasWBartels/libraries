/*
 * #%L
 * anwiba commons core
 * %%
 * Copyright (C) 2007 - 2016 Andreas Bartels
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
package net.anwiba.commons.utilities.property;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import net.anwiba.commons.logging.ILevel;

public class PropertyUtilities {

  private static net.anwiba.commons.logging.ILogger logger = net.anwiba.commons.logging.Logging
      .getLogger(PropertyUtilities.class);

  public static Properties getProperties(final String propertiesFile) {

    final Properties properties = new Properties(System.getProperties());

    if (propertiesFile != null) {
      try (InputStream inputstream = new FileInputStream(propertiesFile)) {
        properties.load(inputstream);
        System.setProperties(properties);
      } catch (final FileNotFoundException exception) {
        logger.log(ILevel.WARNING, propertiesFile + " ist nicht vorhanden"); //$NON-NLS-1$
        return null;
      } catch (final IOException exception) {
        logger.log(ILevel.WARNING, propertiesFile + " konnte nicht geladen werden"); //$NON-NLS-1$
        return null;
      }
    }
    return properties;
  }

}
