package net.anwiba.eclipse.project.dependency.graph;

import net.anwiba.commons.lang.exception.CanceledException;
import net.anwiba.commons.thread.program.ProgramLauncher;
import net.anwiba.eclipse.project.dependency.java.LibraryType;
import net.anwiba.tools.graphml.io.GraphMarkupLanguageWriter;
import net.anwiba.tools.graphml.utilities.GraphUtilities;
import net.anwiba.tools.simple.graphml.generated.Data;
import net.anwiba.tools.simple.graphml.generated.Edge;
import net.anwiba.tools.simple.graphml.generated.Graph;
import net.anwiba.tools.simple.graphml.generated.Key;
import net.anwiba.tools.simple.graphml.generated.Node;
import net.anwiba.tools.yworks.shapenode.generated.Fill;
import net.anwiba.tools.yworks.shapenode.generated.NodeLabel;
import net.anwiba.tools.yworks.shapenode.generated.Shape;
import net.anwiba.tools.yworks.shapenode.generated.ShapeNode;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class GraphmlUtilities {

  public static Key createEdgeKey(
      final String keyName,
      final String attrName,
      final String attrType,
      final String yfileType) {
    return createKey("edge", keyName, attrName, attrType, yfileType);
  }

  public static Key createKey(
      final String fcr,
      final String keyName,
      final String attrName,
      final String attrType,
      final String yfileType) {
    final Key key = new Key();
    key.setId(keyName);
    key.setFor(fcr);
    key.setAttrName(attrName);
    key.setAttrType(attrType);
    key.setYfilesType(yfileType);
    return key;
  }

  public static Key createNodeKey(
      final String keyName,
      final String attrName,
      final String attrType,
      final String yfileType) {
    return createKey("node", keyName, attrName, attrType, yfileType);
  }

  public static Object createShapeNode(final String string, final LibraryType libraryType) {
    final NodeLabel label = new NodeLabel();
    label.setModelName("eight_pos");
    label.setModelPosition("e");
    final Fill fill = new Fill();
    final Shape shape = new Shape();
    label.setContent(string.substring(string.lastIndexOf("/") + 1));
    switch (libraryType) {
      case JAR: {
        shape.setType("rectangle");
        fill.setColor("#ff9900");
        break;
      }
      case PROJECT: {
        shape.setType("roundrectangle");
        fill.setColor("#339966");
        break;
      }
    }
    final ShapeNode shapeNode = new ShapeNode();
    shapeNode.setFill(fill);
    shapeNode.setNodeLabel(label);
    shapeNode.setShape(shape);
    return shapeNode;
  }

  public static Data createData(final String keyName, final Object value) {
    final Data data = new Data();
    data.setKey(keyName);
    data.getContent().add(value);
    return data;
  }

  public static Graph createGraph(final LibraryGraph libraryGraph, final boolean isNormalizeEnabled) {
    final Map<String, List<String>> graphMap = isNormalizeEnabled
        ? GraphUtilities.normalize(libraryGraph.getGraph())
        : libraryGraph.getGraph();
    // final Map<String, List<String>> graphMap = this.graph;
    final Graph graph = new Graph();
    graph.setId("G"); //$NON-NLS-1$
    graph.setEdgedefault("directed"); //$NON-NLS-1$
    final Set<String> keysset = graphMap.keySet();
    final String[] keys = keysset.toArray(new String[keysset.size()]);
    Arrays.sort(keys);
    for (final String key : keys) {
      final Node node = new Node();
      node.setId(key);
      graph.getNode().add(node);
      final String label = libraryGraph.getLabel(key);
      node.getData().add(createData("d4", label)); //$NON-NLS-1$
      node.getData().add(createData("d5", createShapeNode(label, libraryGraph.getType(key)))); //$NON-NLS-1$
    }
    for (final String key : keys) {
      if (!graphMap.containsKey(key)) {
        continue;
      }
      final List<String> nodes = graphMap.get(key);
      for (final String node : nodes) {
        final Edge edge = new Edge();
        edge.setSource(key);
        edge.setTarget(node);
        graph.getEdge().add(edge);
      }
    }
    return graph;
  }

  public static void saveAndLoad(final File file, final Graph graph) throws IOException {
    try (GraphMarkupLanguageWriter writer = new GraphMarkupLanguageWriter(new FileWriter(file))) {
      if (!file.exists()) {
        file.createNewFile();
      }
      writer.add(createKey("port", "d0", null, null, "portgraphics")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
      writer.add(createKey("port", "d1", null, null, "portgeometry")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
      writer.add(createKey("port", "d2", null, null, "portuserdata")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
      writer.add(createNodeKey("d3", "url", "string", null)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
      writer.add(createNodeKey("d4", "description", "string", null)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
      writer.add(createNodeKey("d5", null, null, "nodegraphics")); //$NON-NLS-1$ //$NON-NLS-2$
      writer.add(createKey("graphml", "d6", null, null, "nodegraphics")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
      writer.add(createNodeKey("d7", "url", "string", null)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
      writer.add(createEdgeKey("d8", "description", "string", null)); //$NON-NLS-1$ //$NON-NLS-2$
      writer.add(createEdgeKey("d9", null, null, "edgegraphics")); //$NON-NLS-1$ //$NON-NLS-2$
      writer.set(graph);
      final Properties properties = System.getProperties();
      if (properties.getProperty("net.anwiba.eclipse.project.dependency.yed.jar") != null) {
        final String command = //
            System.getProperty("java.home") //$NON-NLS-1$
                + File.separator + "bin" //$NON-NLS-1$
                + File.separator + "java" //$NON-NLS-1$
        ;
        new ProgramLauncher() //
            .command(command)
            .argument("-jar")
            .argument(properties.getProperty("net.anwiba.eclipse.project.dependency.yed.jar"))
            .argument(file.getAbsolutePath())
            .launch();
      }
    } catch (final CanceledException exception) {
      // nothing to do
    }
  }

}
