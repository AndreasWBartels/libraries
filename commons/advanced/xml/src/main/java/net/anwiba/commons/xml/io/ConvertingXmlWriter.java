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
import java.io.OutputStream;

import javax.xml.transform.OutputKeys;

import net.anwiba.commons.lang.parameter.IParameters;
import net.anwiba.commons.utilities.property.Properties;
import net.anwiba.commons.utilities.registry.AbstractApplicableRegistry;
import net.anwiba.commons.utilities.registry.IApplicableRegistry;

public class ConvertingXmlWriter<C> implements IConvertingXmlWriter<C> {

  private final IApplicableRegistry<C, IRegistableConvertingXmlWriter<C, ?>> registry =
      new AbstractApplicableRegistry<C, IRegistableConvertingXmlWriter<C, ?>>(
          null) {
        // nothing to do
      };
  private boolean isIndentEnabled = false;
  private boolean isOmitXMLDeclaration = false;

  public void enableXMLDeclaration() {
    this.isOmitXMLDeclaration = false;
  }

  public void disableXMLDeclaration() {
    this.isOmitXMLDeclaration = true;
  }

  public void enableIndent() {
    this.isIndentEnabled = true;
  }

  public void disableIndent() {
    this.isIndentEnabled = false;
  }

  @SuppressWarnings("unchecked")
  public void add(final IRegistableConvertingXmlWriter<C, ?> persister) {
    this.registry.add(persister);
  }

  @Override
  public <I> void write(final C context, final I object, final IParameters parameters, final OutputStream outputStream)
      throws IOException {
    @SuppressWarnings("unchecked")
    final IRegistableConvertingXmlWriter<C, I> persister = (IRegistableConvertingXmlWriter<C, I>) this.registry
        .get(context);
    if (persister == null) {
      throw new UnsupportedOperationException();
    }
    persister.write(object,
        parameters,
        outputStream,
        Properties.builder()
            .put(OutputKeys.INDENT, this.isIndentEnabled ? "yes" : "no")
            .put(OutputKeys.OMIT_XML_DECLARATION, this.isOmitXMLDeclaration ? "yes" : "no")
            .build());
  }
}
