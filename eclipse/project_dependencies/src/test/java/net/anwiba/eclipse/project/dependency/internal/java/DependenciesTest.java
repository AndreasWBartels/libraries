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
