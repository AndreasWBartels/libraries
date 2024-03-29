/*
 * #%L
 * anwiba commons core
 * %%
 * Copyright (C) 2007 - 2016 Andreas Bartels
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.anwiba.commons.lang.primitive.IBooleanProvider;

public interface IBooleanDistributor extends IBooleanProvider, IObjectChangedNotifier {

  default IBooleanDistributor and(final IBooleanDistributor model, final IBooleanDistributor... others) {
    final List<IBooleanDistributor> models = new ArrayList<>();
    models.add(this);
    models.add(model);
    models.addAll(Arrays.asList(others));
    return new AndAggregatedBooleanDistributor(models);
  }

  default IBooleanDistributor or(final IBooleanDistributor model, final IBooleanDistributor... others) {
    final List<IBooleanDistributor> models = new ArrayList<>();
    models.add(this);
    models.add(model);
    models.addAll(Arrays.asList(others));
    return new OrAggregatedBooleanDistributor(models);
  }

  default IBooleanDistributor not() {
    final IBooleanDistributor model = this;
    return new IBooleanDistributor() {
      
      List<IChangeableObjectListener> listeners = new ArrayList<>(); 
      @Override
      public void removeChangeListeners() {
        listeners.forEach(model::removeChangeListener);
      }
      
      @Override
      public void removeChangeListener(IChangeableObjectListener listener) {
        listeners.remove(listener);
        model.removeChangeListener(listener);
      }
      
      @Override
      public void addChangeListener(IChangeableObjectListener listener) {
        listeners.add(listener);
        model.addChangeListener(listener);
      }
      
      @Override
      public boolean isTrue() {
        return model.notTrue();
      }
    };
  }
}