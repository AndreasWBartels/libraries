/*
 * #%L
 *
 * %%
 * Copyright (C) 2007 - 2018 Andreas W. Bartels
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
package net.anwiba.commons.datasource.constaint;

import net.anwiba.commons.datasource.resource.IResourceDescription;
import net.anwiba.commons.lang.collection.IObjectList;
import net.anwiba.commons.lang.optional.IOptional;

public interface IResourceConstraintStorage {

  void save(IResourceConstraint constraint);

  boolean delete(IResourceConstraint constraint);

  IObjectList<IResourceConstraint> read(IResourceDescription description);

  IOptional<ILicense, RuntimeException> read(String identifier);

  IObjectList<ILicense> readLicenses();

}