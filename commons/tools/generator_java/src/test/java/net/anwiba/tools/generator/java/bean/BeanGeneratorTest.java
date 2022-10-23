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
package net.anwiba.tools.generator.java.bean;

import static net.anwiba.tools.generator.java.bean.JavaConstants.JAVA_LANG_OBJECT;
import static net.anwiba.tools.generator.java.bean.configuration.Builders.bean;
import static net.anwiba.tools.generator.java.bean.configuration.Builders.creator;
import static net.anwiba.tools.generator.java.bean.configuration.Builders.member;
import static net.anwiba.tools.generator.java.bean.configuration.Builders.properties;
import static net.anwiba.tools.generator.java.bean.configuration.Builders.type;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;

import net.anwiba.commons.lang.exception.CreationException;
import net.anwiba.commons.lang.functional.IFactory;
import net.anwiba.tools.generator.java.bean.configuration.Annotation;
import net.anwiba.tools.generator.java.bean.configuration.BeanBuilder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;

public class BeanGeneratorTest {

  private static final String COPYRIGHT = ""; //$NON-NLS-1$
  private static final String JAVADOC = "javadoc text\nthis is for testing";
  private final IFactory<String, Class<? extends java.lang.annotation.Annotation>,
      CreationException> annotationClassfactory =
          new IFactory<>() {

            @Override
            public Class<? extends java.lang.annotation.Annotation> create(final String name) throws CreationException {
              try {
                return (Class<? extends java.lang.annotation.Annotation>) this
                    .getClass()
                    .getClassLoader()
                    .loadClass(name);
              } catch (final ClassNotFoundException exception) {
                throw new CreationException(exception.getMessage(), exception);
              }
            }
          };

  @SuppressWarnings({ "nls" })
  @Test
  public void mutable() throws CreationException, IOException {
    final BeanGenerator generator = new BeanGenerator(this.annotationClassfactory);
    final BeanBuilder builder = bean("net.anwiba.test.Bean");
    builder.comment(JAVADOC);
    builder.member(member(type(String.class.getName()).build(), "name")
        .fieldComment(JAVADOC)
        .setterComment(JAVADOC)
        .getterComment(JAVADOC)
        .build());
    builder.member(member(type(String.class.getName(), 1).build(), "labels").isNullable(false).build());
    builder.member(member(type("int", 1).build(), "values").isNullable(true).value(new int[] { 1, 2 }).build());
    builder.member(member(type(String.class.getName()).build(), "type").value("Bean").isSetterEnabled(false).build());
    builder.setEqualsEnabled(true);
    generator.add(builder.build());
    final ByteArrayOutputStream ouputStream = new ByteArrayOutputStream();
    generator.generate(ouputStream);
    String string = ouputStream.toString();
    assertThat(ouputStream.size(), greaterThan(0));
    assertThat(ouputStream.toString(), equalTo(BeanGeneratorTestResources.mutableBeanSource));
  }

  @SuppressWarnings({ "nls" })
  @Test
  public void imutable() throws CreationException, IOException {
    final BeanGenerator generator = new BeanGenerator(this.annotationClassfactory);
    final BeanBuilder builder = bean("net.anwiba.test.Bean");
    builder.setEnableBuilder(true);
    builder.setMutable(false);
    builder.member(member(type(String.class.getName()).build(), "name").build());
    builder.member(member(type(String.class.getName()).build(), "type").value("Bean").isSetterEnabled(false).build());
    builder.properties(properties(type(JAVA_LANG_OBJECT).build(), "properties").isNullable(true).build());
    generator.add(builder.build());
    final ByteArrayOutputStream ouputStream = new ByteArrayOutputStream();
    generator.generate(ouputStream);
    assertThat(ouputStream.size(), greaterThan(0));
    assertThat(ouputStream.toString(), equalTo(BeanGeneratorTestResources.imutableBeanSource));
  }

  @SuppressWarnings({ "nls" })
  @Test
  public void namedValueProvider() throws CreationException, IOException {
    final BeanGenerator generator = new BeanGenerator(this.annotationClassfactory);
    final BeanBuilder builder = bean("net.anwiba.test.Bean");
    builder.member(member(type(String.class.getName()).build(), "type").value("Bean").isSetterEnabled(false).build());
    builder.properties(
        properties(type(Object.class.getName()).build(), "properties")
            .isNullable(true)
            .setImplementsNamedValueProvider(true)
            .build());
    generator.add(builder.build());
    final ByteArrayOutputStream ouputStream = new ByteArrayOutputStream();
    generator.generate(ouputStream);
    assertThat(ouputStream.size(), greaterThan(0));
    assertThat(ouputStream.toString(), equalTo(BeanGeneratorTestResources.namedValueProvider));
  }

  @SuppressWarnings({ "nls" })
  @Test
  public void beanBuilder() throws CreationException, IOException {
    final BeanGenerator generator = new BeanGenerator(this.annotationClassfactory);
    final BeanBuilder builder = bean("net.anwiba.test.Bean");
    builder.setMutable(false);
    builder.member(member(type(String.class.getName()).build(), "type").value("Bean").isSetterEnabled(false).build());
    builder.properties(
        properties(type(Object.class.getName()).build(), "properties")
            .isNullable(true)
            .setImplementsNamedValueProvider(true)
            .build());
    builder.setEnableBuilder(true);
    generator.add(builder.build());
    final ByteArrayOutputStream ouputStream = new ByteArrayOutputStream();
    generator.generate(ouputStream);
    assertThat(ouputStream.size(), greaterThan(0));
    assertThat(ouputStream.toString(), equalTo(BeanGeneratorTestResources.beanBuilderSource));
  }

  @SuppressWarnings({ "nls" })
  @Test
  public void creatorBuilder() throws CreationException, IOException {
    final BeanGenerator generator = new BeanGenerator(this.annotationClassfactory);
    final BeanBuilder builder = bean("net.anwiba.test.Bean");
    builder.member(member(type(String.class.getName()).build(), "type").value("Bean").isSetterEnabled(false).build());
    builder.creator(
        creator("create").addArgument(type("java.lang.String").build(), "type", new ArrayList<Annotation>()).build());
    generator.add(builder.build());
    final ByteArrayOutputStream ouputStream = new ByteArrayOutputStream();
    generator.generate(ouputStream);
    assertThat(ouputStream.size(), greaterThan(0));
    assertThat(ouputStream.toString(), equalTo(BeanGeneratorTestResources.creatorBeanSource));
  }
}
