package net.anwiba.eclipse.project.dependency.internal.java;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import net.anwiba.eclipse.project.dependency.java.IDependencies;
import net.anwiba.eclipse.project.dependency.java.IDependency;
import net.anwiba.eclipse.project.dependency.java.ILibrary;
import net.anwiba.eclipse.project.dependency.java.IPath;
import net.anwiba.eclipse.project.dependency.java.IType;

public class Dependencies implements IDependencies {

  private final Map<String, IDependency> map = new HashMap<String, IDependency>();
  private final Set<IDependency> dependencies = new HashSet<IDependency>();

  public void add(final IDependency dependency) {
    if (this.map.containsKey(dependency.getIdentifier())) {
      this.dependencies.remove(this.map.remove(dependency.getIdentifier()));
    }
    this.dependencies.add(dependency);
    this.map.put(dependency.getIdentifier(), dependency);
  }

  public boolean containts(final ILibrary library) {
    if (library == null) {
      return false;
    }
    for (final IDependency dependency : this.dependencies) {
      if (library.equals(dependency.getLibrary()) || dependency.containts(library)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public Set<IDependency> getDependencies() {
    return this.dependencies;
  }

  @Override
  public IType getType(final IPath path) {
    for (final IDependency dependency : this.dependencies) {
      final IType type = dependency.getType(path);
      if (type != null) {
        return type;
      }
    }
    return null;
  }

  @Override
  public boolean containts(final IType type) {
    return getType(type.getPath()) != null;
  }

  public void reset() {
    this.map.clear();
    this.dependencies.clear();
  }

  @Override
  public Iterator<IDependency> iterator() {
    return this.dependencies.iterator();
  }
}