package net.anwiba.eclipse.project.dependency.java;

public interface IPackage extends ITypeContainer, IJavaItem {

  Iterable<IType> getType(IPath path);

  ILibrary getLibrary();

  boolean isParent(IPackage pakkage);

}
