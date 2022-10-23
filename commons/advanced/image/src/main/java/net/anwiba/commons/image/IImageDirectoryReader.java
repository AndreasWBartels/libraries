/*
 * #%L
 * anwiba commons
 * %%
 * Copyright (C) 2007 - 2022 Andreas W. Bartels
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
package net.anwiba.commons.image;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import net.anwiba.commons.lang.exception.CanceledException;
import net.anwiba.commons.reference.IResourceReference;
import net.anwiba.commons.reference.ResourceReferenceFactory;
import net.anwiba.commons.reference.utilities.IoUtilities;
import net.anwiba.commons.thread.cancel.ICanceler;

public interface IImageDirectoryReader {

  final ResourceReferenceFactory resourceReferenceFactory = new ResourceReferenceFactory();

  IImageDirectory read(ICanceler canceler, IResourceReference resourceReference) throws CanceledException, IOException;

  default IImageDirectory read(final ICanceler canceler, final InputStream inputStream) throws CanceledException,
      IOException {
    try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
      return read(canceler, resourceReferenceFactory.create(IoUtilities.toByteArray(inputStream), "image", "UTF-8"));
    }
  }

  default IImageDirectory read(final ICanceler canceler, final File randomAccessFile) throws CanceledException,
      IOException {
    return read(canceler, resourceReferenceFactory.create(randomAccessFile));
  }
}
