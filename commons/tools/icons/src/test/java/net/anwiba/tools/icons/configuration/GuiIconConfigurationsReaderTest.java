/*
 * #%L
 * anwiba commons advanced
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
package net.anwiba.tools.icons.configuration;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

import org.junit.jupiter.api.Test;

public class GuiIconConfigurationsReaderTest {

  @Test
  public void add() throws Exception {
    final IOutput output = new IOutput() {

      @Override
      public void warn(final String message) {
        // nothing to do
      }

      @Override
      public void info(final String message) {
        // nothing to do
      }

      @Override
      public void error(final String message) {
        // nothing to do
      }

      @Override
      public void error(final String message, final Throwable throwable) {
        // nothing to do
      }
    };
    final ArrayList<File> imageResources = new ArrayList<>();
    imageResources.add(new File("src/test/resources/ProjectB/resources/test.jar")); //$NON-NLS-1$
    final IImageExistsValidator imageExistsValidator = new ImageExistsValidator(imageResources, output);
    final GuiIconConfigurationsReader reader = new GuiIconConfigurationsReader(imageExistsValidator, output);
    reader.add(new File("src/test/resources/ProjectB/resources/icons.xml")); //$NON-NLS-1$
    final Map<String, IconResource> configurations = reader.getIconConfigurations();
    assertThat(7, equalTo(configurations.size()));
    assertThat("cancel.png", equalTo(configurations.get("CANCEL_ICON").getImage())); //$NON-NLS-1$ //$NON-NLS-2$
    assertThat("exit.png", equalTo(configurations.get("EXIT_ICON").getImage())); //$NON-NLS-1$ //$NON-NLS-2$
    assertThat("reload.png", equalTo(configurations.get("RELOAD_ICON").getImage())); //$NON-NLS-1$ //$NON-NLS-2$
    assertThat("stop.png", equalTo(configurations.get("STOP_ICON").getImage())); //$NON-NLS-1$ //$NON-NLS-2$
    assertThat("foo.png", equalTo(configurations.get("FOO_ICON").getImage())); //$NON-NLS-1$ //$NON-NLS-2$
    final IconResource iconResource = configurations.get("WARNING_ICON"); //$NON-NLS-1$
    iconResource.getName();
  }
}
