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
package net.anwiba.commons.xml.dom;

import org.dom4j.Attribute;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.QName;
import org.dom4j.tree.DefaultAttribute;

public class ObjectToDomConverterUtilities {

  public static Element createElement(final String elementName, final boolean value) {
    return createElement(elementName, Boolean.toString(value));
  }

  public static Element createElement(final String elementName, final String text) {
    return createElement(new QName(elementName), text);
  }

  public static Element createElement(final String elementName, final int value) {
    return createElement(elementName, Integer.toString(value));
  }

  public static Element createElement(final String elementName, final double value) {
    return createElement(elementName, Double.toString(value));
  }

  public static Attribute createAttribute(final String attributeName, final boolean value) {
    return createAttribute(attributeName, Boolean.toString(value));
  }

  public static Attribute createAttribute(final String attributeName, final Object value) {
    return createAttribute(attributeName, value == null ? null : value.toString());
  }

  public static Attribute createAttribute(final String attributeName, final int value) {
    return createAttribute(attributeName, Integer.toString(value));
  }

  public static Attribute createAttribute(final String attributeName, final String value) {
    return new DefaultAttribute(attributeName, value);
  }

  public static Attribute createAttribute(final String attributeName, final long value) {
    return createAttribute(attributeName, Long.toString(value));
  }

  public static Element createElement(final QName tagName, final String text) {
    final Element nameElement = DocumentHelper.createElement(tagName);
    nameElement.addText(text);
    return nameElement;
  }
}