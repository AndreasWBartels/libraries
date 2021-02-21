/*
 * #%L
 * anwiba commons tools
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
package net.anwiba.tools.icons.configuration;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import net.anwiba.tools.icons.schema.eclipse.classpath.Classpath;
import net.anwiba.tools.icons.schema.eclipse.classpath.ClasspathEntry;

public class IconRecourceSearcher {

  private final boolean aggregate;
  private final IOutput output;

  public IconRecourceSearcher(final IOutput output, final boolean aggregate) {
    this.output = output;
    this.aggregate = aggregate;
  }

  public List<File> search(final File projectPath) throws IOException {
    return search(projectPath, true);
  }

  public List<File> search(final File projectPath, final boolean isExported) throws IOException {
    this.output.info(MessageFormat.format("project: {0} exported {1}", projectPath, Boolean.valueOf(isExported))); //$NON-NLS-1$
    final File classpathFile = new File(projectPath, ".classpath");
    if (classpathFile.exists()) {
      return search(projectPath, classpathFile, isExported);
    }
    final List<File> files = new ArrayList<>();
    File file = new File(projectPath, "resources/icons.xml"); //$NON-NLS-1$
    if (file.exists() && file.canRead()) {
      System.out.println(MessageFormat.format("resource file: {0}", file.getAbsolutePath())); //$NON-NLS-1$
      files.add(file);
    }
    file = new File(projectPath, "src/main/resources/icons.xml"); //$NON-NLS-1$
    if (file.exists() && file.canRead()) {
      System.out.println(MessageFormat.format("resource file: {0}", file.getAbsolutePath())); //$NON-NLS-1$
      files.add(file);
    }
    return files;
  }

  public List<File> search(final File projectPath, final File classpathFile, final boolean isExported)
      throws IOException {
    try {
      this.output.info(MessageFormat.format("project: {0} exported {1}", projectPath, Boolean.valueOf(isExported))); //$NON-NLS-1$
      final List<File> files = new ArrayList<>();
      final JAXBContext jc =
          JAXBContext.newInstance(
              net.anwiba.tools.icons.schema.eclipse.classpath.KindType.class,
              net.anwiba.tools.icons.schema.eclipse.classpath.Classpath.class,
              net.anwiba.tools.icons.schema.eclipse.classpath.ClasspathEntry.class);
      final Unmarshaller u = jc.createUnmarshaller();
      final Classpath classpath = (Classpath) u.unmarshal(classpathFile); // $NON-NLS-1$
      if (classpath == null) {
        this.output.info("no classpath"); //$NON-NLS-1$
        return files;
      }
      for (final ClasspathEntry classpathEntry : classpath.getClasspathentry()) {
        if (classpathEntry == null) {
          this.output.info("classpath entry is null"); //$NON-NLS-1$
          continue;
        }
        if (classpathEntry.getKind() == null) {
          this.output.info(MessageFormat.format("classpath entry: {0} is null", classpathEntry.getPath())); //$NON-NLS-1$
          this.output.info("classpath entry is null"); //$NON-NLS-1$
          continue;
        }
        switch (classpathEntry.getKind()) {
          case SRC: {
            if (classpathEntry.getPath().startsWith("/")) { //$NON-NLS-1$
              if (!this.aggregate) {
                continue;
              }
              if (!isExported) {
                continue;
              }
              files
                  .addAll(search(
                      new File(projectPath, MessageFormat.format("..{0}", classpathEntry.getPath())).getCanonicalFile(), //$NON-NLS-1$
                      classpathEntry.isExported()));
              continue;
            }
            if (classpathEntry.getPath().equals("resources")) { //$NON-NLS-1$
              final File file = new File(projectPath.getCanonicalFile(), "resources/icons.xml"); //$NON-NLS-1$
              if (file.exists() && file.canRead()) {
                this.output.info(MessageFormat.format("resource file: {0}", file.getAbsolutePath())); //$NON-NLS-1$
                files.add(file);
                continue;
              }
              continue;
            }
            if (classpathEntry.getPath().equals("src/main/resources")) { //$NON-NLS-1$
              final File file = new File(projectPath.getCanonicalFile(), "src/main/resources/icons.xml"); //$NON-NLS-1$
              if (file.exists() && file.canRead()) {
                this.output.info(MessageFormat.format("resource file: {0}", file.getAbsolutePath())); //$NON-NLS-1$
                files.add(file);
                continue;
              }
              continue;
            }
            break;
          }
          case LIB:
          case CON:
          case OUTPUT: {
            continue;
          }
        }
      }
      return files;
    } catch (final JAXBException exception) {
      throw new IOException(exception);
    }
  }
}
