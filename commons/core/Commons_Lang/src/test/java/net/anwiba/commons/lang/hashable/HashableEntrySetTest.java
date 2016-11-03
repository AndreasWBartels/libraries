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
package net.anwiba.commons.lang.hashable;

import java.util.Map.Entry;

import net.anwiba.commons.lang.hashable.HashableEntrySet;
import net.anwiba.commons.lang.hashable.HashableIdentifier;
import net.anwiba.commons.lang.hashable.MapEntry;

import org.junit.Test;

public class HashableEntrySetTest {

  @Test
  public void add() {
    final Entry<HashableIdentifier, Object> entry = new MapEntry<>(new HashableIdentifier(), new Object());
    final Entry<HashableIdentifier, Object> otherEntry = new MapEntry<>(new HashableIdentifier(), new Object());
    final HashableEntrySet<HashableIdentifier, Object> set = new HashableEntrySet<>();
    final HashableEntrySet<HashableIdentifier, Object> otherSet = new HashableEntrySet<>();
    CollectionTestUtilities.assertSet(
        set,
        otherSet,
        entry,
        otherEntry,
        new ISetClonner<Entry<HashableIdentifier, Object>, HashableEntrySet<HashableIdentifier, Object>>() {
          @Override
          public HashableEntrySet<HashableIdentifier, Object> clone(
              @SuppressWarnings("hiding") final HashableEntrySet<HashableIdentifier, Object> set) {
            return set.clone();
          }
        });
  }
}
