/*
* #%L
*
* %%
* Copyright (C) 2007 - 2018 Andreas W. Bartels (bartels@anwiba.de)
* %%
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Lesser General Public License as
* published by the Free Software Foundation, either version 2.1 of the
* License, or (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU General Lesser Public License for more details.
*
* You should have received a copy of the GNU General Lesser Public
* License along with this program. If not, see
* <http://www.gnu.org/licenses/lgpl-2.1.html>.
* #L%
*/
package net.anwiba.commons.swing.image;

import static net.anwiba.testing.demo.JFrames.show;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.io.File;
import java.io.FileFilter;
import java.time.Duration;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.junit.jupiter.api.Test;

import net.anwiba.commons.http.HttpClientConnector;
import net.anwiba.commons.http.HttpRequestExecutorFactoryBuilder;
import net.anwiba.commons.http.IHttpRequestExecutorFactory;
import net.anwiba.commons.image.IImageReader;
import net.anwiba.commons.image.ImageContainerFactory;
import net.anwiba.commons.image.ImageFileFilter;
import net.anwiba.commons.image.ImageReader;
import net.anwiba.commons.model.IChangeableObjectListener;
import net.anwiba.commons.model.IObjectModel;
import net.anwiba.commons.model.ISelectionListener;
import net.anwiba.commons.model.ISelectionModel;
import net.anwiba.commons.model.ObjectModel;
import net.anwiba.commons.model.SelectionEvent;
import net.anwiba.commons.reference.IResourceReference;
import net.anwiba.commons.reference.ResourceReferenceFactory;
import net.anwiba.commons.reference.ResourceReferenceHandler;
import net.anwiba.commons.swing.filechooser.FileFieldBuilder;
import net.anwiba.commons.swing.list.ObjectListComponent;
import net.anwiba.commons.swing.list.ObjectListComponentBuilder;
import net.anwiba.commons.swing.list.ObjectListComponentModel;
import net.anwiba.commons.swing.object.IObjectField;
import net.anwiba.commons.swing.ui.ObjectUiBuilder;

public class ImagePanelDemo {

  private final IHttpRequestExecutorFactory httpRequestExcecutorFactory = new HttpRequestExecutorFactoryBuilder()
      .useAlwaysANewConnection()
      .build();
  private final ResourceReferenceHandler resourceReferenceHandler = new ResourceReferenceHandler(
      new HttpClientConnector(this.httpRequestExcecutorFactory));
  private final ImageContainerFactory imageContainerFactory =
      ImageContainerFactory.of(null, this.resourceReferenceHandler);
  private final IImageReader imageReader = new ImageReader(
      this.imageContainerFactory,
      this.resourceReferenceHandler,
      this.httpRequestExcecutorFactory);

  @Test
  public void showTifImage() throws Exception {
    final IResourceReference resourceReference = create("image.tif");
    final IObjectModel<IResourceReference> model = new ObjectModel<>();
    final ImagePanel imagePanel = new ImagePanel(
        this.imageContainerFactory,
        this.imageReader,
        model,
        ImageScaleBehavior.SCALE_DOWN);
    show(Duration.ofMinutes(5), imagePanel, frame -> {
      try {
        Thread.sleep(500);
        model.set(resourceReference);
        Thread.sleep(2000);
      } catch (InterruptedException exception) {
      }
    });
  }

  private IResourceReference create(final String name) {
    return new ResourceReferenceFactory().create(getClass().getResource(name));
  }

  @Test
  public void showImage() throws Exception {
    final IObjectModel<IResourceReference> model = new ObjectModel<>();
    final ImagePanel imagePanel = new ImagePanel(
        this.imageContainerFactory,
        this.imageReader,
        model,
        ImageScaleBehavior.SCALE_DOWN);
    final FileFieldBuilder builder = new FileFieldBuilder();
    final IObjectField<File> field = builder
        .addFolderOpenChooserAction(null)
        .addClearAction(null)
        .setFolderValidator()
        .build();
    final ObjectListComponentModel<File> listModel = new ObjectListComponentModel<File>(List.of());
    final ObjectListComponent<File> list = new ObjectListComponentBuilder<File>()
        .setModel(listModel)
        .setObjectUi(new ObjectUiBuilder<File>()
            .text(f -> f.getName())
            .tooltip(f -> f.toString())
            .build())
        .build();
    final ISelectionModel<File> selectionModel = list.getSelectionModel();

    final IObjectModel<File> fileModel = field.getModel();
    fileModel.addChangeListener(new IChangeableObjectListener() {

      @Override
      public void objectChanged() {
        if (fileModel.isEmpty()) {
          listModel.removeAll();
          return;
        }
        File[] files = fileModel.get().listFiles(new FileFilter() {

          ImageFileFilter filter = new ImageFileFilter();

          @Override
          public boolean accept(final File pathname) {
            return this.filter.accept(pathname);
          }
        });
        if (files == null) {
          listModel.removeAll();
          return;
        }
        Arrays.sort(files);
        listModel.add(files);
      }
    });

    selectionModel.addSelectionListener(new ISelectionListener<File>() {

      @Override
      public void selectionChanged(final SelectionEvent<File> event) {
        Iterable<File> selectedObjects = event.getSource().getSelectedObjects();
        final Iterator<File> iterator = selectedObjects.iterator();
        if (iterator.hasNext()) {
          final File next = iterator.next();
          model.set(new ResourceReferenceFactory().create(next));
        } else {
          model.set(null);
        }
      }
    });

    final JPanel component = new JPanel();
    component.setMinimumSize(new Dimension(600, 320));
    component.setPreferredSize(new Dimension(600, 320));
    component.setLayout(new BorderLayout());
    component.add(field.getComponent(), BorderLayout.NORTH);
    JPanel imageContainer = new JPanel();
    imageContainer.setLayout(new GridLayout(1, 1));
    imageContainer.setMinimumSize(new Dimension(300, 300));
    imageContainer.setPreferredSize(new Dimension(300, 300));
    imageContainer.add(imagePanel);

    final JComponent listComponent = list.getComponent();
    listComponent.setMinimumSize(new Dimension(300, 300));
    listComponent.setPreferredSize(new Dimension(300, 300));

    component.add(imageContainer, BorderLayout.CENTER);
    component.add(listComponent, BorderLayout.WEST);
    show(component);
//    model.set(ImageResourceProviders.tiffImage);
  }

}