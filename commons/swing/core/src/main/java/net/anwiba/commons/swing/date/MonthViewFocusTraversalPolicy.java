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

import java.awt.Component;
import java.awt.Container;
import java.awt.FocusTraversalPolicy;
import java.awt.Point;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class MonthViewFocusTraversalPolicy extends FocusTraversalPolicy {

  final DatePanel[][] datePanes;
  final int height;
  final int width;

  public MonthViewFocusTraversalPolicy(final DatePanel[][] datePanes) {
    this.datePanes = datePanes;
    this.height = this.datePanes.length;
    this.width = this.datePanes[0].length;
  }

  @Override
  public Component getComponentAfter(final Container focusCycleRoot, final Component component) {
    if (component instanceof DatePanel) {
      final int era = ((DatePanel) component).getERA();
      final int year = era == GregorianCalendar.AD
          ? ((DatePanel) component).getYear()
          : (((DatePanel) component).getYear() - 1) * -1;
      final int month = ((DatePanel) component).getMonth();
      final int day = ((DatePanel) component).getDay();
      Point pos = getCurrentPosition(year, month, day);
      if (pos.x == this.height - 1 && pos.y == this.width - 1) {
        if (month == 11) {
          ((MonthView) focusCycleRoot).setMonth((year + 1), 0);
        } else {
          ((MonthView) focusCycleRoot).setMonth(year, (month + 1));
        }
        pos = getCurrentPosition(year, month + 1, 1);
      } else {
        if (pos.y == this.width - 1) {
          pos.x++;
          pos.y = 0;
        } else {
          pos.y++;
        }
      }
      if ((pos.x > 0 && pos.x < this.datePanes.length) && (pos.y > 0 && pos.y < this.datePanes[pos.x].length)) {
        return this.datePanes[pos.x][pos.y];
      }
    }
    return getDefaultComponent(focusCycleRoot);
  }

  @Override
  public Component getLastComponent(final Container focusCycleRoot) {
    final int year = ((MonthView) focusCycleRoot).getYear();
    final int month = ((MonthView) focusCycleRoot).getMonth();
    final Point pos = getCurrentPosition(year, (month + 1), -1);
    return this.datePanes[pos.x][pos.y];
  }

  @Override
  public Component getFirstComponent(final Container focusCycleRoot) {
    final int year = ((MonthView) focusCycleRoot).getYear();
    final int month = ((MonthView) focusCycleRoot).getMonth();
    final Point pos = getCurrentPosition(year, month, 1);
    return this.datePanes[pos.x][pos.y];
  }

  @Override
  public Component getDefaultComponent(final Container focusCycleRoot) {
    final int year = ((MonthView) focusCycleRoot).getYear();
    final int month = ((MonthView) focusCycleRoot).getMonth();
    final Point pos = getCurrentPosition(year, month, 1);
    return this.datePanes[pos.x][pos.y];
  }

  @Override
  public Component getComponentBefore(final Container focusCycleRoot, final Component component) {
    if (component instanceof DatePanel) {
      final int era = ((DatePanel) component).getERA();
      final int year = era == GregorianCalendar.AD
          ? ((DatePanel) component).getYear()
          : (((DatePanel) component).getYear() - 1) * -1;
      final int month = ((DatePanel) component).getMonth();
      final int day = ((DatePanel) component).getDay();
      Point pos = getCurrentPosition(year, month, day);
      if (pos.x == 0 && pos.y == 0) {
        if (month == 0) {
          ((MonthView) focusCycleRoot).setMonth((year - 1), 11);
        } else {
          ((MonthView) focusCycleRoot).setMonth(year, (month - 1));
        }
        pos = getCurrentPosition(year, month, day - 1);
      } else {
        if (pos.y == 0) {
          pos.x--;
          pos.y = this.width - 1;
        } else {
          pos.y--;
        }
      }
      if ((pos.x > 0 && pos.x < this.datePanes.length) && (pos.y > 0 && pos.y < this.datePanes[pos.x].length)) {
        return this.datePanes[pos.x][pos.y];
      }
    }
    return getDefaultComponent(focusCycleRoot);
  }

  private Point getCurrentPosition(final int year, final int month, final int day) {
    final DatePanel nullPane = this.datePanes[0][0];
    final int firstDate = getDayOfYear(
        (nullPane.getERA() == GregorianCalendar.AD ? nullPane.getYear() : (nullPane.getYear() - 1) * -1),
        nullPane.getMonth(),
        nullPane.getDay());
    int selectedDate = getDayOfYear(year, month, day);
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

  private int getDayOfYear(final int year, final int month, final int day) {
    return getDate(year, month, day).get(Calendar.DAY_OF_YEAR);
  }

  private GregorianCalendar getDate(final int year, final int month, final int day) {
    final GregorianCalendar date = new GregorianCalendar(year, month, day);
    if (year < 1) {
      date.set(Calendar.ERA, GregorianCalendar.AD);
    }
    return date;
  }
}
