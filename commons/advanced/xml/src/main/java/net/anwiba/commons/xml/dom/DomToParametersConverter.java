/*
 * #%L
 *
 * %%
 * Copyright (C) 2007 - 2018 Andreas W. Bartels (bartels@anwiba.de)
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

import java.util.ArrayList;

import org.dom4j.Element;

import net.anwiba.commons.utilities.parameter.IParameter;
import net.anwiba.commons.utilities.parameter.IParameters;
import net.anwiba.commons.utilities.parameter.Parameter;
import net.anwiba.commons.utilities.parameter.Parameters;

public final class DomToParametersConverter implements IDomToObjectConverter<IParameters> {

  @SuppressWarnings("nls")
  @Override
  public IParameters convert(final Element element) throws DomConverterException {
    if (element == null) {
      return Parameters.empty();
    }
    final ArrayList<IParameter> parameters = new ArrayList<>();
    for (final Element parameterElement : elements(element, "parameter")) {
      parameters.add(Parameter.of(value(parameterElement, "name"), value(parameterElement, "value")));
    }
    return Parameters.of(parameters);
  }
}
