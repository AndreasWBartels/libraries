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

import java.time.Duration;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;

import org.dom4j.Element;
import org.dom4j.QName;

import net.anwiba.commons.utilities.time.ZonedDateTimeUtilities;

public interface IDomToObjectConverter<T> {

  T convert(Element element) throws DomConverterException;

  default Element element(final Element element, final QName name) throws DomConverterException {
    if (element == null) {
      throw new DomConverterException("Missing element for element '" + name + "'"); //$NON-NLS-1$ //$NON-NLS-2$
    }
    return element.element(name);
  }

  default Element element(final Element element, final String name) throws DomConverterException {
    if (element == null) {
      throw new DomConverterException("Missing element for element '" + name + "'"); //$NON-NLS-1$ //$NON-NLS-2$
    }
    return element.element(name);
  }

  default List<Element> elements(final Element element, final QName name) throws DomConverterException {
    if (element == null) {
      throw new DomConverterException("Missing element for elements '" + name + "'"); //$NON-NLS-1$ //$NON-NLS-2$
    }
    return element.elements(name);
  }

  default List<Element> elements(final Element element, final String name) throws DomConverterException {
    if (element == null) {
      throw new DomConverterException("Missing element for elements '" + name + "'"); //$NON-NLS-1$ //$NON-NLS-2$
    }
    return element.elements(name);
  }

  default String text(final Element element) throws DomConverterException {
    if (element == null) {
      throw new DomConverterException("Missing element"); //$NON-NLS-1$
    }
    return element.getText();
  }

