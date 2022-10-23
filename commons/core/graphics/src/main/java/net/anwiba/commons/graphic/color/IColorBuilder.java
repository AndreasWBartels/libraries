/*
 * #%L
 * anwiba commons
 * %%
 * Copyright (C) 2007 - 2022 Andreas W. Bartels
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
package net.anwiba.commons.graphic.color;

public interface IColorBuilder {

  IColorBuilder component(int index, float value);

  IColorBuilder min();

  IColorBuilder max();

  IColorBuilder opacity(float opacity);

  IColorBuilder opacity(int opacity);
  
  IColorBuilder component(final int index, final int value);

  default IColorBuilder components(float[] compoments) {
    for (int i = 0; i < compoments.length; i++) {
      component(i, compoments[i]);
    }
    return this;
  }

  IColor build();


}
