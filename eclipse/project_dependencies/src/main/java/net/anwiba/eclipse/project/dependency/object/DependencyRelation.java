package net.anwiba.eclipse.project.dependency.object;

import net.anwiba.eclipse.project.dependency.java.IItem;

public class DependencyRelation implements IDependencyRelation {

  private final IItem item;
  private final RelationType relation;

  public DependencyRelation(final IItem item, final RelationType relation) {
    this.item = item;
    this.relation = relation;
  }

  @Override
  public IItem getItem() {
    return this.item;
  }

  @Override
  public RelationType getRelationType() {
    return this.relation;
  }

}
