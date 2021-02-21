package net.anwiba.eclipse.project.dependency.java;

import java.net.URI;

public interface IProject extends ILibrary {

  Iterable<ILibrary> getClasspath();

  URI getUri();

}
