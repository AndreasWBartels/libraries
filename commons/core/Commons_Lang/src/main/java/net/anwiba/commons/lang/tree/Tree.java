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

import net.anwiba.commons.lang.collection.IObjectIteratorFactory;
import net.anwiba.commons.lang.exception.UnreachableCodeReachedException;
import net.anwiba.commons.lang.tree.distance.IObjectDistanceCalculator;
import net.anwiba.commons.lang.tree.iterator.BreadthFirstSearchValueIteratorFactory;
import net.anwiba.commons.lang.tree.iterator.DeepFirstSearchValueIteratorFactory;
import net.anwiba.commons.lang.tree.iterator.SortedKeyIteratorFactory;
import net.anwiba.commons.lang.tree.iterator.SortedValueIteratorFactory;
import net.anwiba.commons.lang.tree.iterator.TreeIterable;
import net.anwiba.commons.lang.tree.walker.ITreeWalker;
import net.anwiba.commons.lang.tree.walker.TreeWalker;

import java.util.Comparator;

public class Tree<K, V> implements ITree<K, V> {

  private TreeItem<K, V> first = null;
  private TreeItem<K, V> last = null;
  private TreeItem<K, V> root = null;

  private int numberOfItems = 0;
  private final int maximumOfItems;
  private Comparator<K> comparator;
  private ITreeItemChooser<K, V> treeItemChooser;

  public Tree(final Comparator<K> comparator) {
    this(comparator, Integer.MAX_VALUE, new TreeItemChooser<K, V>(new IObjectDistanceCalculator<K>() {

      @Override
      public double calculate(final K object, final K other) {
        return Double.NaN;
      }
    }));
  }

  public Tree(final Comparator<K> comparator, final int maximumOfItems, final ITreeItemChooser<K, V> treeItemChooser) {
    this.comparator = comparator;
    this.maximumOfItems = maximumOfItems;
    this.treeItemChooser = treeItemChooser;
  }

  @Override
  public void insert(final K key, final V element) {
    if (key == null) {
      throw new NullPointerException();
    }
    if (element == null) {
      throw new NullPointerException();
    }
    if (this.root == null) {
      this.root = new TreeItem<>(key, element);
      this.first = this.root;
      this.last = this.root;
      this.numberOfItems++;
    } else {
      insert(this.root, key, element);
    }
  }

  private boolean insert(final TreeItem<K, V> item, final K key, final V element) {
    if (item == null) {
      throw new NullPointerException();
    }
    if (element == null) {
      throw new NullPointerException();
    }
    if (this.comparator.compare(key, item.getKey()) < 0) {
      if (item.left == null) {
        if (this.numberOfItems >= this.maximumOfItems) {
          changeItemIfNecesary(item, key, element);
          return false;
        }
        item.left = new TreeItem<>(key, element);
        item.left.parent = item;
        item.balanced--;
        item.left.next = item;
        item.left.prev = item.prev;
        item.prev = item.left;
        if (item.left.prev != null) {
          item.left.prev.next = item.left;
        } else {
          this.first = item.left;
        }
        this.numberOfItems++;
        return item.right == null;
      }
      if (insert(item.left, key, element)) {
        switch (item.balanced) {
          case TreeItem.BALANCED: {
            item.balanced = TreeItem.LEFT;
            return true;
          }
          case TreeItem.RIGHT: {
            item.balanced = TreeItem.BALANCED;
            return false;
          }
          case TreeItem.LEFT: {
            if (item.left.balanced == TreeItem.RIGHT) {
              leftRightRotation(item);
            } else {
              rightRotation(item);
            }
            item.balanced = TreeItem.BALANCED;
            return false;
          }
        }
        throw new UnreachableCodeReachedException();
      }
      return false;
    } else if (this.comparator.compare(key, item.getKey()) > 0) {
      if (item.right == null) {
        if (this.numberOfItems >= this.maximumOfItems) {
          changeItemIfNecesary(item, key, element);
          return false;
        }
        item.right = new TreeItem<>(key, element);
        item.right.parent = item;
        item.balanced++;
        item.right.prev = item;
        item.right.next = item.next;
        item.next = item.right;
        if (item.right.next != null) {
          item.right.next.prev = item.right;
        } else {
          this.last = item.right;
        }
        this.numberOfItems++;
        return item.left == null;
      }
      if (insert(item.right, key, element)) {
        switch (item.balanced) {
          case TreeItem.BALANCED: {
            item.balanced = TreeItem.RIGHT;
            return true;
          }
          case TreeItem.LEFT: {
            item.balanced = TreeItem.BALANCED;
            return false;
          }
          case TreeItem.RIGHT: {
            if (item.right.balanced == TreeItem.LEFT) {
              rightLeftRotation(item);
            } else {
              leftRotation(item);
            }
            item.balanced = TreeItem.BALANCED;
            return false;
          }
        }
        throw new UnreachableCodeReachedException();
      }
      return false;
    } else {
      item.setElement(element);
      return false;
    }
  }

