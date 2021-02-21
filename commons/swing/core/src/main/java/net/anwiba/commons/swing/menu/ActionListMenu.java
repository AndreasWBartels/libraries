/*
 * #%L
 * anwiba commons swing
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
package net.anwiba.commons.swing.menu;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import net.anwiba.commons.lang.functional.IBlock;
import net.anwiba.commons.model.IChangeableListListener;

public class ActionListMenu extends JMenu {

  private static final long serialVersionUID = 1L;

  public ActionListMenu(
      final String title,
      final ActionListModel actionListModel,
      final int limit,
      final Action action) {
    super(title);
    final int limited = limit;

    final Action limitReachedAction = action;

    actionListModel.addListModelListener(new IChangeableListListener<Action>() {

      private final Set<Action> visibleActions = new HashSet<>();
      private final Set<Action> hiddenActions = new HashSet<>();
      private final Set<Action> limitReachedActions = new HashSet<>();
      private final AtomicInteger counter = new AtomicInteger(0);

      private final int limit = limited;

      private void execute(final IBlock<RuntimeException> block) {
        this.limitReachedActions.forEach(a -> remove(a));
        this.limitReachedActions.clear();
        block.execute();
        if (this.counter.get() >= this.limit) {
          this.limitReachedActions.add(limitReachedAction);
          add(limitReachedAction);
        }
      }

      @Override
      public void objectsAdded(final Iterable<Integer> indeces, final Iterable<Action> actions) {
        execute(() -> {
          for (final Action action : actions) {
            addAction(action);
          }
        });
      }

      @Override
      public void objectsRemoved(final Iterable<Integer> indeces, final Iterable<Action> objects) {
        execute(() -> {
          for (final Action action : objects) {
            removeAction(action);
          }
        });
      }

      @Override
      public void objectsUpdated(
          final Iterable<Integer> indeces,
          final Iterable<Action> oldObjects,
          final Iterable<Action> newObjects) {
        execute(() -> {
          for (final Action action : oldObjects) {
            removeAction(action);
          }
          for (final Action action : newObjects) {
            addAction(action);
          }
        });
      }

      @Override
      public void objectsChanged(final Iterable<Action> oldObjects, final Iterable<Action> newObjects) {
        execute(() -> {
          for (final Action action : oldObjects) {
            removeAction(action);
          }
          for (final Action action : newObjects) {
            addAction(action);
          }
        });
      }

      private void addAction(final Action action) {
        if (this.counter.get() < this.limit) {
          addVisible(action);
        } else {
          addHidden(action);
        }
      }

      private void addHidden(final Action action) {
        this.hiddenActions.add(action);
      }

      private void addVisible(final Action action) {
        add(action);
        this.visibleActions.add(action);
        this.counter.incrementAndGet();
      }

      private void removeAction(final Action action) {
        if (this.hiddenActions.contains(action)) {
          this.hiddenActions.remove(action);
        } else if (this.visibleActions.contains(action)) {
          this.visibleActions.remove(action);
          remove(action);
          final int count = this.counter.decrementAndGet();
          if (count < this.limit && !this.hiddenActions.isEmpty()) {
            final Action hiddenAction = this.hiddenActions.iterator().next();
            this.hiddenActions.remove(hiddenAction);
            addVisible(hiddenAction);
          }
        }
      }
    });
  }

  void remove(final Action action) {
    for (int i = 0; i < getItemCount(); i++) {
      final JMenuItem item = getItem(i);
      if (item.getAction().equals(action)) {
        remove(item);
        return;
      }
    }
  }
}