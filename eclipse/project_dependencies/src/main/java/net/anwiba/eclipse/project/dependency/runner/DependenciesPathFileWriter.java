/*
 * #%L
 * anwiba eclipse java tools
 * %%
 * Copyright (C) 2007 - 2021 Andreas Bartels
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
package net.anwiba.eclipse.project.dependency.runner;

import net.anwiba.commons.eclipse.logging.ILogger;
import net.anwiba.commons.internal.eclipse.logging.Level;
import net.anwiba.commons.lang.object.IObjectProvider;
import net.anwiba.commons.thread.cancel.ICanceler;
import net.anwiba.eclipse.project.dependency.java.IDependency;
import net.anwiba.eclipse.project.dependency.java.ILibrary;
import net.anwiba.eclipse.project.dependency.java.IProject;
import net.anwiba.eclipse.project.dependency.java.IWorkspace;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.eclipse.jdt.core.IJavaModel;

public class DependenciesPathFileWriter {

  private final ILogger logger;
  private final IJavaModel model;

  public DependenciesPathFileWriter(final ILogger logger, final IJavaModel model) {
    this.logger = logger;
    this.model = model;
  }

  public void write(final ICanceler canceler, final IWorkspace workspace) {
    for (final IProject project : workspace.getProjects().values()) {
      if (canceler.isCanceled()) {
        return;
      }
      write(project);
    }
  }

  private void write(final IProject project) {
    final File path =
        new File(new File(this.model.getWorkspace().getRoot().getLocation().toOSString()), project.getName());
    final IObjectProvider<Iterable<ILibrary>> usedProvider = new IObjectProvider<Iterable<ILibrary>>() {

      @Override
      public Iterable<ILibrary> get() {
        final ArrayList<ILibrary> list = new ArrayList<ILibrary>();
        for (final ILibrary library : project.getUsedLibraries()) {
          list.add(library);
        }
        Collections.sort(list, new Comparator<ILibrary>() {

          @Override
          public int compare(final ILibrary value, final ILibrary other) {
            return String.CASE_INSENSITIVE_ORDER.compare(value.getName(), other.getName());
          }
        });
        return list;
      }
    };
    write(path, ".used", project, usedProvider); //$NON-NLS-1$
    final IObjectProvider<Iterable<ILibrary>> pathProvider = new IObjectProvider<Iterable<ILibrary>>() {

      @Override
      public Iterable<ILibrary> get() {
        final ArrayList<ILibrary> list = new ArrayList<ILibrary>();
        for (final ILibrary library : project.getClasspath()) {
          list.add(library);
        }
        Collections.sort(list, new Comparator<ILibrary>() {

          @Override
          public int compare(final ILibrary value, final ILibrary other) {
            return String.CASE_INSENSITIVE_ORDER.compare(value.getName(), other.getName());
          }
        });
        return list;
      }
    };
    write(path, ".path", project, pathProvider); //$NON-NLS-1$
    final IObjectProvider<Iterable<ILibrary>> dependenciesProvider = new IObjectProvider<Iterable<ILibrary>>() {

      @Override
      public Iterable<ILibrary> get() {
        final Iterable<IDependency> dependencies = project.getDependencies().getDependencies();
        final ArrayList<ILibrary> list = new ArrayList<ILibrary>();
        for (final IDependency dependency : dependencies) {
          list.add(dependency.getLibrary());
        }
        Collections.sort(list, new Comparator<ILibrary>() {

          @Override
          public int compare(final ILibrary value, final ILibrary other) {
            return String.CASE_INSENSITIVE_ORDER.compare(value.getName(), other.getName());
          }
        });
        return list;
      }
    };
    write(path, ".dependencies", project, dependenciesProvider); //$NON-NLS-1$
  }

  private void write(
      final File path,
      final String filename,
      final IProject project,
      final IObjectProvider<Iterable<ILibrary>> provider) {
    Writer writer = null;
    try {
      final File file = new File(path, filename);
      writer = new FileWriter(file);
      for (final ILibrary library : provider.get()) {
        writer.append(library.getName());
        writer.append("\t"); //$NON-NLS-1$
        writer.append(String.valueOf(library.isInstance(project)));
        writer.append("\n"); //$NON-NLS-1$
      }
    } catch (final IOException exception) {
      this.logger.log(Level.WARNING, exception);
    } finally {
      try {
        if (writer != null) {
          writer.close();
        }
      } catch (final IOException ioException) {
        // nothing to do
      }
    }
  }
}
