package net.anwiba.eclipse.project.dependency.java;

public interface IDependency {

  String getIdentifier();

  ILibrary getLibrary();

  boolean isExported();

  IType getType(IPath path);

  boolean containts(ILibrary library);

}
