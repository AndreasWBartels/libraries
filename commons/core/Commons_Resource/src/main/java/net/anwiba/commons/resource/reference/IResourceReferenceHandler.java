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
package net.anwiba.commons.resource.reference;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public interface IResourceReferenceHandler {

  public abstract File getFile(IResourceReference resourceReference) throws URISyntaxException;

  public abstract URL getUrl(IResourceReference resourceReference) throws MalformedURLException;

  public abstract URI getUri(IResourceReference resourceReference) throws URISyntaxException;

  public abstract String getExtension(IResourceReference resourceReference);

  public abstract OutputStream openOnputStream(IResourceReference resourceReference) throws IOException;

  public abstract InputStream openInputStream(IResourceReference resourceReference) throws IOException;

  public abstract boolean exsits(IResourceReference resourceReference);

  public abstract boolean canRead(IResourceReference resourceReference);

  public abstract boolean canWrite(IResourceReference resourceReference);

  public abstract boolean canDelete(IResourceReference resourceReference);

  public abstract void delete(IResourceReference resourceReference) throws IOException;

  public abstract boolean hasLocation(IResourceReference resourceReference);

  public abstract String getContent(IResourceReference resourceReference) throws IOException;

  public abstract boolean isFileSystemResource(IResourceReference resourceReference);

  public abstract long getContentLength(IResourceReference resourceReference);

  public abstract String toString(IResourceReference resourceReference);

  //  public abstract IResourceReference getParent(IResourceReference resourceReference);

}
