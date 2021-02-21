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
package net.anwiba.commons.swing.table;

import java.awt.event.MouseEvent;
import java.util.Optional;

import javax.swing.JTable;
import javax.swing.table.TableModel;

import net.anwiba.commons.swing.utilities.JTableUtilities;
import net.anwiba.commons.utilities.string.IStringSubstituter;

public class Table extends JTable {
  private static final long serialVersionUID = 1L;
  private final IStringSubstituter substituter;

  public Table(final TableModel tableModel) {
    this(tableModel, null);
  }

  public Table(final TableModel tableModel, final IStringSubstituter substituter) {
    super(tableModel);
    this.substituter = substituter;
  }

  @Override
  public String getToolTipText(final MouseEvent event) {
    final String string = getCellContentAsString(this, event);
    final String toolTipString = Optional.ofNullable(this.substituter).map(s -> s.substitute(string)).orElse(string);
    return toolTipString;
  }

  private String getCellContentAsString(final Table table, final MouseEvent event) {
    final String value = JTableUtilities.getToolTipText(table, event);
    if (value == null) {
      return super.getToolTipText(event);
    }
    return value;
  }
}