package net.anwiba.eclipse.project.dependency.java;

public interface ILibrary extends ITypeContainer {

  LibraryType getLibraryType();

  IType getType(IPath path);

  IDependencies getDependencies();

  boolean containts(IPath path);

  Iterable<ILibrary> getUsedLibraries();

  boolean isInstance(ILibrary library);

  Iterable<ILibrary> getUsedByLibraries();

  Iterable<ILibrary> getImplementedFromLibraries();

  Iterable<ILibrary> getImplementedLibraries();

  void addImplemets(ILibrary library);

}
