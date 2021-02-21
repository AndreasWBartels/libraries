package net.anwiba.eclipse.project.dependency.runner;

import net.anwiba.eclipse.project.name.INameCollector;
import net.anwiba.eclipse.project.name.INamePartsIterable;
import net.anwiba.eclipse.project.name.NamePartsIterable;

public class NameCollector implements INameCollector {

  private final INameHitMaps nameHitMaps;

  public NameCollector(final INameHitMaps nameHitMaps) {
    this.nameHitMaps = nameHitMaps;
  }

  @Override
  public void add(final String name) {
    this.nameHitMaps.getNames().add(name);
    final INamePartsIterable iterable = new NamePartsIterable(name);
    String lastNamePart = null;
    String firstNamePart = null;
    for (final String namePart : iterable) {
      this.nameHitMaps.getNameParts().add(namePart);
      if (firstNamePart == null && (name.startsWith(namePart) || name.startsWith("I" + namePart))) { //$NON-NLS-1$
        firstNamePart = namePart;
      }
      lastNamePart = namePart;
    }
    if (lastNamePart == null || !name.endsWith(lastNamePart) || firstNamePart == null) {
      this.nameHitMaps.getUnmatchedNames().add(name);
    }
    if (lastNamePart == null) {
      return;
    }
    if (lastNamePart.equals(firstNamePart)
        && (name.length() == lastNamePart.length() || name.length() - 1 == lastNamePart.length())) {
      this.nameHitMaps.getOneWordNames().add(lastNamePart);
      return;
    }
    if (name.endsWith(lastNamePart)) {
      this.nameHitMaps.getNamePostfixes().add(lastNamePart);
    }
    if (firstNamePart != null) {
      this.nameHitMaps.getNamePrefixes().add(firstNamePart);
      return;
    }
  }
}
