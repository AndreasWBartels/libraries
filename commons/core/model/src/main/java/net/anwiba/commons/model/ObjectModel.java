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

import net.anwiba.commons.lang.functional.IEqualComperator;
import net.anwiba.commons.lang.object.ObjectUtilities;

public class ObjectModel<T> extends AbstractObjectChangedNotifier implements IObjectModel<T> {

  private final Object monitor = new Object();
  private T value;
  private final boolean isNullable;
  private IEqualComperator<T> comperator;

  public ObjectModel() {
    this(null);
  }

  public ObjectModel(final IEqualComperator<T> comperator, final T value) {
    this(true, comperator, value);
  }

  public ObjectModel(final T value) {
    this(true, (t, o) -> ObjectUtilities.equals(t, o), value);
  }

  public ObjectModel(final boolean isNullable, final IEqualComperator<T> comperator, final T value) {
    accept(isNullable, value);
    this.comperator = comperator;
    this.isNullable = isNullable;
    this.value = value;
  }

  @SuppressWarnings("hiding")
  private void accept(final boolean isNullable, final T value) {
    if (!isNullable && value == null) {
      throw new IllegalArgumentException("argument is null"); //$NON-NLS-1$
    }
  }

  @Override
  public void set(final T value) {
    accept(this.isNullable, value);
    synchronized (this.monitor) {
      if (this.comperator.equals(this.value, value)) {
        return;
      }
      this.value = value;
    }
    fireObjectChanged();
  }

  @Override
  public T get() {
    synchronized (this.monitor) {
      return this.value;
    }
  }
}
