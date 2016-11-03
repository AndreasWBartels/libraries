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
package net.anwiba.commons.lang.tree;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.anwiba.commons.lang.object.ObjectUtilities;
import net.anwiba.commons.lang.tree.distance.IObjectDistanceCalculator;

public final class TreeItemChooser<K, V> implements ITreeItemChooser<K, V> {

  public class ObjectPair<T, U> {

    private final int hashCode;
    private final T firstObject;
    private final U secondObject;

    public ObjectPair(final T firstObject, final U secondObject) {
      this.firstObject = firstObject;
      this.secondObject = secondObject;
      this.hashCode = calculate();
    }

    public int calculate() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((this.firstObject == null) ? 0 : this.firstObject.hashCode());
      result = prime * result + ((this.secondObject == null) ? 0 : this.secondObject.hashCode());
      return result;
    }

    public final T getFirstObject() {
      return this.firstObject;
    }

    public final U getSecondObject() {
      return this.secondObject;
    }

    @Override
    public boolean equals(final Object obj) {
      if (!(obj instanceof ObjectPair)) {
        return false;
      }
      @SuppressWarnings("unchecked")
      final ObjectPair<?, ?> other = (ObjectPair<?, ?>) obj;
      return ObjectUtilities.equals(this.firstObject, other.firstObject)
          && ObjectUtilities.equals(this.secondObject, other.secondObject);
    }

    @Override
    public final int hashCode() {
      return this.hashCode;
    }

  }

  private final IObjectDistanceCalculator<K> distanceCalculator;
  private final Map<ObjectPair<K, K>, Double> distances = new HashMap<>();
  private final Map<K, Set<ObjectPair<K, K>>> distanceKeys = new HashMap<>();

  public TreeItemChooser(final IObjectDistanceCalculator<K> distanceCalculator) {
    this.distanceCalculator = distanceCalculator;
  }

  @Override
  public K choose(final Comparator<K> comparator, final ITreeItem<K, V> item, final K key, final V element) {
    final K itemKey = item.getKey();
    final double distancePrecursorSuccessor = calculate(item.getPrevious().getKey(), item.getNext().getKey());
    if (Double.isNaN(distancePrecursorSuccessor)) {
      return itemKey;
    }
    final double distancePrecursorItem = calculate(item.getPrevious().getKey(), itemKey);
    if (Double.isNaN(distancePrecursorItem)) {
      return key;
    }
    final double distancePrecursorOther = calculate(item.getPrevious().getKey(), key);
    if (Double.isNaN(distancePrecursorOther)) {
      return itemKey;
    }
    if (isOtherMoreCentral(distancePrecursorSuccessor, distancePrecursorItem, distancePrecursorOther)) {
      return key;
    }
    return itemKey;
  }

  private boolean isOtherMoreCentral(final double distance, final double distanceToItem, final double distanceToOther) {
    return Math.abs((distance / 2.) - distanceToOther) < Math.abs((distance / 2.) - distanceToItem);
  }

  private double calculate(final K key, final K other) {
    final ObjectPair<K, K> distanceKey = new ObjectPair<>(key, other);
    if (this.distances.containsKey(distanceKey)) {
      return this.distances.get(distanceKey).doubleValue();
    }
    final double distance = this.distanceCalculator.calculate(key, other);
    cache(distanceKey, distance);
    return distance;
  }

  private void cache(final ObjectPair<K, K> distanceKey, final double distance) {
    this.distances.put(distanceKey, Double.valueOf(distance));
    cache(distanceKey.getFirstObject(), distanceKey);
    cache(distanceKey.getSecondObject(), distanceKey);
  }

  private void cache(final K key, final ObjectPair<K, K> distanceKey) {
    if (!this.distanceKeys.containsKey(key)) {
      this.distanceKeys.put(key, new HashSet<ObjectPair<K, K>>());
    }
    this.distanceKeys.get(key).add(distanceKey);
  }

  @Override
  public void removed(final K key) {
    if (!this.distanceKeys.containsKey(key)) {
      return;
    }
    final Set<ObjectPair<K, K>> set = this.distanceKeys.get(key);
    for (final ObjectPair<K, K> distanceKey : set) {
      this.distances.remove(distanceKey);
    }
    this.distanceKeys.remove(key);
  }
}