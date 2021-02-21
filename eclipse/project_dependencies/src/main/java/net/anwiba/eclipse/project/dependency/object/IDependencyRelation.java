package net.anwiba.eclipse.project.dependency.object;

import net.anwiba.eclipse.project.dependency.java.IItem;

public interface IDependencyRelation {

  IItem getItem();

  RelationType getRelationType();
}
