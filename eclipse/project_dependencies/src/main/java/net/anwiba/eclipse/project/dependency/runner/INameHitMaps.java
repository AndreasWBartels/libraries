package net.anwiba.eclipse.project.dependency.runner;

import net.anwiba.eclipse.project.name.INameHitMap;

public interface INameHitMaps {

  INameHitMap getNameParts();

  INameHitMap getNamePostfixes();

  INameHitMap getOneWordNames();

  INameHitMap getUnmatchedNames();

  INameHitMap getNamePrefixes();

  INameHitMap getNames();

  void reset();

  boolean isEmpty();

}
