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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.Optional;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;

import net.anwiba.commons.lang.functional.IFactory;
import net.anwiba.commons.model.BooleanModel;
import net.anwiba.commons.model.IBooleanModel;
import net.anwiba.commons.model.IChangeableListListener;
import net.anwiba.commons.model.IChangeableObjectListener;
import net.anwiba.commons.model.IObjectModel;
import net.anwiba.commons.model.ObjectModel;
import net.anwiba.commons.swing.action.ConfigurableActionBuilder;
import net.anwiba.commons.swing.component.IComponentProvider;
import net.anwiba.commons.swing.component.search.action.IAdvancedSearchActionFactory;
import net.anwiba.commons.swing.component.search.action.NextAction;
import net.anwiba.commons.swing.component.search.action.PreviousAction;
import net.anwiba.commons.swing.object.IObjectFieldConfiguration;
import net.anwiba.commons.swing.object.IToolTipFactory;
import net.anwiba.commons.swing.object.StringField;
import net.anwiba.commons.swing.object.StringObjectFieldConfigurationBuilder;
import net.anwiba.commons.swing.utilities.GuiUtilities;
import net.anwiba.commons.utilities.string.StringUtilities;

public class SearchComponent<C, R> implements IComponentProvider {

  private JPanel contentPane;
  private final ISearchEngine<C, R> engine;
  private StringField stringField;
  private final JComponent[] components;
  private final IAdvancedSearchActionFactory<C> advancedSearchActionFactory;
  private final IFactory<String, C, RuntimeException> stringConditionFactory;
  private IBooleanModel enabledDistributor;
  private IToolTipFactory toolTipFactory;

  public SearchComponent(
      final IBooleanModel enabledDistributor,
      final IToolTipFactory toolTipFactory,
      final ISearchEngine<C, R> engine,
      final IFactory<String, C, RuntimeException> stringConditionFactory,
      final JComponent... components) {
    this(enabledDistributor, toolTipFactory, engine, stringConditionFactory, null, components);
  }

  public SearchComponent(
      final IBooleanModel enabledDistributor,
      final IToolTipFactory toolTipFactory,
      final ISearchEngine<C, R> engine,
      final IFactory<String, C, RuntimeException> stringConditionFactory,
      final IAdvancedSearchActionFactory<C> advancedSearchActionFactory,
      final JComponent... components) {
    this.enabledDistributor = enabledDistributor;
    this.toolTipFactory = toolTipFactory;
    this.engine = engine;
    this.stringConditionFactory = stringConditionFactory;
    this.advancedSearchActionFactory = advancedSearchActionFactory;
    this.components = components;
  }

  @Override
  public JComponent getComponent() {
    if (this.contentPane == null) {
      this.contentPane = new JPanel(new BorderLayout());
      this.contentPane.setBorder(BorderFactory.createEmptyBorder(2, 2, 0, 2));;
      final JPanel controllPane = new JPanel(new FlowLayout(FlowLayout.LEFT));
      final ISearchEngine<C, R> engine = this.engine;

      final AbstractAction previousAction = new PreviousAction<>(engine);
      previousAction.setEnabled(false);
      final AbstractAction nextAction = new NextAction<>(engine);
      nextAction.setEnabled(false);

      final ObjectModel<String> searchStringModel = new ObjectModel<>();
      final StringObjectFieldConfigurationBuilder builder = new StringObjectFieldConfigurationBuilder()
          .setToolTipFactory(this.toolTipFactory)
          .setColumns(24)
          .setModel(searchStringModel);
      final IObjectModel<C> searchFeatureAcceptorModel = new ObjectModel<>();
      final AbstractAction searchAction = new ConfigurableActionBuilder()
          .setEnabledDistributor(this.enabledDistributor)
          .setIcon(net.anwiba.commons.swing.icons.gnome.contrast.high.ContrastHightIcons.MEDIA_PLAYBACK_START)
          .setTooltip("search")
          .setTask(() -> {
            engine.reset();
            engine.search(
                Optional.ofNullable(searchFeatureAcceptorModel.get()).orElseGet(
                    () -> this.stringConditionFactory.create(searchStringModel.get())));
          })
          .build();
      final BooleanModel resetActionEnabledModel = new BooleanModel(!engine.getSearchResultsModel().isEmpty());
      final AbstractAction resetAction = new ConfigurableActionBuilder()
          .setIcon(net.anwiba.commons.swing.icons.gnome.contrast.high.ContrastHightIcons.EDIT_DELETE)
          .setTooltip("reset")
          .setEnabledDistributor(resetActionEnabledModel)
          .setTask(() -> {
            engine.reset();
          })
          .build();
      builder.addActionFactory((c, d, e, b) -> searchAction);
      if (this.advancedSearchActionFactory != null) {
        builder.addActionFactory(
            (context, document, enabled, clearBlock) -> SearchComponent.this.advancedSearchActionFactory
                .create(searchStringModel, searchFeatureAcceptorModel, engine));
      }
      builder.addClearAction("clear");
      builder.addActionFactory((c, d, e, b) -> previousAction);
      builder.addActionFactory((c, d, e, b) -> nextAction);
      builder.addActionFactory((c, d, e, b) -> resetAction);
      final IObjectFieldConfiguration<String> configuration = builder.setEnabledModel(this.enabledDistributor).build();
      final StringField field = new StringField(configuration);
      this.stringField = field;
      final IFactory<String, C, RuntimeException> conditionFactory = this.stringConditionFactory;
      engine.getSearchResultsModel().addListModelListener(new IChangeableListListener<R>() {

        @Override
        public void objectsAdded(final Iterable<Integer> indeces, final Iterable<R> object) {
          resetActionEnabledModel.set(!engine.getSearchResultsModel().isEmpty());
        }

        @Override
        public void objectsRemoved(final Iterable<Integer> indeces, final Iterable<R> object) {
          resetActionEnabledModel.set(!engine.getSearchResultsModel().isEmpty());
        }

        @Override
        public void objectsUpdated(
            final Iterable<Integer> indeces,
            final Iterable<R> oldObjects,
            final Iterable<R> newObjects) {
          resetActionEnabledModel.set(!engine.getSearchResultsModel().isEmpty());
        }

        @Override
        public void objectsChanged(final Iterable<R> oldObjects, final Iterable<R> newObjects) {
          resetActionEnabledModel.set(!engine.getSearchResultsModel().isEmpty());
        }
      });
      searchStringModel.addChangeListener(new IChangeableObjectListener() {

        @Override
        public void objectChanged() {
          searchFeatureAcceptorModel.set(null);
          if (StringUtilities.isNullOrEmpty(searchStringModel.get())) {
            return;
          }
          engine.search(conditionFactory.create(searchStringModel.get()));
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
              field.getComponent().setBackground(isValid ? Color.WHITE : Color.RED);
              field.getComponent().setForeground(isValid ? Color.BLACK : Color.WHITE);
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
      for (final JComponent component : this.components) {
        controllPane.add(component);
      }
      this.contentPane.add(createSearchField(field), BorderLayout.NORTH);
      this.contentPane.add(controllPane, BorderLayout.CENTER);
    }
    return this.contentPane;
  }

  private Component createSearchField(final StringField field) {
    final JPanel searchPanel = new JPanel(new GridLayout(1, 1));
    searchPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
    searchPanel.add(field.getComponent());
    return searchPanel;
  }

  public void grapFocus() {
    if (this.stringField != null) {
      this.stringField.getComponent().grabFocus();
    }
  }
}
