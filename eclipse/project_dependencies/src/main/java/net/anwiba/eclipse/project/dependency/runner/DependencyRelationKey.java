package net.anwiba.eclipse.project.dependency.runner;

import net.anwiba.commons.lang.object.ObjectUtilities;
import net.anwiba.eclipse.project.dependency.object.RelationType;

public class DependencyRelationKey  {

  private final String identifier;
  private final RelationType relationType;
  private int hashCode;

  public DependencyRelationKey(final String identifier, final RelationType relationType) {
    this.identifier = identifier;
    this.relationType = relationType;
    hashCode = ObjectUtilities.hashCode(this.identifier, this.relationType);
  }

  private String getIdentifier() {
    return this.identifier;
  }

  private RelationType getRelationType() {
    return this.relationType;
  }

  @Override
  public int hashCode() {
    return hashCode;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof DependencyRelationKey)) {
      return false;
    }
    final DependencyRelationKey other = (DependencyRelationKey) obj;
    return ObjectUtilities.equals(this.getIdentifier(), other.getIdentifier())
        && ObjectUtilities.equals(this.getRelationType(), other.getRelationType());
  }
}