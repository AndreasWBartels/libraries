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
package net.anwiba.commons.xml.xsd;

import java.util.List;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.QName;

public class XsdElementsFactory {

  public static final String ANNOTATION = "annotation"; //$NON-NLS-1$
  public static final String APPINFO = "appinfo"; //$NON-NLS-1$
  public static final String BASE = "base"; //$NON-NLS-1$
  public static final String CHOICE = "choice"; //$NON-NLS-1$
  public static final String CLASS = "class"; //$NON-NLS-1$
  public static final String COMPLEX_TYPE = "complexType"; //$NON-NLS-1$
  public static final String ELEMENT = "element"; //$NON-NLS-1$
  public static final String ELEMENT_FORM_DEFAULT = "elementFormDefault"; //$NON-NLS-1$
  public static final String GENERATE_IS_SET_METHOD = "generateIsSetMethod"; //$NON-NLS-1$
  public static final String GLOBAL_BINDINGS = "globalBindings"; //$NON-NLS-1$
  public static final String MAX_OCCURS = "maxOccurs"; //$NON-NLS-1$
  public static final String MIN_OCCURS = "minOccurs"; //$NON-NLS-1$
  public static final String NAME = "name"; //$NON-NLS-1$
  public static final String PACKAGE = "package"; //$NON-NLS-1$
  public static final String QUALIFIED = "qualified"; //$NON-NLS-1$
  public static final String RESTRICTION = "restriction"; //$NON-NLS-1$
  public static final String SCHEMA_BINDINGS = "schemaBindings"; //$NON-NLS-1$
  public static final String SEQUENCE = "sequence"; //$NON-NLS-1$
  public static final String SIMPLE_TYPE = "simpleType"; //$NON-NLS-1$
  public static final String TRUE = "true"; //$NON-NLS-1$
  public static final String TYPE = "type"; //$NON-NLS-1$
  public static final String UNBOUNDED = "unbounded"; //$NON-NLS-1$
  public static final String VERSION = "version"; //$NON-NLS-1$
  public static final Namespace XSD_NAMESPACE = new Namespace("xsd", "http://www.w3.org/2001/XMLSchema"); //$NON-NLS-1$ //$NON-NLS-2$
  public static final QName SCHEMA = new QName("schema", XSD_NAMESPACE); //$NON-NLS-1$

  public static Element createSchemaElement() {
    final Element element = DocumentHelper.createElement(SCHEMA);
    element.addAttribute(createName(ELEMENT_FORM_DEFAULT), QUALIFIED);
    return element;
  }

  public static Element createElement(
      final String name,
      final String contentTypeName,
      final int minimumOccurs,
      final int maximumOccurs) {
    final Element element = DocumentHelper.createElement(createXsdName(ELEMENT));
    element.addAttribute(createName(NAME), name);
    element.addAttribute(createName(TYPE), contentTypeName);
    element.addAttribute(createName(MIN_OCCURS), String.valueOf(minimumOccurs));
    element.addAttribute(
        createName(MAX_OCCURS),
        Integer.MAX_VALUE == maximumOccurs ? UNBOUNDED : String.valueOf(maximumOccurs));
    return element;
  }

  public static Element createSimpleTypeElement(final String typeName, final String baseTypeName) {
    final Element restrictionElement = DocumentHelper.createElement(createXsdName(RESTRICTION));
    restrictionElement.addAttribute(createName(BASE), baseTypeName);
    final Element simpleTypeElement = DocumentHelper.createElement(createXsdName(SIMPLE_TYPE));
    simpleTypeElement.addAttribute(createName(NAME), typeName);
    simpleTypeElement.add(restrictionElement);
    return simpleTypeElement;
  }

  public static Element createComplexTypeElement(final String typeName, final Element structurElement) {
    final Element complexTypeElement = DocumentHelper.createElement(createXsdName(COMPLEX_TYPE));
    complexTypeElement.addAttribute(createName(NAME), typeName);
    complexTypeElement.add(structurElement);
    return complexTypeElement;
  }

  public static Element createSequenceElement(final List<Element> elements) {
    final Element sequenceElement = DocumentHelper.createElement(createXsdName(SEQUENCE));
    for (final Element element : elements) {
      sequenceElement.add(element);
    }
    return sequenceElement;
  }

  public static Element createChoiceElement(final List<Element> elements) {
    final Element sequenceElement = DocumentHelper.createElement(createXsdName(CHOICE));
    for (final Element element : elements) {
      sequenceElement.add(element);
    }
    return sequenceElement;
  }

  public static Element createApplicationInfoAnnotation(final Element... elements) {
    final Element annotationElement = DocumentHelper.createElement(createXsdName(ANNOTATION));
    final Element applicationInfoElement = DocumentHelper.createElement(createXsdName(APPINFO));
    for (final Element element : elements) {
      applicationInfoElement.add(element);
    }
    annotationElement.add(applicationInfoElement);
    return annotationElement;
  }

  public static QName createName(final String name) {
    return new QName(name);
  }

  public static QName createXsdName(final String name) {
    return new QName(name, XSD_NAMESPACE);
  }

}