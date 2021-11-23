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

import javax.xml.transform.Templates;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.transform.sax.SAXTransformerFactory;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import net.anwiba.commons.lang.exception.CreationException;
import net.anwiba.commons.lang.functional.IApplicable;
import net.anwiba.commons.lang.functional.IFunction;
import net.anwiba.commons.lang.parameter.IParameters;
import net.anwiba.commons.xml.jaxb.IJaxbContext;
import net.anwiba.commons.xml.jaxb.JaxbTransformer;

public class RegistableConvertingXmlReaderFactory {

  static net.anwiba.commons.logging.ILogger logger = net.anwiba.commons.logging.Logging
      .getLogger(RegistableConvertingXmlReaderFactory.class.getName());

  private final TemplateFactory templateFactory;

  public RegistableConvertingXmlReaderFactory() {
    this(new IFunction<String, InputStream, IOException>() {

      @Override
      public InputStream execute(final String script) throws IOException {
        return new BufferedInputStream(this.getClass().getResourceAsStream(script));
      }
    });
  }

  public RegistableConvertingXmlReaderFactory(final IFunction<String, InputStream, IOException> connector) {
    this.templateFactory = new TemplateFactory(connector);
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
// access restriction throws exception if external resource reference is not a "file" or "jar:file" references 
//      transformerFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "file, jar:file");
//      transformerFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "file, jar:file");
//      transformerFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "file, jar:file");
      final Templates inputTransformerTemplate = this.templateFactory.create(inputXsltScript, transformerFactory);
      final JaxbTransformer<T> jaxbTransformer = new JaxbTransformer<>(
          transformerFactory,
          inputTransformerTemplate,
          null,
          JAXBContext.newInstance(jaxbContext.getContextPath(), jaxbContext.getClassLoader()));

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
          "Couldn't create xml converting persister for xslt script '" + inputXsltScript + "', ", //$NON-NLS-1$//$NON-NLS-2$
          exception);
    }
  }
}
