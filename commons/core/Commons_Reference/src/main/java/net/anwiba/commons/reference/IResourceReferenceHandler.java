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
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;

import net.anwiba.commons.lang.functional.IAcceptor;

public interface IResourceReferenceHandler {

  File getFile(IResourceReference resourceReference) throws URISyntaxException;

  URL getUrl(IResourceReference resourceReference) throws MalformedURLException;

  URI getUri(IResourceReference resourceReference) throws URISyntaxException;

  String getExtension(IResourceReference resourceReference);

  OutputStream openOnputStream(IResourceReference resourceReference) throws IOException;

  InputStream openInputStream(IResourceReference resourceReference) throws IOException;

  InputStream openInputStream(IResourceReference resourceReference, IAcceptor<String> contentTypeAcceptor)
      throws IOException;

  boolean exsits(IResourceReference resourceReference);

  boolean canRead(IResourceReference resourceReference);

  boolean canWrite(IResourceReference resourceReference);

  boolean canDelete(IResourceReference resourceReference);

  void delete(IResourceReference resourceReference) throws IOException;

  boolean isMemoryResource(IResourceReference resourceReference);

  String getContent(IResourceReference resourceReference) throws IOException;

  boolean isFileSystemResource(IResourceReference resourceReference);

  long getContentLength(IResourceReference resourceReference);

  String toString(IResourceReference resourceReference);

  String getContentType(IResourceReference resourceReference);

  Path getPath(IResourceReference resourceReference) throws URISyntaxException;

  //   IResourceReference getParent(IResourceReference resourceReference);

}
