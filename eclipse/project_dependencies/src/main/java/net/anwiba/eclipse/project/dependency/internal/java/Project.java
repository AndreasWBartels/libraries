package net.anwiba.eclipse.project.dependency.internal.java;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import net.anwiba.eclipse.project.dependency.java.ILibrary;
import net.anwiba.eclipse.project.dependency.java.IProject;
import net.anwiba.eclipse.project.dependency.java.LibraryType;

public class Project extends Library implements IProject {

  private final List<ILibrary> classpath = new ArrayList<ILibrary>();
  private final URI uri;

  public Project(final String name, final URI uri) {
    super(name, LibraryType.PROJECT);
    this.uri = uri;
  }

  @Override
  public boolean equals(final Object obj) {
    return super.equals(obj);
  }

  @Override
  public int hashCode() {
    return super.hashCode();
  }

  public void setClasspath(final Iterable<ILibrary> libraries) {
    this.classpath.clear();
    for (final ILibrary library : libraries) {
      this.classpath.add(library);
    }
  }

  @Override
  public Iterable<ILibrary> getClasspath() {
    return this.classpath;
  }

  @Override
  public URI getUri() {
    return this.uri;
  }
}
