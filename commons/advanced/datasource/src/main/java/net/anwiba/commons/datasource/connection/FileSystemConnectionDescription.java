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
package net.anwiba.commons.datasource.connection;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import net.anwiba.commons.datasource.DataSourceType;
import net.anwiba.commons.ensure.Ensure;
import net.anwiba.commons.lang.object.ObjectUtilities;
import net.anwiba.commons.reference.IResourceReference;
import net.anwiba.commons.reference.ResourceReferenceFactory;
import net.anwiba.commons.reference.url.IAuthentication;
import net.anwiba.commons.reference.utilities.PathUtilities;
import net.anwiba.commons.utilities.property.Properties;

public class FileSystemConnectionDescription extends AbstractConnectionDescription implements
    IFileSystemConnectionDescription {

  private static final long serialVersionUID = 5088280043009120362L;
  private transient Path rootPath;
  private transient Path homePath;

  private void writeObject(final ObjectOutputStream out) throws IOException {
    out.defaultWriteObject();
    out.writeObject(this.rootPath.toUri());
    out.defaultWriteObject();
    out.writeObject(this.homePath.toUri());
  }

  private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
    in.defaultReadObject();
    URI uri = (URI) in.readObject();
    this.rootPath = PathUtilities.create(uri);
    uri = (URI) in.readObject();
    this.homePath = PathUtilities.create(uri);
  }

  public FileSystemConnectionDescription(final File file) {
    this(file.toPath().getRoot(), file.toPath().toAbsolutePath());
  }

  public FileSystemConnectionDescription(final Path root, final Path homePath) {
    super(DataSourceType.FILESYSTEM, Properties.empty());
    Ensure.ensureArgumentNotNull(root);
    Ensure.ensureArgumentNotNull(homePath);
    this.rootPath = root;
    this.homePath = homePath;
    if (!Files.isDirectory(homePath)) {
      throw new IllegalArgumentException("homepath isn't a directory " + homePath.toAbsolutePath().toString()); //$NON-NLS-1$
    }
  }

  @Override
  public FileSystemConnectionDescription adapt(final IAuthentication authentication) {
    return new FileSystemConnectionDescription(this.rootPath, this.homePath);
  }

  @Override
  public IAuthentication getAuthentication() {
    return null;
  }

  @Override
  public IResourceReference getResourceReference() {
    return new ResourceReferenceFactory().create(this.homePath);
  }

  @Override
  public String getUrl() {
    return getURI().toASCIIString();
  }

  @Override
  public URI getURI() {
    if (Objects.equals(getScheme(), "file")) { //$NON-NLS-1$
      final URI uri = this.homePath.toAbsolutePath().toFile().toURI();
      return uri;
    }
    return URI.create(getScheme() + ":" + this.rootPath.toString() + "!" + this.homePath.toAbsolutePath().toString()); //$NON-NLS-1$ //$NON-NLS-2$
  }

  @Override
  public String toString() {
    if (Objects.equals(getScheme(), "file")) { //$NON-NLS-1$
      return this.homePath.toAbsolutePath().toString();
    }
    return this.homePath.toUri().toString();
  }

  @Override
  public String getFormat() {
    return "Filesystem"; //$NON-NLS-1$
  }

  @Override
  public int hashCode() {
    return 31 * ((this.rootPath == null)
        ? 0
        : this.rootPath.hashCode()) + ((this.homePath == null)
            ? 0
            : this.homePath.hashCode());
  }

  @Override
  public boolean equals(final Object object) {
    if (this == object) {
      return true;
    }
    if (!(object instanceof IFileSystemConnectionDescription)) {
      return false;
    }
    final IFileSystemConnectionDescription other = (IFileSystemConnectionDescription) object;
    return ObjectUtilities.equals(getDataSourceType(), other.getDataSourceType())
        && ObjectUtilities.equals(getHomePath(), other.getHomePath())
        && ObjectUtilities.equals(getRootPath(), other.getRootPath());
  }

  @Override
  public Path getRootPath() {
    return this.rootPath;
  }

  @Override
  public Path getHomePath() {
    return this.homePath;
  }

  @Override
  public String getScheme() {
    return this.homePath.getFileSystem().provider().getScheme();
  }
}
