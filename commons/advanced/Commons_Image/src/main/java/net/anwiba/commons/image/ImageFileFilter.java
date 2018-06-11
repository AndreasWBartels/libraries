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
package net.anwiba.commons.image;

import java.io.File;

import javax.swing.filechooser.FileFilter;

import net.anwiba.commons.image.codec.ImageCodec;
import net.anwiba.commons.reference.utilities.FileUtilities;

public class ImageFileFilter extends FileFilter {
  @Override
  public boolean accept(final File file) {
    if (file.isDirectory()) {
      return true;
    }
    return isImage(file);
  }

  public boolean isImage(final File file) {
    if (file == null || file.isDirectory()) {
      return false;
    }
    final String extension = FileUtilities.getExtension(file);
    if (extension != null && ImageCodec.getByExtension(extension) != ImageCodec.UNKNOWN) {
      return true;
    }
    return false;
  }

  @Override
  public String getDescription() {
    return "Image (*.bmp, *.gif, *.png, *.jpg, *.tiff)"; //$NON-NLS-1$
  }
}
