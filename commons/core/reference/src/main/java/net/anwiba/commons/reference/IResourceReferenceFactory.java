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
package net.anwiba.commons.reference;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;

import net.anwiba.commons.lang.exception.CreationException;

public interface IResourceReferenceFactory {

  public abstract IResourceReference create(File file);

  public abstract IResourceReference create(URL url);

  public abstract IResourceReference create(URI uri);

  public abstract IResourceReference create(Path path);

  public abstract IResourceReference create(String pathString) throws CreationException;

  public abstract IResourceReference createTemporaryResourceReference(String prefix, String suffix) throws IOException;

  public abstract IResourceReference create(byte[] buffer, String mimeType, String encoding);

}