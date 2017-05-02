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

import java.util.Set;

public interface IType extends IJavaItem {

  TypeType getType();

  ILibrary getLibrary();

  Iterable<IImport> getImports();

  Iterable<IPath> getSuperTypes();

  Iterable<IPath> getMethodTypes();

  Iterable<IType> getImplementedBy();

  Iterable<IType> getUsedBy();

  boolean isUsed();

  Set<IPath> getAnnotationTypes();

  boolean isImplemented();

  String getQualifiedName();

  IPackage getPackage();

}