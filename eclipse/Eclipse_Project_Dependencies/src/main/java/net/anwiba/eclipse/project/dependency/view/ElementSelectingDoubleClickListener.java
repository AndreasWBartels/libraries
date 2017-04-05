package net.anwiba.eclipse.project.dependency.view;

import net.anwiba.eclipse.project.dependency.java.IItem;
import net.anwiba.eclipse.project.dependency.java.IPackage;
import net.anwiba.eclipse.project.dependency.java.IProject;
import net.anwiba.eclipse.project.dependency.java.IType;
import net.anwiba.eclipse.project.dependency.object.IDependencyRelation;

import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IViewPart;

public final class ElementSelectingDoubleClickListener implements IDoubleClickListener {
  private final IViewPart view;

  public ElementSelectingDoubleClickListener(final IViewPart view) {
    this.view = view;
  }

  @Override
  public void doubleClick(final DoubleClickEvent event) {
    final ISelection selection = event.getSelection();
    if (selection.isEmpty()
        || !(selection instanceof StructuredSelection || ((StructuredSelection) selection).size() != 1)) {
      return;
    }
    final StructuredSelection structuredSelection = (StructuredSelection) selection;
    final Object firstElement = structuredSelection.getFirstElement();
    if ((firstElement instanceof IDependencyRelation)) {
      select(((IDependencyRelation) firstElement).getItem());
      return;
    }
    if ((firstElement instanceof IType)) {
      select(((IType) firstElement));
      return;
    }
  }

  private void select(final IItem item) {
    if (item instanceof IProject) {
      select(WorkspaceUtilities.getProjects((IProject) item).stream().collect(Collectors.toList()));
      return;
    }
    if (item instanceof IType) {
      final IType type = (IType) item;
      final List<Object> eclipseTypes = WorkspaceUtilities.getTypes(type);
      select(eclipseTypes.stream().collect(Collectors.toList()));
      return;
    }
    if (item instanceof IPackage) {
      final IPackage paccage = (IPackage) item;
      final List<Object> eclipseTypes = WorkspaceUtilities.getPackages(paccage);
      select(eclipseTypes.stream().collect(Collectors.toList()));
      return;
    }
  }

  private void select(final List<Object> openProjects) {
    if (openProjects.isEmpty()) {
      return;
    }
    this.view.getSite().getSelectionProvider().setSelection(new StructuredSelection(openProjects.toArray()));
  }
}