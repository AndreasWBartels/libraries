package net.anwiba.eclipse.project.dependency.object;

import org.eclipse.jdt.core.IJavaElement;

public enum ItemType {
  UNKOWN(0), PACKAGE_ROOT(IJavaElement.PACKAGE_FRAGMENT_ROOT), PACKAGE(IJavaElement.PACKAGE_FRAGMENT), CLASS(
      IJavaElement.CLASS_FILE), COMPILATION_UNIT(IJavaElement.COMPILATION_UNIT), TYPE(IJavaElement.TYPE);

  private final int elementType;

  private ItemType(final int elementType) {
    this.elementType = elementType;
  }

  public static ItemType getByElementType(final int elementType) {
    final ItemType[] values = values();
    for (final ItemType itemType : values) {
      if (itemType.elementType == elementType) {
        return itemType;
      }
    }
    return UNKOWN;
  }
}
