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

import java.util.Objects;

import net.anwiba.commons.lang.optional.If;
import net.anwiba.commons.lang.stream.Streams;
import net.anwiba.commons.utilities.string.StringUtilities;
import net.anwiba.spatial.ckan.json.schema.v1_0.Dataset;
import net.anwiba.spatial.ckan.json.schema.v1_0.Organization;
import net.anwiba.spatial.ckan.json.schema.v1_0.Publisher;
import net.anwiba.spatial.ckan.json.types.DateString;
import net.anwiba.spatial.ckan.utilities.CkanUtilities;
import net.anwiba.spatial.swing.ckan.search.message.Messages;

public final class DataSetDescriptionTextFactory {

  public String create(final Dataset dataset) {
    if (dataset == null) {
      return ""; //$NON-NLS-1$
    }
    final StringBuilder text = new StringBuilder();
    Streams.of(dataset.getExtras()).first(e -> Objects.equals(e.getKey(), "spatial_text")).consume(e -> { //$NON-NLS-1$
      text.append("<b>" + Messages.location + ": </b>"); //$NON-NLS-1$ //$NON-NLS-2$
      text.append(e.getValue());
    });
    If.isTrue(!StringUtilities.isNullOrTrimmedEmpty(CkanUtilities.toString(dataset.getDescription()))).excecute(() -> {
      final String trimedString = CkanUtilities.toString(dataset.getDescription()).trim();
      text.append(trimedString);
    });
    If.isTrue(!StringUtilities.isNullOrTrimmedEmpty(dataset.getNotes())).excecute(() -> {
      final String trimedNotes = dataset.getNotes().trim();
      if (text.length() == 0) {
        text.append(dataset.getNotes());
      } else {
        text.append("<p>"); //$NON-NLS-1$
        text.append(trimedNotes);
        text.append("</p>"); //$NON-NLS-1$
      }
    });

    if (dataset.getGroups() != null && dataset.getGroups().length > 0) {
      text.append("<p>"); //$NON-NLS-1$
      text.append("<b>" + Messages.categories + ": </b>"); //$NON-NLS-1$ //$NON-NLS-2$
      text.append(
          StringUtilities
              .concat(", ", Streams.of(dataset.getGroups()).convert(g -> CkanUtilities.toString(g)).asList())); //$NON-NLS-1$
      text.append("</p>"); //$NON-NLS-1$
    }

    if (dataset.getTags() != null && dataset.getTags().length > 0) {
      text.append("<p>"); //$NON-NLS-1$
      text.append("<b>" + Messages.tags + ": </b>"); //$NON-NLS-1$ //$NON-NLS-2$
      text.append(
          StringUtilities.concat(", ", Streams.of(dataset.getTags()).convert(g -> CkanUtilities.toString(g)).asList())); //$NON-NLS-1$
      text.append("</p>"); //$NON-NLS-1$
    }

    final Organization organization = dataset.getOrganization();
    if (organization != null) {
      text.append("<p>"); //$NON-NLS-1$
      text.append("<b>" + "Organization" + ": </b>"); //$NON-NLS-1$ //$NON-NLS-3$
      text.append(CkanUtilities.toString(organization));
      text.append("</p>"); //$NON-NLS-1$
    }

    if (!StringUtilities.isNullOrTrimmedEmpty(dataset.getMaintainer())) {
      text.append("<p>"); //$NON-NLS-1$
      text.append("<b>" + Messages.maintainer + ": </b>"); //$NON-NLS-1$ //$NON-NLS-2$
      if (!StringUtilities.isNullOrTrimmedEmpty(dataset.getMaintainer_email())) {
        text.append(
            "<a href=\"" + converToUrl(dataset.getMaintainer_email()) + "\">" + dataset.getMaintainer() + "</a"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
      } else {
        text.append(dataset.getMaintainer());
      }
      text.append("</p>"); //$NON-NLS-1$
    }
    if (!StringUtilities.isNullOrTrimmedEmpty(dataset.getAuthor())) {
      text.append("<p>"); //$NON-NLS-1$
      text.append("<b>" + Messages.author + ": </b>"); //$NON-NLS-1$ //$NON-NLS-2$
      if (!StringUtilities.isNullOrTrimmedEmpty(dataset.getAuthor_email())) {
        text.append("<a href=\"" + converToUrl(dataset.getAuthor_email()) + "\">" + dataset.getAuthor() + "</a"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
      } else {
        text.append(dataset.getAuthor());
      }
      text.append("</p>"); //$NON-NLS-1$
    }
    if (dataset.getPublisher() != null) {
      final Publisher publisher = dataset.getPublisher();
      final String string = CkanUtilities.toString(publisher);
      if (string != null) {
        text.append("<p>"); //$NON-NLS-1$
        text.append("<b>" + Messages.publisher + ": </b>"); //$NON-NLS-1$ //$NON-NLS-2$
        if (!StringUtilities.isNullOrTrimmedEmpty(publisher.getResource())) {
          text.append("<a href=\"" + publisher.getResource().trim() + "\">" + string.trim() + "</a"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        } else {
          text.append(string.trim());
        }
        text.append("</p>"); //$NON-NLS-1$
      }
    }

    final DateString modified = dataset.getMetadata_modified();
    final DateString created = dataset.getMetadata_created();
    if (modified != null || created != null) {
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
    return text.toString();
  }

  private String converToUrl(final String email) {
    return email.trim().toLowerCase().startsWith("mailto:") ? email.trim() : "mailto:" + email.trim(); //$NON-NLS-1$//$NON-NLS-2$
  }
}
