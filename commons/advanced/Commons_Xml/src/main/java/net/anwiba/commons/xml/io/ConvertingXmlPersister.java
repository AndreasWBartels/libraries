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
import java.io.OutputStream;

import net.anwiba.commons.utilities.registry.AbstractApplicableRegistry;
import net.anwiba.commons.utilities.registry.IApplicableRegistry;

public class ConvertingXmlPersister<C> implements IConvertingXmlPersister<C> {

  IApplicableRegistry<C, IRegistableConvertingXmlPersister<C, ?>> registry = new AbstractApplicableRegistry<C, IRegistableConvertingXmlPersister<C, ?>>(
      null) {
    // nothing to do
  };

  @SuppressWarnings("unchecked")
  public void add(final IRegistableConvertingXmlPersister<C, ?> persister) {
    this.registry.add(persister);
  }

  @Override
  @SuppressWarnings("unchecked")
  public <O> O read(final C context, final InputStream inputStream) throws IOException {
    final IRegistableConvertingXmlPersister<C, O> converter = (IRegistableConvertingXmlPersister<C, O>) this.registry
        .get(context);
    if (converter == null) {
      throw new UnsupportedOperationException();
    }
    return converter.read(inputStream);
  }

  @Override
  public <I> void write(final C context, final I object, final OutputStream outputStream) throws IOException {
    @SuppressWarnings("unchecked")
    final IRegistableConvertingXmlPersister<C, I> persister = (IRegistableConvertingXmlPersister<C, I>) this.registry
        .get(context);
    if (persister == null) {
      throw new UnsupportedOperationException();
    }
    persister.write(object, outputStream);
  }
}
