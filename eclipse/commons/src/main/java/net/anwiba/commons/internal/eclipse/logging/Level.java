/*
 * #%L
 * anwiba eclipse java tools
 * %%
 * Copyright (C) 2007 - 2021 Andreas Bartels
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
package net.anwiba.commons.internal.eclipse.logging;

import net.anwiba.commons.eclipse.logging.ILevel;

import org.eclipse.core.runtime.IStatus;

public enum Level implements ILevel {

  OK(IStatus.OK), ERROR(IStatus.ERROR), INFO(IStatus.INFO), WARNING(IStatus.WARNING), CANCEL(IStatus.CANCEL);

  private final int code;

  private Level(final int code) {
    this.code = code;
  }

  @Override
  public int getCode() {
    return this.code;
  }
}
