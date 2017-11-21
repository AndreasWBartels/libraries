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

import java.util.List;

import org.dom4j.Element;
import org.dom4j.QName;

public interface IDomToObjectConverter<T> {

  T convert(Element element) throws DomConverterException;

  default List<Element> elements(final Element element, final QName name) {
    return element.elements(name);
  }

  default String value(final Element element, final String attributeName, final String defaultValue) {
    if (element == null) {
      return defaultValue;
    }
    final String attributeValue = element.attributeValue(attributeName);
    if (attributeValue == null) {
      return defaultValue;
    }
    return attributeValue;
  }

  default boolean booleanValue(final Element element, final String attributeName) throws DomConverterException {
    final String attributeValue = element.attributeValue(attributeName);
    if (attributeValue == null) {
      throw new DomConverterException("Missing attribute '" + attributeName + "'"); //$NON-NLS-1$ //$NON-NLS-2$
    }
    return Boolean.valueOf(attributeValue);
  }

  default boolean booleanValue(final Element element, final String attributeName, final boolean defaultValue) {
    if (element == null) {
      return defaultValue;
    }
    final String attributeValue = element.attributeValue(attributeName);
    if (attributeValue == null) {
      return defaultValue;
    }
    return Boolean.valueOf(attributeValue);
  }

  default int intValue(final Element element, final String attributeName) throws DomConverterException {
    final String attributeValue = element.attributeValue(attributeName);
    if (attributeValue == null) {
      throw new DomConverterException("Missing attribute '" + attributeName + "'"); //$NON-NLS-1$ //$NON-NLS-2$
    }
    return Integer.valueOf(attributeValue);
  }

  default int intValue(final Element element, final String attributeName, final int defaultValue) {
    final String attributeValue = element.attributeValue(attributeName);
    if (attributeValue == null) {
      return defaultValue;
    }
    return Integer.valueOf(attributeValue);
  }

  default double doubleValue(final Element element, final String attributeName) throws DomConverterException {
    final String attributeValue = element.attributeValue(attributeName);
    if (attributeValue == null) {
      throw new DomConverterException("Missing attribute '" + attributeName + "'"); //$NON-NLS-1$ //$NON-NLS-2$
    }
    return Double.valueOf(attributeValue);
  }

  default double doubleValue(final Element element, final String attributeName, final double defaultValue) {
    if (element == null) {
      return defaultValue;
    }
    final String attributeValue = element.attributeValue(attributeName);
    if (attributeValue == null) {
      return defaultValue;
    }
    return Double.valueOf(attributeValue);
  }

  default <V> void addTo(final List<V> list, final V value) {
    if (value == null) {
      return;
    }
    list.add(value);
  }
}