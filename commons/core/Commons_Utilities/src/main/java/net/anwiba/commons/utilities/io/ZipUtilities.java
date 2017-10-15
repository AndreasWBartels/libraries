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
package net.anwiba.commons.utilities.io;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import net.anwiba.commons.resource.utilities.IoUtilities;

public class ZipUtilities {

  public static void extract(final File resource, final File targetFolder) throws IOException {
    final int BUFFER = 2048;
    try (final InputStream fis = new FileInputStream(resource);) {
      final File folder = targetFolder;
      final ZipInputStream zis = new ZipInputStream(new BufferedInputStream(fis));
      ZipEntry entry;
      while ((entry = zis.getNextEntry()) != null) {
        final File file = new File(folder, entry.getName());
        if (entry.isDirectory()) {
          file.mkdir();
          continue;
        }
        final byte data[] = new byte[BUFFER];
        try (final FileOutputStream fos = new FileOutputStream(file)) {
          try (BufferedOutputStream dest = new BufferedOutputStream(fos, BUFFER);) {
            int count;
            while ((count = zis.read(data, 0, BUFFER)) != -1) {
              dest.write(data, 0, count);
            }
            dest.flush();
          }
        }
      }
    } catch (final IOException exception) {
      throw new IOException(
          "extract of resource '" + resource + "' faild", //$NON-NLS-1$//$NON-NLS-2$
          exception);
    }
  }

  public static void zipFile(final File sourceFile, final File destinationFile) throws IOException {
    try (FileOutputStream fileOutputStream = new FileOutputStream(destinationFile);) {
      zipFile(sourceFile, fileOutputStream);
    }
  }

  public static void zipFile(final File sourceFile, final OutputStream destinationOutputStream) throws IOException {
    zipFile(new File[]{ sourceFile }, destinationOutputStream);
  }

  public static void zipFile(final File[] sourceFiles, final OutputStream destinationOutputStream) throws IOException {
    ZipOutputStream zipOutputStream = null;
    try {
      zipOutputStream = new ZipOutputStream(destinationOutputStream);
      for (final File sourceFile : sourceFiles) {
        if (!sourceFile.exists()) {
          throw new IllegalArgumentException("Source file '" + sourceFile + "' does not exist."); //$NON-NLS-1$ //$NON-NLS-2$
        }
        if (sourceFile.isFile()) {
          addFile(zipOutputStream, sourceFile, ""); //$NON-NLS-1$
        } else {
          addDirectory(zipOutputStream, sourceFile, ""); //$NON-NLS-1$
        }
      }
      zipOutputStream.finish();
    } finally {
      IoUtilities.close(zipOutputStream);
    }
  }

  private static void addFile(final ZipOutputStream zipOutputStream, final File sourceFile, final String folder)
      throws IOException {
    try (FileInputStream inFis = new FileInputStream(sourceFile);) {
      final ZipEntry zipentry = new ZipEntry(folder + sourceFile.getName());
      zipentry.setMethod(ZipEntry.DEFLATED);
      zipOutputStream.putNextEntry(zipentry);
      IoUtilities.pipe(inFis, zipOutputStream);
    }
  }

  private static void addDirectory(final ZipOutputStream zipOutputStream, final File directory, final String folder)
      throws IOException {
    final File[] files = directory.listFiles();
    for (final File inFile : files) {
      if (inFile.isFile()) {
        addFile(zipOutputStream, inFile, folder);
      } else {
        addDirectory(zipOutputStream, inFile, folder + inFile.getName() + "/"); //$NON-NLS-1$
      }
    }
  }
}