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
module net.anwiba.tools.definition.schema.json {
  exports net.anwiba.tools.definition.schema.json.gramma.parser;
  exports net.anwiba.tools.definition.schema.json.generator.java.bean;
  exports net.anwiba.tools.definition.schema.json;
  exports net.anwiba.tools.definition.schema.json.generator.java.maven;
  exports net.anwiba.tools.definition.schema.json.gramma;
  exports net.anwiba.tools.definition.schema.json.generator.java.ant;
  exports net.anwiba.tools.definition.schema.json.gramma.element;

  requires ant;
  requires com.sun.codemodel;
  requires com.fasterxml.jackson.annotation;
  requires com.fasterxml.jackson.databind;
  requires maven.model;
  requires maven.plugin.annotations;
  requires maven.plugin.api;
  requires maven.project;
  requires net.anwiba.commons.ensure;
  requires net.anwiba.commons.lang;
  requires net.anwiba.commons.logging;
  requires net.anwiba.commons.reference;
  requires net.anwiba.commons.utilities;
  requires net.anwiba.tools.generator.java.bean;
  requires org.antlr.antlr4.runtime;
  requires plexus.build.api;
}
