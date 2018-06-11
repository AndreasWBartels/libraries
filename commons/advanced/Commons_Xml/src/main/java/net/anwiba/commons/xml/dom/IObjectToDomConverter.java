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
package net.anwiba.commons.xml.dom;

import java.util.List;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.QName;
import org.dom4j.Text;

public interface IObjectToDomConverter<T> {

  public Element convert(T object) throws DomConverterException;

  default Element element(final QName name) {
    return DocumentHelper.createElement(name);
  }

  default Element element(final String name) {
    return DocumentHelper.createElement(name);
  }

  default Text text(final String string) {
    return DocumentHelper.createText(string);
  }

  default Element addTo(final Element parent, final Element child) {
    if (child == null) {
      return child;
    }
    parent.add(child);
    final List<Namespace> namespaces = child.declaredNamespaces();
    if (child.getNamespace() != null) {
      parent.add(child.getNamespace());
    }
    for (final Namespace namespace : namespaces) {
      parent.add(namespace);
    }
    return child;
  }

  default String value(final int value) {
    return String.valueOf(value);
  }

  default String value(final long value) {
    return String.valueOf(value);
  }

  default String value(final float value) {
    return String.valueOf(value);
  }

  default String value(final double value) {
    return String.valueOf(value);
  }

  default String value(final boolean value) {
    return String.valueOf(value);
  }

}
