package net.anwiba.eclipse.project.dependency.view;

import net.anwiba.eclipse.project.dependency.java.ILibrary;
import net.anwiba.eclipse.project.dependency.java.IPackage;
import net.anwiba.eclipse.project.dependency.java.IProject;
import net.anwiba.eclipse.project.dependency.java.IType;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.ResourcesPlugin;
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

  public static List<Object> getTypes(final IType type) {
    final ILibrary library = type.getPackage().getLibrary();
    final List<Object> eclipseTypes = new ArrayList<>();
    if (library instanceof IProject) {
      final List<IJavaProject> eclipseProjects = getProjects((IProject) library);
      for (final IJavaProject javaProject : eclipseProjects) {
        try {
          final String qualifiedName = type.getQualifiedName();
          final org.eclipse.jdt.core.IType foundedType = javaProject.findType(qualifiedName);
          eclipseTypes.add(foundedType.getParent());
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