package net.anwiba.eclipse.project.dependency.java;

public interface IDependencies extends Iterable<IDependency> {

  public abstract IType getType(final IPath path);

  Iterable<IDependency> getDependencies();

  public abstract boolean containts(IType type);

}