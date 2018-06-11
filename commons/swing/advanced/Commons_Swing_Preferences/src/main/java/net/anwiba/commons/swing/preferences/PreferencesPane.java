/*
 * #%L
 * anwiba commons swing
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
package net.anwiba.commons.swing.preferences;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import net.anwiba.commons.lang.functional.IFunction;
import net.anwiba.commons.lang.object.IObjectContainer;
import net.anwiba.commons.lang.object.ObjectContainer;
import net.anwiba.commons.lang.optional.Optional;
import net.anwiba.commons.model.BooleanModel;
import net.anwiba.commons.model.IBooleanModel;
import net.anwiba.commons.model.IChangeableObjectListener;
import net.anwiba.commons.preferences.IPreferences;
import net.anwiba.commons.swing.action.ConfigurableActionBuilder;
import net.anwiba.commons.swing.component.IComponentProvider;
import net.anwiba.commons.swing.icons.gnome.contrast.high.ContrastHightIcons;
import net.anwiba.commons.swing.object.IObjectField;
import net.anwiba.commons.swing.object.StringFieldBuilder;
import net.anwiba.commons.swing.preference.SplitPanePreferenceUpdaterListener;
import net.anwiba.commons.swing.preferences.editor.IPreferenceNodeEditor;
import net.anwiba.commons.swing.preferences.editor.IPreferenceNodeEditorContext;
import net.anwiba.commons.swing.preferences.editor.IPreferenceNodeEditorFactory;
import net.anwiba.commons.swing.preferences.editor.IPreferenceNodeEditorFactoryRegistry;
import net.anwiba.commons.swing.preferences.editor.PreferenceNodeEditorContext;
import net.anwiba.commons.swing.preferences.tree.IPreferenceNode;
import net.anwiba.commons.swing.preferences.tree.PreferenceNode;
import net.anwiba.commons.swing.preferences.tree.PreferenceNodeTreeFilter;
import net.anwiba.commons.swing.tree.FilteredTreeModel;
import net.anwiba.commons.swing.utilities.GuiUtilities;
import net.anwiba.commons.swing.utilities.JTreeUtilities;

public class PreferencesPane implements IComponentProvider {

  public final class TreeModel extends FilteredTreeModel<PreferenceNode> {
    public TreeModel(final PreferenceNode rootNode) {
      super(rootNode);
    }

    public void removeFromParent(final PreferenceNode node) {
      final PreferenceNode parent = node.getParent();
      final int[] childIndex = new int[1];
      final Object[] removedArray = new Object[1];
      childIndex[0] = parent.getIndex(node);
      parent.remove(node);
      removedArray[0] = node;
      fireTreeNodesRemoved(parent, getPathToRoot(getRoot(), parent, 0), childIndex, removedArray);
    }
  }
  private JPanel contentPane;
  private final IPreferences panelPreferences;
  private final IPreferences contentPreferences;
  private final IPreferenceNodeEditorFactoryRegistry registry;
  final IObjectContainer<IPreferenceNodeEditor> editorContainer = new ObjectContainer<>();
  private final IFunction<IPreferenceNode, Boolean, RuntimeException> storeFunction;
  private final boolean isEditingEnabled;

  public PreferencesPane(
      final IPreferences preferences,
      final IPreferences contentPreferences,
      final boolean isEditingEnabled,
      final IPreferenceNodeEditorFactoryRegistry registry,
      final IFunction<IPreferenceNode, Boolean, RuntimeException> storeFunction) {
    this.panelPreferences = preferences;
    this.contentPreferences = contentPreferences;
    this.isEditingEnabled = isEditingEnabled;
    this.registry = registry;
    this.storeFunction = storeFunction;
  }

  @Override
  public JComponent getComponent() {
    if (this.contentPane == null) {
      this.contentPane = initialize();
    }
    return this.contentPane;
  }

  public JPanel initialize() {
    final JPanel preferenceEditorContainer = new JPanel(new GridLayout(1, 1));
    preferenceEditorContainer.setPreferredSize(new Dimension(300, 200));
    final TreeModel treeModel = new TreeModel(new PreferenceNode(null, this.contentPreferences));
    final JTree tree = new JTree(treeModel);
    @SuppressWarnings("hiding")
    final IPreferenceNodeEditorFactoryRegistry registry = this.registry;
    tree.setRootVisible(true);
    final TreeSelectionModel selectionModel = tree.getSelectionModel();
    selectionModel.setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
    final IObjectContainer<IPreferenceNodeEditor> preferenceNodeEditorContainer = this.editorContainer;
    @SuppressWarnings("hiding")
    final IFunction<IPreferenceNode, Boolean, RuntimeException> storeFunction = this.storeFunction;
    @SuppressWarnings("hiding")
    final boolean isEditingEnabled = this.isEditingEnabled;
    final IBooleanModel isNodeDeleteEnabledModel = new BooleanModel(false);

    selectionModel.addTreeSelectionListener(new TreeSelectionListener() {

      @Override
      public void valueChanged(final TreeSelectionEvent event) {
        final TreePath newLeadSelectionPath = event.getNewLeadSelectionPath();
        final PreferenceNode node = getPreferenceNode(newLeadSelectionPath);
        isNodeDeleteEnabledModel.set(isEditingEnabled && node != null && node.getParent() != null);
        final IPreferenceNodeEditorContext context = new PreferenceNodeEditorContext(isEditingEnabled, node);
        final IPreferenceNodeEditorFactory editorFactory = registry.get(context);
        @SuppressWarnings("hiding")
        final IObjectContainer<IPreferenceNodeEditor> editorContainer = preferenceNodeEditorContainer;
        final IPreferenceNodeEditor editor = editorContainer.get();
        editorContainer.set(editorFactory.create(context));
        GuiUtilities.invokeLater(new Runnable() {

          @Override
          public void run() {
            preferenceEditorContainer.removeAll();
            final IPreferenceNodeEditor preferenceNodeEditor = editorContainer.get();
            if (preferenceNodeEditor == null) {
              return;
            }
            preferenceEditorContainer.add(preferenceNodeEditor.getComponent());
            preferenceEditorContainer.validate();
            preferenceEditorContainer.repaint();
          }
        });
        if (editor != null) {
          storeFunction.execute(editor.getPreferenceNode());
        }
      }
    });
    final JScrollPane preferencesTreeScrollPane = new JScrollPane(tree);
    preferencesTreeScrollPane.setPreferredSize(new Dimension(150, 200));
    final JSplitPane splitPane = new JSplitPane(
        JSplitPane.HORIZONTAL_SPLIT,
        preferencesTreeScrollPane,
        preferenceEditorContainer);

    splitPane.setOneTouchExpandable(true);
    splitPane.setResizeWeight(1);
    SplitPanePreferenceUpdaterListener.connect(splitPane, this.panelPreferences);

    @SuppressWarnings("hiding")
    final JPanel contentPane = new JPanel(new BorderLayout());
    final JToolBar toolbar = new JToolBar();
    toolbar.setFloatable(false);
    final IObjectField<String> stringField = new StringFieldBuilder()
        .addClearAction(PreferencesPaneMessages.PreferencesPaneClearFilter)
        .build();

    stringField.getModel().addChangeListener(new IChangeableObjectListener() {

      @Override
      public void objectChanged() {
        treeModel.getFilterReceiver().set(new PreferenceNodeTreeFilter(stringField.getModel().get()));
        JTreeUtilities.expandAll(tree);
      }
    });
    toolbar.add(stringField.getComponent());
    toolbar.addSeparator();
    toolbar.add(
        new ConfigurableActionBuilder()//
            .setIcon(ContrastHightIcons.EDIT_DELETE)
            .setEnabledDistributor(isNodeDeleteEnabledModel)
            .setTooltip(PreferencesPaneMessages.PreferencesPaneRemoveSelectedPreference)
            .setTask(() -> {
              final PreferenceNode preferenceNode = getPreferenceNode(selectionModel.getLeadSelectionPath());
              Optional
                  .of(preferenceNode)
                  .convert(node -> node.getParent())
                  .accept(node -> node != treeModel.getRoot())
                  .consume(node -> selectionModel.setSelectionPath(getTreePath(treeModel.getRoot(), node)))
                  .or(() -> selectionModel.setSelectionPath(new TreePath(new PreferenceNode[]{ treeModel.getRoot() })));
              treeModel.removeFromParent(preferenceNode);
            })
            .build());
    contentPane.add(toolbar, BorderLayout.NORTH);
    contentPane.add(splitPane, BorderLayout.CENTER);
    return contentPane;
  }

  private TreePath getTreePath(final PreferenceNode root, final PreferenceNode node) {
    return new TreePath(getPathToRoot(root, node, 0));
  }

  public IPreferenceNode getPreferenceNode() {
    final IPreferenceNodeEditor editor = this.editorContainer.get();
    if (editor == null) {
      return null;
    }
    return editor.getPreferenceNode();
  }

  private PreferenceNode getPreferenceNode(final TreePath path) {
    if (path == null) {
      return null;
    }
    return (PreferenceNode) path.getLastPathComponent();
  }

  private PreferenceNode[] getPathToRoot(final PreferenceNode root, final PreferenceNode node, final int depth) {
    if (node == null) {
      return depth == 0 ? null : new PreferenceNode[depth];
    }
    final int step = depth + 1;
    PreferenceNode[] nodes = node == root
        ? nodes = new PreferenceNode[step]
        : getPathToRoot(root, node.getParent(), step);
    nodes[nodes.length - step] = node;
    return nodes;
  }

}
