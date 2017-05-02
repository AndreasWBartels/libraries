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
package net.anwiba.commons.swing.component.search;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;

import net.anwiba.commons.model.IChangeableListListener;
import net.anwiba.commons.model.IChangeableObjectListener;
import net.anwiba.commons.model.ObjectModel;
import net.anwiba.commons.swing.component.IComponentProvider;
import net.anwiba.commons.swing.component.search.action.IAdvancedSearchActionFactory;
import net.anwiba.commons.swing.component.search.action.NextAction;
import net.anwiba.commons.swing.component.search.action.PreviousAction;
import net.anwiba.commons.swing.object.IObjectFieldConfiguration;
import net.anwiba.commons.swing.object.StringField;
import net.anwiba.commons.swing.object.StringObjectFieldConfigurationBuilder;
import net.anwiba.commons.swing.utilities.GuiUtilities;
import net.anwiba.commons.utilities.factory.IFactory;

public class SearchComponent<C, R> implements IComponentProvider {

  private JPanel contentPane;
  private final ISearchEngine<C, R> engine;
  private StringField stringField;
  private final JComponent[] components;
  private final IAdvancedSearchActionFactory<C> advancedSearchActionFactory;
  private final IFactory<String, C, RuntimeException> stringConditionFactory;

  public SearchComponent(
      final ISearchEngine<C, R> engine,
      final IFactory<String, C, RuntimeException> stringConditionFactory,
      final JComponent... components) {
    this(engine, stringConditionFactory, null, components);
  }

  public SearchComponent(
      final ISearchEngine<C, R> engine,
      final IFactory<String, C, RuntimeException> stringConditionFactory,
      final IAdvancedSearchActionFactory<C> advancedSearchActionFactory,
      final JComponent... components) {
    this.engine = engine;
    this.stringConditionFactory = stringConditionFactory;
    this.advancedSearchActionFactory = advancedSearchActionFactory;
    this.components = components;
  }

  @Override
  public JComponent getComponent() {
    if (this.contentPane == null) {
      this.contentPane = new JPanel(new FlowLayout(FlowLayout.LEFT));
      final ISearchEngine<C, R> engine = this.engine;

      final AbstractAction previousAction = new PreviousAction<>(engine);
      previousAction.setEnabled(false);
      final AbstractAction nextAction = new NextAction<>(engine);
      nextAction.setEnabled(false);

      final ObjectModel<String> searchStringModel = new ObjectModel<String>();
      final StringObjectFieldConfigurationBuilder builder = new StringObjectFieldConfigurationBuilder()
          .setToolTipFactory((validationResult, context) -> "feature search condition")
          .setColumns(24)
          .setModel(searchStringModel);
      if (this.advancedSearchActionFactory != null) {
        builder.addActionFactory((context, document, clearBlock) -> SearchComponent.this.advancedSearchActionFactory
            .create(searchStringModel, engine));
      }
      builder.addClearAction("clear");
      builder.addActionFactory((c, d, b) -> previousAction);
      builder.addActionFactory((c, d, b) -> nextAction);
      final IObjectFieldConfiguration<String> configuration = builder.build();
      final StringField stringField = new StringField(configuration);
      this.stringField = stringField;
      final IFactory<String, C, RuntimeException> stringConditionFactory = this.stringConditionFactory;
      searchStringModel.addChangeListener(new IChangeableObjectListener() {

        @Override
        public void objectChanged() {
          engine.search(stringConditionFactory.create(searchStringModel.get()));
        }
      });
      engine.getSearchResultsModel().addListModelListener(new IChangeableListListener<R>() {

        @Override
        public void objectsAdded(final Iterable<Integer> indeces, final Iterable<R> object) {
          updateComponent();
        }

        @Override
        public void objectsRemoved(final Iterable<Integer> indeces, final Iterable<R> object) {
          updateComponent();
        }

        @Override
        public void objectsUpdated(
            final Iterable<Integer> indeces,
            final Iterable<R> oldObjects,
            final Iterable<R> newObjects) {
          updateComponent();
        }

        @Override
        public void objectsChanged(final Iterable<R> oldObjects, final Iterable<R> newObjects) {
          updateComponent();
        }

        private synchronized void updateComponent() {
          final boolean isValid = !engine.getSearchResultsModel().isEmpty() || engine.getCondition() == null;
          final boolean hasPrevious = engine.hasPrevious();
          final boolean hasNext = engine.hasNext();
          GuiUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
              stringField.getComponent().setBackground(isValid ? Color.WHITE : Color.RED);
              stringField.getComponent().setForeground(isValid ? Color.BLACK : Color.WHITE);
              previousAction.setEnabled(hasPrevious);
              nextAction.setEnabled(hasNext);
            }
          });
        }
      });
      engine.getResultCursorModel().addChangeListener(new IChangeableObjectListener() {

        @Override
        public void objectChanged() {
          final boolean hasPrevious = engine.hasPrevious();
          final boolean hasNext = engine.hasNext();
          GuiUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
              previousAction.setEnabled(hasPrevious);
              nextAction.setEnabled(hasNext);
            }
          });
        }
      });
      this.contentPane.add(createSearchField(stringField));
      for (final JComponent component : this.components) {
        this.contentPane.add(component);
      }
    }
    return this.contentPane;
  }

  private Component createSearchField(final StringField stringField) {
    final JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
    searchPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
    searchPanel.add(stringField.getComponent());
    return searchPanel;
  }

  public void grapFocus() {
    if (this.stringField != null) {
      this.stringField.getComponent().grabFocus();
    }
  }
}