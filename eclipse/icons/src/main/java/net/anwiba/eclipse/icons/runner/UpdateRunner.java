// Copyright (c) 2010 by Andreas W. Bartels (bartels@anwiba.de)
package net.anwiba.eclipse.icons.runner;

import net.anwiba.commons.thread.cancel.ICanceler;
import net.anwiba.eclipse.icons.description.GuiIconDescriptionsFactory;
import net.anwiba.eclipse.icons.description.IGuiIconDescription;
import net.anwiba.eclipse.icons.io.IconConfigurationReader;
import net.anwiba.eclipse.icons.io.IconContext;
import net.anwiba.tools.icons.schema.configuration.Class;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.graphics.Device;

public final class UpdateRunner implements IRunnableWithProgress {
  private final IconConfigurationReader reader = new IconConfigurationReader();
  private final WritableList<IGuiIconDescription> descriptions;
  private final Device device;
  private final ICanceler canceler;
  private final IJavaProject[] projects;

  public UpdateRunner(
    final ICanceler canceler,
    final Device device,
    final WritableList<IGuiIconDescription> descriptions,
    final IJavaProject... projects) {
    this.device = device;
    this.descriptions = descriptions;
    this.projects = projects;
    this.canceler = canceler;
  }

  @Override
  public void run(final IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
    try {
      monitor.beginTask("GuiIcons update", IProgressMonitor.UNKNOWN);
      final List<IGuiIconDescription> iconDescriptions = read(this.projects);
      if (this.canceler.isCanceled()) {
        return;
      }
      if (iconDescriptions.size() == this.descriptions.size() && iconDescriptions.containsAll(this.descriptions)) {
        return;
      }
      clear();
      this.descriptions.addAll(iconDescriptions);
    } catch (final IOException exception) {
      throw new InvocationTargetException(exception);
    } finally {
      monitor.done();
    }
  }

  private void clear() {
    final IGuiIconDescription[] array;
    array = this.descriptions.toArray(new IGuiIconDescription[this.descriptions.size()]);
    this.descriptions.clear();
    for (final IGuiIconDescription description : array) {
      description.dispose();
    }
  }

  private List<IGuiIconDescription> read(final IJavaProject... projects) throws IOException {
    final GuiIconDescriptionsFactory guiIconDescriptionsFactory = new GuiIconDescriptionsFactory(this.device);
    final ArrayList<IGuiIconDescription> descriptions = new ArrayList<>();
    for (final IJavaProject project : projects) {
      final Map<Class, List<IconContext>> configurations = this.reader.read(project);
      descriptions.addAll(guiIconDescriptionsFactory.create(this.canceler, configurations));
    }
    return descriptions;

  }
}