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
