package net.anwiba.eclipse.project.dependency.relation;

import net.anwiba.eclipse.project.dependency.java.IItem;
import net.anwiba.eclipse.project.dependency.java.ILibrary;
import net.anwiba.eclipse.project.dependency.java.IPackage;
import net.anwiba.eclipse.project.dependency.java.IType;
import net.anwiba.eclipse.project.dependency.object.IDependencyRelation;

public class CellValueFactory {

  public static String create(final IDependencyRelation description, final int columnIndex) {
    final IItem item = description.getItem();
    switch (columnIndex) {
    case 0: {
      return description.getRelationType().name();
    }
    case 1: {
      if (item instanceof ILibrary) {
        return ((ILibrary) item).getLibraryType().name();
      }
      if (item instanceof IPackage) {
        return "Package";
      }
      if (item instanceof IType) {
        return ((IType) item).getType().name();
      }
      return null;
    }
    case 2: {
      return item.getName();
    }
    }
    return null;
  }
}