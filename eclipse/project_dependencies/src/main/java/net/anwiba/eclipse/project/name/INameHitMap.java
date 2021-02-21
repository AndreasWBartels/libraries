package net.anwiba.eclipse.project.name;

public interface INameHitMap {

  void add(String name);

  void reset();

  Iterable<String> getNames();

  long getNumberOfUses(String name);

  boolean isEmpty();
}
