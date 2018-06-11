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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.UnmarshallerHandler;
import javax.xml.transform.Result;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import net.anwiba.commons.lang.io.NoneClosingInputStream;
import net.anwiba.commons.utilities.parameter.IParameter;
import net.anwiba.commons.utilities.parameter.IParameters;

public class JaxbTransformer<T> {

  private final SAXTransformerFactory transformerFactory;
  private final JAXBContext jaxbContext;
  private final Templates inputTransformerTemplate;
  private final Templates outputTransformerTemplate;

  public JaxbTransformer(
      final SAXTransformerFactory transformerFactory,
      final Templates inputTransformerTemplate,
      final Templates outputTransformerTemplate,
      final JAXBContext jaxbContext) {
    this.transformerFactory = transformerFactory;
    this.inputTransformerTemplate = inputTransformerTemplate;
    this.outputTransformerTemplate = outputTransformerTemplate;
    this.jaxbContext = jaxbContext;
  }

  public void marshall(final T bindingObject, final OutputStream outputStream)
      throws JAXBException,
      TransformerException {
    final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    final Marshaller marshaller = this.jaxbContext.createMarshaller();
    final Result outputResult = new StreamResult(byteArrayOutputStream);
    final TransformerHandler handler = this.transformerFactory.newTransformerHandler(this.outputTransformerTemplate);
    handler.setResult(outputResult);
    marshaller.marshal(bindingObject, handler);
    try {
      final byte[] byteArray = byteArrayOutputStream.toByteArray();
      outputStream.write(byteArray);
    } catch (final IOException e) {
    }
  }

  @SuppressWarnings({ "unchecked", "resource" })
  public T unmarshall(final InputStream inputStream, final IParameters parameters)
      throws JAXBException,
      TransformerException {
    final Unmarshaller unmarshaller = this.jaxbContext.createUnmarshaller();
    final UnmarshallerHandler unmarshallerHandler = unmarshaller.getUnmarshallerHandler();
    final Transformer transformer = this.inputTransformerTemplate.newTransformer();

    for (final IParameter parameter : parameters.parameters()) {
      transformer.setParameter(parameter.getName(), parameter.getValue());
    }
    final SAXResult saxResult = new SAXResult(unmarshallerHandler);
    final StreamSource streamSource = new StreamSource(new NoneClosingInputStream(inputStream));
    transformer.transform(streamSource, saxResult);
    final Object result = unmarshallerHandler.getResult();
    return (T) result;
  }
}
