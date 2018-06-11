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
package net.anwiba.commons.swing.dialog.wizard;

import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Iterator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JPanel;

import net.anwiba.commons.lang.functional.IApplicable;
import net.anwiba.commons.lang.object.ObjectUtilities;
import net.anwiba.commons.model.IObjectModel;
import net.anwiba.commons.model.ISelectionListener;
import net.anwiba.commons.model.SelectionEvent;
import net.anwiba.commons.model.SelectionModel;
import net.anwiba.commons.swing.dialog.DataState;
import net.anwiba.commons.swing.list.IObjectListConfiguration;
import net.anwiba.commons.swing.list.ObjectListComponent;
import net.anwiba.commons.swing.list.ObjectListComponentModel;
import net.anwiba.commons.swing.list.ObjectListConfigurationBuilder;
import net.anwiba.commons.swing.ui.IObjectUi;

public class ChooseOneOfManyWizardPage<T> extends AbstractWizardPage {

  private final boolean finishable;
  private final IObjectModel<T> mobel;
  private final List<T> values;
  private final IObjectUi<T> objectUi;
  private final T originalValue;
  private JPanel component;
  private final IObjectModel<IWizardAction> wizardActionModel;

  public ChooseOneOfManyWizardPage(
      final String title,
      final String message,
      final Icon icon,
      final DataState state,
      final boolean finishable,
      final IObjectUi<T> objectUi,
      final List<T> values,
      final IObjectModel<T> mobel,
      final IObjectModel<IWizardAction> wizardActionModel,
      final IApplicable<IWizardPage> applicable) {
    super(title, message, icon, applicable);
    this.finishable = finishable;
    this.objectUi = objectUi;
    this.values = values;
    this.mobel = mobel;
    this.wizardActionModel = wizardActionModel;
    this.originalValue = mobel.get();
    getNextEnabledModel().set(this.originalValue != null);
    getDataStateModel().set(this.originalValue == null ? DataState.UNKNOWN : state);
  }

  @Override
  public JComponent getComponent() {

    if (this.component != null) {
      return this.component;
    }
    this.component = new JPanel(new GridLayout(1, 1, 5, 5));
    this.component.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    final SelectionModel<T> selectionModel = new SelectionModel<>();
    final IObjectListConfiguration<T> configuration = new ObjectListConfigurationBuilder<T>()
        .setObjectUi(this.objectUi)
        .setSelectionModel(selectionModel)
        .setMouseListener(new MouseAdapter() {

          @Override
          public void mouseReleased(final MouseEvent event) {
            if (selectionModel.isEmpty()) {
              return;
            }
            if (event.getButton() == MouseEvent.BUTTON1 && event.getClickCount() == 2) {
              ChooseOneOfManyWizardPage.this.wizardActionModel.set(new IWizardAction() {

                @Override
                public void execute(final IWizardController controller) {
                  if (controller.hasNext() && controller.getNextEnabledDistributor().get()) {
                    controller.next();
                  }
                }
              });
            }
          }
        })
        .setSingleSelectionMode()
        .build();
    final ObjectListComponent<T> listComponent = new ObjectListComponent<>(
        configuration,
        new ObjectListComponentModel<>(this.values));
    selectionModel.addSelectionListener(new ISelectionListener<T>() {

      @Override
      public void selectionChanged(final SelectionEvent<T> event) {
        final Iterator<T> iterator = event.getSource().getSelectedObjects().iterator();
        final T object = iterator.hasNext() ? iterator.next() : null;
        ChooseOneOfManyWizardPage.this.mobel.set(object);
        final DataState state = ChooseOneOfManyWizardPage.this.mobel.get() == null
            ? DataState.INVALIDE
            : ObjectUtilities.equals(object, ChooseOneOfManyWizardPage.this.originalValue)
                ? DataState.VALIDE
                : DataState.MODIFIED;
        getDataStateModel().set(state);
        getNextEnabledModel().set(DataState.VALIDE.equals(state) || DataState.MODIFIED.equals(state));
      }
    });
    selectionModel.setSelectedObject(this.originalValue);
    this.component.add(listComponent.getComponent());
    return this.component;
  }

  @Override
  public boolean finishable() {
    return this.finishable;
  }
}
