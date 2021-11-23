/*
 * #%L
 * anwiba commons
 * %%
 * Copyright (C) 2007 - 2021 Andreas W. Bartels
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
package net.anwiba.commons.swing.dialog;

import java.awt.Window;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.border.Border;

import net.anwiba.commons.message.Message;
import net.anwiba.commons.model.IChangeableObjectListener;
import net.anwiba.commons.model.IObjectModel;
import net.anwiba.commons.preferences.IPreferences;
import net.anwiba.commons.swing.component.GridBagLayoutComponentBuilder;
import net.anwiba.commons.swing.dialog.pane.AbstractContentPane;
import net.anwiba.commons.swing.dialog.pane.IContentPanel;
import net.anwiba.commons.swing.object.IObjectField;
import net.anwiba.commons.utilities.validation.IValidationResult;

public class ContentPaneFactoryBuilder {

  GridBagLayoutComponentBuilder componentBuilder = new GridBagLayoutComponentBuilder();
  List<IObjectField<?>> objectFields = new ArrayList<>(); 
  
  public ContentPaneFactoryBuilder header(final String text) {
    componentBuilder.header(text);
    return this;
  }

  public ContentPaneFactoryBuilder label(final String text) {
    componentBuilder.newlineAndLabel(text);
    return this;
  }

  public ContentPaneFactoryBuilder add(final IObjectField<?> field) {
    objectFields.add(field);
    componentBuilder.add(field.getComponent());
    return this;
  }

  public ContentPaneFactoryBuilder add(final JComponent component) {
    componentBuilder.add(component);
    return this;
  }

  public ContentPaneFactoryBuilder add(final JComponent component, final int with) {
    componentBuilder.add(component, with);
    return this;
  }

  public ContentPaneFactoryBuilder newline() {
    componentBuilder.newline();
    return this;
  }

  public ContentPaneFactoryBuilder emptyLine() {
    componentBuilder.emptyLine();
    return this;
  }

  public ContentPaneFactoryBuilder setBorder(final Border border) {
    componentBuilder.setBorder(border);
    return this;
  }

  public IContentPaneFactory build() {
    return new IContentPaneFactory() {
      
      @Override
      public IContentPanel create(Window owner, IPreferences preferences, IObjectModel<DataState> dataStateModel) {
        JComponent component = componentBuilder.build();
        return new AbstractContentPane(dataStateModel) {
          
          JComponent contentComponent;
          
          @Override
          public JComponent getComponent() {
            if (contentComponent == null) {
              contentComponent = component;
              
              Map<IObjectField,Object> originalValues = objectFields.stream().collect(Collectors.toConcurrentMap(f -> f, f -> f.getModel().get()));
              
              IChangeableObjectListener listener = () -> {
                for (IObjectField<?> field : objectFields) {
                  IValidationResult validationResult = field.getValidationResultDistributor().get();
                  if (!validationResult.isValid()) {
                    getMessageModel().set(Message
                        .builder()
                        .setText(validationResult.getMessage())
                        .setError()
                        .build());
                    getDataStateModel().set(DataState.INVALIDE);
                    return;
                  }
                }
                getMessageModel().set(null);
                for (IObjectField<?> field: objectFields) {
                  if (!Objects.equals(originalValues.get(field), field.getModel().get())) {
                    getDataStateModel().set(DataState.MODIFIED);
                    return;
                  }
                }
                getDataStateModel().set(DataState.VALIDE);
              };
              objectFields.forEach(f -> {
                f.getModel().addChangeListener(listener);
                f.getValidationResultDistributor().addChangeListener(listener);
              });
            }
            return new JScrollPane(contentComponent);
          }
        };
      }
    };
  }
}
