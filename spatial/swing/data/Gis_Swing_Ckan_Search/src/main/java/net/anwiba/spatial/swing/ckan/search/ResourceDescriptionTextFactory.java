/*
 * #%L
 * *
 * %%
 * Copyright (C) 2007 - 2018 Andreas W. Bartels (bartels@anwiba.de)
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
package net.anwiba.spatial.swing.ckan.search;

import net.anwiba.commons.utilities.string.StringUtilities;
import net.anwiba.spatial.ckan.json.schema.v1_0.Resource;
import net.anwiba.spatial.ckan.json.types.DateString;
import net.anwiba.spatial.ckan.json.types.I18String;
import net.anwiba.spatial.ckan.utilities.CkanUtilities;
import net.anwiba.spatial.swing.ckan.search.message.Messages;

public class ResourceDescriptionTextFactory {

  public String create(final Resource resource) {
    if (resource == null) {
      return ""; //$NON-NLS-1$
    }

    final StringBuilder text = new StringBuilder();

    final I18String description = resource.getDescription();

    if (description != null && !StringUtilities.isNullOrTrimmedEmpty(description.toString())) {
      final String trimedString = CkanUtilities.toString(description).trim();
      text.append(trimedString);
    }

    final String state = resource.getState();

    if (state != null && StringUtilities.isNullOrTrimmedEmpty(state)) {
      if (text.length() == 0) {
        text.append("<b>" + Messages.state + ": </b>"); //$NON-NLS-1$ //$NON-NLS-2$
        text.append(state);
      } else {
        text.append("<p>"); //$NON-NLS-1$
        text.append("<b>" + Messages.state + ": </b>"); //$NON-NLS-1$ //$NON-NLS-2$
        text.append(state);
        text.append("</p>"); //$NON-NLS-1$
      }
    }

    final DateString modified = resource.getLast_modified();
    final DateString created = resource.getCreated();
    if (modified != null || created != null) {
      if (text.length() == 0) {
        if (created != null) {
          text.append("<b>" + Messages.created + ": </b>"); //$NON-NLS-1$ //$NON-NLS-2$
          text.append(CkanUtilities.toUserTimeString(created));
        }
        if (modified != null) {
          if (created != null) {
            text.append("<br>"); //$NON-NLS-1$
          }
          text.append("<b>" + Messages.modified + ": </b>"); //$NON-NLS-1$ //$NON-NLS-2$
          text.append(CkanUtilities.toUserTimeString(modified));
        }
      } else {
        text.append("<p>"); //$NON-NLS-1$
        if (created != null) {
          text.append("<b>" + Messages.created + ": </b>"); //$NON-NLS-1$ //$NON-NLS-2$
          text.append(CkanUtilities.toUserTimeString(created));
        }
        if (modified != null) {
          if (created != null) {
            text.append("<br>"); //$NON-NLS-1$
          }
          text.append("<b>" + Messages.modified + ": </b>"); //$NON-NLS-1$ //$NON-NLS-2$
          text.append(CkanUtilities.toUserTimeString(modified));
        }
        text.append("</p>"); //$NON-NLS-1$
      }
    }
    return text.toString();
  }

}
