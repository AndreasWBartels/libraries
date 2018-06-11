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
package net.anwiba.commons.reference.utilities;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

import net.anwiba.commons.reference.IStreamConnector;

public final class UrlStreamConnector implements IStreamConnector<URI> {
  @Override
  public boolean exist(final URI uri) {
    return UriUtilities.exist(uri);
  }

  @Override
  public boolean canRead(final URI uri) {
    return UriUtilities.canRead(uri);
  }

  @Override
  public boolean canWrite(final URI uri) {
    return UriUtilities.canWrite(uri);
  }

  @Override
  public InputStream openInputStream(final URI uri) throws IOException {
    return UriUtilities.openInputStream(uri);
  }

  @Override
  public OutputStream openOutputStream(final URI uri) throws IOException {
    return UriUtilities.openOutputStream(uri);
  }

  @Override
  public long getContentLength(final URI uri) throws IOException {
    return UriUtilities.getContentLength(uri);
  }
}