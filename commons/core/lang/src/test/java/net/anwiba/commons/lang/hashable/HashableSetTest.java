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

import org.junit.jupiter.api.Test;

public class HashableSetTest {

  @Test
  public void add() {
    final HashableIdentifier identifier = new HashableIdentifier();
    final HashableIdentifier otherIdentifier = new HashableIdentifier();
    final HashableSet<HashableIdentifier> set = new HashableSet<>();
    final HashableSet<HashableIdentifier> otherSet = new HashableSet<>();
    CollectionTestUtilities.assertSet(
        set,
        otherSet,
        identifier,
        otherIdentifier,
        new ISetClonner<HashableIdentifier, HashableSet<HashableIdentifier>>() {
          @SuppressWarnings("hiding")
          @Override
          public HashableSet<HashableIdentifier> clone(final HashableSet<HashableIdentifier> set) {
            return set.clone();
          }
        });

  }
}
