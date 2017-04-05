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

import net.anwiba.commons.preferences.DummyPreferences;
import net.anwiba.commons.preferences.IPreferences;

public class SplitPanePreferences {

  public static final String NAME = "splitPane"; //$NON-NLS-1$
  private static final String DIVIDER_LOCATION = "dividerLocation"; //$NON-NLS-1$
  private static final String IS_PREFERENCE_ENABLED = "isEnabled"; //$NON-NLS-1$
  private final BooleanPreference enabledPreference;
  private final IPreferences preferences;

  public SplitPanePreferences(final IPreferences preferences) {
    this.preferences = preferences == null ? new DummyPreferences() : preferences;
    this.enabledPreference = new BooleanPreference(preferences, IS_PREFERENCE_ENABLED);
  }

  public void setDividerLocation(final int dividerLocation) {
    if (Integer.MIN_VALUE == dividerLocation || Integer.MAX_VALUE == dividerLocation
        || !this.enabledPreference.isTrue()) {
      return;
    }
    this.preferences.setInt(DIVIDER_LOCATION, dividerLocation);
    this.enabledPreference.setValue(true);
    this.preferences.flush();
  }

  public int getDividerLocation(final int dividerLocation) {
    if (!this.enabledPreference.isTrue()) {
      return dividerLocation;
    }
    return this.preferences.getInt(DIVIDER_LOCATION, dividerLocation);
  }

}
