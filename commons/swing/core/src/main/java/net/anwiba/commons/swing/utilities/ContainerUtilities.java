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
package net.anwiba.commons.swing.utilities;

import java.awt.Component;
import java.awt.Frame;
import java.awt.Window;

public class ContainerUtilities {

  public static Frame getParentFrame(final Component container) {
    if (container == null) {
      return null;
    }
    if (container instanceof Frame) {
      return (Frame) container;
    }
    return getParentFrame(container.getParent());
  }

  public static Window getParentWindow(final Component container) {
    if (container == null) {
      return null;
    }
    if (container instanceof Window) {
      return (Window) container;
    }
    return getParentWindow(container.getParent());
  }
}
