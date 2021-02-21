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
import java.util.List;

public class BooleanModelAggregationModel extends AbstractObjectChangedNotifier
    implements
    IBooleanDistributor {

  private final List<IBooleanDistributor> values = new ArrayList<>();
  private final IChangeableObjectListener listener = new IChangeableObjectListener() {
    @Override
    public void objectChanged() {
      synchronized (BooleanModelAggregationModel.this.mutex) {
        final boolean currentState = BooleanModelAggregationModel.this.state;
        updateState();
        if (currentState == BooleanModelAggregationModel.this.state) {
          return;
        }
      }
      fireObjectChanged();
    }
  };

  private boolean state = true;
  private final Object mutex = new Object();

  public void add(final IBooleanDistributor model) {
    synchronized (this.mutex) {
      final boolean currentState = this.state;
      if (this.values.contains(model)) {
        return;
      }
      model.addChangeListener(this.listener);
      this.values.add(model);
      updateState();
      if (currentState == this.state) {
        return;
      }
    }
    fireObjectChanged();
  }

  public void remove(final IBooleanModel model) {
    synchronized (this.mutex) {
      final boolean currentState = this.state;
      if (!this.values.remove(model)) {
        return;
      }
      model.removeChangeListener(this.listener);
      updateState();
      if (currentState == this.state) {
        return;
      }
    }
    fireObjectChanged();
  }

  void updateState() {
    boolean flag = true;
    for (final IBooleanDistributor value : this.values) {
      flag = flag && value.isTrue();
    }
    setState(flag);
  }

  private void setState(final boolean state) {
    if (this.state == state) {
      return;
    }
    this.state = state;
  }

  public void removeAll() {
    synchronized (this.mutex) {
      final boolean currentState = this.state;
      for (final IBooleanDistributor value : this.values) {
        value.removeChangeListener(this.listener);
      }
      this.values.clear();
      setState(true);
      if (currentState == this.state) {
        return;
      }
    }
    fireObjectChanged();
  }

  @Override
  public boolean isTrue() {
    synchronized (this.mutex) {
      return this.state;
    }
  }
}