/*
 * #%L
 * anwiba eclipse java tools
 * %%
 * Copyright (C) 2007 - 2021 Andreas Bartels
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

import net.anwiba.eclipse.project.name.INameCollector;
import net.anwiba.eclipse.project.name.INamePartsIterable;
import net.anwiba.eclipse.project.name.NamePartsIterable;

public class NameCollector implements INameCollector {

  private final INameHitMaps nameHitMaps;

  public NameCollector(final INameHitMaps nameHitMaps) {
    this.nameHitMaps = nameHitMaps;
  }

  @Override
  public void add(final String name) {
    this.nameHitMaps.getNames().add(name);
    final INamePartsIterable iterable = new NamePartsIterable(name);
    String lastNamePart = null;
    String firstNamePart = null;
    for (final String namePart : iterable) {
      this.nameHitMaps.getNameParts().add(namePart);
      if (firstNamePart == null && (name.startsWith(namePart) || name.startsWith("I" + namePart))) { //$NON-NLS-1$
        firstNamePart = namePart;
      }
      lastNamePart = namePart;
    }
    if (lastNamePart == null || !name.endsWith(lastNamePart) || firstNamePart == null) {
      this.nameHitMaps.getUnmatchedNames().add(name);
    }
    if (lastNamePart == null) {
      return;
    }
    if (lastNamePart.equals(firstNamePart)
        && (name.length() == lastNamePart.length() || name.length() - 1 == lastNamePart.length())) {
      this.nameHitMaps.getOneWordNames().add(lastNamePart);
      return;
    }
    if (name.endsWith(lastNamePart)) {
      this.nameHitMaps.getNamePostfixes().add(lastNamePart);
    }
    if (firstNamePart != null) {
      this.nameHitMaps.getNamePrefixes().add(firstNamePart);
      return;
    }
  }
}
