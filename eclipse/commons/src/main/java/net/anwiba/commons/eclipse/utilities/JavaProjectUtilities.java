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
// Copyright (c) 2010 by Andreas W. Bartels (bartels@anwiba.de)
package net.anwiba.commons.eclipse.utilities;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaModel;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.ui.IPackagesViewPart;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPathEditorInput;
import org.eclipse.ui.IWorkbenchPart;

public class JavaProjectUtilities {

  public static IJavaProject[] getJavaProjects(final IWorkbenchPart part, final ISelection selection) {
    if (part instanceof IEditorPart) {
      return getJavaProjects((IEditorPart) part);
    }
    if (part instanceof IPackagesViewPart) {
      return getJavaProjects((ITreeSelection) selection);
    }
    return new IJavaProject[0];
  }

  public static IJavaProject[] getJavaProjects(final IEditorPart part) {
    if (part == null || part.getEditorInput() == null) {
      return new IJavaProject[0];
    }
    final IEditorInput editorInput = part.getEditorInput();
    if (editorInput instanceof IPathEditorInput) {
      final IPathEditorInput pathEditorInput = (IPathEditorInput) editorInput;
      final IJavaProject javaProject = getJavaProject(pathEditorInput.getPath());
      if (javaProject != null && javaProject.exists()) {
        return new IJavaProject[] { javaProject };
      }
    }
    final IJavaElement element = editorInput.getAdapter(IJavaElement.class);
    if (element == null) {
      return new IJavaProject[0];
    }
    return new IJavaProject[] { element.getJavaProject() };
  }

  public static IJavaProject getJavaProject(final IPath path) {
    final IJavaProject[] javaProjects = getJavaProjects();
    for (final IJavaProject javaProject : javaProjects) {
      final IPath fullPath = javaProject.getProject().getFullPath();
      if (fullPath.equals(path)) {
        return javaProject;
      }
    }

    final IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
    IContainer container = root.getContainerForLocation(path);
    if (container != null) {
      return getJavaProject(container);
    }
    container = root.getContainerForLocation(root.getLocation().append(path));
    return getJavaProject(container);
  }

  private static IJavaProject getJavaProject(final IContainer container) {
    if (container != null) {
      if (container instanceof IProject) {
        final IJavaModel model = JavaCore.create(ResourcesPlugin.getWorkspace().getRoot());
        return model.getJavaProject(container.getName());
      }
      return getJavaProject(container.getParent());
    }
    return null;
  }

  public static IJavaProject[] getJavaProjects() {
    return getJavaProjects(getProjects());
  }

  public static IProject[] getProjects() {
    return ResourcesPlugin.getWorkspace().getRoot().getProjects();
  }

  private static IJavaProject[] getJavaProjects(final IProject[] projects) {
    final List<IJavaProject> javaProjects = new ArrayList<>();
    for (final IProject project : projects) {
      final IJavaProject javaProject = getJavaProject(project);
      if (javaProject == null) {
        continue;
      }
      javaProjects.add(javaProject);
    }
    return javaProjects.toArray(new IJavaProject[javaProjects.size()]);
  }

  public static IJavaProject[] getJavaProjects(final ITreeSelection treeSelection) {
    if (treeSelection == null) {
      return new IJavaProject[0];
    }
    final List<IJavaProject> projects = new ArrayList<>();
    final TreePath[] paths = treeSelection.getPaths();
    for (final TreePath treePath : paths) {
      if (treePath.equals(TreePath.EMPTY)) {
        continue;
      }
      for (int i = 0; i < treePath.getSegmentCount(); i++) {
        final Object segment = treePath.getSegment(i);
        if ((segment instanceof IJavaProject)) {
          projects.add((IJavaProject) segment);
          break;
        }
      }
    }
    return projects.toArray(new IJavaProject[projects.size()]);
  }

  public static IJavaElement[] getJavaElements(final IWorkbenchPart part, final ISelection selection) {
    if (part instanceof IPackagesViewPart) {
      return getJavaElements((ITreeSelection) selection);
    }
    return new IJavaElement[0];
  }

  public static IJavaElement[] getJavaElements(final ITreeSelection selection) {
    if (selection == null) {
      return new IJavaProject[0];
    }
    final List<IJavaElement> projects = new ArrayList<>();
    for (final TreePath treePath : selection.getPaths()) {
      if (treePath.equals(TreePath.EMPTY)) {
        continue;
      }
      final Object lastSegment = treePath.getLastSegment();
      if (!(lastSegment instanceof IJavaElement)) {
        continue;
      }
      projects.add((IJavaElement) lastSegment);
    }
    return projects.toArray(new IJavaElement[projects.size()]);
  }
}
