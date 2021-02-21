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
package net.anwiba.commons.xml.jaxb;

import static net.anwiba.commons.xml.xsd.XsdElementsFactory.*;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.QName;

import net.anwiba.commons.xml.xsd.XsdElementsFactory;

public class JaxbElementsFactory {

  public static final Namespace JAXB_NAMESPACE = new Namespace("jaxb", "http://java.sun.com/xml/ns/jaxb"); //$NON-NLS-1$ //$NON-NLS-2$

  public static Element createSchemaElement(final String packageName) {
    final Element element = DocumentHelper.createElement(SCHEMA);
    element.add(JAXB_NAMESPACE);
    element.addAttribute(createName(ELEMENT_FORM_DEFAULT), QUALIFIED);
    element.addAttribute(createJaxbName(VERSION), "1.0"); //$NON-NLS-1$
    element.add(createGlobalBindingsAnnotation(packageName));
    return element;
  }

  public static Element createClassAnnotation(final String className) {
    final Element element = DocumentHelper.createElement(createJaxbName(CLASS));
    element.addAttribute(createName(NAME), className);
    return XsdElementsFactory.createApplicationInfoAnnotation(element);
  }

  public static Element createGlobalBindingsAnnotation(final String packageName) {
    final Element element = DocumentHelper.createElement(createJaxbName(GLOBAL_BINDINGS));
    element.addAttribute(XsdElementsFactory.createName(GENERATE_IS_SET_METHOD), TRUE);
    if (packageName == null) {
      return createApplicationInfoAnnotation(element);
    }
    final Element schemaBindingsElement = createSchemaBindingsElement(packageName);
    return createApplicationInfoAnnotation(element, schemaBindingsElement);
  }

  public static Element createSchemaBindingsElement(final String packageName) {
    final Element schemaBindingsElement = DocumentHelper.createElement(createJaxbName(SCHEMA_BINDINGS));
    final Element packageElement = DocumentHelper.createElement(createJaxbName(PACKAGE));
    packageElement.addAttribute(createName(NAME), packageName);
    schemaBindingsElement.add(packageElement);
    return schemaBindingsElement;
  }

  public static QName createJaxbName(final String name) {
    return new QName(name, JAXB_NAMESPACE);
  }

  public static Element createSimpleTypeElement(final String typeName, final String className, final String baseTypeName) {
    final Element restrictionElement = DocumentHelper.createElement(XsdElementsFactory
        .createXsdName(XsdElementsFactory.RESTRICTION));
    restrictionElement.addAttribute(XsdElementsFactory.createName(XsdElementsFactory.BASE), baseTypeName);
    final Element simpleTypeElement = DocumentHelper.createElement(XsdElementsFactory
        .createXsdName(XsdElementsFactory.SIMPLE_TYPE));
    simpleTypeElement.addAttribute(XsdElementsFactory.createName(XsdElementsFactory.NAME), typeName);
    simpleTypeElement.add(createClassAnnotation(className));
    simpleTypeElement.add(restrictionElement);
    return simpleTypeElement;
  }

  public static Element createComplexTypeElement(
      final String typeName,
      final String className,
      final Element structurElement) {
    final Element complexTypeElement = DocumentHelper.createElement(XsdElementsFactory
        .createXsdName(XsdElementsFactory.COMPLEX_TYPE));
    complexTypeElement.addAttribute(XsdElementsFactory.createName(XsdElementsFactory.NAME), typeName);
    complexTypeElement.add(createClassAnnotation(className));
    complexTypeElement.add(structurElement);
    return complexTypeElement;
  }

}