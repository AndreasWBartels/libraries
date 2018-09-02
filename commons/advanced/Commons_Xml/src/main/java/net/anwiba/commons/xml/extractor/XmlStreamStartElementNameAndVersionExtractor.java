/*
 * #%L
 * *
 * %%
 * Copyright (C) 2007 - 2017 Andreas W. Bartels
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
package net.anwiba.commons.xml.extractor;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import net.anwiba.commons.lang.functional.IFunction;
import net.anwiba.commons.lang.object.ObjectPair;
import net.anwiba.commons.lang.optional.Optional;
import net.anwiba.commons.version.IVersion;
import net.anwiba.commons.version.VersionBuilder;
import net.anwiba.commons.version.VersionUtilities;

public class XmlStreamStartElementNameAndVersionExtractor {

  private static final String VERSION = "version"; //$NON-NLS-1$

  public ObjectPair<String, IVersion> extract(final InputStream inputStream) throws IOException {
    return process(inputStream, event -> process(event));
  }

  private ObjectPair<String, IVersion> process(final StartElement element) {
    final QName attributeName = new QName(VERSION);
    final Attribute attribute = element.getAttributeByName(attributeName);
    return new ObjectPair<>(
        element.getName().getLocalPart(),
        attribute == null ? null : convertToVersion(attribute.getValue()));
  }

  public static <R, E extends Exception> R process(
      final InputStream inputStream,
      final IFunction<StartElement, R, E> function)
      throws E,
      IOException {
    try {
      inputStream.mark(Integer.MAX_VALUE);
      final XMLInputFactory inputFactory = XMLInputFactory.newInstance();
      inputFactory.setProperty(XMLInputFactory.SUPPORT_DTD, false);
      final XMLEventReader eventReader = inputFactory.createXMLEventReader(inputStream);
      while (eventReader.hasNext()) {
        final XMLEvent event = eventReader.nextEvent();
        if (event.isStartElement()) {
          return function.execute(event.asStartElement());
        }
      }
      return null;
    } catch (final XMLStreamException cause) {
      throw new IOException(cause);
    } finally {
      inputStream.reset();
    }
  }

  private IVersion convertToVersion(final String value) {
    return Optional
        .of(value)
        .convert(s -> VersionUtilities.valueOf(s))
        .convert(v -> Objects.equals(new VersionBuilder().build(), v) ? null : v)
        .getOr(() -> null);
  }
}