  private void changeItemIfNecesary(final TreeItem<K, V> item, final K key, final V element) {
    final TreeItem<K, V> choosed = choose(item, key, element);
    if (choosed == null) {
      this.treeItemChooser.removed(key);
      return;
    }
    replace(choosed, new TreeItem<>(key, element));
    return;
  }

  private final TreeItem<K, V> choose(final TreeItem<K, V> item, final K key, final V element) {
    final K itemKey = item.getKey();
    final int compareResult = this.comparator.compare(itemKey, key);
    if (compareResult == 0) {
      return null;
    }
    if (item.getPrevious() == null) {
      if (compareResult > 0) {
        return item;
      }
      final K choosed = this.treeItemChooser.choose(this.comparator, item.next, key, element);
      if (choosed == key) {
        return item.next;
      }
      return null;
    }
    if (item.getNext() == null) {
      if (compareResult < 0) {
        return item;
      }
      final K choosed = this.treeItemChooser.choose(this.comparator, item.prev, key, element);
      if (choosed == key) {
        return item.prev;
      }
      return null;
    }
    K choosed = this.treeItemChooser.choose(this.comparator, item, key, element);
    if (choosed == key) {
      return item;
    }
    if (compareResult < 0) {
      if (item.next.next != null) {
        choosed = this.treeItemChooser.choose(this.comparator, item.next, key, element);
        if (choosed == key) {
          return item.next;
        }
      }
      return null;
    }
    if (compareResult > 0) {
      if (item.prev.prev != null) {
        choosed = this.treeItemChooser.choose(this.comparator, item.prev, key, element);
        if (choosed == key) {
          return item.prev;
        }
      }
      return null;
    }
    return null;
  }

  private void replace(final TreeItem<K, V> item, final TreeItem<K, V> with) {
    this.treeItemChooser.removed(item.getKey());
    with.balanced = item.balanced;
    with.left = item.left;
    if (with.left != null) {
      with.left.parent = with;
    }
    with.right = item.right;
    if (with.right != null) {
      with.right.parent = with;
    }
    with.parent = item.parent;
    if (with.parent != null) {
      if (with.parent.left == item) {
        with.parent.left = with;
      }
      if (with.parent.right == item) {
        with.parent.right = with;
      }
    }
    with.next = item.next;
    if (with.next != null) {
      with.next.prev = with;
    }
    with.prev = item.prev;
    if (with.prev != null) {
      with.prev.next = with;
    }
    if (this.root == item) {
      this.root = with;
    }
    if (this.last == item) {
      this.last = with;
    }
    if (this.first == item) {
      this.first = with;
    }
  }

  @Override
  public void removeAll() {
    TreeItem<K, V> item = this.first;
    TreeItem<K, V> next = null;
    while (item != null) {
      this.treeItemChooser.removed(item.getKey());
      next = item.next;
      item.prev = null;
      item.next = null;
      item.right = null;
      item.left = null;
      item.parent = null;
      item = next;
    }
    this.first = null;
    this.last = null;
    this.root = null;
    this.numberOfItems = 0;
  }

  @Override
  public V get(final K element) {
    if (element == null) {
      return null;
    }
    return get(this.root, element);
  }

  private V get(final TreeItem<K, V> item, final K key) {
    if (item == null) {
      return null;
    }
    if (this.comparator.compare(key, item.getKey()) < 0) {
      return get(item.left, key);
    } else if (this.comparator.compare(key, item.getKey()) > 0) {
      return get(item.right, key);
    } else {
      return item.getElement();
    }
  }

  @Override
  public void remove(final K key) {
    remove(this.root, key);
  }

  public boolean remove(final TreeItem<K, V> item, final K key) {
    if (item == null) {
      return false;
    }
    if (this.comparator.compare(key, item.getKey()) < 0) {
      if (remove(item.left, key)) {
        return rebalanceLeft(item);
      }
      return false;
    } else if (this.comparator.compare(key, item.getKey()) > 0) {
      if (remove(item.right, key)) {
        return rebalanceRight(item);
      }
      return false;
    } else {
      boolean shorted = false;
      this.treeItemChooser.removed(key);
      if (item.left == null && item.right == null) {
        shorted = removeChildLessItem(item);
      } else if (item.left == null) {
        shorted = removeRightChild(item);
      } else if (item.right == null) {
        shorted = removeLeftChild(item);
      } else {
        shorted = removeItemWithBothChildren(item);
      }
      if (item.next != null && item.prev != null) {
        item.next.prev = item.prev;
        item.prev.next = item.next;
      } else if (item.next != null) {
        item.next.prev = null;
      } else if (item.prev != null) {
        item.prev.next = null;
      }
      return shorted;
    }
  }

