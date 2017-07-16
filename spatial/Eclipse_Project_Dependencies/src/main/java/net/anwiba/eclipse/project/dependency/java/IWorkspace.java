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
package net.anwiba.eclipse.project.dependency.java;

import java.net.URI;
import java.util.List;
import java.util.Map;

public interface IWorkspace {

  Map<String, ILibrary> getLibraries();

  Map<String, IProject> getProjects();

  Map<String, IPackage> getPackages();

  Map<String, IType> getTypes();

  Map<String, List<IType>> getDuplicates();

  URI getUri();

  IType[] getTypes(IPath path);

  IProject getProject(String name);

  IPackage getPackage(String string);

  List<IType> getUsed(IType type);

  List<IType> getImplemented(IType type);

}