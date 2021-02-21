// Copyright (c) 2010 by Andreas W. Bartels (bartels@anwiba.de)
package net.anwiba.eclipse.project.dependency.runner;

import net.anwiba.commons.eclipse.logging.ILogger;
import net.anwiba.commons.internal.eclipse.logging.Level;
import net.anwiba.commons.lang.object.ObjectUtilities;
import net.anwiba.commons.thread.cancel.ICanceler;
import net.anwiba.eclipse.project.dependency.internal.java.Dependency;
import net.anwiba.eclipse.project.dependency.internal.java.Import;
import net.anwiba.eclipse.project.dependency.internal.java.Library;
import net.anwiba.eclipse.project.dependency.internal.java.Package;
import net.anwiba.eclipse.project.dependency.internal.java.Path;
import net.anwiba.eclipse.project.dependency.internal.java.Project;
import net.anwiba.eclipse.project.dependency.internal.java.Type;
import net.anwiba.eclipse.project.dependency.internal.java.WorkspaceBuilder;
import net.anwiba.eclipse.project.dependency.java.IImport;
import net.anwiba.eclipse.project.dependency.java.ILibrary;
import net.anwiba.eclipse.project.dependency.java.IPath;
import net.anwiba.eclipse.project.dependency.java.IWorkspace;
import net.anwiba.eclipse.project.dependency.java.LibraryType;
import net.anwiba.eclipse.project.dependency.java.TypeType;
import net.anwiba.eclipse.project.dependency.object.ItemType;
import net.anwiba.eclipse.project.name.INameCollector;

import java.net.URI;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IImportDeclaration;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaModel;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jdt.internal.core.ExternalPackageFragmentRoot;
import org.eclipse.jdt.internal.core.JarPackageFragmentRoot;

@SuppressWarnings("restriction")
public final class WorkspaceDependenciesInvestigator {

  private final INameCollector nameCollector;
  private final IJavaModel model;
  private final ILogger logger;
  private final IWorkspace previousInvestigatedWorkspace;
  private final INameHitMaps nameHitMaps;

  public WorkspaceDependenciesInvestigator(
    final IWorkspace previousInvestigatedWorkspace,
    final ILogger logger,
    final IJavaModel model,
    final INameHitMaps nameHitMaps) {
    this.previousInvestigatedWorkspace = previousInvestigatedWorkspace;
    this.logger = logger;
    this.model = model;
    this.nameHitMaps = nameHitMaps;
    this.nameCollector = new NameCollector(nameHitMaps);
  }

  public IWorkspace investigate(final IProgressMonitor monitor, final ICanceler canceler)
      throws JavaModelException,
        InterruptedException {
    this.nameHitMaps.reset();
    final URI locationURI = this.model.getWorkspace().getRoot().getLocationURI();
    final WorkspaceBuilder builder = new WorkspaceBuilder(locationURI);
    // addLibraries(builder);
    if (canceler.isCanceled()) {
      throw new InterruptedException();
    }
    addProjects(monitor, canceler, builder);
    addDependencies(monitor, canceler, builder);
    monitor.subTask("build dependencies"); //$NON-NLS-1$
    final IWorkspace workspace = builder.build();
    return workspace;
  }

