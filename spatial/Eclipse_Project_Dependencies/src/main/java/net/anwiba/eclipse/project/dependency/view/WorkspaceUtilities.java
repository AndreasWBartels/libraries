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
package net.anwiba.eclipse.project.dependency.view;

import net.anwiba.eclipse.project.dependency.java.ILibrary;
import net.anwiba.eclipse.project.dependency.java.IPackage;
import net.anwiba.eclipse.project.dependency.java.IProject;
import net.anwiba.eclipse.project.dependency.java.IType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.core.ExternalPackageFragmentRoot;
import org.eclipse.jdt.internal.core.JarPackageFragmentRoot;

public class WorkspaceUtilities {

  public static List<IJavaProject> getProjects(final IProject seletedProject) {
    final List<IJavaProject> openProjects = new ArrayList<>();
    for (final org.eclipse.core.resources.IProject project : ResourcesPlugin.getWorkspace().getRoot().getProjects()) {
      if (project.isOpen()) {
        final IJavaProject javaProject = JavaCore.create(project);
        if (javaProject != null) {
          final String elementName = javaProject.getElementName();
          if (seletedProject.getName().substring(1).equals(elementName)) {
            openProjects.add(javaProject);
          }
        }
      }
    }
    return openProjects;
  }

  public static List<IJavaProject> getProjects() {
    final List<IJavaProject> openProjects = new ArrayList<>();
    for (final org.eclipse.core.resources.IProject project : ResourcesPlugin.getWorkspace().getRoot().getProjects()) {
      if (project.isOpen()) {
        final IJavaProject javaProject = JavaCore.create(project);
        if (javaProject != null) {
          openProjects.add(javaProject);
        }
      }
    }
    return openProjects;
  }

  public static List<Object> getTypes(final IType type) {
    final ILibrary library = type.getPackage().getLibrary();
    final List<Object> eclipseTypes = new ArrayList<>();
    if (library instanceof IProject) {
      final List<IJavaProject> eclipseProjects = getProjects((IProject) library);
      for (final IJavaProject javaProject : eclipseProjects) {
        try {
          final String qualifiedName = type.getQualifiedName();
          final org.eclipse.jdt.core.IType foundedType = javaProject.findType(qualifiedName);
          if (foundedType != null) {
            eclipseTypes.add(foundedType.getParent());
          }
        } catch (final JavaModelException e) {
          // nothing to do
        }
      }
    } else {
      library.getIdentifier();
      for (final IJavaProject javaProject : getProjects()) {
        try {
          final String libraryName = library.getName();
          final IPackageFragmentRoot[] packageFragmentRoots = javaProject.getAllPackageFragmentRoots();
          for (final IPackageFragmentRoot packageFragmentRoot : packageFragmentRoots) {
            if (packageFragmentRoot instanceof JarPackageFragmentRoot) {
              final String elementName = packageFragmentRoot.getPath().toString();
              if (Objects.equals(elementName, libraryName)) {
                final IJavaElement[] children = packageFragmentRoot.getChildren();
                for (final IJavaElement javaElement : children) {
                  final IPackageFragment packageFragment = (IPackageFragment) javaElement;
                  for (final IClassFile classFile : packageFragment.getClassFiles()) {
                    final org.eclipse.jdt.core.IType eclipseType = classFile.findPrimaryType();
                    final String fullyQualifiedName = eclipseType.getFullyQualifiedName();
                    if (Objects.equals(type.getPath().getIdentifier(), fullyQualifiedName)) {
                      eclipseTypes.add(classFile);
                      return eclipseTypes;
                    }
                  }
                }
              }
            }
            packageFragmentRoot.exists();
          }
        } catch (final JavaModelException e) {
          // nothing to do
        }
      }
    }
    return eclipseTypes;
  }

  public static List<Object> getPackages(final IPackage paccage) {
    final ILibrary library = paccage.getLibrary();
    final List<Object> eclipseTypes = new ArrayList<>();
    if (library instanceof IProject) {
      final List<IJavaProject> eclipseProjects = getProjects((IProject) library);
      try {
        for (final IJavaProject javaProject : eclipseProjects) {
          final IPackageFragmentRoot[] allPackageFragmentRoots = javaProject.getAllPackageFragmentRoots();
          for (final IPackageFragmentRoot packageFragmentRoot : allPackageFragmentRoots) {
            if (packageFragmentRoot instanceof JarPackageFragmentRoot) {
              continue;
            }
            if (packageFragmentRoot instanceof ExternalPackageFragmentRoot) {
              continue;
            }
            final IPackageFragment packageFragment = packageFragmentRoot.getPackageFragment(paccage.getName());
            if (packageFragment.exists()) {
              eclipseTypes.add(packageFragment);
            }
          }
        }
      } catch (final JavaModelException e) {
        // nothing to do
      }
    }
    return eclipseTypes;
  }

}
