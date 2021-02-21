/*
 * #%L
 * anwiba commons
 * %%
 * Copyright (C) 2007 - 2019 Andreas W. Bartels
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
package net.anwiba.commons.graphic;

import java.awt.HeadlessException;
import java.awt.Toolkit;

import net.anwiba.commons.lang.optional.Optional;

public class ScreenResolutionUtilities {

  private static Double screenResolution = null;

  public static synchronized double getScreenResolution() {
    return Optional
        .of(screenResolution) //
        .or(
            () -> Optional
                .of(HeadlessException.class, Toolkit.getDefaultToolkit())
                .convert(t -> Double.valueOf(t.getScreenResolution()))
                .failed(e -> Double.valueOf(IGraphicResolution.DEFAULT_DPI))
                .or(() -> Double.valueOf(IGraphicResolution.DEFAULT_DPI))
                .convert(r -> property(r)) // $NON-NLS-1$
                .consume(r -> screenResolution = r)
                .get())
        .get()
        .doubleValue();
  }

  private static Double property(final Double defaultValue) {
    return Double.valueOf(System.getProperty("net.anwiba.screen.resolution", String.valueOf(defaultValue)));
  }

}
