package net.anwiba.eclipse.project.dependency.internal.java;

import net.anwiba.commons.lang.object.ObjectUtilities;
import net.anwiba.eclipse.project.dependency.java.IDependency;
import net.anwiba.eclipse.project.dependency.java.ILibrary;
import net.anwiba.eclipse.project.dependency.java.IPath;
import net.anwiba.eclipse.project.dependency.java.IType;

public class Dependency implements IDependency {

  private final ILibrary library;
  private final boolean isExported;

  public Dependency(final ILibrary library, final boolean exported) {
    this.library = library;
    this.isExported = exported;
  }

  @Override
  public String getIdentifier() {
    return getLibrary().getIdentifier();
  }

  @Override
  public boolean isExported() {
    return this.isExported;
  }

  @Override
  public ILibrary getLibrary() {
    return this.library;
  }

  @Override
  public boolean containts(final ILibrary library) {
    if (library == null) {
      return false;
    }
    for (final IDependency dependency : this.library.getDependencies()) {
      if (!dependency.isExported()) {
        continue;
      }
      if (library.equals(dependency.getLibrary()) || dependency.containts(library)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public IType getType(final IPath path) {
    IType type = this.library.getType(path);
    if (type != null) {
      return type;
    }
    for (final IDependency dependency : this.library.getDependencies()) {
      if (!dependency.isExported()) {
        continue;
      }
      type = dependency.getType(path);
      if (type != null) {
        return type;
      }
    }
    return null;
  }

  @Override
  public int hashCode() {
    return this.library.hashCode();
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof IDependency)) {
      return false;
    }
    final IDependency other = (IDependency) obj;
    return ObjectUtilities.equals(this.library, other.getLibrary());
  }

  @Override
  public String toString() {
    return this.library.toString();
  }
}