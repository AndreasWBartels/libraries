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

import org.dom4j.Element;

import net.anwiba.commons.utilities.factory.IFactory;

public abstract class AbstractDomToObjectConverter<T, E extends DomConverterException> implements
    IDomToObjectConverter<T> {

  private final IFactory<String, E, RuntimeException> exceptionFactory;

  protected AbstractDomToObjectConverter(final IFactory<String, E, RuntimeException> exceptionFactory) {
    this.exceptionFactory = exceptionFactory;
  }

  protected String getTrimtText(final Element element, final String tagName, final String message) throws E {
    if (element == null || !element.getName().equals(tagName)) {
      throw this.exceptionFactory.create(message);
    }
    return element.getTextTrim();
  }
}