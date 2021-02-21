package net.anwiba.eclipse.project.dependency.java;

import java.util.Set;

public interface IType extends IJavaItem {

  TypeType getType();

  ILibrary getLibrary();

  Iterable<IImport> getImports();

  Iterable<IPath> getSuperTypes();

  Iterable<IPath> getMethodTypes();

  Iterable<IType> getImplementedBy();

  Iterable<IType> getUsedBy();

  boolean isUsed();

  Set<IPath> getAnnotationTypes();

  boolean isImplemented();

  String getQualifiedName();

  IPackage getPackage();

}
