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
package net.anwiba.commons.swing.dialog.pane;

import net.anwiba.commons.lang.optional.Optional;
import net.anwiba.commons.message.Message;
import net.anwiba.commons.model.IChangeableObjectListener;
import net.anwiba.commons.model.IObjectModel;
import net.anwiba.commons.model.ObjectModel;
import net.anwiba.commons.swing.date.MonthView;
import net.anwiba.commons.swing.dialog.DataState;
import net.anwiba.commons.swing.object.IObjectField;
import net.anwiba.commons.swing.object.numeric.IntegerFieldBuilder;
import net.anwiba.commons.utilities.time.UserDateTimeUtilities;
import net.anwiba.commons.utilities.validation.IValidationResult;

import java.awt.BorderLayout;
import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public final class LocalDateTimeContentPane extends AbstractContentPane {

  private JComponent contentPanel;
  private final IObjectModel<LocalDateTime> dateTimeModel;
  final IObjectModel<LocalDateTime> model;

  public LocalDateTimeContentPane(
      final IObjectModel<DataState> dataStateModel,
      final IObjectModel<LocalDateTime> model) {
    super(dataStateModel);
    this.dateTimeModel = model;
    this.model = new ObjectModel<>(Optional.of(this.dateTimeModel.get()).getOr(() -> {
      getDataStateModel().set(DataState.MODIFIED);
      LocalDateTime now = UserDateTimeUtilities.now().toLocalDateTime();
      return LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), now.getHour(), now.getMinute(), 0, 0);
    }));
  }

  @Override
  public JComponent getComponent() {
    if (this.contentPanel != null) {
      return this.contentPanel;
    }

    final IObjectModel<Integer> hourModel =
        new ObjectModel<>(Optional.of(this.model.get()).convert(d -> Integer.valueOf(d.getHour())).get());
    final IObjectModel<Integer> minuteModel =
        new ObjectModel<>(Optional.of(this.model.get()).convert(d -> Integer.valueOf(d.getMinute())).get());

    final IObjectModel<LocalDate> dateModel = new ObjectModel<>(
        Optional
            .of(this.model.get())
            .convert(d -> LocalDate.of(d.getYear(), d.getMonthValue(), d.getDayOfMonth()))
            .get());

    hourModel.addChangeListener(() -> this.model.set(this.model.get().withHour(hourModel.get().intValue())));
    minuteModel.addChangeListener(() -> this.model.set(this.model.get().withMinute(minuteModel.get().intValue())));
    dateModel
        .addChangeListener(() -> Optional.of(dateModel.get()).consume(d -> this.model.set(adapt(this.model.get(), d))));

    final IObjectField<Integer> hourField = new IntegerFieldBuilder()
        .setColumns(4)
        .setModel(hourModel)
        .setToolTip("Hour")
        .addModuloSpinnerActions(24, 1)
        .build();
    final IObjectField<Integer> minuteField = new IntegerFieldBuilder()
        .setColumns(4)
        .setModel(minuteModel)
        .setToolTip("Minute")
        .addModuloSpinnerActions(60, 1)
        .build();

    final MonthView monthView = new MonthView(dateModel);

    final IChangeableObjectListener validatorsListener = new IChangeableObjectListener() {

      @Override
      public void objectChanged() {
        if (dateModel.get() == null) {
          set("Date", "missing selected value");
          return;
        }
        IValidationResult result = hourField.getValidationResultDistributor().get();
        if (!result.isValid()) {
          set("Hour", result.getMessage());
          return;
        }
        result = minuteField.getValidationResultDistributor().get();
        if (!result.isValid()) {
          set("Minute", result.getMessage());
          return;
        }
        getMessageModel().set(null);
        getDataStateModel().set(DataState.MODIFIED);
      }

      private void set(final String string, final String message) {
        getMessageModel().set(Message.error(string + ", " + message).build());
        getDataStateModel().set(DataState.INVALIDE);
      }
    };
    hourField.getValidationResultDistributor().addChangeListener(validatorsListener);
    minuteField.getValidationResultDistributor().addChangeListener(validatorsListener);
    dateModel.addChangeListener(validatorsListener);

    final JPanel timePanel = new JPanel();
    timePanel.add(hourField.getComponent());
    timePanel.add(minuteField.getComponent());

    final JPanel headPanel = new JPanel();
    headPanel.setLayout(new BoxLayout(headPanel, BoxLayout.Y_AXIS));
    headPanel.add(monthView);
    headPanel.add(timePanel);

    final JPanel panel = new JPanel();
    panel.setLayout(new BorderLayout());
    panel.add(headPanel, BorderLayout.NORTH);
    this.contentPanel = new JScrollPane(panel);
    return this.contentPanel;
  }

  private LocalDateTime adapt(final LocalDateTime dateTime, final LocalDate date) {
    return dateTime.withYear(date.getYear()).withMonth(date.getMonthValue()).withDayOfMonth(date.getDayOfMonth());
  }

  @Override
  public boolean apply() {
    this.dateTimeModel.set(this.model.get());
    return true;
  }
}
