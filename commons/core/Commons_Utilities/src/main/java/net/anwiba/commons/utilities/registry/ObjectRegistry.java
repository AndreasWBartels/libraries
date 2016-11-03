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
package net.anwiba.commons.utilities.registry;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ObjectRegistry<V> implements IObjectRegistry<V> {

  List<V> objects = new ArrayList<>();

  @Override
  public synchronized Iterator<V> iterator() {
    return this.objects.iterator();
  }

  @Override
  public synchronized void register(final V value) {
    if (this.objects.contains(value)) {
      return;
    }
    this.objects.add(value);
  }

  @Override
  public synchronized boolean isEmpty() {
    return this.objects.isEmpty();
  }

}
