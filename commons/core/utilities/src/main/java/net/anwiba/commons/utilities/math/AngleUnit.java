/*
 * #%L
 * anwiba commons core
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

package net.anwiba.commons.utilities.math;

public enum AngleUnit {

  RADIAN {

    @Override
    public double toRadians() {
      return 1d;
    }

    @Override
    public double fromRadians() {
      return 1d;
    }
  }, DEGREE {

    @Override
    public double toRadians() {
      return 0.017453292519943295;
    }

    @Override
    public double fromRadians() {
      return 57.29577951308232;
    }
  }, GON {

    @Override
    public double toRadians() {
      return 0.015707963;
    }

    @Override
    public double fromRadians() {
      return 63.661977237;
    }
  }, SEMI_CIRCLE {

    @Override
    public double toRadians() {
      return 6.283185307;
    }

    @Override
    public double fromRadians() {
      return 0.318309886;
    }
  };

  public abstract double toRadians();

  public abstract double fromRadians();

} 
