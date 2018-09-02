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
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.anwiba.commons.swing.date.event.DateSelectionEvent;
import net.anwiba.commons.swing.date.event.DateSelectionListener;

class DatePanel extends JPanel {

  private static final long serialVersionUID = 1L;
  Color gridColor = Color.lightGray;
  int era = -1;
  int dayOfWeek = -1;
  int day = -1;
  int month = -1;
  int year = -1;
  boolean isActive = false;

  JLabel dayLabel = null;
  boolean isSelected = false;
  static GregorianCalendar currentDay = new GregorianCalendar();

  DatePanel() {
    setLayout(new BorderLayout());
    setBackground(Color.lightGray);
    setBorder(BorderFactory.createLineBorder(this.gridColor));
    addFocusListener(new FocusAdapter() {

      @Override
      public void focusGained(final FocusEvent event) {
        final Object source = event.getSource();
        if (source == DatePanel.this) {
          if (!DatePanel.this.isSelected) {
            if (!DatePanel.this.isActive) {
              DatePanel.this.isActive = true;
            }
            setSelected(true);
          }
        }
      }
    });
    addMouseListener(new MouseAdapter() {

      @Override
      public void mouseClicked(final MouseEvent event) {
        final Object source = event.getSource();
        if (source == DatePanel.this) {
          if (!DatePanel.this.isSelected) {
            setSelected(true);
          }
        }
      }

    });

    this.dayLabel = new JLabel();
    this.dayLabel.setText(null);
    this.dayLabel.setForeground(Color.black);

    add(this.dayLabel, BorderLayout.NORTH);
  }

  void setSelected(final boolean isSelected) {
    if (isSelected && this.isActive) {
      if (!this.isSelected) {
        this.isSelected = true;
        fireSelectionOccured();
      }
    } else {
      if (this.isSelected) {
        this.isSelected = false;
        fireDeselectionOccured();
      }
    }
    toColor();
  }

  private void toColor() {
    if (this.isSelected) {
      setBorder(BorderFactory.createLineBorder(Color.red));
      this.dayLabel.setForeground(Color.red);
    } else {
      if (DatePanel.currentDay.get(Calendar.ERA) == this.era
          && DatePanel.currentDay.get(Calendar.YEAR) == this.year
          && DatePanel.currentDay.get(Calendar.MONTH) == this.month
          && DatePanel.currentDay.get(Calendar.DAY_OF_MONTH) == this.day) {
        setBorder(BorderFactory.createLineBorder(Color.black));
        this.dayLabel.setForeground(Color.blue);
      } else {
        setBorder(BorderFactory.createLineBorder(this.gridColor));
        this.dayLabel.setForeground(Color.black);
      }
    }
  }

  void setGridColor(final Color gridColor) {
    if (gridColor == null || this.gridColor == gridColor) {
      return;
    }
    this.gridColor = gridColor;
    toColor();
  }

  void setDate(final GregorianCalendar date) {
    setDate(date, (date == null));
  }

  void setDate(final GregorianCalendar date, final boolean isActive) {

    if (date != null
        && date.get(Calendar.YEAR) == this.year
        && date.get(Calendar.MONTH) == this.month
        && date.get(Calendar.DAY_OF_MONTH) == this.day) {
      return;
    }

    if (date == null && this.year == -1 && this.month == -1 && this.day == -1 && this.dayOfWeek == -1) {
      return;
    }

    if (this.isSelected) {
      this.isSelected = false;
      fireDeselectionOccured();
    }

    if (date == null) {

      this.era = -1;
      this.dayOfWeek = -1;
      this.day = -1;
      this.month = -1;
      this.year = -1;
      this.isActive = false;
      this.dayLabel.setText(null);

    } else {

      this.isActive = isActive;
      this.era = date.get(Calendar.ERA);
      this.dayOfWeek = date.get(Calendar.DAY_OF_WEEK);
      this.day = date.get(Calendar.DAY_OF_MONTH);
      this.month = date.get(Calendar.MONTH);
      this.year = this.era == GregorianCalendar.AD ? date.get(Calendar.YEAR) : (date.get(Calendar.YEAR) - 1) * -1;

      this.dayLabel.setText(Integer.toString(this.day));

    }
    toColor();
  }

  int getERA() {
    return this.era;
  }

  int getYear() {
    return this.year;
  }

  int getMonth() {
    return this.month;
  }

  int getDay() {
    return this.day;
  }

  Date getDate() {
    if (this.era == -1 && this.day == -1 && this.month == -1 && this.year == -1) {
      return null;
    }
    return new GregorianCalendar(this.year, this.month, this.day).getTime();
  }

  protected void fireSelectionOccured() {
    final Object[] listeners = this.listenerList.getListenerList();
    DateSelectionEvent e = null;
    for (int i = listeners.length - 2; i >= 0; i -= 2) {
      if (listeners[i] == DateSelectionListener.class) {
        if (e == null) {
          e = new DateSelectionEvent(this, getDate());
        }
        ((DateSelectionListener) listeners[i + 1]).selectionOccurred(e);
      }
    }
  }

  protected void fireDeselectionOccured() {
    final Object[] listeners = this.listenerList.getListenerList();
    DateSelectionEvent e = null;
    for (int i = listeners.length - 2; i >= 0; i -= 2) {
      if (listeners[i] == DateSelectionListener.class) {
        if (e == null) {
          e = new DateSelectionEvent(this, getDate());
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
}
