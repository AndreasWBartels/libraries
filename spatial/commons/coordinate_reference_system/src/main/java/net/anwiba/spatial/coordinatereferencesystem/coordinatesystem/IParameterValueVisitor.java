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

interface IParameterValueVisitor {

  void visitStandardParallel2();

  void visitStandardParallel1();

  void visitLongitudeOfPoint2();

  void visitLongitudeOfPoint1();

  void visitLatitudeOfPoint2();

  void visitLatitudeOfPoint1();

  void visitAzimuth();

  void visitLongitudeOfCenter();

  void visitLatitudeOfOrigin();

  void visitScaleFactor();

  void visitFalseEasting();

  void visitFalseNorthing();

  double getResult();

  void visitSemiMinor();

  void visitSemiMajor();

  void visitAuxiliarySphereType();

  void visitRectifiedGridAngle();

  void visitZoneWidth();

  void visitUnkown();

}
