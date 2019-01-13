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

import net.anwiba.commons.lang.functional.IProcedure;

public class ObjectModelListModel<T> extends AbstractObjectListModel<IObjectModel<T>> {

  public ObjectModelListModel() {
    super(new ArrayList<>());
  }

  public synchronized Iterable<IObjectModel<T>> models() {
    return toCollection();
  }

  public synchronized IObjectModel<T> last() {
    return get(size() - 1);
  }

  private final ListenerList<IChangeableObjectListener> changeableObjectListeners = new ListenerList<>();

  public final synchronized void addChangeListener(final IChangeableObjectListener listener) {
    this.changeableObjectListeners.add(listener);
  }

  public final synchronized void removeChangeListener(final IChangeableObjectListener listener) {
    this.changeableObjectListeners.remove(listener);
  }

  protected final synchronized void fireValueChanged() {
    this.changeableObjectListeners.forAllDo(new IProcedure<IChangeableObjectListener, RuntimeException>() {

      @Override
      public void execute(final IChangeableObjectListener listener) throws RuntimeException {
        listener.objectChanged();
      }
    });
  }
}