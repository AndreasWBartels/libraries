/*
 * #%L
 *
 * %%
 * Copyright (C) 2007 - 2017 Andreas W. Bartels
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
package net.anwiba.commons.swing.tree;

import java.util.List;
import java.util.Objects;
import java.util.Vector;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.SwingWorker;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;

import net.anwiba.commons.lang.functional.IFactory;
import net.anwiba.commons.logging.ILevel;
import net.anwiba.commons.swing.utilities.GuiUtilities;

public class ReloadableFolderTreeNode<T> extends DefaultMutableTreeNode {

  public enum State {
    UNKNOWN,
    LOADING,
    INITALIZED,
    FAILED
  }

  private static final long serialVersionUID = 1L;
  private static net.anwiba.commons.logging.ILogger logger =
      net.anwiba.commons.logging.Logging.getLogger(ReloadableFolderTreeNode.class.getName());

  private final AtomicReference<State> state = new AtomicReference<>(State.UNKNOWN);
  private final IFactory<T, List<DefaultMutableTreeNode>, RuntimeException> initializer;

  public ReloadableFolderTreeNode(
    final IFactory<T, List<DefaultMutableTreeNode>, RuntimeException> initializer,
    final T userObject) {
    super(userObject);
    this.initializer = initializer;
    this.children = new Vector<>();
  }


  @Override
  public boolean isLeaf() {
    return false;
  }
  
  @Override
  public void add(final MutableTreeNode newChild) {
    state.set(State.INITALIZED);
    super.add(newChild);
  }

  public boolean isInitialized() {
    return Objects.equals(state.get(), State.INITALIZED);
  }

  public boolean isLoading() {
    return Objects.equals(state.get(), State.LOADING);
  }

  public void load(final DefaultTreeModel treeModel) {
    if (isInitialized() || isLoading()) {
      return;
    }
    loadTo(treeModel, () -> {});
  }

  public void reload(final DefaultTreeModel treeModel, Runnable postProcess) {
    if (isLoading()) {
      return;
    }
    loadTo(treeModel, postProcess);
  }

  private void loadTo(final DefaultTreeModel treeModel, Runnable postProcess) {
    state.set(State.LOADING);
    SwingWorker<List<DefaultMutableTreeNode>, Void> worker = new SwingWorker<>() {
      @SuppressWarnings("unchecked")
      @Override
      protected List<DefaultMutableTreeNode> doInBackground() throws Exception {
        return ReloadableFolderTreeNode.this.initializer.create((T) getUserObject());
      }

      @Override
      protected void done() {
        try {
          List<DefaultMutableTreeNode> loadedNodes = get();
          if (loadedNodes == null) {
            state.set(State.UNKNOWN);
            super.done();
            return;
          }
          state.set(State.INITALIZED);
          GuiUtilities.invokeLater(() -> {
            List<MutableTreeNode> nodes = children.stream().map(n -> (MutableTreeNode)n).toList();
            for (MutableTreeNode defaultMutableTreeNode : nodes) {
              treeModel.removeNodeFromParent(defaultMutableTreeNode);
            }
            for (MutableTreeNode mutableTreeNode : loadedNodes) {
              treeModel.insertNodeInto(mutableTreeNode, ReloadableFolderTreeNode.this, children.size());
            }
            postProcess.run();
            treeModel.reload(ReloadableFolderTreeNode.this);
          });
          super.done();
        } catch (InterruptedException exception) {
          logger.log(ILevel.ERROR, exception.getMessage(), exception);
          state.set(State.UNKNOWN);
          super.done();
        } catch (ExecutionException exception) {
          logger.log(ILevel.WARNING, exception.getMessage(), exception);
          state.set(State.FAILED);
          super.done();
        }
      }
    };
    worker.execute();
  }

}
