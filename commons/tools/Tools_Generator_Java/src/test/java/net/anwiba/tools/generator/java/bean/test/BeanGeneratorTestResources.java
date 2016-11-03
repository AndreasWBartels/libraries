/*
 * #%L
 * anwiba commons tools
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
package net.anwiba.tools.generator.java.bean.test;

import net.anwiba.commons.resource.annotation.Location;
import net.anwiba.commons.resource.reflaction.AbstractResourceFactory;

public class BeanGeneratorTestResources extends AbstractResourceFactory {

  static {
    initialize(BeanGeneratorTestResources.class);
  }

  @Location("creatorBeanSource.source")
  public static String creatorBeanSource;
  @Location("mutableBeanSource.source")
  public static String mutableBeanSource;
  @Location("imutableBeanSource.source")
  public static String imutableBeanSource;
  @Location("namedValueProvider.source")
  public static Object namedValueProvider;
  @Location("beanBuilderSource.source")
  public static Object beanBuilderSource;

}
