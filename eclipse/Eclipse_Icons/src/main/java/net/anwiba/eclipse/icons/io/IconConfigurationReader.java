/*
 * #%L
 * anwiba commons core
 * %%
 * Copyright (C) 2007 - 2017 Andreas Bartels
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
// Copyright (c) 2010 by Andreas W. Bartels (bartels@anwiba.de)
package net.anwiba.eclipse.icons.io;

import net.anwiba.commons.eclipse.utilities.JavaProjectUtilities;
import net.anwiba.tools.icons.configuration.GuiIconConfigurationsReader;
import net.anwiba.tools.icons.configuration.IImageExistsValidator;
import net.anwiba.tools.icons.configuration.IOutput;
import net.anwiba.tools.icons.configuration.IconResource;
import net.anwiba.tools.icons.configuration.ImageExistsValidator;
import net.anwiba.tools.icons.configuration.generated.Class;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;

public class IconConfigurationReader {

  private static final String RESOURCES_PATH_SEGMENT = "resources"; //$NON-NLS-1$
  private static final String ICONS_FILE_NAME = "icons.xml"; //$NON-NLS-1$
  private static final String ICONS_PATH_SEGMENT = "icons"; //$NON-NLS-1$

  public Map<Class, List<IconContext>> read(final IJavaProject... projects) throws IOException {
    final Map<Class, List<IconContext>> iconConfigurations = new HashMap<>();
    final Set<IJavaProject> visited = new HashSet<>();
    for (final IJavaProject project : projects) {
      iconConfigurations.putAll(read(visited, project));
    }
    return iconConfigurations;
  }

  private Map<Class, List<IconContext>> read(final Set<IJavaProject> visited, final IJavaProject project)
      throws IOException {
    if (visited.contains(project)) {
      return new HashMap<>();
    }
    visited.add(project);
    final Map<Class, List<IconContext>> iconConfigurations = new HashMap<>();
    try {
      final IClasspathEntry[] classpathEntries = project.getResolvedClasspath(true);
      for (final IClasspathEntry classpathEntry : classpathEntries) {
        if (classpathEntry.getEntryKind() == IClasspathEntry.CPE_PROJECT) {
          final IJavaProject javaProject = JavaProjectUtilities.getJavaProject(classpathEntry.getPath());
          if (javaProject != null && javaProject.exists()) {
            iconConfigurations.putAll(read(visited, javaProject));
            continue;
          }
          continue;
        }
        if (classpathEntry.getEntryKind() == IClasspathEntry.CPE_SOURCE) {
          if (classpathEntry.getPath().lastSegment().equals(RESOURCES_PATH_SEGMENT)) {
            final File iconsPath = getIconsPath(project, classpathEntry);
            final File iconsFile = new File(iconsPath, ICONS_FILE_NAME);
            if (iconsFile.exists()) {
              final IOutput output = new IOutput() {

                @Override
                public void warn(final String message) {
                  // getLog().warn(message);
                }

                @Override
                public void info(final String message) {
                  // getLog().info(message);
                }

                @Override
                public void error(final String message, final Throwable throwable) {
                  // getLog().error(message, throwable);
                }

                @Override
                public void error(final String message) {
                  // getLog().error(message);
                }
              };
              final ArrayList<File> resources = new ArrayList<>();
              final IImageExistsValidator imageExistsValidator = new ImageExistsValidator(resources, output);
              final GuiIconConfigurationsReader reader = new GuiIconConfigurationsReader(imageExistsValidator, output);
              reader.add(iconsFile);
              final Class clazz = reader.getClazz();
              final Map<String, IconResource> configurations = reader.getIconConfigurations();
              final ArrayList<IconContext> iconContexts = new ArrayList<>();
              reader.getFolders();
              for (final IconResource entry : configurations.values()) {
                iconContexts.add(new IconContext(project.getElementName(), iconsPath, entry));
              }
              iconConfigurations.put(clazz, iconContexts);
            }
          }
        }
      }
      return iconConfigurations;
    } catch (final JavaModelException exception) {
      throw new IOException(exception);
    }
  }

  private File getIconsPath(final IJavaProject javaProject, final IClasspathEntry classpathEntry) {
    final URI locationURI = javaProject.getProject().getLocationURI();
    final String osString = classpathEntry.getPath().toOSString();
    final File basePath = new File(new File(locationURI).getParentFile(), osString);
    return new File(basePath, ICONS_PATH_SEGMENT);
  }
}