  private void addDependencies(final IProgressMonitor monitor, final ICanceler canceler, final WorkspaceBuilder builder)
      throws JavaModelException,
        InterruptedException {
    for (final IJavaProject javaProject : this.model.getJavaProjects()) {
      final String message = MessageFormat.format("scanne dependencies for project {0}", javaProject.getElementName()); //$NON-NLS-1$
      this.logger.log(Level.INFO, message);
      monitor.subTask(message);
      final Project project = builder.getProject(javaProject.getPath().toPortableString());
      if (project == null) {
        continue;
      }
      final Set<ILibrary> libraries = new HashSet<>();
      ClasspathCollector.collect(canceler, libraries, project);
      for (final IClasspathEntry classpathEntry : javaProject.getRawClasspath()) {
        if (canceler.isCanceled()) {
          throw new InterruptedException();
        }
        final int entryKind = classpathEntry.getEntryKind();
        switch (entryKind) {
          case IClasspathEntry.CPE_LIBRARY:
          case IClasspathEntry.CPE_PROJECT: {
            final org.eclipse.core.runtime.IPath path = classpathEntry.getPath();
            final Library library = builder.getLibrary(path.toPortableString());
            if (library == null || libraries.contains(library)) {
              continue;
            }
            ClasspathCollector.collect(canceler, libraries, library);
            if (libraries.contains(project)) {
              final String submessage =
                  MessageFormat.format("dependencies for project {0} builds cycle over library {1}", //$NON-NLS-1$
                      javaProject.getElementName(), library.getName());
              this.logger.log(Level.ERROR, submessage);
              this.logger.log(Level.ERROR, ObjectUtilities.toString(library));
              throw new JavaModelException(new Exception(submessage), IStatus.ERROR);
            }
            libraries.add(library);
            project.getDependencies().add(new Dependency(library, classpathEntry.isExported()));
            break;
          }
          case IClasspathEntry.CPE_CONTAINER: {
            break;
          }
          case IClasspathEntry.CPE_SOURCE: {
            break;
          }
          case IClasspathEntry.CPE_VARIABLE: {
            break;
          }
          default:
            break;
        }
        project.setClasspath(libraries);
      }
    }
  }

  private void addProjects(final IProgressMonitor monitor, final ICanceler canceler, final WorkspaceBuilder builder)
      throws JavaModelException,
        InterruptedException {
    for (final IJavaProject javaProject : this.model.getJavaProjects()) {
      final String elementName = javaProject.getElementName();
      final String message = MessageFormat.format("scanne content for project {0}", elementName); //$NON-NLS-1$
      this.logger.log(Level.INFO, message);
      monitor.subTask(message);
      final Project project =
          new Project(javaProject.getPath().toPortableString(), javaProject.getProject().getLocationURI());
      builder.add(project);
      adjust(monitor, canceler, builder, project, null, javaProject.getChildren());
    }
  }

  // private void addLibraries(final WorkspaceBuilder builder) {
  // if (this.previousInvestigatedWorkspace != null) {
  // for (final String key : this.previousInvestigatedWorkspace.getLibraries().keySet()) {
  // final Library library = (Library) this.previousInvestigatedWorkspace.getLibraries().get(key);
  // if (library == null || LibraryType.PROJECT.equals(library.getLibraryType())) {
  // continue;
  // }
  // library.reset();
  // builder.add(library);
  // }
  // }
  // }

