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

import net.anwiba.commons.xml.dom.IObjectToDomConverter;

import java.io.Closeable;
import java.io.IOException;
import java.io.Writer;

import org.dom4j.Document;
import org.dom4j.DocumentFactory;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

public class XmlWriter<T> implements Closeable {

  private final IObjectToDomConverter<T> converter;
  private final XMLWriter xmlWriter;

  public XmlWriter(final Writer writer, final IObjectToDomConverter<T> converter) {
    this.converter = converter;
    this.xmlWriter = new XMLWriter(writer, new OutputFormat(" ", true)); //$NON-NLS-1$
  }

  public void write(final T object) throws IOException {
    final DocumentFactory instance = DocumentFactory.getInstance();
    final Document document = instance.createDocument();
    document.add(this.converter.convert(object));
    this.xmlWriter.write(document);
  }

  @Override
  public void close() throws IOException {
    this.xmlWriter.close();
  }

}
