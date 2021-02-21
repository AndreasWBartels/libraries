package net.anwiba.eclipse.project.dependency.runner;

import net.anwiba.commons.thread.cancel.ICanceler;
import net.anwiba.eclipse.project.dependency.java.IDependency;
import net.anwiba.eclipse.project.dependency.java.ILibrary;

import java.util.Set;

public class ClasspathCollector {

  public static void collect(final ICanceler canceler, final Set<ILibrary> libraries, final ILibrary library)
      throws InterruptedException {
    if (canceler.isCanceled()) {
      throw new InterruptedException();
    }
    final Iterable<IDependency> dependencies = library.getDependencies().getDependencies();
    for (final IDependency dependency : dependencies) {
      final ILibrary usedLibrary = dependency.getLibrary();
      if (libraries.contains(usedLibrary)) {
        continue;
      }
      libraries.add(usedLibrary);
      ClasspathCollector.collect(canceler, libraries, usedLibrary);
    }
  }

}
