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

import net.anwiba.commons.model.IChangeableListListener;

import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

public class ActionListMenu extends JMenu {

  private static final long serialVersionUID = 1L;

  public ActionListMenu(final String title, final ActionListModel actionListModel) {
    super(title);
    actionListModel.addListModelListener(new IChangeableListListener<Action>() {

      @Override
      public void objectsAdded(final Iterable<Integer> indeces, final Iterable<Action> actions) {
        for (final Action action : actions) {
          add(action);
        }
      }

      @Override
      public void objectsRemoved(final Iterable<Integer> indeces, final Iterable<Action> objects) {
        for (final Action action : objects) {
          remove(action);
        }
      }

      @Override
      public void objectsUpdated(
          final Iterable<Integer> indeces,
          final Iterable<Action> oldObjects,
          final Iterable<Action> newObjects) {
        for (final Action action : oldObjects) {
          remove(action);
        }
        for (final Action action : newObjects) {
          add(action);
        }
      }

      @Override
      public void objectsChanged(final Iterable<Action> oldObjects, final Iterable<Action> newObjects) {
        for (final Action action : oldObjects) {
          remove(action);
        }
        for (final Action action : newObjects) {
          add(action);
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
