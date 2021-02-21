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
package net.anwiba.commons.swing.icons;

import static net.anwiba.testing.demo.JFrames.show;

import java.net.URL;
import java.util.function.Function;

import org.junit.jupiter.api.Test;

import net.anwiba.commons.swing.icon.GuiIcon;
import net.anwiba.commons.swing.icon.IGuiIcon;

public class GuiIconDemo {

  @Test
  public void demo() {
    final IGuiIcon icon = GuiIcon.of(
        (Function<String, URL>) path -> GuiIconDemo.this.getClass().getResource(path),
        // net.anwiba.commons.swing.icons;
        "net/anwiba/commons/swing/icons/icons/small/out_of_scale_range.png", //$NON-NLS-1$
        "net/anwiba/commons/swing/icons/icons/medium/out_of_scale_range.png", //$NON-NLS-1$
        "net/anwiba/commons/swing/icons/icons/large/out_of_scale_range.png"); //$NON-NLS-1$
    show(icon.getLargeIcon());
  }
}
