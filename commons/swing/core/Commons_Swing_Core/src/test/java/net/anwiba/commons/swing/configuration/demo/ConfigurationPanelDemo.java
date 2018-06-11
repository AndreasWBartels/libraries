/*
 * #%L
 * anwiba commons swing
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
package net.anwiba.commons.swing.configuration.demo;

import java.util.ArrayList;
import java.util.List;

import org.junit.runner.RunWith;

import de.jdemo.annotation.Demo;
import de.jdemo.extensions.SwingDemoCase;
import de.jdemo.junit.DemoAsTestRunner;
import net.anwiba.commons.swing.configuration.Configuration;
import net.anwiba.commons.swing.configuration.ConfigurationPanel;
import net.anwiba.commons.swing.configuration.IConfiguration;
import net.anwiba.commons.utilities.parameter.IParameter;
import net.anwiba.commons.utilities.parameter.IParameters;
import net.anwiba.commons.utilities.parameter.Parameter;
import net.anwiba.commons.utilities.parameter.ParametersBuilder;

@RunWith(DemoAsTestRunner.class)
public class ConfigurationPanelDemo extends SwingDemoCase {

  @Demo
  public void demo() {
    final List<IConfiguration> configuration = createConfiguration();
    show(new ConfigurationPanel(configuration));
  }

  private List<IConfiguration> createConfiguration() {
    final List<IConfiguration> configurations = new ArrayList<>();
    configurations.add(new Configuration("Configuration1", createParameters(2))); //$NON-NLS-1$
    configurations.add(new Configuration("Configuration2", createParameters(10))); //$NON-NLS-1$
    return configurations;
  }

  private IParameters createParameters(final int number) {
    final ArrayList<IParameter> parameters = new ArrayList<>();
    for (int i = 0; i < number; i++) {
      parameters.add(new Parameter("Name" + i, "Value" + 1)); //$NON-NLS-1$//$NON-NLS-2$
    }
    return new ParametersBuilder().add(parameters).build();
  }
}
