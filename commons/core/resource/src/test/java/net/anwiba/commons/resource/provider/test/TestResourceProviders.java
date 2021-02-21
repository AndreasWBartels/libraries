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
package net.anwiba.commons.resource.provider.test;

import net.anwiba.commons.reference.IResourceReference;
import net.anwiba.commons.resource.annotation.Location;
import net.anwiba.commons.resource.annotation.Static;
import net.anwiba.commons.resource.reflaction.AbstractResourceFactory;
import net.anwiba.commons.resource.reflaction.IByteArrayResourceProvider;

public class TestResourceProviders extends AbstractResourceFactory {

  static {
    initialize(TestResourceProviders.class, (c, r) -> c.getResource(r));
  }

  @Location("text.txt")
  public static String relativUrlContentText;

  @Location("subfolder/text.txt")
  public static String relativSubfolderUrlContentText;

  @Location("../subfolder/text.txt")
  public static String relativParentSubfolderUrlContentText;

  @Location("/net/anwiba/commons/resource/provider/test/text.txt")
  public static String absoluteUrlContentText;

  @Location("text.txt")
  public static String[] textArray;

  @Location("text.txt")
  public static IResourceReference resourceReference;

  // @Location("file:src/test/resources/net/anwiba/commons/resource/provider/test/text.txt")
  // public static IResourceReference fileResource;

  @Location("text.txt")
  public static IByteArrayResourceProvider textResource;

  @Static(true)
  @Location("text.txt")
  public static IByteArrayResourceProvider staticAnnotationTextResource;

  @Static(false)
  @Location("text.txt")
  public static IByteArrayResourceProvider noneStaticAnnotationTextResource;

}