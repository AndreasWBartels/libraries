/*
 * #%L
 * anwiba eclipse java tools
 * %%
 * Copyright (C) 2007 - 2022 Andreas Bartels
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
package net.anwiba.eclipse.icons.view;

import net.anwiba.commons.lang.object.IObjectReceiver;
import net.anwiba.commons.model.IObjectModel;
import net.anwiba.commons.model.ObjectModel;
import net.anwiba.eclipse.icons.description.IGuiIconDescription;
import net.anwiba.eclipse.icons.table.TableViewerFactory;
import net.anwiba.eclipse.icons.view.action.CopyAction;
import net.anwiba.eclipse.icons.view.action.ToggleEnabledAction;
import net.anwiba.eclipse.icons.view.listener.GuiIconDragListener;
import net.anwiba.eclipse.icons.view.listener.ViewSiteListener;

import java.util.ArrayList;

import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.progress.IProgressService;

public class GuiIconsView extends ViewPart {

  private final WritableList<IGuiIconDescription> descriptions =
      new WritableList<>(new ArrayList<IGuiIconDescription>(), IGuiIconDescription.class);
  private ViewSiteListener listener;
  private TableViewer viewer;

  public GuiIconsView() {
    super();
  }

  @Override
  public void setFocus() {
    this.viewer.getControl().setFocus();
  }

  @Override
  public void createPartControl(final Composite parent) {
    final Composite composite = createComposite(parent, GridData.FILL_BOTH, 1, true);
    final Composite searchComposite = createComposite(composite, GridData.FILL_HORIZONTAL, 2, false);

    final IObjectModel<String> searchStringModel = new ObjectModel<>();

    final Label searchLabel = new Label(searchComposite, SWT.NONE);
    searchLabel.setText("Search: ");
    final Text searchText = new Text(searchComposite, SWT.BORDER | SWT.SEARCH);
    searchText.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));
    searchText.addKeyListener(new KeyAdapter() {
      @Override
      public void keyReleased(final KeyEvent event) {
        searchStringModel.set(searchText.getText());
      }
    });
    this.viewer = new TableViewerFactory().createTable(composite, this.descriptions, searchStringModel);
    if (this.listener == null) {
      final IProgressService progressService = getViewSite().getWorkbenchWindow().getWorkbench().getProgressService();
      this.listener = new ViewSiteListener(parent.getDisplay(), progressService, this.descriptions);
      final int operations = DND.DROP_COPY;
      final Transfer[] transferTypes = new Transfer[] { TextTransfer.getInstance() };
      this.viewer.addDragSupport(operations, transferTypes, new GuiIconDragListener(this.viewer));
    }
    initActionBar(parent.getDisplay(), this.viewer, this.listener);
    final IObjectReceiver<IGuiIconDescription> messageReciever = createMessageReceiver(composite);
    this.viewer.addSelectionChangedListener(new ISelectionChangedListener() {

      @Override
      public void selectionChanged(final SelectionChangedEvent event) {
        final ISelection selection = event.getSelection();
        if (selection.isEmpty()) {
          messageReciever.set(null);
        }
        final IStructuredSelection structuredSelection = (IStructuredSelection) selection;
        messageReciever.set((IGuiIconDescription) structuredSelection.getFirstElement());
      }
    });
    getViewSite().getPage().addSelectionListener(this.listener);
  }

  private Label createLabel(final Composite composite) {
    final Label label = new Label(composite, SWT.SINGLE);
    label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    return label;
  }

  private IObjectReceiver<IGuiIconDescription> createMessageReceiver(final Composite composite) {
    final IActionBars bars = getViewSite().getActionBars();
    final Label messageLabel = createLabel(composite);
    final IStatusLineManager statusLineManager = bars.getStatusLineManager();
    return new IObjectReceiver<IGuiIconDescription>() {

      @Override
      public void set(final IGuiIconDescription description) {
        final String message = description == null
            ? "" //$NON-NLS-1$
            : description.getConstant().getName();
        messageLabel.setText(message);
        statusLineManager.setMessage(message);
      }
    };
  }

  private Composite createComposite(
      final Composite parent,
      final int style,
      final int numColumns,
      final boolean makeColumnsEqualWidth) {
    final Composite composite = new Composite(parent, SWT.NONE);
    composite.setLayout(createLayout(numColumns, makeColumnsEqualWidth));
    composite.setLayoutData(new GridData(style));
    return composite;
  }

  private GridLayout createLayout(final int numColumns, final boolean makeColumnsEqualWidth) {
    final GridLayout gridLayout = new GridLayout(numColumns, makeColumnsEqualWidth);
    gridLayout.horizontalSpacing = 0;
    gridLayout.verticalSpacing = 0;
    gridLayout.marginWidth = 0;
    gridLayout.marginHeight = 0;
    return gridLayout;
  }

  private void initActionBar(
      final Display display,
      final ISelectionProvider selectionProvider,
      final ViewSiteListener listener) {
    final IActionBars bars = getViewSite().getActionBars();
    final IToolBarManager toolBarManager = bars.getToolBarManager();
    toolBarManager.add(new CopyAction(display, selectionProvider));
    toolBarManager.add(new ToggleEnabledAction(listener));
  }

  @Override
  public void dispose() {
    if (this.listener != null) {
      getViewSite().getPage().removeSelectionListener(this.listener);
    }
    for (final Object object : this.descriptions) {
      final IGuiIconDescription description = (IGuiIconDescription) object;
      description.dispose();
    }
    this.descriptions.clear();
    super.dispose();
  }
}
