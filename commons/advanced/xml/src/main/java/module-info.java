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
module net.anwiba.commons.xml {
  exports net.anwiba.commons.xml.extractor;
  exports net.anwiba.commons.xml.xsd;
  exports net.anwiba.commons.xml.io;
  exports net.anwiba.commons.xml.dom;
  exports net.anwiba.commons.xml.jaxb;

  requires dom4j;
  requires java.xml;
  requires java.xml.bind;
  requires net.anwiba.commons.lang;
  requires net.anwiba.commons.logging;
  requires net.anwiba.commons.reference;
  requires net.anwiba.commons.utilities;
  requires net.anwiba.commons.version;
}