  private boolean removeItemWithBothChildren(final TreeItem<K, V> item) {
    boolean shorted;
    final TreeItem<K, V> rightLeastItem = getMinItem(item.right);
    shorted = remove(item, rightLeastItem.getKey());
    rightLeastItem.left = item.left;
    if (item.left != null) {
      rightLeastItem.left.parent = rightLeastItem;
    }
    rightLeastItem.right = item.right;
    if (item.right != null) {
      rightLeastItem.right.parent = rightLeastItem;
    }
    rightLeastItem.parent = item.parent;
    if (rightLeastItem.next != null) {
      rightLeastItem.next.prev = rightLeastItem;
    }
    if (rightLeastItem.prev != null) {
      rightLeastItem.prev.next = rightLeastItem;
    }
    if (item.parent != null) {
      if (item.parent.left == item) {
        item.parent.left = rightLeastItem;
      } else {
        item.parent.right = rightLeastItem;
      }
    }
    if (this.first == item) {
      this.first = rightLeastItem;
    }
    if (this.last == item) {
      this.last = rightLeastItem;
    }
    if (this.root == item) {
      this.root = rightLeastItem;
    }
    if (shorted) {
      shorted = rebalanceRight(item);
    }
    return shorted;
  }

  private boolean removeLeftChild(final TreeItem<K, V> item) {
    item.left.parent = item.parent;
    if (item.parent != null) {
      if (item.parent.left == item) {
        item.parent.left = item.left;
      } else {
        item.parent.right = item.left;
      }
    }
    this.numberOfItems--;
    if (this.first == item) {
      this.first = item.next;
    }
    if (this.root == item) {
      this.root = item.left;
    }
    return true;
  }

  private boolean removeRightChild(final TreeItem<K, V> item) {
    item.right.parent = item.parent;
    if (item.parent != null) {
      if (item.parent.left == item) {
        item.parent.left = item.right;
      } else {
        item.parent.right = item.right;
      }
    }
    this.numberOfItems--;
    if (this.last == item) {
      this.last = item.prev;
    }
    if (this.root == item) {
      this.root = item.right;
    }
    return true;
  }

  private boolean removeChildLessItem(final TreeItem<K, V> item) {
    if (item.parent != null) {
      if (item.parent.left == item) {
        item.parent.left = null;
      } else {
        item.parent.right = null;
      }
    }
    this.numberOfItems--;
    if (this.first == item) {
      this.first = item.next;
    }
    if (this.last == item) {
      this.last = item.prev;
    }
    if (this.root == item) {
      this.root = null;
    }
    return true;
  }

  private TreeItem<K, V> getMinItem(final TreeItem<K, V> item) {
    if (item.left == null) {
      return item;
    }
    return getMinItem(item.left);
  }

  @Override
  public int size() {
    return this.numberOfItems;
  }

  @Override
  public boolean isEmpty() {
    return this.root == null;
  }

  private boolean rebalanceRight(final TreeItem<K, V> item) {
    final int balanced = item.left != null
        ? item.left.balanced
        : TreeItem.BALANCED;
    switch (item.balanced) {
      case TreeItem.RIGHT: {
        item.balanced = TreeItem.BALANCED;
        return true;
      }
      case TreeItem.BALANCED: {
        item.balanced = TreeItem.LEFT;
        return false;
      }
      case TreeItem.LEFT: {
        switch (balanced) {
          case TreeItem.RIGHT: {
            leftRightRotation(item);
            return true;
          }
          case TreeItem.BALANCED: {
            final TreeItem<K, V> dummy = rightRotation(item);
            dummy.right.balanced = TreeItem.LEFT;
            dummy.balanced = TreeItem.RIGHT;
            return false;
          }
          case TreeItem.LEFT: {
            final TreeItem<K, V> dummy = rightRotation(item);
            dummy.right.balanced = TreeItem.BALANCED;
            dummy.balanced = TreeItem.BALANCED;
            return true;
          }
          default: {
            throw new UnreachableCodeReachedException();
          }
        }
      }
      default: {
        throw new UnreachableCodeReachedException();
      }
    }
  }

