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
package net.anwiba.commons.image;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.awt.image.BufferedImage;

import org.junit.Test;

import net.anwiba.commons.http.HttpClientConnector;
import net.anwiba.commons.http.HttpRequestExecutorFactoryBuilder;
import net.anwiba.commons.http.IHttpRequestExecutorFactory;
import net.anwiba.commons.reference.ResourceReferenceHandler;
import net.anwiba.commons.thread.cancel.Canceler;

public class ImageContainerTest {

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

  //  @Test
  //  public void scaleImage() throws Exception {
  //    final IImageContainer container = this.imageReader
  //        .read(Canceler.DummyCanceler, ImageResourceProviders.geotiffImage);
  //    assertThat(container.getWidth(), equalTo(512));
  //    assertThat(container.getHeight(), equalTo(512));
  //    final BufferedImage bufferImage = container.asBufferImage();
  //    assertNotNull(bufferImage);
  //
  //    final IImageContainer scaledImageContainer = container.scale(0.01f);
  //    BufferedImage scaledBufferImage = scaledImageContainer.asBufferImage();
  //    assertThat(scaledImageContainer.getWidth(), equalTo(6));
  //    assertThat(scaledImageContainer.getHeight(), equalTo(6));
  //    assertNotNull(scaledBufferImage);
  //    assertThat(scaledBufferImage.getWidth(), equalTo(6));
  //    assertThat(scaledBufferImage.getHeight(), equalTo(6));
  //
  //    scaledBufferImage = this.imageContainerFactory.create(bufferImage).scale(0.01f).asBufferImage();
  //    assertNotNull(scaledBufferImage);
  //    assertThat(scaledBufferImage.getWidth(), equalTo(6));
  //    assertThat(scaledBufferImage.getHeight(), equalTo(6));
  //  }

  @Test
  public void cropImage() throws Exception {
    final IImageContainer container = this.imageReader
        .read(Canceler.DummyCanceler, ImageResourceProviders.geotiffImage);
    final BufferedImage bufferImage = container.asBufferImage();
    assertNotNull(bufferImage);
    assertThat(bufferImage.getWidth(), equalTo(512));
    assertThat(bufferImage.getHeight(), equalTo(512));

    IImageContainer cropedImageContainer = container.crop(10, 10, 100, 100);
    BufferedImage cropedBufferImage = cropedImageContainer.asBufferImage();
    assertThat(cropedImageContainer.getWidth(), equalTo(100));
    assertThat(cropedImageContainer.getHeight(), equalTo(100));
    assertNotNull(cropedBufferImage);
    assertThat(cropedBufferImage.getWidth(), equalTo(100));
    assertThat(cropedBufferImage.getHeight(), equalTo(100));

    cropedImageContainer = container.crop(20, 20, 200, 200);
    cropedBufferImage = cropedImageContainer.asBufferImage();
    assertThat(cropedImageContainer.getWidth(), equalTo(200));
    assertThat(cropedImageContainer.getHeight(), equalTo(200));
    assertNotNull(cropedBufferImage);
    assertThat(cropedBufferImage.getWidth(), equalTo(200));
    assertThat(cropedBufferImage.getHeight(), equalTo(200));

    cropedBufferImage = this.imageContainerFactory.create(bufferImage).crop(10, 10, 100, 100).asBufferImage();
    assertNotNull(cropedBufferImage);
    assertThat(cropedBufferImage.getWidth(), equalTo(100));
    assertThat(cropedBufferImage.getHeight(), equalTo(100));
  }

  //  @Test
  //  public void toGrayScaleImage() throws Exception {
  //    final IImageContainer container = this.imageReader
  //        .read(Canceler.DummyCanceler, ImageResourceProviders.coloredtiffImage);
  //    assertThat(container.getColorSpaceType(), equalTo(ColorSpace.TYPE_3CLR));
  //    final BufferedImage bufferImage = container.asBufferImage();
  //    assertNotNull(bufferImage);
  //    assertThat(bufferImage.getColorModel().getColorSpace().getType(), equalTo(ColorSpace.TYPE_RGB));
  //    final IImageContainer grayScaledImageContainer = container.toGrayScale();
  //    assertThat(grayScaledImageContainer.getColorSpaceType(), equalTo(ColorSpace.TYPE_GRAY));
  //    BufferedImage grayScaledbufferImage = grayScaledImageContainer.asBufferImage();
  //    assertNotNull(grayScaledbufferImage);
  //    assertThat(grayScaledbufferImage.getColorModel().getColorSpace().getType(), equalTo(ColorSpace.TYPE_GRAY));
  //    grayScaledbufferImage = this.imageContainerFactory.create(bufferImage).toGrayScale().asBufferImage();
  //    assertNotNull(grayScaledbufferImage);
  //    assertThat(grayScaledbufferImage.getColorModel().getColorSpace().getType(), equalTo(ColorSpace.TYPE_GRAY));
  //  }
}