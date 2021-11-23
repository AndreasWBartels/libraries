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
import java.text.MessageFormat;

import net.anwiba.commons.preferences.IPreferences;

public class ColorListPreference {

  private static final String NUMBER_OF_COLORS = "numberOfColors"; //$NON-NLS-1$
  private final IPreferences preferences;

  public ColorListPreference(final IPreferences preferences) {
    this.preferences = preferences;
  }

  public Color[] getColors() {
    final int numberOfColors = this.preferences.getInt(NUMBER_OF_COLORS, 0);
    final Color[] colors = new Color[numberOfColors];
    for (int i = 0; i < numberOfColors; i++) {
      final ColorPreference preference = getColorPreferences(i);
      colors[i] = preference.getColor();
    }
    return colors;
  }

  public void setColors(final Color[] colors) {
    final int numberOfColors = colors.length;
    for (int i = 0; i < numberOfColors; i++) {
      final ColorPreference preference = getColorPreferences(i);
      preference.setColor(colors[i]);
    }
    this.preferences.setInt(NUMBER_OF_COLORS, numberOfColors);
    this.preferences.flush();
  }

  public ColorPreference getColorPreferences(final int i) {
    return new ColorPreference(this.preferences.node(MessageFormat.format("color{0,number,000}", Integer.valueOf(i)))); //$NON-NLS-1$
  }

}
