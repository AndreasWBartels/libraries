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
package net.anwiba.tools.graphml.io;

import java.io.Closeable;
import java.io.IOException;
import java.io.Writer;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import net.anwiba.tools.simple.graphml.generated.Edge;
import net.anwiba.tools.simple.graphml.generated.Graph;
import net.anwiba.tools.simple.graphml.generated.GraphMl;
import net.anwiba.tools.simple.graphml.generated.Key;
import net.anwiba.tools.simple.graphml.generated.Node;

public class GraphMarkupLanguageWriter implements Closeable {

  private final Writer writer;
  private final GraphMl root;

  public GraphMarkupLanguageWriter(final Writer writer) {
    final Graph graph = new Graph();
    graph.setId("G"); //$NON-NLS-1$
    graph.setEdgedefault("directed"); //$NON-NLS-1$
    this.writer = writer;
    this.root = new GraphMl();
    this.root.setGraph(graph);
  }

  public void set(final Graph graph) {
    this.root.setGraph(graph);
  }

  public void add(final Key key) {
    this.root.getKey().add(key);
  }

  public void add(final Node node) {
    this.root.getGraph().getNode().add(node);
  }

  public void add(final Edge edge) {
    this.root.getGraph().getEdge().add(edge);
  }

  @Override
  public void close() throws IOException {
    try {
      final JAXBContext jaxbContext = JAXBContext.newInstance(
          net.anwiba.tools.simple.graphml.generated.ObjectFactory.class,
          net.anwiba.tools.yworks.shapenode.generated.ObjectFactory.class,
          net.anwiba.tools.yworks.labels.generated.ObjectFactory.class);
      final Marshaller marshaller = jaxbContext.createMarshaller();
      marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
      marshaller.marshal(this.root, this.writer);
    } catch (final JAXBException exception) {
      throw new IOException(exception);
    } finally {
      if (this.writer != null) {
        this.writer.close();
      }
    }
  }
}