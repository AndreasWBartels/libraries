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
package net.anwiba.commons.xml.jaxb;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

public class JaxbContextBuilder {

////(1)
//TransformerFactory tf = TransformerFactory.newInstance();
//tf.setAttribute("indent-number", new Integer(2));
//
////(2)
//Transformer t = tf.newTransformer();
//t.setOutputProperty(OutputKeys.INDENT, "yes");
//
////(3)
//t.transform(new DOMSource(doc),
//new StreamResult(new OutputStreamWriter(out, "utf-8"));

  public static final class JaxbContext implements IJaxbContext {
    private final List<Class<?>> objectFactories;
    private final List<Source> schemaSources;
    private final ClassLoader classLoader;

    public JaxbContext(
        final List<Class<?>> objectFactories,
        final List<Source> schemaSources,
        final ClassLoader classLoader) {
      this.objectFactories = objectFactories;
      this.schemaSources = schemaSources;
      this.classLoader = classLoader;
    }

    @Override
    public ClassLoader getClassLoader() {
      return this.classLoader;
    }

    @Override
    public Class<?>[] getObjectFactories() {
      return this.objectFactories.toArray(new Class<?>[this.objectFactories.size()]);
    }

    @Override
    public Source[] getSchemaSources() {
      return this.schemaSources.toArray(new Source[this.schemaSources.size()]);
    }
  }

  ClassLoader classLoader = getClass().getClassLoader();
  List<Class<?>> objectFactories = new ArrayList<>();
  List<Source> schemaSources = new ArrayList<>();

  public JaxbContextBuilder add(final Class<?> objectFactory, final String schemaSource) {
    add(objectFactory, getSourceSchema(objectFactory, schemaSource));
    return this;
  }

  public JaxbContextBuilder add(final Class<?> objectFactory, final Source schemaSource) {
    this.objectFactories.add(objectFactory);
    this.schemaSources.add(schemaSource);
    return this;
  }

  public void setClassLoader(final ClassLoader classLoader) {
    this.classLoader = classLoader;
  }

  private Source getSourceSchema(final Class<?> objectFactory, final String filename) {
    if (filename == null) {
      throw new IllegalArgumentException();
    }
    final URL resource = objectFactory.getResource(filename);
    return new StreamSource(resource.toString());
  }

  public IJaxbContext build() {
    return new JaxbContext(this.objectFactories, this.schemaSources, this.classLoader);
  }
}