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

import java.awt.Color;

import net.anwiba.commons.preferences.IPreferences;

public class ColorPreference {

  public static final String NAME = "color"; //$NON-NLS-1$
  private static final String ALPHA = "ALPHA"; //$NON-NLS-1$
  private static final String BLUE = "BLUE"; //$NON-NLS-1$
  private static final String GREEN = "GREEN"; //$NON-NLS-1$
  private static final String RED = "RED"; //$NON-NLS-1$
  private final IPreferences preferences;

  public ColorPreference(final IPreferences preferences) {
    this.preferences = preferences;
  }

  public Color getColor() {
    final int r = this.preferences.getInt(RED, 0);
    final int g = this.preferences.getInt(GREEN, 0);
    final int b = this.preferences.getInt(BLUE, 0);
    final int a = this.preferences.getInt(ALPHA, 0);
    return new Color(r, g, b, a);
  }

  public void setColor(final Color color) {
    this.preferences.setInt(RED, color.getRed());
    this.preferences.setInt(GREEN, color.getGreen());
    this.preferences.setInt(BLUE, color.getBlue());
    this.preferences.setInt(ALPHA, color.getAlpha());
    this.preferences.flush();
  }

}
