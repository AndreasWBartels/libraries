package net.anwiba.eclipse.project.dependency.internal.java;

import net.anwiba.commons.lang.object.ObjectUtilities;
import net.anwiba.eclipse.project.dependency.java.IImport;
import net.anwiba.eclipse.project.dependency.java.IPackage;
import net.anwiba.eclipse.project.dependency.java.IPath;
import net.anwiba.eclipse.project.dependency.java.IType;
import net.anwiba.eclipse.project.dependency.java.TypeType;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Type implements IType {
  private final IPath path;
  private final String identifier;
  private final String name;
  private final Library library;
  private final TypeType type;
  private final List<IImport> imports;
  private final List<IPath> superTypes;
  private final Map<IPath, IType> usedBy = new HashMap<>();
  private final Map<IPath, IType> implementedBy = new HashMap<>();
  private final Set<IPath> methodTypes;
  private final Set<IPath> annotationTypes;
  private final Package pakkage;

  public Type(
    final Library library,
    final IPath path,
    final Package pakkage,
    final String name,
    final TypeType type,
    final List<IImport> imports,
    final List<IPath> superTypes,
    final Set<IPath> methodTypes,
    final Set<IPath> annotationTypes) {
    this.library = library;
    this.path = path;
    this.name = name;
    this.pakkage = pakkage;
    this.type = type;
    this.annotationTypes = annotationTypes;
    this.imports = Collections.unmodifiableList(imports);
    this.identifier = MessageFormat.format("{0}.{1}", library, path); //$NON-NLS-1$
    this.superTypes = Collections.unmodifiableList(superTypes);
    this.methodTypes = Collections.unmodifiableSet(methodTypes);
    pakkage.add(this);
  }

  @Override
  public IPackage getPackage() {
    return this.pakkage;
  }

  @Override
  public IPath getPath() {
    return this.path;
  }

  @Override
  public String getIdentifier() {
    return this.identifier;
  }

  @Override
  public String getQualifiedName() {
    return this.pakkage.getName() + "." + this.name;
  }

  @Override
  public String getName() {
    return this.name;
  }

  @Override
  public Library getLibrary() {
    return this.library;
  }

  @Override
  public String toString() {
    return this.path.toString();
  }

  @Override
  public TypeType getType() {
    return this.type;
  }

  @Override
  public Iterable<IImport> getImports() {
    return this.imports;
  }

  @Override
  public Iterable<IPath> getSuperTypes() {
    return this.superTypes;
  }

  @Override
  public Iterable<IPath> getMethodTypes() {
    return this.methodTypes;
  }

  @Override
  public Set<IPath> getAnnotationTypes() {
    return this.annotationTypes;
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj instanceof IType) {
      final IType other = (IType) obj;
      return ObjectUtilities.equals(this.getIdentifier(), other.getIdentifier());
    }
    return false;
  }

  @Override
  public int hashCode() {
    return ObjectUtilities.hashCode(this.getIdentifier());
  }

  public void addUsedBy(final IType type) {
    if (this.equals(type)) {
      return;
    }
    this.usedBy.put(type.getPath(), type);
  }

  public void addInstance(final IType type) {
    if (this.equals(type)) {
      return;
    }
    this.implementedBy.put(type.getPath(), type);
  }

  public void reset() {
    this.usedBy.clear();
    this.implementedBy.clear();
  }

  @Override
  public Iterable<IType> getImplementedBy() {
    return this.implementedBy.values();
  }

  @Override
  public Iterable<IType> getUsedBy() {
    return this.usedBy.values();
  }

  @Override
  public boolean isUsed() {
    return !this.usedBy.isEmpty();
  }

  @Override
  public boolean isImplemented() {
    return !this.implementedBy.isEmpty();
  }
}