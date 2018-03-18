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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.transform.Templates;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.stream.StreamSource;

import net.anwiba.commons.lang.exception.CreationException;
import net.anwiba.commons.lang.functional.IApplicable;
import net.anwiba.commons.lang.functional.IFunction;
import net.anwiba.commons.lang.io.NoneClosingInputStream;
import net.anwiba.commons.logging.ILevel;
import net.anwiba.commons.utilities.parameter.IParameters;
import net.anwiba.commons.xml.jaxb.IJaxbContext;
import net.anwiba.commons.xml.jaxb.JaxbContextBuilder;
import net.anwiba.commons.xml.jaxb.JaxbTransformer;

public class RegistableConvertingXmlReaderFactory {

  private static net.anwiba.commons.logging.ILogger logger = net.anwiba.commons.logging.Logging
      .getLogger(RegistableConvertingXmlReaderFactory.class.getName());

  private final IFunction<String, InputStream, IOException> connector;

  public RegistableConvertingXmlReaderFactory() {
    this(new IFunction<String, InputStream, IOException>() {

      @Override
      public InputStream execute(final String script) throws IOException {
        return new BufferedInputStream(this.getClass().getResourceAsStream(script));
      }
    });
  }

  public RegistableConvertingXmlReaderFactory(final IFunction<String, InputStream, IOException> connector) {
    this.connector = connector;
  }

  public <C, T> IRegistableConvertingXmlReader<C, T> create(
      final IApplicable<C> applicable,
      final URIResolver uriResolver,
      final String inputXsltScript,
      final IJaxbContext jaxbContext)
      throws CreationException {

    try {
      final SAXTransformerFactory transformerFactory = (SAXTransformerFactory) TransformerFactory.newInstance();
      transformerFactory.setURIResolver(uriResolver);
      //      transformerFactory.setFeature(XMLConstants.ACCESS_EXTERNAL_DTD, false);
      final Templates inputTransformerTemplate = create(inputXsltScript, transformerFactory);

      new JaxbContextBuilder();

      final JaxbTransformer<T> jaxbTransformer = new JaxbTransformer<>(
          transformerFactory,
          inputTransformerTemplate,
          null,
          JAXBContext.newInstance(jaxbContext.getObjectFactories()));

      return new IRegistableConvertingXmlReader<C, T>() {

        @Override
        public boolean isApplicable(final C context) {
          return applicable.isApplicable(context);
        }

        @Override
        public T read(final InputStream inputStream, final IParameters parameters) throws IOException {
          try {
            return jaxbTransformer.unmarshall(inputStream, parameters);
          } catch (JAXBException | TransformerException exception) {
            throw new IOException(exception);
          }
        }
      };
    } catch (TransformerConfigurationException | JAXBException exception) {
      throw new CreationException(
          "Couldn't create xml converting persister for binding '" //$NON-NLS-1$
              + Arrays.toString(jaxbContext.getObjectFactories())
              + ",", //$NON-NLS-1$
          exception);
    } catch (final IOException exception) {
      throw new CreationException(
          "Couldn't create xml converting persister for xsd resource '" + inputXsltScript + ",", //$NON-NLS-1$//$NON-NLS-2$
          exception);
    }
  };

  @SuppressWarnings("resource")
  private Templates create(final String inputXsltScript, final SAXTransformerFactory transformerFactory)
      throws TransformerConfigurationException,
      IOException {
    if (inputXsltScript == null) {
      return null;
    }
    try (final InputStream stream = this.connector.execute(inputXsltScript)) {
      if (logger.isLoggable(ILevel.DEBUG)) {
        logger.log(ILevel.DEBUG, inputXsltScript);
      }
      return transformerFactory.newTemplates(new StreamSource(new NoneClosingInputStream(stream)));
    }
  }
}