  private void adjust(
      final IProgressMonitor monitor,
      final ICanceler canceler,
      final WorkspaceBuilder builder,
      final Library library,
      final Path path,
      final IJavaElement... children) throws InterruptedException {
    final String message = MessageFormat.format("scanne content dependencies for library {0}", library.getName()); //$NON-NLS-1$
    // this.logger.log(Level.INFO, message);
    monitor.subTask(message);
    for (final IJavaElement javaElement : children) {
      if (canceler.isCanceled()) {
        throw new InterruptedException();
      }
      if (javaElement.getElementName().length() == 0) {
        continue;
      }
      final ItemType itemType = ItemType.getByElementType(javaElement.getElementType());
      try {
        switch (itemType) {
          case PACKAGE_ROOT: {
            if (javaElement instanceof ExternalPackageFragmentRoot) {
              break;
            }
            if (javaElement instanceof JarPackageFragmentRoot) {
              final JarPackageFragmentRoot jarPackageFragmentRoot = (JarPackageFragmentRoot) javaElement;
              final org.eclipse.core.runtime.IPath jarPath = jarPackageFragmentRoot.getPath();
              if (builder.getLibrary(jarPath.toPortableString()) != null) {
                library.add(new Dependency(builder.getLibrary(jarPath.toPortableString()), false));
                break;
              }
              final Library jarFile = new Library(jarPath.toPortableString(), LibraryType.JAR);
              builder.add(jarFile);
              library.add(new Dependency(jarFile, false));
              adjust(monitor, canceler, builder, jarFile, null, jarPackageFragmentRoot.getChildren());
              monitor.subTask(message);
              break;
            }
            final IPackageFragmentRoot fragmentRoot = (IPackageFragmentRoot) javaElement;
            final IJavaElement[] children2 = fragmentRoot.getChildren();
            adjust(monitor, canceler, builder, library, null, children2);
            break;
          }
          case PACKAGE: {
            final IPackageFragment packagFragment = (IPackageFragment) javaElement;
            if (path == null) {
              final Path packagePath = create(path, packagFragment.getElementName());
              final Package pakkage = new Package(library, packagePath);
              builder.add(pakkage);
              adjust(monitor, canceler, builder, library, packagePath, packagFragment.getChildren());
              break;
            }
            adjust(monitor, canceler, builder, library, create(path, packagFragment
                .getPath()
                .lastSegment()), packagFragment.getChildren());
            break;
          }
          case CLASS: {
            if (!(javaElement instanceof IClassFile)) {
              continue;
            }
            final IClassFile classFile = (IClassFile) javaElement;
            final Package pakkage = builder.getPackage(path.getIdentifier());
            try {
              final IJavaElement[] childrens = classFile.getChildren();
              for (final IJavaElement element : childrens) {
                if (!(element instanceof IType)) {
                  continue;
                }
                final IType type = (IType) element;
                adjust(monitor, canceler, builder, library, pakkage, type, new IImportDeclaration[0]);
              }

            } catch (final Exception e) {
              this.logger.log(Level.WARNING, MessageFormat
                  .format("{0}\n\t scanning java element ''{1}.{2} type {3}'' faild", message, pakkage
                      .getName(), classFile.getElementName(), classFile.getType().getElementName()));
            }
            break;
          }
          case COMPILATION_UNIT: {
            if (!(javaElement instanceof ICompilationUnit)) {
              continue;
            }
            final ICompilationUnit compilationUnit = (ICompilationUnit) javaElement;
            final Package pakkage = builder.getPackage(path.getIdentifier());

            for (final IType type : compilationUnit.getTypes()) {
              this.nameCollector.add(type.getElementName());
              adjust(monitor, canceler, builder, library, pakkage, type, compilationUnit.getImports());
            }
            break;
          }
          case TYPE: {
            throw new RuntimeException("Unreachable code reached"); //$NON-NLS-1$
          }
          case UNKOWN: {
            break;
          }
        }

      } catch (final JavaModelException exception) {
        this.logger.log(Level.WARNING, MessageFormat
            .format("scanning java element ''{0}'' type {1} faild", javaElement.getElementName(), itemType.name()), //$NON-NLS-1$
            exception);
      }
    }
  }

  private void adjust(
      final IProgressMonitor monitor,
      final ICanceler cancler,
      final WorkspaceBuilder builder,
      final Library library,
      final Package pakkage,
      final IType typeElement,
      final IImportDeclaration[] imports) throws InterruptedException {
    if (cancler.isCanceled()) {
      throw new InterruptedException();
    }
    if (typeElement.getElementName().length() == 0) {
      return;
    }
    final Path path = create(null, typeElement.getFullyQualifiedName());
    final List<IImport> importedTypes = getImportedTypes(imports);
    final List<IAnnotation> annotations = getAnnotations(typeElement);
    try {
      final List<IPath> superTypes = getSuperTypes(typeElement, importedTypes, path);
      final Set<IPath> methodTypes = getMethodTypes(typeElement, importedTypes, path);
      final Set<IPath> annotationTypes = getAnnotationTypes(annotations, importedTypes, path);
      final Type type = new Type(library, path, pakkage, typeElement.getElementName(), typeElement.isInterface()
          ? TypeType.INTERFACE
          : TypeType.CLASS, importedTypes, superTypes, methodTypes, annotationTypes);
      builder.add(type);
      for (final IType childType : typeElement.getTypes()) {
        adjust(monitor, cancler, builder, library, pakkage, childType, imports);
      }
    } catch (final JavaModelException exception) {
      this.logger
          .log(Level.WARNING, MessageFormat.format("scanning java element ''{0}'' faild", typeElement.getElementName()), //$NON-NLS-1$
              exception);
      final Type type = new Type(
          library,
          path,
          pakkage,
          typeElement.getElementName(),
          TypeType.CLASS,
          importedTypes,
          new ArrayList<IPath>(),
          new HashSet<IPath>(),
          new HashSet<IPath>());
      builder.add(type);
    }
  }

  private List<IAnnotation> getAnnotations(final IType typeElement) {
    try {
      return Arrays.asList(typeElement.getAnnotations());

    } catch (final JavaModelException exception) {
      return new ArrayList<>();
    }
  }

