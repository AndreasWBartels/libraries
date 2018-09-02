/*
 * #%L
 * anwiba commons core
 * %%
 * Copyright (C) 2007 - 2018 Andreas W. Bartels
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

package net.anwiba.spatial.coordinatereferencesystem.coordinatesystem;

import net.anwiba.commons.ensure.Ensure;

public enum ProjectionType implements IProjectionType {

  UTM(CoordinateSystemMessages.UTM) {

    @Override
    protected boolean accept(final String name) {
      return name.indexOf(name()) != -1;
    }
  },
  HOTINE_OBLIQUE_MERCATOR_AZIMUTH_CENTER(CoordinateSystemMessages.HOTINE_OBLIQUE_MERCATOR_AZIMUTH_CENTER) {

    @Override
    protected boolean accept(final String name) {
      return name.indexOf(name()) != -1 || name.indexOf("OBLIQUE_MERCATOR") != -1; //$NON-NLS-1$;
    }
  },
  MERCATOR_1SP(CoordinateSystemMessages.MERCATOR_SP1) {

    @Override
    protected boolean accept(final String name) {
      return name.indexOf(name()) != -1 //
          || name.indexOf("PSEUDO_MERCATOR") != -1 //$NON-NLS-1$
          || name.indexOf("MERCATOR_AUXILIARY_SPHERE") != -1; //$NON-NLS-1$
    }
  },
  TRANSVERSE_MERCATOR(CoordinateSystemMessages.TRANSVERSE_MERCATOR) {

    @Override
    protected boolean accept(final String name) {
      return name.indexOf(name()) != -1;
    }
  },
  MERCATOR(CoordinateSystemMessages.MERCATOR) {

    @Override
    protected boolean accept(final String name) {
      return name.indexOf(name()) != -1 && !TRANSVERSE_MERCATOR.accept(name);
    }
  },
  GAUSS_KRUGER(CoordinateSystemMessages.GAUSS_KRUGER) {

    @Override
    protected boolean accept(final String name) {
      return name.replace("Ü", "U").indexOf(name()) != -1; //$NON-NLS-1$//$NON-NLS-2$
    }
  },
  LAMBERT_CONFORMAL_CONIC(CoordinateSystemMessages.LAMBERT_CONFORMAL_CONIC) {

    @Override
    protected boolean accept(final String name) {
      return name.indexOf(name()) != -1 //
          || name.indexOf("LAMBERT_CONIC_CONFORMAL") != -1; //$NON-NLS-1$
    }
  },
  LAMBERT_CONFORMAL_CONIC_1SP(CoordinateSystemMessages.LAMBERT_CONFORMAL_CONIC_1SP) {

    @Override
    protected boolean accept(final String name) {
      return name.indexOf(name()) != -1 //
          || name.indexOf("LAMBERT_CONIC_CONFORMAL_1SP") != -1; //$NON-NLS-1$
    }
  },
  LAMBERT_CONFORMAL_CONIC_2SP(CoordinateSystemMessages.LAMBERT_CONFORMAL_CONIC_2SP) {

    @Override
    protected boolean accept(final String name) {
      return name.indexOf(name()) != -1 //
          || name.indexOf("LAMBERT_CONIC_CONFORMAL_1SP") != -1; //$NON-NLS-1$
    }
  },
  LAMBERT_AZIMUTHAL_EQUAL_AREA(CoordinateSystemMessages.LAMBERT_AZIMUTHAL_EQUAL_AREA) {

    @Override
    protected boolean accept(final String name) {
      return name.indexOf(name()) != -1;
    }
  },
  CASSINI_SOLDNER(CoordinateSystemMessages.CASSINI_SOLDNER) {

    @Override
    protected boolean accept(final String name) {
      return name.indexOf(name()) != -1;
    }
  },
  POLAR_STEREOGRAPHIC(CoordinateSystemMessages.POLAR_STEREOGRAPHIC) {

    @Override
    protected boolean accept(final String name) {
      return name.indexOf(name()) != -1;
    }
  },
  UNKOWN(CoordinateSystemMessages.UNKOWN) {

    @Override
    protected boolean accept(final String name) {
      return name.indexOf(name()) != -1;
    }
  };

  public static IProjectionType getByName(final String name) {
    Ensure.ensureArgumentNotNull(name);
    final String cleanedName = name
        .trim() //
        .toUpperCase()
        .replaceAll("\\(", "") //$NON-NLS-1$//$NON-NLS-2$
        .replaceAll("\\)", "") //$NON-NLS-1$//$NON-NLS-2$
        .replaceAll("-", "_") //$NON-NLS-1$//$NON-NLS-2$
        .replaceAll(" ", "_") //$NON-NLS-1$ //$NON-NLS-2$
    ;
    for (final ProjectionType type : values()) {
      if (type.accept(cleanedName)) {
        return type;
      }
    }
    return new IProjectionType() {

      @Override
      public String getName() {
        return cleanedName;
      }

      @Override
      public String getPrintName() {
        return name;
      }

      @Override
      public ProjectionType getType() {
        return ProjectionType.UNKOWN;
      }
    };
  }

  protected abstract boolean accept(String name);

  private final String printName;

  private ProjectionType(final String printName) {
    this.printName = printName;
  }

  @Override
  public String getName() {
    return this.printName;
  }

  @Override
  public String getPrintName() {
    return this.printName;
  }

  @Override
  public ProjectionType getType() {
    return this;
  }
}