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

import javax.xml.transform.Source;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.SAXException;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;

public class UnmarshallerFactory {
  public Unmarshaller create(final IJaxbContext jaxbContext) throws JAXBException, SAXException {
    final JAXBContext context = JAXBContext.newInstance(jaxbContext.getContextPath(), jaxbContext.getClassLoader());
    final Unmarshaller unmarshaller = context.createUnmarshaller();
    final SchemaFactory schemaFactory = SchemaFactory.newInstance(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI);
    final Source[] sources = jaxbContext.getSchemaSources();
    final Schema schema = schemaFactory.newSchema(sources);
    unmarshaller.setSchema(schema);
    unmarshaller.setEventHandler(new jakarta.xml.bind.helpers.DefaultValidationEventHandler());
    return unmarshaller;
  }

}