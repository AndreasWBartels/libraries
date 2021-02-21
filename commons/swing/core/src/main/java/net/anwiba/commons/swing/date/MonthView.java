/*
 * #%L
 * anwiba commons swing
 * %%
 * Copyright (C) 2005 - 2018 Andreas Bartels
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
package net.anwiba.commons.swing.date;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToolBar;

import net.anwiba.commons.lang.optional.Optional;
import net.anwiba.commons.model.IChangeableObjectListener;
import net.anwiba.commons.model.IObjectModel;
import net.anwiba.commons.model.ObjectModel;
import net.anwiba.commons.swing.date.event.DateSelectionEvent;
import net.anwiba.commons.swing.date.event.DateSelectionListener;
import net.anwiba.commons.swing.date.event.MonthChangedEvent;
import net.anwiba.commons.swing.date.event.MonthChangedListener;
import net.anwiba.commons.swing.object.IObjectField;
import net.anwiba.commons.swing.object.IntegerFieldBuilder;
import net.anwiba.commons.swing.utilities.GuiUtilities;

public class MonthView extends JPanel {

  private static final long serialVersionUID = 1L;
  DatePanel[][] datePanes = null;
  // DatePanel selectedDayPane = null;
  IObjectModel<Date> selectedDateModel = new ObjectModel<>();

  JToolBar toolBar = null;

  int month = 0;
  int year = 0;
  int height = 6;
  int width = 7;

  IObjectModel<Integer> yearModel = new ObjectModel<>();

  JComboBox<String> monthBox = null;
  private final IObjectModel<LocalDate> dateModel;

  public MonthView() {
    this(new ObjectModel<>(LocalDate.now()));
  }

  public MonthView(final IObjectModel<LocalDate> dateModel) {
    this(dateModel, 6, 7);
  }

  public MonthView(final IObjectModel<LocalDate> dateModel, final int height, final int width) {
    if (height * width != 42) {
      throw new IllegalArgumentException("program error: dayfield number is not 42");
    }
    if (width % 7 != 0) {
      throw new IllegalArgumentException("program error: week lenght is not a mutiple of 7");
    }
    this.dateModel = dateModel;
    this.height = height;
    this.width = width;
    init();
  }

  public void init() {

    setLayout(new BorderLayout(0, 0));
    setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

    this.toolBar = new JToolBar("Month");
    this.toolBar.setFloatable(false);
    this.toolBar.setRollover(true);

    final JButton firstButton = new JButton();
    firstButton.setAction(new FirstAction());
    firstButton.setFocusable(false);
    firstButton.setFocusPainted(false);
    firstButton.setText(null);
    this.toolBar.add(firstButton);

    final JButton previousButton = new JButton();
    previousButton.setAction(new PreviousAction());
    previousButton.setFocusable(false);
    previousButton.setFocusPainted(false);
    previousButton.setText(null);
    this.toolBar.add(previousButton);

    final String[] months = {
        "January",
        "February",
        "March",
        "April",
        "May",
        "June",
        "July",
        "August",
        "September",
        "October",
        "November",
        "December" };

    this.monthBox = new JComboBox<>(months);
    this.monthBox.setAction(new MonthAction());
    this.toolBar.add(this.monthBox);

    final IObjectField<Integer> yearField = new IntegerFieldBuilder()
        .setModel(this.yearModel)
        .setColumns(6)
        .setToolTip("Year")
        .addSpinnerActions(Integer.MIN_VALUE, Integer.MAX_VALUE, 1)
        .build();
    this.yearModel.addChangeListener(new IChangeableObjectListener() {

      @Override
      public void objectChanged() {
        if (MonthView.this.year == MonthView.this.yearModel.get()) {
          return;
        }
        setMonth(MonthView.this.yearModel.get(), MonthView.this.month);
      }
    });
    this.toolBar.add(yearField.getComponent());

    final JButton nextButton = new JButton();
    nextButton.setAction(new NextAction());
    nextButton.setFocusable(false);
    nextButton.setFocusPainted(false);
    nextButton.setText(null);
    this.toolBar.add(nextButton);

    final JButton lastButton = new JButton();
    lastButton.setAction(new LastAction());
    lastButton.setFocusable(false);
    lastButton.setFocusPainted(false);
    lastButton.setText(null);
    this.toolBar.add(lastButton);

    this.toolBar.add(new JPanel());
    final JPanel toolBarPane = new JPanel();
    toolBarPane.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
    toolBarPane.add(this.toolBar);

    final JPanel daysPane = new JPanel();
    daysPane.setLayout(new GridLayout(1, this.width));

    final String[] days = { "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat" };

    JPanel dayPane = null;
    JLabel dayLabel = null;
    for (int i = 0; i < this.width; i++) {
      dayPane = new JPanel();
      dayPane.setLayout(new BorderLayout(0, 0));
      dayPane.setBorder(BorderFactory.createLineBorder(getBackground()));
      if (i % 7 == 0) {
        dayPane.setBackground(Color.gray);
      } else {
        dayPane.setBackground(Color.decode("#AAAAAA"));
      }
      dayLabel = new JLabel(days[i % 7]);
      dayPane.add(dayLabel, BorderLayout.NORTH);
      daysPane.add(dayPane);
    }

    this.datePanes = new DatePanel[this.height][this.width];

    final JPanel datesPane = new JPanel();
    datesPane.setLayout(new GridLayout(this.height, this.width));

    final DateSelectionListener dateSelectionListener = new DateSelectionListener() {

      @Override
      public void selectionOccurred(final DateSelectionEvent event) {
        final Object source = event.getSource();
        if (source instanceof DatePanel) {
          final DatePanel datePane = (DatePanel) source;
          if (MonthView.this.selectedDateModel.get() == null
              || !MonthView.this.selectedDateModel.get().equals(datePane.getDate())) {
            final int era = datePane.getERA();
            final int year = era == GregorianCalendar.AD ? datePane.getYear() : (datePane.getYear() - 1) * -1;
            final int month = datePane.getMonth();
            final int day = datePane.getDay();
            if (MonthView.this.month == month && MonthView.this.year == year) {
              if (MonthView.this.selectedDateModel.get() != null) {
                final GregorianCalendar calendar = new GregorianCalendar();
                calendar.setTime(MonthView.this.selectedDateModel.get());
                final Point pos = getPosition(
                    (calendar.get(Calendar.ERA) == GregorianCalendar.AD
                        ? calendar.get(Calendar.YEAR)
                        : calendar.get(Calendar.YEAR) * -1),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH));
                final DatePanel selectedDayPane = MonthView.this.datePanes[pos.x][pos.y];
                selectedDayPane.setSelected(false);
              }
              MonthView.this.selectedDateModel.set(datePane.getDate());
              if (!datePane.hasFocus()) {
                datePane.requestFocus();
              }
            } else {
              setSelectedDate(year, month, day);
            }
            fireSelectionOccured((DatePanel) source);
          }
        }
      }

      @Override
      public void deselectionOccurred(final DateSelectionEvent event) {
        final Object source = event.getSource();
        if (source instanceof DatePanel) {
          if (MonthView.this.selectedDateModel.get() != null
              && MonthView.this.selectedDateModel.get().equals(((DatePanel) source).getDate())) {
            MonthView.this.selectedDateModel.set(null);
            fireDeselectionOccured((DatePanel) source);
          }
        }
      }
    };

    for (int i = 0; i < this.height; i++) {
      for (int j = 0; j < this.width; j++) {
        final DatePanel datePane = new DatePanel();
        datePane.setFocusable(true);
        datePane.setGridColor(getBackground());
        datePane.addDaySelectionListener(dateSelectionListener);
        datesPane.add(datePane);
        this.datePanes[i][j] = datePane;
      }
    }

    final JPanel monthPane = new JPanel();
    monthPane.setLayout(new BorderLayout(0, 0));

    monthPane.add(daysPane, BorderLayout.NORTH);
    monthPane.add(datesPane, BorderLayout.CENTER);

    add(toolBarPane, BorderLayout.NORTH);
    add(monthPane, BorderLayout.CENTER);
    setFocusCycleRoot(true);
    setFocusTraversalPolicy(new MonthViewFocusTraversalPolicy(this.datePanes));

    Optional
        .of(this.dateModel.get()) //
        .consume(d -> setSelectedDate(d.getYear(), d.getMonthValue() - 1, d.getDayOfMonth()))
        .or(() -> {
          final LocalDate d = LocalDate.now();
          GuiUtilities.invokeLater(() -> setMonth(d.getYear(), d.getMonthValue() - 1));
        })
        .getOr(() -> LocalDate.now());

    this.dateModel.addChangeListener(
        () -> Optional
            .of(this.dateModel.get()) //
            .consume(d -> setSelectedDate(d.getYear(), d.getMonthValue() - 1, d.getDayOfMonth()))
            .or(() -> GuiUtilities.invokeLater(() -> {
              for (int i = 0; i < this.height; i++) {
                for (int j = 0; j < this.width; j++) {
                  this.datePanes[i][j].setSelected(false);
                }
              }
            })));

    this.selectedDateModel.addChangeListener(
        () -> Optional
            .of(this.selectedDateModel.get()) //
            .consume(
                d -> this.dateModel.set(Instant.ofEpochMilli(d.getTime()).atZone(ZoneId.systemDefault()).toLocalDate()))
            .or(() -> this.dateModel.set(null)));
  }

  void setMonth(final int year, final int month) {
    if (this.year == year && this.month == month) {
      return;
    }
    this.year = year;
    this.month = month;
    this.monthBox.setSelectedIndex(month);
    this.yearModel.set(year);
    GregorianCalendar date = new GregorianCalendar();
    date = getDate(year, month, 1);
    int day = 1 - (date.get(Calendar.DAY_OF_WEEK) - date.get(Calendar.DAY_OF_WEEK_IN_MONTH));
    DatePanel datePane = null;
    for (int i = 0; i < this.height; i++) {
      for (int j = 0; j < this.width; j++) {
        if (year == 1582 && month == 9 && day == 5) {
          day = 15;
        }
        date = getDate(year, month, day++);
        datePane = this.datePanes[i][j];
        datePane.setDate(date, true);
        if (month == date.get(Calendar.MONTH)) {
          if (date.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            datePane.setBackground(Color.lightGray);
            datePane.setForeground(Color.black);
          } else {
            datePane.setBackground(Color.white);
            datePane.setForeground(Color.black);
          }
        } else {
          if (date.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            datePane.setBackground(Color.gray.darker());
            datePane.setForeground(Color.white);
          } else {
            datePane.setBackground(Color.gray);
            datePane.setForeground(Color.white);
          }
        }
      }
    }
    date = getDate(year, month, 1);
    fireMonthChanged(date.getTime());
  }

  int getMonth() {
    return this.month;
  }

  int getYear() {
    return this.year;
  }

  public void setSelectedDate(final int year, final int month, final int day) {
    setMonth(year, month);
    final Point pos = getPosition(year, month, day);
    final DatePanel datePane = this.datePanes[pos.x][pos.y];
    if (this.selectedDateModel.get() == null || !this.selectedDateModel.get().equals(datePane.getDate())) {
      datePane.setSelected(true);
      if (!datePane.hasFocus()) {
        datePane.requestFocus();
      }
    }
  }

  private Point getPosition(final int year, final int month, final int day) {
    GregorianCalendar date = null;
    final DatePanel nullPane = this.datePanes[0][0];
    date = getDate(
        (nullPane.getERA() == GregorianCalendar.AD ? nullPane.getYear() : (nullPane.getYear() - 1) * -1),
        nullPane.getMonth(),
        nullPane.getDay());
    final int firstDate = date.get(Calendar.DAY_OF_YEAR);

    date = getDate(year, month, day);
    int selectedDate = date.get(Calendar.DAY_OF_YEAR);
    if (year == 1582 && month == 9 && day > 4) {
      selectedDate -= 10;
    }
    if (firstDate > selectedDate) {
      selectedDate = getDate(year - 1, 11, 31).get(Calendar.DAY_OF_YEAR) + selectedDate;
    }
    final int difference = selectedDate - firstDate;
    final int dayOfWeek = difference % this.width;
    final int weekOfMonth = difference / this.width;
    return new Point(weekOfMonth, dayOfWeek);
  }

  public Date getSelectedDate() {
    return this.selectedDateModel.get();
  }

  public IObjectModel<LocalDate> getDateModel() {
    return this.dateModel;
  }

  private GregorianCalendar getDate(final int year, final int month, final int day) {
    final GregorianCalendar date = new GregorianCalendar(year, month, day);
    if (year < 1) {
      date.set(Calendar.ERA, GregorianCalendar.AD);
    }
    return date;
  }

  private class FirstAction extends AbstractAction {
    private static final long serialVersionUID = 1L;

    public FirstAction() {
      super(
          "First",
          net.anwiba.commons.swing.icons.gnome.contrast.high.ContrastHightIcons.MEDIA_SEEK_FORWARD_RTL.getSmallIcon());
      putValue(Action.SHORT_DESCRIPTION, "first month");
    }

    @Override
    public void actionPerformed(final ActionEvent event) {
      int m = -1;
      int y = -1;

      if (MonthView.this.month == 0) {
        y = MonthView.this.year - 1;
        m = MonthView.this.month;
      } else {
        y = MonthView.this.year;
        m = 0;
      }
      setMonth(y, m);
    }
  }

  private class PreviousAction extends AbstractAction {

    private static final long serialVersionUID = 1L;

    public PreviousAction() {
      super(
          "Previous",
          net.anwiba.commons.swing.icons.gnome.contrast.high.ContrastHightIcons.MEDIA_PLAYBACK_START_RTL
              .getSmallIcon());
      putValue(Action.SHORT_DESCRIPTION, "previous month");
    }

    @Override
    public void actionPerformed(final ActionEvent event) {
      int m = -1;
      int y = -1;

      if (MonthView.this.month == 0) {
        y = MonthView.this.year - 1;
        m = 11;
      } else {
        y = MonthView.this.year;
        m = MonthView.this.month - 1;
      }
      setMonth(y, m);
    }
  }

  private class NextAction extends AbstractAction {

    private static final long serialVersionUID = 1L;

    public NextAction() {
      super(
          "Next",
          net.anwiba.commons.swing.icons.gnome.contrast.high.ContrastHightIcons.MEDIA_PLAYBACK_START.getSmallIcon());
      putValue(Action.SHORT_DESCRIPTION, "next month");
    }

    @Override
    public void actionPerformed(final ActionEvent event) {
      int m = -1;
      int y = -1;

      if (MonthView.this.month == 11) {
        y = MonthView.this.year + 1;
        m = 0;
      } else {
        y = MonthView.this.year;
        m = MonthView.this.month + 1;
      }
      setMonth(y, m);
    }
  }

  private class LastAction extends AbstractAction {

    private static final long serialVersionUID = 1L;

    public LastAction() {
      super(
          "Last",
          net.anwiba.commons.swing.icons.gnome.contrast.high.ContrastHightIcons.MEDIA_SEEK_FORWARD.getSmallIcon());
      putValue(Action.SHORT_DESCRIPTION, "last month");
    }

    @Override
    public void actionPerformed(final ActionEvent event) {
      int m = -1;
      int y = -1;

      if (MonthView.this.month == 11) {
        y = MonthView.this.year + 1;
        m = MonthView.this.month;
      } else {
        y = MonthView.this.year;
        m = 11;
      }
      setMonth(y, m);
    }
  }

  private class MonthAction extends AbstractAction {

    private static final long serialVersionUID = 1L;

    public MonthAction() {
      super("Month", net.anwiba.commons.swing.icons.GuiIcons.MISC_ICON.getSmallIcon());
      putValue(Action.SHORT_DESCRIPTION, "set month");
    }

    @Override
    public void actionPerformed(final ActionEvent event) {
      if (MonthView.this.month == MonthView.this.monthBox.getSelectedIndex()) {
        return;
      }
      setMonth(MonthView.this.year, MonthView.this.monthBox.getSelectedIndex());
    }
  }

  protected void fireSelectionOccured(final DatePanel datePane) {
    final Object[] listeners = this.listenerList.getListenerList();
    DateSelectionEvent e = null;
    for (int i = listeners.length - 2; i >= 0; i -= 2) {
      if (listeners[i] == DateSelectionListener.class) {
        if (e == null) {
          e = new DateSelectionEvent(this, datePane.getDate());
        }
        ((DateSelectionListener) listeners[i + 1]).selectionOccurred(e);
      }
    }
  }

  protected void fireDeselectionOccured(final DatePanel datePane) {
    final Object[] listeners = this.listenerList.getListenerList();
    DateSelectionEvent e = null;
    for (int i = listeners.length - 2; i >= 0; i -= 2) {
      if (listeners[i] == DateSelectionListener.class) {
        if (e == null) {
          e = new DateSelectionEvent(this, datePane.getDate());
        }
        ((DateSelectionListener) listeners[i + 1]).deselectionOccurred(e);
      }
    }
  }

  public void addDaySelectionListener(final DateSelectionListener l) {
    this.listenerList.add(DateSelectionListener.class, l);
  }

  public void removeDaySelectionListener(final DateSelectionListener l) {
    this.listenerList.remove(DateSelectionListener.class, l);
  }

  protected void fireMonthChanged(final Date date) {
    final Object[] listeners = this.listenerList.getListenerList();
    MonthChangedEvent e = null;

    for (int i = listeners.length - 2; i >= 0; i -= 2) {
      if (listeners[i] == MonthChangedListener.class) {
        if (e == null) {
          e = new MonthChangedEvent(this, date);
        }
        ((MonthChangedListener) listeners[i + 1]).monthChanged(e);
      }
    }
  }

  public void addMonthChangeListener(final MonthChangedListener l) {
    this.listenerList.add(MonthChangedListener.class, l);
  }

  public void removeMonthChangeListener(final MonthChangedListener l) {
    this.listenerList.remove(MonthChangedListener.class, l);
  }
}
