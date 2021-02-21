package net.anwiba.eclipse.project.dependency.type;

import net.anwiba.eclipse.project.dependency.java.IType;

public class CellValueFactory {

  public static String create(final IType type, final int columnIndex) {
    switch (columnIndex) {
      case 0: {
        return type.getQualifiedName();
      }
    }
    return null;
  }
}