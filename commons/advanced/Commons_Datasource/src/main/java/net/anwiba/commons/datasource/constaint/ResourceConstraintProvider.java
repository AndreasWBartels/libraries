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

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import net.anwiba.commons.datasource.resource.IResourceDescription;
import net.anwiba.commons.lang.collection.IObjectList;
import net.anwiba.commons.lang.collection.ObjectList;
import net.anwiba.commons.lang.stream.Streams;

public class ResourceConstraintProvider implements IResourceConstraintProvider {

  private final IResourceConstraintStorage resourceConstraintStorage;
  final Collection<IRegisterableResourceConstraintProvider> providers = new ArrayList<>();

  public ResourceConstraintProvider(
      final Collection<IRegisterableResourceConstraintProvider> providers,
      final IResourceConstraintStorage resourceConstraintStorage) {
    this.resourceConstraintStorage = resourceConstraintStorage;
    this.providers.addAll(providers);
  }

  @Override
  public IObjectList<IResourceConstraint> getConstaints(final IResourceDescription description) {
    final Set<IResourceConstraint> constraints = new LinkedHashSet<>();
    constraints.addAll(this.resourceConstraintStorage.read(description).toCollection());
    Streams.of(this.providers).filter(p -> p.isApplicable(description)).foreach(
        p -> constraints.addAll(p.getConstaints(description).toCollection()));
    return new ObjectList<>(constraints);
  }

}
