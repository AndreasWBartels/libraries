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

import java.io.IOException;

import org.eclipse.imagen.media.codec.SeekableStream;

import net.anwiba.commons.image.apache.ApacheImageDirectoryReader;
import net.anwiba.commons.image.imagen.ISeekableStreamConnector;
import net.anwiba.commons.image.imagen.ImagenImageDirectoryReader;
import net.anwiba.commons.image.imagen.InputStreamConnectorFactory;
import net.anwiba.commons.lang.exception.CanceledException;
import net.anwiba.commons.reference.IResourceReference;
import net.anwiba.commons.reference.IResourceReferenceHandler;
import net.anwiba.commons.thread.cancel.ICanceler;

public class ImageDirectoryReader implements IImageDirectoryReader {

  private final InputStreamConnectorFactory inputStreamConnectorFactory;
  private final ImagenImageDirectoryReader imagenImageDirectoryReader;
  private final ApacheImageDirectoryReader apacheImageDirectoryReader;

  public ImageDirectoryReader(final IResourceReferenceHandler resourceReferenceHandler) {
    this.imagenImageDirectoryReader = new ImagenImageDirectoryReader(resourceReferenceHandler);
    this.inputStreamConnectorFactory = new InputStreamConnectorFactory(resourceReferenceHandler);
    this.apacheImageDirectoryReader = new ApacheImageDirectoryReader(resourceReferenceHandler);
  }

  @Override
  public IImageDirectory read(final ICanceler canceler, final IResourceReference resourceReference)
      throws CanceledException,
      IOException {
    if (this.apacheImageDirectoryReader.isSupported(resourceReference)) {
      return this.apacheImageDirectoryReader.read(canceler, resourceReference);
    }
    ISeekableStreamConnector connector = this.inputStreamConnectorFactory.create(resourceReference);
    try (final SeekableStream seekableStream = connector.connect()) {
      if (this.imagenImageDirectoryReader.isSupported(seekableStream)) {
        seekableStream.seek(0);
        return this.imagenImageDirectoryReader.read(canceler, resourceReference);
      }
    }
    return new IImageDirectory() {
    };
  }

}
