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

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;

import net.anwiba.commons.reference.utilities.IoUtilities;
import net.anwiba.commons.xml.dom.DomConverterException;
import net.anwiba.commons.xml.dom.IDomToObjectConverter;

public class XmlReader<T> implements Closeable {

  private final IDomToObjectConverter<T> converter;
  private final InputStream inputStream;

  public XmlReader(final InputStream stream, final IDomToObjectConverter<T> converter) {
    this.inputStream = stream;
    this.converter = converter;
  }

  public T read() throws DomConverterException {
    final SAXReader saxReader = new SAXReader();
    try {
      final Document document = saxReader.read(this.inputStream);
      return this.converter.convert(document.getRootElement());
    } catch (final DocumentException exception) {
      throw new DomConverterException(exception);
    }
  }

  @Override
  public void close() throws IOException {
    IoUtilities.close(this.inputStream);
  }
}