  default String text(final Element element, final String defaultText) {
    if (element == null) {
      return defaultText;
    }
    return element.getTextTrim();
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

  default String value(final Element element, final String attributeName) throws DomConverterException {
    if (element == null) {
      throw new DomConverterException("Missing element for attribute '" + attributeName + "'"); //$NON-NLS-1$ //$NON-NLS-2$
    }
    final String attributeValue = element.attributeValue(attributeName);
    if (attributeValue == null) {
      throw new DomConverterException("Missing attribute '" + attributeName + "'"); //$NON-NLS-1$ //$NON-NLS-2$
    }
    return attributeValue;
  }

  default Duration durationValue(final Element element, final String attributeName) throws DomConverterException {
    if (element == null) {
      throw new DomConverterException("Missing element for attribute '" + attributeName + "'"); //$NON-NLS-1$ //$NON-NLS-2$
    }
    final String attributeValue = element.attributeValue(attributeName);
    if (attributeValue == null) {
      throw new DomConverterException("Missing attribute '" + attributeName + "', value '" + attributeValue + "'"); //$NON-NLS-1$ //$NON-NLS-2$
    }
    try {
      return Duration.parse(attributeValue);
    } catch (DateTimeParseException e) {
      throw new DomConverterException(e.getMessage(), e);
    }
  }

  default Duration durationValue(final Element element, final String attributeName, final Duration defaultValue)
      throws DomConverterException {
    if (element == null) {
      return defaultValue;
    }
    final String attributeValue = element.attributeValue(attributeName);
    if (attributeValue == null) {
      return defaultValue;
    }
    try {
      return Duration.parse(attributeValue);
    } catch (DateTimeParseException e) {
      throw new DomConverterException(e.getMessage(), e);
    }
  }

  default boolean booleanValue(final Element element, final String attributeName) throws DomConverterException {
    if (element == null) {
      throw new DomConverterException("Missing element for attribute '" + attributeName + "'"); //$NON-NLS-1$ //$NON-NLS-2$
    }
    final String attributeValue = element.attributeValue(attributeName);
    if (attributeValue == null) {
      throw new DomConverterException("Missing attribute '" + attributeName + "', value '" + attributeValue + "'"); //$NON-NLS-1$ //$NON-NLS-2$
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

  default int intValue(final Element element, final int defaultValue) throws DomConverterException {
    if (element == null) {
      return defaultValue;
    }
    final String value = element.getTextTrim();
    if (value == null) {
      return defaultValue;
    }
    try {
      return Integer.valueOf(value);
    } catch (final NumberFormatException exception) {
      throw new DomConverterException(
          "Number format exception for attribut '" + element.getName() + "', value '" + value + "'");
    }
  }

  default int intValue(final Element element, final String attributeName) throws DomConverterException {
    if (element == null) {
      throw new DomConverterException("Missing element for attribute '" + attributeName + "'"); //$NON-NLS-1$ //$NON-NLS-2$
    }
    final String attributeValue = element.attributeValue(attributeName);
    if (attributeValue == null) {
      throw new DomConverterException("Missing attribute '" + attributeName + "', value '" + attributeValue + "'"); //$NON-NLS-1$ //$NON-NLS-2$
    }
    return Integer.valueOf(attributeValue);
  }

  default int intValue(final Element element, final String attributeName, final int defaultValue)
      throws DomConverterException {
    if (element == null) {
      return defaultValue;
    }
    final String attributeValue = element.attributeValue(attributeName);
    if (attributeValue == null) {
      return defaultValue;
    }
    try {
      return Integer.valueOf(attributeValue);
    } catch (final NumberFormatException exception) {
      throw new DomConverterException(
          "Number format exception for attribut '" + attributeName + "', value '" + attributeValue + "'");
    }
  }

  default float floatValue(final Element element, final String attributeName, final float defaultValue)
      throws DomConverterException {
    if (element == null) {
      return defaultValue;
    }
    final String attributeValue = element.attributeValue(attributeName);
    if (attributeValue == null) {
      return defaultValue;
    }
    try {
      return Float.valueOf(attributeValue);
    } catch (final NumberFormatException exception) {
      throw new DomConverterException(
          "Number format exception for attribut '" + attributeName + "', value '" + attributeValue + "'");
    }
  }

  default double doubleValue(final Element element, final String attributeName) throws DomConverterException {
    if (element == null) {
      throw new DomConverterException("Missing element for attribute '" + attributeName + "'"); //$NON-NLS-1$ //$NON-NLS-2$
    }
    final String attributeValue = element.attributeValue(attributeName);
    if (attributeValue == null) {
      throw new DomConverterException("Missing attribute '" + attributeName + "'"); //$NON-NLS-1$ //$NON-NLS-2$
    }
    try {
      return Double.valueOf(attributeValue);
    } catch (final NumberFormatException exception) {
      throw new DomConverterException(
          "Number format exception for attribut '" + attributeName + "', value '" + attributeValue + "'");
    }
  }

  default double doubleValue(final Element element, final String attributeName, final double defaultValue)
      throws DomConverterException {
    if (element == null) {
      return defaultValue;
    }
    final String attributeValue = element.attributeValue(attributeName);
    if (attributeValue == null) {
      return defaultValue;
    }
    try {
      return Double.valueOf(attributeValue);
    } catch (final NumberFormatException exception) {
      throw new DomConverterException(
          "Number format exception for attribut '" + attributeName + "', value '" + attributeValue + "'");
    }
  }

  default double doubleValue(final Element element, final double defaultValue) throws DomConverterException {
    if (element == null) {
      return defaultValue;
    }
    final String value = element.getTextTrim();
    if (value == null) {
      return defaultValue;
    }
    try {
      return Double.valueOf(value);
    } catch (final NumberFormatException exception) {
      throw new DomConverterException(
          "Number format exception for attribut '" + element.getName() + "', value '" + value + "'");
    }
  }

  default ZonedDateTime dateTimeValue(final Element element, final String attributeName) throws DomConverterException {
    if (element == null) {
      throw new DomConverterException("Missing element for attribute '" + attributeName + "'"); //$NON-NLS-1$ //$NON-NLS-2$
    }
    final String attributeValue = element.attributeValue(attributeName);
    if (attributeValue == null) {
      throw new DomConverterException("Missing attribute '" + attributeName + "'"); //$NON-NLS-1$ //$NON-NLS-2$
    }
    try {
      return ZonedDateTimeUtilities.valueOf(attributeValue);
    } catch (final DateTimeParseException exception) {
      throw new DomConverterException(
          "Date time parse exception for attribut '" + attributeName + "', value '" + attributeValue + "'");
    }
  }

  default <V> void addTo(final List<V> list, final V value) {
    if (value == null) {
      return;
    }
    list.add(value);
  }
}
