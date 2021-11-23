/*
 * #%L
 * anwiba spatial
 * %%
 * Copyright (C) 2007 - 2019 Andreas Bartels
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
// Copyright (c) 2007 by Andreas W. Bartels
package net.anwiba.spatial.coordinatereferencesystem.coordinatesystem;

import net.anwiba.commons.ensure.Ensure;

public enum ProjectionType implements IProjectionType {

  UTM(CoordinateSystemMessages.UTM,
      Surface.CYLINDRICAL,
      Property.CONFORMAL,
      Orientation.TRANSVERSE) {

    @Override
    protected boolean accept(final String name) {
      return name.indexOf(name()) != -1;
    }
  },
  HOTINE_OBLIQUE_MERCATOR_AZIMUTH_CENTER(CoordinateSystemMessages.HOTINE_OBLIQUE_MERCATOR_AZIMUTH_CENTER,
      Surface.CYLINDRICAL,
      Property.CONFORMAL,
      Orientation.OBLIQUE) {

    @Override
    protected boolean accept(final String name) {
      return name.indexOf(name()) != -1 || name.indexOf("OBLIQUE_MERCATOR") != -1; //$NON-NLS-1$ ;
    }
  },
  MERCATOR_1SP(CoordinateSystemMessages.MERCATOR_SP1,
      Surface.CYLINDRICAL,
      Property.CONFORMAL,
      Orientation.TANGENT) {

    @Override
    protected boolean accept(final String name) {
      return name.indexOf(name()) != -1 //
          || name.indexOf("PSEUDO_MERCATOR") != -1 //$NON-NLS-1$
          || name.indexOf("MERCATOR_AUXILIARY_SPHERE") != -1; //$NON-NLS-1$
    }
  },
  TRANSVERSE_MERCATOR_ZONED_GRID_SYSTEM(CoordinateSystemMessages.TRANSVERSE_MERCATOR_ZONED_GRID_SYSTEM,
      Surface.CYLINDRICAL,
      Property.CONFORMAL,
      Orientation.TRANSVERSE) {

    @Override
    protected boolean accept(final String name) {
      return name.indexOf(name()) != -1;
    }
  },
  TRANSVERSE_MERCATOR(CoordinateSystemMessages.TRANSVERSE_MERCATOR,
      Surface.CYLINDRICAL,
      Property.CONFORMAL,
      Orientation.TRANSVERSE) {

    @Override
    protected boolean accept(final String name) {
      return name.indexOf(name()) != -1
          && !TRANSVERSE_MERCATOR_ZONED_GRID_SYSTEM.accept(name);
    }
  },
  MERCATOR(CoordinateSystemMessages.MERCATOR,
      Surface.CYLINDRICAL,
      Property.CONFORMAL,
      Orientation.TANGENT) {

    @Override
    protected boolean accept(final String name) {
      return name.indexOf(name()) != -1
          && !TRANSVERSE_MERCATOR.accept(name)
          && !TRANSVERSE_MERCATOR_ZONED_GRID_SYSTEM.accept(name);
    }
  },
  GAUSS_KRUGER(CoordinateSystemMessages.GAUSS_KRUGER,
      Surface.CYLINDRICAL,
      Property.CONFORMAL,
      Orientation.TRANSVERSE) {

    @Override
    protected boolean accept(final String name) {
      return name.replace("Ü", "U").indexOf(name()) != -1; //$NON-NLS-1$//$NON-NLS-2$
    }
  },
  ALBERS_CONIC_EQUAL_AREA(CoordinateSystemMessages.ALBERS_CONIC_EQUAL_AREA,
      //      Surface.CYLINDRICAL,
      Surface.CONICAL,
      Property.EQUAL_AREA,
      Orientation.TANGENT) {

    @Override
    protected boolean accept(final String name) {
      return name.indexOf(name()) != -1 //
          || name.indexOf("ALBERS_EQUAL_AREA_CONIC") != -1
          || name.indexOf("ALBERS") != -1; //$NON-NLS-1$
    }
  },
  LAMBERT_CONFORMAL_CONIC_1SP(CoordinateSystemMessages.LAMBERT_CONFORMAL_CONIC_1SP,
      Surface.CONICAL,
      Property.CONFORMAL,
      Orientation.TANGENT) {

    @Override
    protected boolean accept(final String name) {
      return name.indexOf(name()) != -1 //
          || name.indexOf("LAMBERT_CONIC_CONFORMAL_1SP") != -1; //$NON-NLS-1$
    }
  },
  LAMBERT_CONFORMAL_CONIC_2SP(CoordinateSystemMessages.LAMBERT_CONFORMAL_CONIC_2SP,
      Surface.CONICAL,
      Property.CONFORMAL,
      Orientation.TANGENT) {

    @Override
    protected boolean accept(final String name) {
      return name.indexOf(name()) != -1 //
          || name.indexOf("LAMBERT_CONIC_CONFORMAL_2SP") != -1; //$NON-NLS-1$
    }
  },
  LAMBERT_CONFORMAL_CONIC(CoordinateSystemMessages.LAMBERT_CONFORMAL_CONIC,
      Surface.CONICAL,
      Property.CONFORMAL,
      Orientation.TANGENT) {

    @Override
    protected boolean accept(final String name) {
      return name.indexOf(name()) != -1 //
          || name.indexOf("LAMBERT_CONIC_CONFORMAL") != -1; //$NON-NLS-1$
    }
  },
  LAMBERT_AZIMUTHAL_EQUAL_AREA(CoordinateSystemMessages.LAMBERT_AZIMUTHAL_EQUAL_AREA,
      Surface.AZIMUTHAL,
      Property.EQUAL_AREA,
      Orientation.TANGENT) {

    @Override
    protected boolean accept(final String name) {
      return name.indexOf(name()) != -1;
    }
  },
  CASSINI_SOLDNER(CoordinateSystemMessages.CASSINI_SOLDNER,
      Surface.CYLINDRICAL,
      Property.APHYLACTIC,
      Orientation.TANGENT) {

    @Override
    protected boolean accept(final String name) {
      return name.indexOf(name()) != -1;
    }
  },
  POLAR_STEREOGRAPHIC(CoordinateSystemMessages.POLAR_STEREOGRAPHIC,
      Surface.AZIMUTHAL,
      Property.CONFORMAL,
      Orientation.TANGENT) {

    @Override
    protected boolean accept(final String name) {
      return name.indexOf(name()) != -1;
    }
  },
  UNKOWN(CoordinateSystemMessages.UNKOWN,
      null,
      null,
      null) {

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
  private final Surface surface;
  private final Property property;
  private final Orientation orientation;

  private ProjectionType(final String printName,
      final Surface surface,
      final Property property,
      final Orientation orientation) {
    this.printName = printName;
    this.surface = surface;
    this.property = property;
    this.orientation = orientation;
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

  public Surface getSurface() {
    return this.surface;
  }

  public Property getProperty() {
    return this.property;
  }

  public Orientation getOrientation() {
    return this.orientation;
  }

  /**
   * Projection classification based on the surface type.
   */
  public enum Surface {

    AZIMUTHAL, // or stereographic
    CONICAL,
    CYLINDRICAL,
    HYBRID,
    MISCELLANEOUS,
    POLYCONICAL,
    PSEUDOAZIMUTHAL,
    PSEUDOCONICAL,
    PSEUDOCYLINDRICAL,
    RETROAZIMUTHAL
  }

  /**
   * Projection property.
   */
  public enum Property {

    APHYLACTIC, // A term sometimes used to describe a map projection
    //which is neither equal-area  nor conformal
    CONFORMAL, // Locally shape preserving (angle preserving)
    EQUAL_AREA, // Area preserving (also called Equiarea, Equivalent, Authalic)
    EQUIDISTANT, // Distance preserving
    GNOMONIC
  } // Shortest route preserving;

  /**
   * Projection orientation.
   */
  public enum Orientation {

    OBLIQUE,
    SECANT,
    TANGENT,
    TRANSVERSE
  }
}
