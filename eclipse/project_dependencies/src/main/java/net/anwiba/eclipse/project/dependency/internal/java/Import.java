package net.anwiba.eclipse.project.dependency.internal.java;

import net.anwiba.eclipse.project.dependency.java.IImport;
import net.anwiba.eclipse.project.dependency.java.IPath;

public class Import implements IImport {

  private final String name;
  private final IPath path;

  public Import(final String name, final IPath path) {
    this.name = name;
    this.path = path;
  }

  @Override
  public IPath getPath() {
    return this.path;
  }

  @Override
  public String getIdentifier() {
    return this.name;
  }

  @Override
  public String getName() {
    return this.name;
  }

  @Override
  public String toString() {
    return this.name;
  }
}