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
module net.anwiba.commons.utilities {
  exports net.anwiba.commons.utilities.lang;
  exports net.anwiba.commons.utilities.color;
  exports net.anwiba.commons.utilities.number;
  exports net.anwiba.commons.utilities.factory;
  exports net.anwiba.commons.utilities.string;
  exports net.anwiba.commons.utilities.date;
  exports net.anwiba.commons.utilities.regex;
  exports net.anwiba.commons.utilities.collection;
  exports net.anwiba.commons.utilities.parameter;
  exports net.anwiba.commons.utilities.time;
  exports net.anwiba.commons.utilities.math;
  exports net.anwiba.commons.utilities.io.number;
  exports net.anwiba.commons.utilities;
  exports net.anwiba.commons.utilities.name;
  exports net.anwiba.commons.utilities.io;
  exports net.anwiba.commons.utilities.validation;
  exports net.anwiba.commons.utilities.regex.tokenizer;
  exports net.anwiba.commons.utilities.io.url.parser;
  exports net.anwiba.commons.utilities.scale;
  exports net.anwiba.commons.utilities.enumeration;
  exports net.anwiba.commons.utilities.interval;
  exports net.anwiba.commons.utilities.io.url;
  exports net.anwiba.commons.utilities.property;
  exports net.anwiba.commons.utilities.cache;
  exports net.anwiba.commons.utilities.boole;
  exports net.anwiba.commons.utilities.registry;
  exports net.anwiba.commons.utilities.provider;

  requires org.apache.commons.lang3;
  requires java.desktop;
  requires java.sql;
  requires net.anwiba.commons.ensure;
  requires net.anwiba.commons.lang;
  requires net.anwiba.commons.logging;
  requires net.anwiba.commons.reference;
  requires net.jafama;
}
