/*
 * #%L
 * anwiba commons
 * %%
 * Copyright (C) 2007 - 2021 Andreas W. Bartels
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
module net.anwiba.tools.icons {
  exports net.anwiba.tools.icons.configuration;
  exports net.anwiba.tools.icons.generator;
  exports net.anwiba.tools.icons.generator.ant;
  exports net.anwiba.tools.icons.generator.maven;
  exports net.anwiba.tools.icons.schema.configuration;
  exports net.anwiba.tools.icons.schema.eclipse.classpath;
  exports net.anwiba.tools.icons.utilities;

  requires ant;
  requires java.xml;
  requires jakarta.xml.bind;
  requires net.anwiba.commons.ensure;
  requires net.anwiba.commons.lang;
  requires net.anwiba.commons.reference;
  requires net.anwiba.commons.utilities;
  requires maven.plugin.annotations;
  requires maven.plugin.api;
  requires maven.project;
  requires plexus.build.api;

  opens net.anwiba.tools.icons.schema.configuration to jakarta.xml.bind;
  opens net.anwiba.tools.icons.schema.eclipse.classpath to jakarta.xml.bind;
}
