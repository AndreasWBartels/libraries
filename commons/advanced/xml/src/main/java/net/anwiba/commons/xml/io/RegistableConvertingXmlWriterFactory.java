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

package net.anwiba.commons.xml.io;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.transform.Templates;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.transform.sax.SAXTransformerFactory;

import net.anwiba.commons.lang.exception.CreationException;
import net.anwiba.commons.lang.functional.IApplicable;
import net.anwiba.commons.lang.functional.IFunction;
import net.anwiba.commons.reference.utilities.IoUtilities;
import net.anwiba.commons.utilities.parameter.IParameters;
import net.anwiba.commons.utilities.property.IProperties;
import net.anwiba.commons.xml.jaxb.IJaxbContext;
import net.anwiba.commons.xml.jaxb.JaxbTransformer;

public class RegistableConvertingXmlWriterFactory {

  private static net.anwiba.commons.logging.ILogger logger = net.anwiba.commons.logging.Logging
      .getLogger(RegistableConvertingXmlWriterFactory.class.getName());

  private final TemplateFactory templateFactory;

  public RegistableConvertingXmlWriterFactory() {
    this(new IFunction<String, InputStream, IOException>() {

      @Override
      public InputStream execute(final String script) throws IOException {
        try (InputStream stream = this.getClass().getResourceAsStream(script)) {
          final byte[] byteArray = IoUtilities.toByteArray(stream);
          ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArray);
          byteArrayInputStream.mark(byteArray.length + 1);
          return byteArrayInputStream;
        } catch (IOException exception) {
          return null;
        }
      }
    });
  }

  public RegistableConvertingXmlWriterFactory(final IFunction<String, InputStream, IOException> connector) {
    this.templateFactory = new TemplateFactory(connector);
  }

  public <C, T> IRegistableConvertingXmlWriter<C, T> create(
      final IApplicable<C> applicable,
      final URIResolver uriResolver,
      final String outputXsltScript,
      final IJaxbContext jaxbContext)
      throws CreationException {

    try {
      final SAXTransformerFactory transformerFactory = (SAXTransformerFactory) TransformerFactory.newInstance();
      transformerFactory.setURIResolver(uriResolver);
      final Templates outputTransformerTemplate = this.templateFactory.create(outputXsltScript, transformerFactory);
      final JaxbTransformer<T> jaxbTransformer = new JaxbTransformer<>(
          transformerFactory,
          null,
          outputTransformerTemplate,
          JAXBContext.newInstance(jaxbContext.getContextPath(), jaxbContext.getClassLoader()));

      return new IRegistableConvertingXmlWriter<C, T>() {

        @Override
        public boolean isApplicable(final C context) {
          return applicable.isApplicable(context);
        }

        @Override
        public void write(final T object,
            final IParameters parameters,
            final OutputStream outputStream,
            final IProperties outputProperties)
            throws IOException {
          try {
            jaxbTransformer.marshall(object, parameters, outputStream, outputProperties);
          } catch (JAXBException | TransformerException exception) {
            throw new IOException(exception);
          }
        }
      };
    } catch (TransformerConfigurationException | JAXBException exception) {
      throw new CreationException(
          "Couldn't create xml converting persister for binding '" + Arrays.toString(jaxbContext.getObjectFactories()) //$NON-NLS-1$
              + ",", //$NON-NLS-1$
          exception);
    } catch (final IOException exception) {
      throw new CreationException(
          "Couldn't create xml converting persister with xslt script '" + outputXsltScript + "',", //$NON-NLS-1$//$NON-NLS-2$
          exception);
    }
  }
}
