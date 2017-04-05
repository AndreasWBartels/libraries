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
package net.anwiba.commons.swing.preference;

import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JSplitPane;

public class SplitPanePreferenceUpdaterListener implements PropertyChangeListener, HierarchyListener {

  private final JSplitPane splitPane;
  private int dividerLocation;
  private final SplitPanePreferences splitPanePreferences;

  public SplitPanePreferenceUpdaterListener(final JSplitPane splitPane, final SplitPanePreferences splitPanePreferences) {
    this.splitPane = splitPane;
    this.splitPanePreferences = splitPanePreferences;
    this.dividerLocation = splitPane.getDividerLocation();
  }

  @Override
  public synchronized void propertyChange(final PropertyChangeEvent event) {
    if (JSplitPane.DIVIDER_LOCATION_PROPERTY.equals(event.getPropertyName())) {
      final int dividerLocation = this.splitPane.getDividerLocation();
      if (dividerLocation != this.dividerLocation) {
        this.dividerLocation = dividerLocation;
        this.splitPanePreferences.setDividerLocation(dividerLocation);
      }
    }
  }

  @Override
  public void hierarchyChanged(final HierarchyEvent event) {
    if ((event.getChangeFlags() & HierarchyEvent.PARENT_CHANGED) != 0 && event.getChangedParent() == null) {
      this.splitPane.removeHierarchyListener(this);
      this.splitPane.removePropertyChangeListener(this);
    }
  }

}