  private List<IImport> getImportedTypes(final IImportDeclaration[] importDeclarations) {
    final List<IImport> importedTypes = new ArrayList<>();
    for (final IImportDeclaration importDeclaration : importDeclarations) {
      final String importName = importDeclaration.getElementName();
      final Path importPath = create(null, importName);
      importedTypes.add(new Import(importName, importName.endsWith("*") //$NON-NLS-1$
          ? importPath.getParent()
          : importPath));
    }
    return importedTypes;
  }

  private Set<IPath> getMethodTypes(final IType typeElement, final List<IImport> imports, final Path path)
      throws JavaModelException {
    final IMethod[] methods = typeElement.getMethods();
    final Set<IPath> methodTypes = new HashSet<>();
    for (final IMethod method : methods) {
      final String returnType = method.getReturnType();
      addToPaths(imports, path, methodTypes, returnType);
      final String[] parameterTypes = method.getParameterTypes();
      for (final String parameterType : parameterTypes) {
        addToPaths(imports, path, methodTypes, parameterType);
      }
      final String[] exceptionTypes = method.getExceptionTypes();
      for (final String exceptionType : exceptionTypes) {
        addToPaths(imports, path, methodTypes, exceptionType);
      }
    }
    return methodTypes;
  }

  private void addToPaths(
      final List<IImport> imports,
      final Path path,
      final Set<IPath> paths,
      final String parameterType) {
    String signatureSimpleName;
    signatureSimpleName = Signature.getSignatureSimpleName(parameterType);
    if ("void".equals(signatureSimpleName)) { //$NON-NLS-1$
      return;
    }
    paths.add(createTypePath(imports, path, signatureSimpleName));
  }

  private Set<IPath> getAnnotationTypes(
      final List<IAnnotation> annotations,
      final List<IImport> imports,
      final Path path) {
    final Set<IPath> annotationPaths = new HashSet<>();
    for (final IAnnotation annotation : annotations) {
      final String qualifiedName = annotation.getElementName();
      annotationPaths.add(createTypePath(imports, path, qualifiedName));
    }
    return annotationPaths;
  }

  private List<IPath> getSuperTypes(final IType typeElement, final List<IImport> imports, final Path path)
      throws JavaModelException {
    final String superclassName = typeElement.getSuperclassName();
    final List<IPath> superTypes = new ArrayList<>();
    if (superclassName != null) {
      final IPath superTypePath = createTypePath(imports, path, superclassName);
      superTypes.add(superTypePath);
    }
    final String[] superInterfaceNames = typeElement.getSuperInterfaceNames();
    for (final String superInterfaceName : superInterfaceNames) {
      final IPath createSuperTypePath = createTypePath(imports, path, superInterfaceName);
      createSuperTypePath.getClass();
      superTypes.add(createSuperTypePath);
    }
    return superTypes;
  }

  private IPath createTypePath(final List<IImport> imports, final Path path, final String typeName) {
    if (typeName == null) {
      return null;
    }
    final String name = removeGenerics(typeName);
    Path dummy = create(null, name);
    if (dummy.getParent().isEmpty() && !path.getParent().isEmpty()) {
      for (final IImport impcrt : imports) {
        if (name.equals(impcrt.getPath().lastSegment())) {
          return impcrt.getPath();
        }
      }
      dummy = create((Path) path.getParent(), name);
      return dummy;
    }
    return dummy;
  }

  private String removeGenerics(final String name) {
    if (!name.contains("<")) { //$NON-NLS-1$
      return name;
    }
    return name.substring(0, name.indexOf('<'));
  }

  private Path create(final Path path, final String segment) {
    if (path == null) {
      final StringTokenizer tokenizer = new StringTokenizer(segment, "."); //$NON-NLS-1$
      final List<String> tokens = new ArrayList<>();
      while (tokenizer.hasMoreElements()) {
        tokens.add(tokenizer.nextToken());
      }
      return new Path(tokens.toArray(new String[tokens.size()]));
    }
    final String[] segements = path.getSegments();
    final String[] result = new String[segements.length + 1];
    System.arraycopy(segements, 0, result, 0, segements.length);
    result[segements.length] = segment;
    return new Path(result);
  }
}