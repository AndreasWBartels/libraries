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

import net.anwiba.eclipse.project.name.INameHitMap;
import net.anwiba.eclipse.project.name.NameHitMap;

public class NameHitMaps implements INameHitMaps {

  private final INameHitMap nameParts = new NameHitMap();
  private final INameHitMap namePostfixes = new NameHitMap();
  private final INameHitMap oneWordNames = new NameHitMap();
  private final INameHitMap unmatchedNames = new NameHitMap();
  private final INameHitMap namePrefixes = new NameHitMap();
  private final INameHitMap names = new NameHitMap();

  @Override
  public INameHitMap getNameParts() {
    return this.nameParts;
  }

  @Override
  public INameHitMap getNamePostfixes() {
    return this.namePostfixes;
  }

  @Override
  public INameHitMap getOneWordNames() {
    return this.oneWordNames;
  }

  @Override
  public void reset() {
    this.nameParts.reset();
    this.oneWordNames.reset();
    this.namePostfixes.reset();
    this.namePrefixes.reset();
    this.unmatchedNames.reset();
    this.names.reset();
  }

  @Override
  public INameHitMap getUnmatchedNames() {
    return this.unmatchedNames;
  }

  @Override
  public INameHitMap getNamePrefixes() {
    return this.namePrefixes;
  }

  @Override
  public INameHitMap getNames() {
    return this.names;
  }

  @Override
  public boolean isEmpty() {
    return this.nameParts.isEmpty() && this.namePostfixes.isEmpty() && this.namePrefixes.isEmpty()
        && this.oneWordNames.isEmpty() && this.unmatchedNames.isEmpty() && this.names.isEmpty();
  }

}
