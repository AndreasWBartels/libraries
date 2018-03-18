/*
 * #%L
 *
 * %%
 * Copyright (C) 2007 - 2017 Andreas W. Bartels
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
package net.anwiba.commons.model;

import java.util.Collection;
import java.util.stream.Collectors;

public class OrAggregatedBooleanDistributor extends AbstractObjectChangedNotifier implements IBooleanDistributor {

  private final IBooleanModel model;

  public OrAggregatedBooleanDistributor create(final Collection<? extends IBooleanDistributor> models) {
    return new OrAggregatedBooleanDistributor(models.stream().collect(Collectors.toList()));
  }

  public OrAggregatedBooleanDistributor(final Collection<IBooleanDistributor> models) {
    @SuppressWarnings("hiding")
    final IBooleanModel model = new BooleanModel(models.stream().filter(m -> m.get()).findFirst().isPresent());
    model.addChangeListener(() -> fireObjectChanged());
    final IChangeableObjectListener listener = () -> model
        .set(models.stream().filter(m -> m.get()).findFirst().isPresent());
    models.stream().forEach(m -> m.addChangeListener(listener));
    this.model = model;
  }

  @Override
  public boolean get() {
    return this.model.get();
  }

}
