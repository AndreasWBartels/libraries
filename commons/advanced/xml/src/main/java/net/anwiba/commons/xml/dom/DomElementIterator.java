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
package net.anwiba.commons.xml.dom;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Function;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.QName;

import net.anwiba.commons.lang.functional.IAcceptor;
import net.anwiba.commons.lang.primitive.IBooleanProvider;
import net.anwiba.commons.reference.utilities.IoUtilities;
import net.anwiba.commons.utilities.io.IClosableConnector;
import net.anwiba.commons.utilities.io.IClosableIoIterator;

public class DomElementIterator<T> implements IClosableIoIterator<T> {

  private final IClosableConnector connector;
  private final IAcceptor<XMLEvent> acceptor;
  private final IDomToObjectConverter<T> converter;
  private IAcceptor<XMLEvent> errorAcceptor;
  private final Function<Element, IOException> exceptionFactory;

  private boolean isClosed = false;
  private XMLEventReader eventReader = null;
  private T object = null;
  private InputStream inputStream;
  private IBooleanProvider terminate;

  public DomElementIterator(final IClosableConnector connector,
      final IAcceptor<XMLEvent> acceptor,
      final IDomToObjectConverter<T> converter) {
    this(() -> false, connector, acceptor, converter, event -> false, element -> null);
  }

  public DomElementIterator(final IBooleanProvider terminate,
      final IClosableConnector connector,
      final IAcceptor<XMLEvent> acceptor,
      final IDomToObjectConverter<T> converter,
      final IAcceptor<XMLEvent> errorAcceptor,
      final Function<Element, IOException> errorConsumer) {
    this.terminate = terminate;
    this.connector = connector;
    this.acceptor = acceptor;
    this.converter = converter;
    this.errorAcceptor = errorAcceptor;
    this.exceptionFactory = errorConsumer;
  }

  @Override
  public void close() throws IOException {
    if (this.isClosed) {
      return;
    }
    try {
      IOException ioException = IoUtilities.close(() -> {
        if (this.eventReader != null) {
          try {
            this.eventReader.close();
          } catch (XMLStreamException exception) {
            throw new IOException(exception.getMessage(), exception);
          }
        }
      }, this.inputStream, this.connector);
      IoUtilities.throwIfNotNull(ioException);
    } finally {
      this.isClosed = true;
      this.object = null;
    }
  }

  @Override
  public boolean hasNext() throws IOException {
    if (this.isClosed) {
      throw new IOException("Iterator is closed"); //$NON-NLS-1$
    }
    if (this.terminate.isTrue()) {
      return false;
    }
    if (this.object != null) {
      return true;
    }
    try {
      if (this.eventReader == null) {
        this.eventReader = initialize();
      }
      while (this.eventReader.hasNext()) {
        XMLEvent event = this.eventReader.nextEvent();
        if (event.isStartElement()) {
          if (this.errorAcceptor.accept(event)) {
            Element element = convert(event.asStartElement());
            IOException exception = this.exceptionFactory.apply(element);
            if (exception != null) {
              throw exception;
            }
          }
          if (this.acceptor.accept(event)) {
            Element element = convert(event.asStartElement());
            this.object = this.converter.convert(element);
            return this.object != null;
          }
        }
      }
      return false;
    } catch (XMLStreamException | DomConverterException | ClassCastException exception) {
      throw new IOException(exception.getMessage(), exception);
    }
  }

  private Element convert(final StartElement element) throws XMLStreamException, IOException {
    final Element resultElement = create(element);
    final javax.xml.namespace.QName qName = getQName(element);
    while (this.eventReader.hasNext()) {
      XMLEvent event = this.eventReader.nextEvent();
      if (event.isStartElement() && this.errorAcceptor.accept(event)) {
        Element childElement = convert(event.asStartElement());
        IOException exception = this.exceptionFactory.apply(childElement);
        if (exception != null) {
          throw exception;
        }
      }
      if (event.isEndElement() && Objects.equals(qName, getQName(event))) {
        return resultElement;
      }
      if (event.isStartElement()) {
        Element childElement = convert(event.asStartElement());
        resultElement.add(childElement);
      }
      if (event.isCharacters()) {
        final String data = event.asCharacters().getData();
        if (!data.isBlank()) {
          resultElement.add(DocumentHelper.createText(data));
        }
      }
    }
    return resultElement;
  }

  private javax.xml.namespace.QName getQName(final XMLEvent value) {
    javax.xml.namespace.QName name = value.isStartElement()
        ? value.asStartElement().getName()
        : value.isEndElement()
            ? value.asEndElement().getName()
        : null;
    return name;
  }

  private Element create(final StartElement element) {
    final Element resultElement = DocumentHelper.createElement(convert(element.getName()));
    Iterator<Attribute> attributes = element.getAttributes();
    while (attributes.hasNext()) {
      Attribute attribute = attributes.next();
      resultElement.addAttribute(convert(attribute.getName()), attribute.getValue());
    }
    return resultElement;
  }

  private QName convert(final javax.xml.namespace.QName name) {
    final Namespace namespace =
        name.getPrefix() != null && name.getNamespaceURI() != null
            ? new Namespace(name.getPrefix(), name.getNamespaceURI())
            : Namespace.NO_NAMESPACE;
    return new QName(name.getLocalPart(), namespace);
  }

  private XMLEventReader initialize() throws FactoryConfigurationError, XMLStreamException, IOException {
    final XMLInputFactory inputFactory = XMLInputFactory.newInstance();
    inputFactory.setProperty(XMLInputFactory.SUPPORT_DTD, Boolean.FALSE);
    this.inputStream = this.connector.connect();
    return inputFactory.createXMLEventReader(this.inputStream);
  }

  @Override
  public T next() throws IOException {
    try {
      if (hasNext()) {
        return this.object;
      }
      throw new NoSuchElementException();
    } finally {
      this.object = null;
    }
  }
}
