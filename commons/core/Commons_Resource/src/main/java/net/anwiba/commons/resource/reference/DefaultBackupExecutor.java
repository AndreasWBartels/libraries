/*
 * #%L
 * *
 * %%
 * Copyright (C) 2007 - 2017 Andreas W. Bartels (bartels@anwiba.de)
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
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public final class DefaultBackupExecutor implements IBackupExecutor {

  @Override
  public IResourceReference backup(final IResourceReference resourceReference) throws IOException {
    try {
      if (!ResourceReferenceUtilities.isFileSystemResource(resourceReference)) {
        return null;
      }
      final File file = ResourceReferenceUtilities.getFile(resourceReference);
      if (file.exists()) {
        final File backup = new File(file.getPath() + "~"); //$NON-NLS-1$
        Files.move(file.toPath(), backup.toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        if (!backup.exists()) {
          throw new IOException("Coudn't create backup file " + backup); //$NON-NLS-1$
        }
        if (file.exists()) {
          throw new IOException("Coudn't delete file " + file); //$NON-NLS-1$
        }
        return new ResourceReferenceFactory().create(backup);
      }
      return null;
    } catch (final URISyntaxException exception) {
      throw new IOException(exception.getLocalizedMessage(), exception);
    }
  }
}