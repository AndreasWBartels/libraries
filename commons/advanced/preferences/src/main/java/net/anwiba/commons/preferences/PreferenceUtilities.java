/*
 * #%L
 * anwiba commons advanced
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
package net.anwiba.commons.preferences;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.prefs.Preferences;

import net.anwiba.commons.utilities.parameter.IParameter;
import net.anwiba.commons.utilities.parameter.IParameters;
import net.anwiba.commons.utilities.parameter.Parameter;
import net.anwiba.commons.utilities.parameter.ParametersBuilder;

public class PreferenceUtilities {

  public static String[] createPath(final Preferences preferences) {
    final List<String> nodeNames = new ArrayList<>();
    Preferences current = preferences;
    do {
      nodeNames.add(current.name());
    } while ((current = current.parent()) != null);
    Collections.reverse(nodeNames);
    return nodeNames.toArray(new String[nodeNames.size()]);
  }

  public static void store(final String[] path, final Iterable<IParameter> parameters) {
    final IPreferences preferences = getPreferences(path);
    for (final IParameter parameter : parameters) {
      preferences.put(parameter.getName(), parameter.getValue());
    }
    preferences.flush();
  }

  public static IPreferences getPreferences(final String[] path) {
    final UserPreferencesFactory factory = new UserPreferencesFactory();
    return factory.create(path);
  }

  public static IParameters getParameters(final IPreferences preferences) {
    final ArrayList<IParameter> parameters = new ArrayList<>();
    for (final String key : preferences.keys()) {
      parameters.add(Parameter.of(key, preferences.get(key, null)));
    }
    return new ParametersBuilder().add(parameters).build();
  }
}
