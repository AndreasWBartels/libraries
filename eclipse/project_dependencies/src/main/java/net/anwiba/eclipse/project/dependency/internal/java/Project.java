/*
 * #%L
 * anwiba eclipse java tools
 * %%
 * Copyright (C) 2007 - 2022 Andreas Bartels
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */
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
