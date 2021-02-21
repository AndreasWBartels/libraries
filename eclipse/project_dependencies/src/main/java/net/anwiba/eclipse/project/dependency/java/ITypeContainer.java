package net.anwiba.eclipse.project.dependency.java;

public interface ITypeContainer extends IItem {

  boolean containts(IType type);

  Iterable<IType> getTypes();

}
