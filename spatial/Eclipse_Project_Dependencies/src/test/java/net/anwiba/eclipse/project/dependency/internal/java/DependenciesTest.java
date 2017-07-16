/*
 * #%L
 * anwiba commons core
 * %%
 * Copyright (C) 2007 - 2017 Andreas Bartels
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

import net.anwiba.eclipse.project.dependency.internal.java.Dependencies;
import net.anwiba.eclipse.project.dependency.internal.java.Dependency;
import net.anwiba.eclipse.project.dependency.internal.java.Library;
import net.anwiba.eclipse.project.dependency.internal.java.Path;
import net.anwiba.eclipse.project.dependency.internal.java.Type;
import net.anwiba.eclipse.project.dependency.java.IImport;
import net.anwiba.eclipse.project.dependency.java.IPath;
import net.anwiba.eclipse.project.dependency.java.IType;
import net.anwiba.eclipse.project.dependency.java.LibraryType;
import net.anwiba.eclipse.project.dependency.java.TypeType;

import java.util.ArrayList;
import java.util.HashSet;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;

import static org.junit.Assert.*;

public class DependenciesTest {

  @Test
  public void test() throws Exception {
    final Dependencies dependencies = new Dependencies();
    dependencies.add(new Dependency(new Library("lib00.jar", LibraryType.JAR), false));
    final Library library = new Library("lib10.jar", LibraryType.JAR);
    dependencies.add(new Dependency(library, false));
    final Library exportedLibrary = new Library("lib11.jar", LibraryType.JAR);
    final IPath path = new Path(new String[] { "package", "Class" });
    final IType type =
        new Type(
            exportedLibrary,
            path,
            path.toString(),
            TypeType.CLASS,
            new ArrayList<IImport>(),
            new ArrayList<IPath>(),
            new HashSet<IPath>(),
            new HashSet<IPath>());
    exportedLibrary.add(type);
    final Dependency exportedDependency = new Dependency(exportedLibrary, true);
    library.add(exportedDependency);
    assertThat(exportedLibrary.getType(path), equalTo(type));
    assertThat(exportedDependency.getType(path), equalTo(type));
    assertThat(dependencies.getType(path), equalTo(type));
  }
}
