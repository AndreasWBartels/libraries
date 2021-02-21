package net.anwiba.eclipse.project.dependency.java;

public interface IPath {

  String getIdentifier();

  String[] getSegments();

  IPath getParent();

  String lastSegment();

  boolean isEmpty();

  boolean isParent(IPath path);

}
