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
package net.anwiba.commons.utilities.test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

import net.anwiba.commons.resource.utilities.IoUtilities;
import net.anwiba.commons.utilities.property.PropertyUtilities;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Test;

import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class PropertyUtilitiesTest {
  private static final class IsEquals extends BaseMatcher<Properties> {

    private final Properties properties;

    public IsEquals(final Properties properties) {
      this.properties = properties;
    }

    @Override
    public void describeTo(final Description description) {
      // nothing to do
    }

    @Override
    public boolean matches(final Object item) {
      return this.properties.equals(item);
    }
  }

  @Test
  public void testGetProperties() throws IOException {
    FileWriter writer = null;
    try {
      final File propertiesFile = File.createTempFile("tmp", ".properties"); //$NON-NLS-1$ //$NON-NLS-2$
      final Properties properties = new Properties();
      properties.put("key", "value"); //$NON-NLS-1$ //$NON-NLS-2$
      writer = new FileWriter(propertiesFile);
      properties.store(writer, "Test properties file"); //$NON-NLS-1$
      assertThat(properties, new IsEquals(PropertyUtilities.getProperties(propertiesFile.getCanonicalPath())));
    } finally {
      IoUtilities.close(writer);
    }
    assertTrue(true);
  }
}