  private boolean rebalanceLeft(final TreeItem<K, V> item) {
    final int balanced = item.right != null
        ? item.right.balanced
        : TreeItem.BALANCED;
    switch (item.balanced) {
      case TreeItem.LEFT: {
        item.balanced = TreeItem.BALANCED;
        return true;
      }
      case TreeItem.BALANCED: {
        item.balanced = TreeItem.RIGHT;
        return false;
      }
      case TreeItem.RIGHT: {
        switch (balanced) {
          case TreeItem.LEFT: {
            rightLeftRotation(item);
            return true;
          }
          case TreeItem.BALANCED: {
            final TreeItem<K, V> dummy = leftRotation(item);
            dummy.left.balanced = TreeItem.RIGHT;
            dummy.balanced = TreeItem.LEFT;
            return false;
          }
          case TreeItem.RIGHT: {
            final TreeItem<K, V> dummy = leftRotation(item);
            dummy.left.balanced = TreeItem.BALANCED;
            dummy.balanced = TreeItem.BALANCED;
            return true;
          }
          default: {
            throw new UnreachableCodeReachedException();
          }
        }
      }
      default: {
        throw new UnreachableCodeReachedException();
      }
    }
  }

  private TreeItem<K, V> leftRotation(final TreeItem<K, V> item) {
    final TreeItem<K, V> parent = item.parent;
    final TreeItem<K, V> result = item.right;
    result.parent = parent;
    if (parent != null) {
      if (parent.left == item) {
        parent.left = result;
      } else {
        parent.right = result;
      }
    }
    item.right = result.left;
    if (item.right != null) {
      item.right.parent = item;
      item.balanced = item.left == null
          ? TreeItem.RIGHT
          : TreeItem.BALANCED;
    } else {
      item.balanced = item.left == null
          ? TreeItem.BALANCED
          : TreeItem.LEFT;
    }
    result.balanced = TreeItem.BALANCED;
    result.left = item;
    result.left.parent = result;
    if (item == this.root) {
      this.root = result;
    }
    return result;
  }

  private TreeItem<K, V> rightRotation(final TreeItem<K, V> item) {
    final TreeItem<K, V> parent = item.parent;
    final TreeItem<K, V> result = item.left;
    result.parent = parent;
    if (parent != null) {
      if (parent.left == item) {
        parent.left = result;
      } else {
        parent.right = result;
      }
    }
    item.left = result.right;
    if (item.left != null) {
      item.left.parent = item;
      item.balanced = item.right == null
          ? TreeItem.LEFT
          : TreeItem.BALANCED;
    } else {
      item.balanced = item.right == null
          ? TreeItem.BALANCED
          : TreeItem.RIGHT;
    }
    result.balanced = TreeItem.BALANCED;
    result.right = item;
    result.right.parent = result;
    if (item == this.root) {
      this.root = result;
    }
    return result;
  }

  private void leftRightRotation(final TreeItem<K, V> item) {
    leftRotation(item.left);
    rightRotation(item);
  }

  private void rightLeftRotation(final TreeItem<K, V> item) {
    rightRotation(item.right);
    leftRotation(item);
  }

  @Override
  public Iterable<V> getValues() {
    return createIterable(new SortedValueIteratorFactory<K, V>());
  }

  @Override
  public Iterable<V> getDeepSearchFirstValues() {
    return createIterable(new DeepFirstSearchValueIteratorFactory<K, V>(), this.root);
  }

  @Override
  public Iterable<V> getBreadthSearchFirstValues() {
    return createIterable(new BreadthFirstSearchValueIteratorFactory<K, V>(), this.root);
  }

  private <O> TreeIterable<K, V, O> createIterable(
      final IObjectIteratorFactory<ITreeItem<K, V>, O> factory,
      final TreeItem<K, V> item) {
    return new TreeIterable<>(factory, item);
  }

  private <O> TreeIterable<K, V, O> createIterable(final IObjectIteratorFactory<ITreeItem<K, V>, O> factory) {
    return createIterable(factory, this.first);
  }

  @Override
  public Iterable<K> getKeys() {
    return createIterable(new SortedKeyIteratorFactory<K, V>());
  }

  @Override
  public ITreeWalker<K, V> getTreeWalker() {
    return new TreeWalker<>(this.first, this.root);
  }

  public ITreeItem<K, V> getRoot() {
    return this.root;
  }

  public ITreeItem<K, V> getFirst() {
    return this.first;
  }

  public ITreeItem<K, V> getLast() {
    return this.last;
  }
}
