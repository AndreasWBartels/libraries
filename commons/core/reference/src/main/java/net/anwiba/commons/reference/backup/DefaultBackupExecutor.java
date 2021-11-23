/*
 * #%L
 *
 * %%
 * Copyright (C) 2007 - 2017 Andreas W. Bartels
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
package net.anwiba.commons.reference.backup;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import net.anwiba.commons.lang.optional.IOptional;
import net.anwiba.commons.lang.optional.Optional;
import net.anwiba.commons.reference.IResourceReference;
import net.anwiba.commons.reference.IResourceReferenceHandler;
import net.anwiba.commons.reference.ResourceReferenceFactory;

public final class DefaultBackupExecutor implements IBackupExecutor {

  private final IResourceReferenceHandler referenceHandler;

  public DefaultBackupExecutor(final IResourceReferenceHandler referenceHandler) {
    this.referenceHandler = referenceHandler;
  }

  @Override
  public IOptional<IResourceReference, IOException> backup(final IResourceReference resourceReference) throws IOException {
    if (!this.referenceHandler.exists(resourceReference)) {
      return Optional.empty(IOException.class);
    }
    try {
      final Path file = this.referenceHandler.getPath(resourceReference);
      if (Files.exists(file)) {
        final Path backup = file.getFileSystem().getPath(file.toString() + "~"); //$NON-NLS-1$
        Files.move(file, backup, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        if (!Files.exists(backup)) {
          throw new IOException("Coudn't create backup file " + backup); //$NON-NLS-1$
        }
        if (Files.exists(file)) {
          throw new IOException("Coudn't delete file " + file); //$NON-NLS-1$
        }
        return Optional.of(IOException.class, new ResourceReferenceFactory().create(backup));
      }
      return Optional.empty(IOException.class);
    } catch (final URISyntaxException exception) {
      throw new IOException(exception.getLocalizedMessage(), exception);
    }
  }
}
