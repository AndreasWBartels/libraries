/*
 * #%L
 * anwiba eclipse java tools
 * %%
 * Copyright (C) 2007 - 2022 Andreas Bartels
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
package net.anwiba.eclipse.project.name;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class NameHitMap implements INameHitMap {

  Map<String, AtomicLong> names = new HashMap<>();

  @Override
  public void add(final String name) {
    if (!this.names.containsKey(name)) {
      this.names.put(name, new AtomicLong());
    }
    this.names.get(name).incrementAndGet();
  }

  @Override
  public void reset() {
    this.names.clear();
  }

  @Override
  public Iterable<String> getNames() {
    final ArrayList<String> values = new ArrayList<>(this.names.keySet());
    Collections.sort(values);
    return values;
  }

  @Override
  public long getNumberOfUses(final String name) {
    return this.names.get(name).get();
  }

  @Override
  public boolean isEmpty() {
    return this.names.isEmpty();
  }
}
