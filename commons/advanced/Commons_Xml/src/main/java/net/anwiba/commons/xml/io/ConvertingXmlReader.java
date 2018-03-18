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

import java.io.IOException;
import java.io.InputStream;

import javax.xml.bind.JAXBElement;

import net.anwiba.commons.lang.object.ObjectPair;
import net.anwiba.commons.utilities.parameter.IParameters;
import net.anwiba.commons.utilities.registry.AbstractApplicableRegistry;
import net.anwiba.commons.utilities.registry.IApplicableRegistry;
import net.anwiba.commons.version.IVersion;
import net.anwiba.commons.xml.extractor.XmlStreamStartElementNameAndVersionExtractor;

public class ConvertingXmlReader implements IConvertingXmlReader {

  IApplicableRegistry<IXmlConverterContext, IRegistableConvertingXmlReader<IXmlConverterContext, ?>> registry = new AbstractApplicableRegistry<IXmlConverterContext, IRegistableConvertingXmlReader<IXmlConverterContext, ?>>(
      null) {
    // nothing to do
  };

  @SuppressWarnings("unchecked")
  public void add(final IRegistableConvertingXmlReader<IXmlConverterContext, ?> persister) {
    this.registry.add(persister);
  }

  @Override
  @SuppressWarnings("unchecked")
  public <O> O read(final Class<O> clazz, final InputStream inputStream, final IParameters parameters)
      throws IOException {

    final ObjectPair<String, IVersion> result = new XmlStreamStartElementNameAndVersionExtractor().extract(inputStream);
    final String name = result.getFirstObject();
    final IVersion version = result.getSecondObject();
    final IXmlConverterContext context = new XmlConverterContext(name, version, clazz);

    final IRegistableConvertingXmlReader<IXmlConverterContext, O> converter = (IRegistableConvertingXmlReader<IXmlConverterContext, O>) this.registry
        .get(context);
    if (converter == null) {
      throw new UnsupportedOperationException();
    }
    final Object object = converter.read(inputStream, parameters);
    if (object instanceof JAXBElement) {
      @SuppressWarnings("rawtypes")
      final JAXBElement element = (JAXBElement) object;
      final Object value = element.getValue();
      return (O) value;
    }
    return (O) object;
  }
}
