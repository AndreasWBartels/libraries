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

import java.text.MessageFormat;

import org.dom4j.Attribute;
import org.dom4j.Element;
import org.dom4j.QName;

public class DomToObjectConverterUtilities {

  private static final String FALSE = "FALSE"; //$NON-NLS-1$
  private static final String TRUE = "TRUE"; //$NON-NLS-1$

  public static String getStringValue(final Element element, final String attributeName) throws DomConverterException {
    final Attribute attribute = element.attribute(attributeName);
    if (attribute == null) {
      throw new DomConverterException(MessageFormat.format(
          "Missing attribute {0} in element {1}", attributeName, element.getName())); //$NON-NLS-1$
    }
    return attribute.getValue();
  }

  public static int getIntValue(final Element element, final String attributeName) throws DomConverterException {
    final Attribute attribute = element.attribute(attributeName);
    if (attribute == null) {
      throw new DomConverterException(MessageFormat.format(
          "Missing attribute {0} in element {1}", attributeName, element.getName())); //$NON-NLS-1$
    }
    try {
      return Integer.parseInt(attribute.getValue());
    } catch (final NumberFormatException exception) {
      throw new DomConverterException(MessageFormat.format(
          "Number format exception in element {0} attribute {1} and value {2} ", //$NON-NLS-1$
          element.getName(),
          attributeName,
          attribute.getValue()), exception);
    }
  }

  public static long getLongValue(final Element element, final String attributeName) throws DomConverterException {
    final Attribute attribute = element.attribute(attributeName);
    if (attribute == null) {
      throw new DomConverterException(MessageFormat.format(
          "Missing attribute {0} in element {1}", attributeName, element.getName())); //$NON-NLS-1$
    }
    try {
      return Long.parseLong(attribute.getValue());
    } catch (final NumberFormatException exception) {
      throw new DomConverterException(MessageFormat.format(
          "Number format exception in element {0} attribute {1} and value {2} ", //$NON-NLS-1$
          element.getName(),
          attributeName,
          attribute.getValue()), exception);
    }
  }

  public static boolean getBooleanValue(final Element element, final String attributeName) throws DomConverterException {
    final Attribute attribute = element.attribute(attributeName);
    if (attribute == null) {
      throw new DomConverterException(MessageFormat.format(
          "Missing attribute {0} in element {1}", attributeName, element.getName())); //$NON-NLS-1$
    }
    final String value = attribute.getValue();
    if (TRUE.equalsIgnoreCase(value)) {
      return true;
    }
    if (FALSE.equalsIgnoreCase(value)) {
      return false;
    }
    throw new DomConverterException(MessageFormat.format(
        "Boolean format exception in element {0} attribute {1} and value {2} ", //$NON-NLS-1$
        element.getName(),
        attributeName,
        value));
  }

  public static String getText(final Element element, final QName tagName) throws DomConverterException {
    final Element textElement = element.element(tagName);
    if (textElement == null) {
      throw new DomConverterException(MessageFormat.format("Missing element {0}", element.getName())); //$NON-NLS-1$
    }
    return textElement.getTextTrim();
  }

  public static String getOptionalText(final Element element, final QName tagName) {
    final Element textElement = element.element(tagName);
    if (textElement == null) {
      return null;
    }
    return textElement.getTextTrim();
  }

  public static String getText(final Element element, final QName tagName, final String defaultValue) {
    final Element textElement = element.element(tagName);
    if (textElement == null) {
      return defaultValue;
    }
    return textElement.getTextTrim();
  }

  public static double getNecessaryDoubleValue(final Element element) throws DomConverterException {
    if (element == null) {
      throw new DomConverterException("missing tag"); //$NON-NLS-1$
    }
    try {
      final String text = element.getTextTrim();
      return Double.parseDouble(text);
    } catch (final NumberFormatException exception) {
      throw new DomConverterException(exception);
    }
  }

  public static double getDoubleValue(final Element element, final double defaultValue) throws DomConverterException {
    if (element == null) {
      return defaultValue;
    }
    try {
      final String text = element.getTextTrim();
      return Double.parseDouble(text);
    } catch (final NumberFormatException exception) {
      throw new DomConverterException(exception);
    }
  }

  public static int getNecessaryIntValue(final Element element) throws DomConverterException {
    if (element == null) {
      throw new DomConverterException("missing tag"); //$NON-NLS-1$
    }
    try {
      final String text = element.getTextTrim();
      return Integer.parseInt(text);
    } catch (final NumberFormatException exception) {
      throw new DomConverterException(exception);
    }
  }

  public static int getIntValue(final Element element, final int defaultValue) throws DomConverterException {
    if (element == null) {
      return defaultValue;
    }
    try {
      final String text = element.getTextTrim();
      return Integer.parseInt(text);
    } catch (final NumberFormatException exception) {
      throw new DomConverterException(exception);
    }
  }

  public static Element getNecessaryElement(final Element element, final String tag) throws DomConverterException {
    if (element == null) {
      throw new DomConverterException("missing tag"); //$NON-NLS-1$
    }
    final Element tagElement = element.element(tag);
    if (tagElement == null) {
      throw new DomConverterException(MessageFormat.format("missing tag: ''{0}''", tag)); //$NON-NLS-1$
    }
    return tagElement;
  }
}