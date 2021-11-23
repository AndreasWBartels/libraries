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

import java.awt.Rectangle;

import net.anwiba.commons.preferences.DummyPreferences;
import net.anwiba.commons.preferences.IPreferences;

public class BoundsPreference {

  private static final String HEIGHT = "HEIGHT"; //$NON-NLS-1$
  private static final String WIDTH = "WIDTH"; //$NON-NLS-1$
  private static final String Y = "Y"; //$NON-NLS-1$
  private static final String X = "X"; //$NON-NLS-1$
  private final IPreferences preferences;
  public static final String NAME = "bounds"; //$NON-NLS-1$
  private static final String IS_PREFERENCE_ENABLED = "isEnabled"; //$NON-NLS-1$
  private final BooleanPreference enabledPreference;

  public BoundsPreference(final IPreferences preferences) {
    this.preferences = preferences == null ? new DummyPreferences() : preferences;
    this.enabledPreference = new BooleanPreference(preferences, IS_PREFERENCE_ENABLED);
  }

  public Rectangle getRectangle() {
    if (!this.enabledPreference.isTrue()) {
      return null;
    }
    final int x = this.preferences.getInt(X, -1);
    if (x == -1) {
      return null;
    }
    final int y = this.preferences.getInt(Y, -1);
    if (y == -1) {
      return null;
    }
    final int width = this.preferences.getInt(WIDTH, -1);
    if (width == -1) {
      return null;
    }
    final int height = this.preferences.getInt(HEIGHT, -1);
    if (height == -1) {
      return null;
    }
    return new Rectangle(x, y, width, height);
  }

  public void setRectangle(final Rectangle bounds) {
    if (bounds == null || !this.enabledPreference.isTrue()) {
      return;
    }
    this.preferences.setInt(X, bounds.x);
    this.preferences.setInt(Y, bounds.y);
    this.preferences.setInt(WIDTH, bounds.width);
    this.preferences.setInt(HEIGHT, bounds.height);
    this.enabledPreference.setValue(true);
    this.preferences.flush();
  }
}
