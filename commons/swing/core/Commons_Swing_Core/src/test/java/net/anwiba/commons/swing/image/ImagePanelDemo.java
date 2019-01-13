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

import de.jdemo.annotation.Demo;
import de.jdemo.extensions.SwingDemoCase;
import net.anwiba.commons.http.HttpClientConnector;
import net.anwiba.commons.http.HttpRequestExecutorFactoryBuilder;
import net.anwiba.commons.http.IHttpRequestExecutorFactory;
import net.anwiba.commons.image.IImageReader;
import net.anwiba.commons.image.ImageContainerFactory;
import net.anwiba.commons.image.ImageReader;
import net.anwiba.commons.model.IObjectModel;
import net.anwiba.commons.model.ObjectModel;
import net.anwiba.commons.reference.IResourceReference;
import net.anwiba.commons.reference.ResourceReferenceHandler;

public class ImagePanelDemo extends SwingDemoCase {

  private final IHttpRequestExecutorFactory httpRequestExcecutorFactory = new HttpRequestExecutorFactoryBuilder()
      .useAlwaysANewConnection()
      .build();
  private final ResourceReferenceHandler resourceReferenceHandler = new ResourceReferenceHandler(
      new HttpClientConnector(this.httpRequestExcecutorFactory));
  private final ImageContainerFactory imageContainerFactory = new ImageContainerFactory(null);
  private final IImageReader imageReader = new ImageReader(
      this.imageContainerFactory,
      this.resourceReferenceHandler,
      this.httpRequestExcecutorFactory);

  @Demo
  public void showTifImage() throws Exception {
    final IObjectModel<IResourceReference> model = new ObjectModel<>();
    final ImagePanel imagePanel = new ImagePanel(
        this.imageContainerFactory,
        this.imageReader,
        model,
        ImageScaleBehavior.SCALE_DOWN);
    show(imagePanel);
    model.set(ImageResourceProviders.tiffImage);
  }

}