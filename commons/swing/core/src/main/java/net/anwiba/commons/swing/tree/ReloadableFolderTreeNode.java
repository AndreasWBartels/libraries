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
import java.util.Vector;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;

import net.anwiba.commons.lang.functional.IFactory;
import net.anwiba.commons.logging.ILevel;

public class ReloadableFolderTreeNode<T> extends DefaultMutableTreeNode {

  private static final long serialVersionUID = 1L;
  private static net.anwiba.commons.logging.ILogger logger =
      net.anwiba.commons.logging.Logging.getLogger(ReloadableFolderTreeNode.class.getName());
  private boolean isInitialize = false;
  private final IFactory<T, List<DefaultMutableTreeNode>, RuntimeException> initializer;

  public ReloadableFolderTreeNode(
    final IFactory<T, List<DefaultMutableTreeNode>, RuntimeException> initializer,
    final T userObject) {
    super(userObject);
    this.initializer = initializer;
  }

  @Override
  public void add(final MutableTreeNode newChild) {
    this.isInitialize = true;
    super.add(newChild);
  }

  public boolean isInitialize() {
    return this.isInitialize;
  }

  public void reset() {
    if (!this.isInitialize) {
      return;
    }
    this.children = null;
    this.isInitialize = false;
  }

  public void load(final DefaultTreeModel treeModel) {
    if (isInitialize(true)) {
      return;
    }
    SwingWorker<List<DefaultMutableTreeNode>, Void> worker = new SwingWorker<>() {
      @SuppressWarnings("unchecked")
      @Override
      protected List<DefaultMutableTreeNode> doInBackground() throws Exception {
        return ReloadableFolderTreeNode.this.initializer.create((T) getUserObject());
      }

      @Override
      protected void done() {
        try {
          ReloadableFolderTreeNode.this.children = new Vector<>(get());
          treeModel.nodeStructureChanged(ReloadableFolderTreeNode.this);
          super.done();
        } catch (InterruptedException exception) {
          super.done();
        } catch (ExecutionException exception) {
          logger.log(ILevel.WARNING, exception.getMessage(), exception);
          super.done();
        }
      }
    };
    worker.execute();
  }

  private synchronized boolean isInitialize(final boolean value) {
    boolean initialize = this.isInitialize;
    this.isInitialize = value;
    return initialize;
  }
}
