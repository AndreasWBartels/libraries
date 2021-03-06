/*
 * #%L
 * anwiba commons advanced
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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import net.anwiba.commons.lang.exception.CreationException;
import net.anwiba.commons.logging.LoggingUtilities;
import net.anwiba.tools.definition.schema.json.gramma.parser.JssdParserException;

public class JsonBeanGeneratorTest {

  private static final String NET_ANWIBA_GENERATED_TEST_BEAN = "net.anwiba.generated.test.bean";
  private static final String COPYRIGHT = "";

  @BeforeAll
  static public void initialization() {
    LoggingUtilities.initialize();
  }

  @Test
  public void defaultValueBean() throws CreationException, JssdParserException, IOException {
    final JsonBeanGenerator generator = new JsonBeanGenerator(NET_ANWIBA_GENERATED_TEST_BEAN, COPYRIGHT, false);
    generator
        .add(
            new ByteArrayInputStream(JsonBeanGeneratorTestResources.defaultValueBeanDefinition.getBytes()),
            "net.anwiba.generated.test.bean.DefaultValue");
    final ByteArrayOutputStream ouputStream = new ByteArrayOutputStream();
    generator.generate(ouputStream);
    assertThat(ouputStream.size(), greaterThan(0));
    final String actual = ouputStream.toString();
    final String expected = JsonBeanGeneratorTestResources.defaultValueBeanSource;
    assertThat(actual, equalTo(expected));
  }

  @Test
  public void typeInfoFactoryBean() throws CreationException, JssdParserException, IOException {
    final JsonBeanGenerator generator = new JsonBeanGenerator(NET_ANWIBA_GENERATED_TEST_BEAN, COPYRIGHT, false);
    generator
        .add(
            new ByteArrayInputStream(JsonBeanGeneratorTestResources.typeInfoFactoryBeanDefinition.getBytes()),
            "net.anwiba.generated.test.bean.Factory");
    final ByteArrayOutputStream ouputStream = new ByteArrayOutputStream();
    generator.generate(ouputStream);
    assertThat(ouputStream.size(), greaterThan(0));
    final String actual = ouputStream.toString();
    final String expected = JsonBeanGeneratorTestResources.typeInfoFactoryBeanSource;
    assertThat(actual, equalTo(expected));
  }

  @Test
  public void typeInfoWithNameFactoryBean() throws CreationException, JssdParserException, IOException {
    final JsonBeanGenerator generator = new JsonBeanGenerator(NET_ANWIBA_GENERATED_TEST_BEAN, COPYRIGHT, false);
    generator
        .add(
            new ByteArrayInputStream(JsonBeanGeneratorTestResources.typeInfoWithNameFactoryBeanDefinition.getBytes()),
            "net.anwiba.generated.test.bean.Factory");
    final ByteArrayOutputStream ouputStream = new ByteArrayOutputStream();
    generator.generate(ouputStream);
    assertThat(ouputStream.size(), greaterThan(0));
    final String actual = ouputStream.toString();
    final String expected = JsonBeanGeneratorTestResources.typeInfoWithNameFactoryBeanSource;
    assertThat(actual, equalTo(expected));
  }

  @Test
  public void valueBean() throws CreationException, JssdParserException, IOException {
    final JsonBeanGenerator generator = new JsonBeanGenerator(NET_ANWIBA_GENERATED_TEST_BEAN, COPYRIGHT, false);
    generator
        .add(
            new ByteArrayInputStream(JsonBeanGeneratorTestResources.valueBeanDefinition.getBytes()),
            "net.anwiba.generated.test.bean.Factory");
    final ByteArrayOutputStream ouputStream = new ByteArrayOutputStream();
    generator.generate(ouputStream);
    assertThat(ouputStream.size(), greaterThan(0));
    final String actual = ouputStream.toString();
    final String expected = JsonBeanGeneratorTestResources.valueBeanSource;
    assertThat(actual, equalTo(expected));
  }

  @Test
  public void patternBean() throws CreationException, JssdParserException, IOException {
    final JsonBeanGenerator generator = new JsonBeanGenerator(NET_ANWIBA_GENERATED_TEST_BEAN, COPYRIGHT, false);
    generator
        .add(
            new ByteArrayInputStream(JsonBeanGeneratorTestResources.patternBeanDefinition.getBytes()),
            "net.anwiba.generated.test.bean.Factory");
    final ByteArrayOutputStream ouputStream = new ByteArrayOutputStream();
    generator.generate(ouputStream);
    assertThat(ouputStream.size(), greaterThan(0));
    final String actual = ouputStream.toString();
    final String expected = JsonBeanGeneratorTestResources.patternBeanSource;
    assertThat(actual, equalTo(expected));
  }

  @Test
  public void reflactionFactoryWithConfiguredArgumentNameBuilder()
      throws CreationException,
      JssdParserException,
      IOException {
    final JsonBeanGenerator generator = new JsonBeanGenerator(NET_ANWIBA_GENERATED_TEST_BEAN, COPYRIGHT, false);
    generator
        .add(
            new ByteArrayInputStream(
                JsonBeanGeneratorTestResources.reflactionFactoryWithConfiguredArgumentNameBeanDefinition.getBytes()),
            "net.anwiba.generated.test.bean.Factory");
    final ByteArrayOutputStream ouputStream = new ByteArrayOutputStream();
    generator.generate(ouputStream);
    assertThat(ouputStream.size(), greaterThan(0));
    assertThat(
        ouputStream.toString(),
        equalTo(JsonBeanGeneratorTestResources.reflactionFactoryWithConfiguredArgumentNameBeanSource));
  }

  @Test
  public void classMemberBuilder() throws CreationException, JssdParserException, IOException {
    final JsonBeanGenerator generator = new JsonBeanGenerator(NET_ANWIBA_GENERATED_TEST_BEAN, COPYRIGHT, false);
    final String classMemberBeanDefinition = JsonBeanGeneratorTestResources.classMemberBeanDefinition;
    generator
        .add(
            new ByteArrayInputStream(classMemberBeanDefinition.getBytes()),
            "net.anwiba.generated.test.bean.MemberClass");
    final ByteArrayOutputStream ouputStream = new ByteArrayOutputStream();
    generator.generate(ouputStream);
    assertThat(ouputStream.size(), greaterThan(0));
    final String string = ouputStream.toString();
    assertThat(string, equalTo(JsonBeanGeneratorTestResources.classMemberBeanSource));
  }

  @Test
  public void errorBeanAndBuilder() throws CreationException, IOException, JssdParserException {
    final JsonBeanGenerator generator = new JsonBeanGenerator(NET_ANWIBA_GENERATED_TEST_BEAN, COPYRIGHT, true);
    generator
        .add(
            new ByteArrayInputStream(JsonBeanGeneratorTestResources.errorBeanDefinition.getBytes()),
            "net.anwiba.generated.test.bean.Error");
    final ByteArrayOutputStream ouputStream = new ByteArrayOutputStream();
    generator.generate(ouputStream);
    assertThat(ouputStream.size(), greaterThan(0));
    assertThat(ouputStream.toString(), equalTo(JsonBeanGeneratorTestResources.errorBeanAndBuilderSource));
  }

  @Test
  public void errorBean() throws CreationException, IOException, JssdParserException {
    final JsonBeanGenerator generator = new JsonBeanGenerator(NET_ANWIBA_GENERATED_TEST_BEAN, COPYRIGHT, false);
    generator
        .add(
            new ByteArrayInputStream(JsonBeanGeneratorTestResources.errorBeanDefinition.getBytes()),
            "net.anwiba.generated.test.bean.Error");
    final ByteArrayOutputStream ouputStream = new ByteArrayOutputStream();
    generator.generate(ouputStream);
    assertThat(ouputStream.size(), greaterThan(0));
    assertThat(ouputStream.toString(), equalTo(JsonBeanGeneratorTestResources.errorBeanSource));
  }

  @Test
  public void featureBean() throws CreationException, IOException, JssdParserException {
    final JsonBeanGenerator generator = new JsonBeanGenerator(NET_ANWIBA_GENERATED_TEST_BEAN, COPYRIGHT, false);
    generator
        .add(
            new ByteArrayInputStream(JsonBeanGeneratorTestResources.envelopeBeanDefinition.getBytes()),
            "net.anwiba.generated.test.bean.Envelope");
    generator
        .add(
            new ByteArrayInputStream(JsonBeanGeneratorTestResources.featureBeanDefinition.getBytes()),
            "net.anwiba.generated.test.bean.Feature");
    final ByteArrayOutputStream ouputStream = new ByteArrayOutputStream();
    generator.generate(ouputStream);
    assertThat(ouputStream.size(), greaterThan(0));
    assertThat(ouputStream.toString(), equalTo(JsonBeanGeneratorTestResources.featureBeanSource));
  }

  @Test
  public void entityBean() throws CreationException, IOException, JssdParserException {
    final JsonBeanGenerator generator = new JsonBeanGenerator(NET_ANWIBA_GENERATED_TEST_BEAN, COPYRIGHT, false);
    generator
        .add(
            new ByteArrayInputStream(JsonBeanGeneratorTestResources.entityBeanDefinition.getBytes()),
            "net.anwiba.generated.test.bean.Entity");
    final ByteArrayOutputStream ouputStream = new ByteArrayOutputStream();
    generator.generate(ouputStream);
    assertThat(ouputStream.size(), greaterThan(0));
    assertThat(ouputStream.toString(), equalTo(JsonBeanGeneratorTestResources.entityBeanSource));
  }

  @Test
  public void factoryBean() throws CreationException, IOException, JssdParserException {
    final JsonBeanGenerator generator = new JsonBeanGenerator(NET_ANWIBA_GENERATED_TEST_BEAN, COPYRIGHT, false);
    generator
        .add(
            new ByteArrayInputStream(JsonBeanGeneratorTestResources.factoryBeanDefinition.getBytes()),
            "net.anwiba.generated.test.bean.Factory");
    final ByteArrayOutputStream ouputStream = new ByteArrayOutputStream();
    generator.generate(ouputStream);
    assertThat(ouputStream.size(), greaterThan(0));
    assertThat(ouputStream.toString(), equalTo(JsonBeanGeneratorTestResources.factoryBeanSource));
  }

  @Test
  public void factoryWithUnknownMembersBean() throws CreationException, IOException, JssdParserException {
    final JsonBeanGenerator generator = new JsonBeanGenerator(NET_ANWIBA_GENERATED_TEST_BEAN, COPYRIGHT, false);
    generator
        .add(
            new ByteArrayInputStream(JsonBeanGeneratorTestResources.factoryWithUnknownMembersBeanDefinition.getBytes()),
            "net.anwiba.generated.test.bean.Factory");
    final ByteArrayOutputStream ouputStream = new ByteArrayOutputStream();
    generator.generate(ouputStream);
    assertThat(ouputStream.size(), greaterThan(0));
    assertThat(ouputStream.toString(), equalTo(JsonBeanGeneratorTestResources.factoryWithUnknownMembersBeanSource));
  }

  @Test
  public void factoryWithIgnoreUnknownMembersBean() throws CreationException, IOException, JssdParserException {
    final JsonBeanGenerator generator = new JsonBeanGenerator(NET_ANWIBA_GENERATED_TEST_BEAN, COPYRIGHT, false);
    generator
        .add(
            new ByteArrayInputStream(
                JsonBeanGeneratorTestResources.factoryWithIgnoreUnknownMembersBeanDefinition.getBytes()),
            "net.anwiba.generated.test.bean.Factory");
    final ByteArrayOutputStream ouputStream = new ByteArrayOutputStream();
    generator.generate(ouputStream);
    assertThat(ouputStream.size(), greaterThan(0));
    assertThat(
        ouputStream.toString(),
        equalTo(JsonBeanGeneratorTestResources.factoryWithIgnoreUnknownMembersBeanSource));
  }

  @Test
  public void injectTypeFactoryBean() throws CreationException, IOException, JssdParserException {
    final JsonBeanGenerator generator = new JsonBeanGenerator(NET_ANWIBA_GENERATED_TEST_BEAN, COPYRIGHT, false);
    generator
        .add(
            new ByteArrayInputStream(JsonBeanGeneratorTestResources.injectTypeFactoryBeanDefinition.getBytes()),
            "net.anwiba.generated.test.bean.Factory");
    final ByteArrayOutputStream ouputStream = new ByteArrayOutputStream();
    generator.generate(ouputStream);
    assertThat(ouputStream.size(), greaterThan(0));
    assertThat(ouputStream.toString(), equalTo(JsonBeanGeneratorTestResources.injectTypeFactoryBeanSource));
  }

  @Test
  public void complexFactoryBean() throws CreationException, IOException, JssdParserException {
    final JsonBeanGenerator generator = new JsonBeanGenerator(NET_ANWIBA_GENERATED_TEST_BEAN, COPYRIGHT, false);
    generator
        .add(
            new ByteArrayInputStream(JsonBeanGeneratorTestResources.complexFactoryBeanDefinition.getBytes()),
            "net.anwiba.generated.test.bean.Factory");
    final ByteArrayOutputStream ouputStream = new ByteArrayOutputStream();
    generator.generate(ouputStream);
    assertThat(ouputStream.size(), greaterThan(0));
    assertThat(ouputStream.toString(), equalTo(JsonBeanGeneratorTestResources.complexFactoryBeanSource));
  }

  @Test
  public void pointBean() throws CreationException, IOException, JssdParserException {
    final JsonBeanGenerator generator = new JsonBeanGenerator(NET_ANWIBA_GENERATED_TEST_BEAN, COPYRIGHT, false);
    generator
        .add(
            new ByteArrayInputStream(JsonBeanGeneratorTestResources.geometryBeanDefinition.getBytes()),
            "net.anwiba.generated.test.bean.Geometry");
    generator
        .add(
            new ByteArrayInputStream(JsonBeanGeneratorTestResources.pointBeanDefinition.getBytes()),
            "net.anwiba.generated.test.bean.Point");
    generator
        .add(
            new ByteArrayInputStream(JsonBeanGeneratorTestResources.spatialReferenceBeanDefinition.getBytes()),
            "net.anwiba.generated.test.bean.SpatialReference");
    final ByteArrayOutputStream ouputStream = new ByteArrayOutputStream();
    generator.generate(ouputStream);
    assertThat(ouputStream.size(), greaterThan(0));
    assertThat(ouputStream.toString(), equalTo(JsonBeanGeneratorTestResources.pointBeanSource));
  }

  @Test
  public void moduleBean() throws CreationException, IOException, JssdParserException {
    final JsonBeanGenerator generator = new JsonBeanGenerator(NET_ANWIBA_GENERATED_TEST_BEAN, COPYRIGHT, false);
    generator.add(new ByteArrayInputStream(JsonBeanGeneratorTestResources.moduleBeanDefinition.getBytes()), "module");
    final ByteArrayOutputStream ouputStream = new ByteArrayOutputStream();
    generator.generate(ouputStream);
    assertThat(ouputStream.size(), greaterThan(0));
    assertThat(ouputStream.toString(), equalTo(JsonBeanGeneratorTestResources.moduleBeanSource));
  }
}
