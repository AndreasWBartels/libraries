package net.anwiba.eclipse.project.dependency.java;

import java.net.URI;
import java.util.List;
import java.util.Map;

public interface IWorkspace {

  Map<String, ILibrary> getLibraries();

  Map<String, IProject> getProjects();

  Map<String, IPackage> getPackages();

  Map<String, IType> getTypes();

  Map<String, List<IType>> getDuplicates();

  URI getUri();

  IType[] getTypes(IPath path);

  IProject getProject(String name);

  IPackage getPackage(String string);

  List<IType> getUsed(IType type);

  List<IType> getImplemented(IType type);

}