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
package net.anwiba.tools.definition.schema.json.generator.java.bean;

import net.anwiba.commons.resource.annotation.Location;
import net.anwiba.commons.resource.reflaction.AbstractResourceFactory;

public class JsonBeanGeneratorTestResources extends AbstractResourceFactory {

  static {
    initialize(JsonBeanGeneratorTestResources.class);
  }

  @Location("typeInfoFactory.jssd")
  public static String typeInfoFactoryBeanDefinition;

  @Location("typeInfoFactory.source")
  public static String typeInfoFactoryBeanSource;

  @Location("typeInfoWithNameFactory.jssd")
  public static String typeInfoWithNameFactoryBeanDefinition;

  @Location("typeInfoWithNameFactory.source")
  public static String typeInfoWithNameFactoryBeanSource;

  @Location("value.jssd")
  public static String valueBeanDefinition;

  @Location("value.source")
  public static String valueBeanSource;

  @Location("pattern.jssd")
  public static String patternBeanDefinition;

  @Location("pattern.source")
  public static String patternBeanSource;

  @Location("error.jssd")
  public static String errorBeanDefinition;

  @Location("error.source")
  public static String errorBeanSource;

  @Location("errorBeanAndBuilder.source")
  public static String errorBeanAndBuilderSource;

  @Location("envelope.jssd")
  public static String envelopeBeanDefinition;

  @Location("feature.jssd")
  public static String featureBeanDefinition;

  @Location("feature.source")
  public static String featureBeanSource;

  @Location("point.jssd")
  public static String pointBeanDefinition;

  @Location("spatialReference.jssd")
  public static String spatialReferenceBeanDefinition;

  @Location("point.source")
  public static String pointBeanSource;

  @Location("geometry.jssd")
  public static String geometryBeanDefinition;

  @Location("entity.source")
  public static String entityBeanSource;

  @Location("entity.jssd")
  public static String entityBeanDefinition;

  @Location("factoryWithUnknownMembers.source")
  public static String factoryWithUnknownMembersBeanSource;

  @Location("factoryWithUnknownMembers.jssd")
  public static String factoryWithUnknownMembersBeanDefinition;

  @Location("factoryWithIgnoreUnknownMembers.source")
  public static String factoryWithIgnoreUnknownMembersBeanSource;

  @Location("factoryWithIgnoreUnknownMembers.jssd")
  public static String factoryWithIgnoreUnknownMembersBeanDefinition;

  @Location("factory.source")
  public static String factoryBeanSource;

  @Location("factory.jssd")
  public static String factoryBeanDefinition;

  @Location("injectTypeFactory.source")
  public static String injectTypeFactoryBeanSource;

  @Location("injectTypeFactory.jssd")
  public static String injectTypeFactoryBeanDefinition;

  @Location("complexFactory.source")
  public static String complexFactoryBeanSource;

  @Location("complexFactory.jssd")
  public static String complexFactoryBeanDefinition;

  @Location("module.jssd")
  public static String moduleBeanDefinition;

  @Location("module.source")
  public static Object moduleBeanSource;

  @Location("classMember.jssd")
  public static String classMemberBeanDefinition;

  @Location("classSource.source")
  public static Object classMemberBeanSource;

  @Location("reflactionFactoryWithConfiguredArgumentName.jssd")
  public static String reflactionFactoryWithConfiguredArgumentNameBeanDefinition;

  @Location("reflactionFactoryWithConfiguredArgumentName.source")
  public static String reflactionFactoryWithConfiguredArgumentNameBeanSource;
}
