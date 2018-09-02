/*
 * #%L
 *
 * %%
 * Copyright (C) 2007 - 2018 Andreas W. Bartels
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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import net.anwiba.commons.lang.object.IObjectToStringConverter;
import net.anwiba.commons.lang.optional.Optional;
import net.anwiba.commons.model.IObjectModel;
import net.anwiba.commons.swing.object.IObjectComponent;
import net.anwiba.commons.swing.object.ObjectComponentBuilder;
import net.anwiba.commons.swing.object.ObjectLabelBuilder;
import net.anwiba.commons.swing.ui.ObjectUiBuilder;
import net.anwiba.commons.utilities.string.StringUtilities;
import net.anwiba.spatial.ckan.json.schema.v1_0.License;
import net.anwiba.spatial.ckan.json.types.DateString;
import net.anwiba.spatial.ckan.utilities.CkanUtilities;
import net.anwiba.spatial.swing.ckan.search.message.Messages;

public final class DescriptionPanelFactory {

  public <T> JPanel create(
      final IObjectModel<T> model,
      final IObjectToStringConverter<T> converter,
      final Color background,
      final int height,
      final IObjectModel<DateString> createDateModel,
      final IObjectModel<License> licenceModel) {
    final JPanel headerLabelsPanel = new JPanel();
    headerLabelsPanel.setLayout(new BoxLayout(headerLabelsPanel, BoxLayout.LINE_AXIS));
    headerLabelsPanel.add(new JLabel(Messages.description));
    headerLabelsPanel.add(Box.createHorizontalGlue());
    headerLabelsPanel.add(
        new ObjectLabelBuilder<DateString>()
            .setModel(createDateModel)
            .setObjectUi(
                new ObjectUiBuilder<DateString>().text(CkanUtilities::toUserTimeString).tooltip(o -> "created").build())
            .build()
            .getComponent());

    final IObjectComponent<T> objectComponent = new ObjectComponentBuilder<T>()
        .setModel(model)
        .setBackgroundColor(background)
        .setToStringConverter(converter)
        .build();

    final JPanel panel = new JPanel();
    final JScrollPane scrollPanel = new JScrollPane(objectComponent.getComponent());
    scrollPanel.setBorder(BorderFactory.createEtchedBorder());
    panel.setLayout(new BorderLayout());
    panel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
    panel.add(headerLabelsPanel, BorderLayout.NORTH);
    panel.add(scrollPanel, BorderLayout.CENTER);
    Optional.of(licenceModel).consume(m -> {
      final JPanel footerLabelsPanel = new JPanel();
      footerLabelsPanel.setLayout(new BoxLayout(footerLabelsPanel, BoxLayout.LINE_AXIS));
      final JComponent component = new ObjectComponentBuilder<License>()
          .setBackgroundColor(background)
          .setToStringConverter(object -> toString(object))
          .setModel(m)
          .build()
          .getComponent();
      footerLabelsPanel.add(component);
      panel.add(footerLabelsPanel, BorderLayout.SOUTH);
    });
    panel.setMinimumSize(new Dimension(200, height));
    panel.setPreferredSize(new Dimension(1000, height));
    panel.setMaximumSize(new Dimension(2000, 500));
    return panel;
  }

  private String toString(final License license) {
    final StringBuilder text = new StringBuilder();
    text.append("<font size=-1><b>" + Messages.license + ": </b>"); //$NON-NLS-1$ //$NON-NLS-2$
    if (license == null) {
      text.append("</font>"); //$NON-NLS-1$
      return text.toString();
    }
    final String url = Optional.of(license.getUrl()).or(() -> license.getLicense_url()).get();
    final String title = Optional
        .of(license)
        .convert(l -> CkanUtilities.toString(l.getTitle()))
        .or(() -> CkanUtilities.toString(license.getName()))
        .or(() -> license.getId())
        .or(() -> license.getLicense_id())
        .get();
    if (!StringUtilities.isNullOrTrimmedEmpty(title)) {
      if (!StringUtilities.isNullOrTrimmedEmpty(url)) {
        text.append("<a href=\"" + url + "\">" + title + "</a"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
      } else {
        text.append(title);
      }
    }
    text.append("</font>"); //$NON-NLS-1$
    return text.toString();
  }
}
