/*
 * #%L
 * anwiba commons core
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
package net.anwiba.commons.reference.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import net.anwiba.commons.lang.functional.IAcceptor;

public interface IStreamConnector<T> {

  boolean exist(T uri);

  boolean canRead(T uri);

  boolean canWrite(T uri);

  InputStream openInputStream(T uri, IAcceptor<String> contentTypeAcceptor) throws IOException;

  InputStream openInputStream(T uri) throws IOException;

  OutputStream openOutputStream(T uri) throws IOException;

  long getContentLength(T uri) throws IOException;

  String getContentType(T uri) throws IOException;

  default boolean isApplicable(T uri) {
    return true;
  }
  
}
