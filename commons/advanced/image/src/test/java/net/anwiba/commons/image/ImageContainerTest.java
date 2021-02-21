/*
 * #%L
 * anwiba commons
 * %%
 * Copyright (C) 2007 - 2021 Andreas W. Bartels
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */
package net.anwiba.commons.image;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.color.ColorSpace;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;

import javax.imageio.ImageIO;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import net.anwiba.commons.http.HttpClientConnector;
import net.anwiba.commons.http.HttpRequestExecutorFactoryBuilder;
import net.anwiba.commons.http.IHttpRequestExecutorFactory;
import net.anwiba.commons.image.codec.ImageCodec;
import net.anwiba.commons.lang.exception.CanceledException;
import net.anwiba.commons.lang.exception.CreationException;
import net.anwiba.commons.lang.functional.ConversionException;
import net.anwiba.commons.lang.random.RandomObjectGenerator;
import net.anwiba.commons.reference.IResourceReference;
import net.anwiba.commons.reference.IResourceReferenceFactory;
import net.anwiba.commons.reference.ResourceReferenceFactory;
import net.anwiba.commons.reference.ResourceReferenceHandler;
import net.anwiba.commons.reference.utilities.IoUtilities;
import net.anwiba.commons.thread.cancel.ICanceler;

@TestMethodOrder(OrderAnnotation.class)
public class ImageContainerTest {

  @BeforeAll
  public static void beforAll() {
    ImageIO.setUseCache(false);
  }

  private final IResourceReferenceFactory resourceReferenceFactory = new ResourceReferenceFactory();
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
  private final ImageWriter imageWriter = new ImageWriter();

  @Test
  @Order(1)
  public void bufferedImage() throws IOException,
      CreationException,
      CanceledException {
    boolean hasAlpha = true;
    BufferedImage bufferedImage = generate(hasAlpha, true);
    write(bufferedImage, ImageCodec.PNG);
    IImageContainer container = this.imageContainerFactory.create(bufferedImage);
    assertContainer(container, 1000, 1000);
    IImageContainer scaledToContainer = container.scaleTo(500, 500);
    assertContainer(scaledToContainer, 500, 500);
    IImageContainer scaledContainer = container.scale(0.5, 0.5);
    assertContainer(scaledContainer, 500, 500);
    IImageContainer asymetricScaledContainer = container.scale(0.5, 1.5);
    assertContainer(asymetricScaledContainer, 500, 1500);
    IImageContainer croppedContainer = container.crop(500, 500, 100, 100);
    assertContainer(croppedContainer, 100, 100);
    write(croppedContainer.asBufferImage(ICanceler.DummyCanceler),
        ImageCodec.PNG);
    IImageContainer channelModifiedContainer =
        container.mapBands(new int[] { 3, 1, 2, 0 });
    assertContainer(channelModifiedContainer, 1000, 1000);
    write(container.asBufferImage(ICanceler.DummyCanceler),
        ImageCodec.PNG);
  }

  @ParameterizedTest
  @ValueSource(strings = { "png", "gif", "tif", "bmp", "jpg" })
  @Order(2)
  public void readWriteWithOutAlpha(final String formatName) throws IOException,
      CreationException,
      CanceledException,
      ConversionException,
      URISyntaxException {
    ImageCodec format = ImageCodec.getByExtension(formatName);
    boolean hasAlpha = false;
    IResourceReference original = write(generate(hasAlpha, true), format);
    IImageContainer container = this.imageReader.read(ICanceler.DummyCanceler, original);
    assertContainer(container, 1000, 1000);

    if (!Set.of(ImageCodec.JPEG, ImageCodec.BMP).contains(format)) {
      IResourceReference copy = write(container, format);
      assertTrue(
          IoUtilities.contentEquals(this.resourceReferenceHandler.getFile(original),
              this.resourceReferenceHandler.getFile(copy)));
    }
  }

  @ParameterizedTest
  @ValueSource(strings = { "png", "gif", "tif", "jpg" })
  @Order(3)
  public void readWriteWithAlpha(final String formatName) throws IOException,
      CreationException,
      CanceledException,
      ConversionException,
      URISyntaxException {
    ImageCodec format = ImageCodec.getByExtension(formatName);
    boolean hasAlpha = true;
    IResourceReference original = write(generate(hasAlpha), format);
    IImageContainer container = this.imageReader.read(ICanceler.DummyCanceler, original);
    assertContainer(container, 1000, 1000);

    if (!Objects.equals(format, ImageCodec.JPEG)) {
      IResourceReference copy = write(container, format);
      assertTrue(
          IoUtilities.contentEquals(this.resourceReferenceHandler.getFile(original),
              this.resourceReferenceHandler.getFile(copy)));
    }
  }

  @ParameterizedTest
  @ValueSource(strings = { "png", "gif", "tif", "bmp", "jpg" })
  @Order(4)
  public void scaleToSizeWithOutAlpha(final String formatName) throws IOException,
      CreationException,
      CanceledException,
      ConversionException {
    ImageCodec format = ImageCodec.getByExtension(formatName);
    boolean hasAlpha = false;
    IResourceReference original = write(generate(hasAlpha), format);
    IImageContainer container = this.imageReader.read(ICanceler.DummyCanceler, original);
    container = container.scaleTo(500, 500);
    assertContainer(container, 500, 500);
  }

  @ParameterizedTest
  @ValueSource(strings = { "png", "gif", "tif", "jpg" })
  @Order(5)
  public void scaleToSizeWithAlpha(final String formatName)
      throws IOException,
      CreationException,
      CanceledException {
    ImageCodec format = ImageCodec.getByExtension(formatName);
    boolean hasAlpha = true;
    IResourceReference original = write(generate(hasAlpha), format);
    IImageContainer container = this.imageReader.read(ICanceler.DummyCanceler, original);
    container = container.scaleTo(500, 500);
    assertContainer(container, 500, 500);
  }

  @ParameterizedTest
  @ValueSource(strings = { "png", "jpg" })
  @Order(6)
  public void scaleAndCropWithAlpha(final String formatName)
      throws IOException,
      CreationException,
      CanceledException,
      ConversionException,
      URISyntaxException {
    ImageCodec format = ImageCodec.getByExtension(formatName);
    boolean hasAlpha = true;
    IResourceReference original = write(generate(hasAlpha), format);
    IImageContainer container = this.imageReader.read(ICanceler.DummyCanceler, original);
    IImageContainer scaleAndCropContainer = container.scaleTo(500, 500);
    scaleAndCropContainer = scaleAndCropContainer.crop(200, 200, 50, 50);
    IImageContainer cropAndScaleContainer = container.crop(400, 400, 100, 100);
    cropAndScaleContainer = cropAndScaleContainer.scale(0.5, 0.5);
    IResourceReference scaleAndCropContainerReference = write(scaleAndCropContainer, format);
    IResourceReference cropAndScaleContainerReference = write(cropAndScaleContainer, format);

    assertContainer(cropAndScaleContainer, 50, 50);
    assertContainer(scaleAndCropContainer, 50, 50);

    assertImage(format, scaleAndCropContainerReference, cropAndScaleContainerReference);

    assertColor(container.asBufferImage(ICanceler.DummyCanceler),
        410,
        410,
        scaleAndCropContainer.asBufferImage(ICanceler.DummyCanceler),
        5,
        5,
        Objects::equals);
    assertColor(container.asBufferImage(ICanceler.DummyCanceler),
        410,
        410,
        cropAndScaleContainer.asBufferImage(ICanceler.DummyCanceler),
        5,
        5,
        Objects::equals);
  }

  private void assertImage(final ImageCodec format,
      final IResourceReference expectedImageReference,
      final IResourceReference actualImageReference) throws IOException,
      ConversionException,
      URISyntaxException {
    if (!Set.of(ImageCodec.JPEG, ImageCodec.BMP).contains(format)) {
      assertTrue(
          IoUtilities.contentEquals(this.resourceReferenceHandler.getFile(expectedImageReference),
              this.resourceReferenceHandler.getFile(actualImageReference)));
    }
  }

  @ParameterizedTest
  @ValueSource(strings = { "png" })
  @Order(7)
  public void channelModifiedWithAlpha(final String formatName) throws IOException,
      CreationException,
      CanceledException {
    ImageCodec format = ImageCodec.getByExtension(formatName);
    boolean hasAlpha = true;
    IResourceReference original = write(generate(hasAlpha), format);
    IImageContainer container = this.imageReader.read(ICanceler.DummyCanceler, original);
    BufferedImage generate = generate(hasAlpha);
    container = container.mapBands(new int[] { 3, 1, 2, 0 });
    assertContainer(container, 1000, 1000);
    BufferedImage transformed = container.asBufferImage(ICanceler.DummyCanceler);
    write(transformed, format);

    assertColor(generate, transformed, 10, 10, (expected, actual) -> {
      assertEquals(expected.getAlpha(), actual.getRed());
      assertEquals(expected.getRed(), actual.getAlpha());
      assertEquals(expected.getGreen(), actual.getGreen());
      assertEquals(expected.getBlue(), actual.getBlue());
    });
  }

  @ParameterizedTest
  @ValueSource(strings = { "png", "gif", "tif", "bmp", "jpg" })
  @Order(8)
  public void toGrayScaleImage(final String formatName) throws Exception {
    ImageCodec format = ImageCodec.getByExtension(formatName);
    boolean hasAlpha = false;
    IResourceReference original = write(generate(hasAlpha, true), format);
    IImageContainer container = this.imageReader.read(ICanceler.DummyCanceler, original);
    assertThat(container.getColorSpaceType(), equalTo(ColorSpace.TYPE_RGB));
    final BufferedImage bufferImage = container.asBufferImage();
    assertNotNull(bufferImage);
    assertThat(bufferImage.getColorModel().getColorSpace().getType(), equalTo(ColorSpace.TYPE_RGB));
    final IImageContainer grayScaledImageContainer = container.toGrayScale();
    assertThat(grayScaledImageContainer.getColorSpaceType(), equalTo(ColorSpace.TYPE_GRAY));
    BufferedImage grayScaledbufferImage = grayScaledImageContainer.asBufferImage();
    assertNotNull(grayScaledbufferImage);
    assertThat(grayScaledbufferImage.getColorModel().getColorSpace().getType(), equalTo(ColorSpace.TYPE_GRAY));
    grayScaledbufferImage = this.imageContainerFactory.create(bufferImage).toGrayScale().asBufferImage();
    assertNotNull(grayScaledbufferImage);
    assertThat(grayScaledbufferImage.getColorModel().getColorSpace().getType(), equalTo(ColorSpace.TYPE_GRAY));
  }

  @ParameterizedTest
  @ValueSource(strings = { "png", "gif", "tif" })
  @Order(9)
  public void toGrayScaleImageFromTranparent(final String formatName) throws Exception {
    ImageCodec format = ImageCodec.getByExtension(formatName);
    boolean hasAlpha = true;
    IResourceReference original = write(generate(hasAlpha), format);
    IImageContainer container = this.imageReader.read(ICanceler.DummyCanceler, original);
    assertThat(container.getColorSpaceType(), equalTo(ColorSpace.TYPE_RGB));
    final BufferedImage bufferImage = container.asBufferImage();
    assertNotNull(bufferImage);
    assertThat(bufferImage.getColorModel().getColorSpace().getType(), equalTo(ColorSpace.TYPE_RGB));
    final IImageContainer grayScaledImageContainer = container.toGrayScale();
    assertThat(grayScaledImageContainer.getColorSpaceType(), equalTo(ColorSpace.TYPE_GRAY));
    BufferedImage grayScaledbufferImage = grayScaledImageContainer.asBufferImage();
    assertNotNull(grayScaledbufferImage);
    assertThat(grayScaledbufferImage.getColorModel().getColorSpace().getType(), equalTo(ColorSpace.TYPE_GRAY));
    grayScaledbufferImage = this.imageContainerFactory.create(bufferImage).toGrayScale().asBufferImage();
    assertNotNull(grayScaledbufferImage);
    assertThat(grayScaledbufferImage.getColorModel().getColorSpace().getType(), equalTo(ColorSpace.TYPE_GRAY));
  }

  @ParameterizedTest
  @ValueSource(strings = { "png", "gif", "tif", "bmp", "jpg" })
  @Order(10)
  public void cropImage(final String formatName) throws Exception {
    ImageCodec format = ImageCodec.getByExtension(formatName);
    boolean hasAlpha = false;
    IResourceReference original = write(ImageUtilities
        .getNonTransparentImage(new Dimension(512, 512)), format);
    IImageContainer container = this.imageReader.read(ICanceler.DummyCanceler, original);
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

  @ParameterizedTest
  @ValueSource(strings = { "png", "gif", "tif", "bmp", "jpg" })
  @Order(10)
  public void scaleImage(final String formatName) throws Exception {
    ImageCodec format = ImageCodec.getByExtension(formatName);
    boolean hasAlpha = false;
    IResourceReference original = write(ImageUtilities
        .getNonTransparentImage(new Dimension(512, 512)), format);
    IImageContainer container = this.imageReader.read(ICanceler.DummyCanceler, original);
    assertThat(container.getWidth(), equalTo(512));
    assertThat(container.getHeight(), equalTo(512));
    final BufferedImage bufferImage = container.asBufferImage();
    assertNotNull(bufferImage);

    IImageContainer scaledImageContainer = container.scale(0.01f);
    BufferedImage scaledBufferImage = scaledImageContainer.asBufferImage();
    assertThat(scaledImageContainer.getWidth(), equalTo(5));
    assertThat(scaledImageContainer.getHeight(), equalTo(5));
    assertNotNull(scaledBufferImage);
    assertThat(scaledBufferImage.getWidth(), equalTo(5));
    assertThat(scaledBufferImage.getHeight(), equalTo(5));

    scaledBufferImage = this.imageContainerFactory.create(bufferImage).scale(0.01f).asBufferImage();
    assertNotNull(scaledBufferImage);
    assertThat(scaledBufferImage.getWidth(), equalTo(6));
    assertThat(scaledBufferImage.getHeight(), equalTo(6));

    scaledImageContainer = container.scale(0.05f);
    scaledBufferImage = scaledImageContainer.asBufferImage();
    assertThat(scaledImageContainer.getWidth(), equalTo(26));
    assertThat(scaledImageContainer.getHeight(), equalTo(26));
    assertNotNull(scaledBufferImage);
    assertThat(scaledBufferImage.getWidth(), equalTo(26));
    assertThat(scaledBufferImage.getHeight(), equalTo(26));

    scaledBufferImage = this.imageContainerFactory.create(bufferImage).scale(0.05f).asBufferImage();
    assertNotNull(scaledBufferImage);
    assertThat(scaledBufferImage.getWidth(), equalTo(26));
    assertThat(scaledBufferImage.getHeight(), equalTo(26));
  }

  private void assertColor(final BufferedImage expected,
      final BufferedImage actual,
      final int x,
      final int y,
      final BiConsumer<Color, Color> consumer) {
    assertColor(expected, x, y, actual, x, y, consumer);
  }

  private void assertColor(final BufferedImage expected,
      final int xExpected,
      final int yExpected,
      final BufferedImage actual,
      final int xActual,
      final int yActual,
      final BiConsumer<Color, Color> consumer) {
    consumer.accept(getColorFrom(expected, xExpected, yExpected), getColorFrom(actual, xActual, yActual));
  }

  private Color getColorFrom(final BufferedImage image, final int x, final int y) {
    return new Color(image.getRGB(x, y), image.getColorModel().hasAlpha());
  }

  private void
      assertContainer(final IImageContainer container, final Integer width, final Integer height)
          throws CreationException,
          CanceledException {
    assertNotNull(container);

    assertEquals(container.getWidth(), width);
    assertEquals(container.getHeight(), height);

    BufferedImage bufferedImage = container.asBufferImage(ICanceler.DummyCanceler);
    assertNotNull(bufferedImage);

    assertEquals(width, bufferedImage.getWidth());
    assertEquals(height, bufferedImage.getHeight());
  }

  private IResourceReference write(final BufferedImage image, final ImageCodec format)
      throws IOException {
    IImageContainer represetation = this.imageContainerFactory.create(image);
    return write(represetation, format);
  }

  private IResourceReference
      write(final IImageContainer represetation, final ImageCodec format)
          throws IOException {
    IResourceReference reference =
        this.resourceReferenceFactory.createTemporaryResourceReference("ImageContainerTest",
            "." + format.getExtension());
    try (OutputStream outputStream = this.resourceReferenceHandler.openOnputStream(reference)) {
      this.imageWriter.write(represetation.asBufferImage(), outputStream, format.getExtension());
    }
    return reference;
  }

  private BufferedImage generate(final boolean hasAlpha) {
    return generate(hasAlpha, hasAlpha);
  }

  private BufferedImage generate(final boolean hasAlpha, final boolean isColourful) {
    BufferedImage image = hasAlpha ? ImageUtilities.getTransparentImage(new Dimension(1000, 1000))
        : ImageUtilities.getNonTransparentImage(new Dimension(1000, 1000));
    Graphics2D graphics = image.createGraphics();
    try {
      final FontRenderContext fontRenderContext = graphics.getFontRenderContext();
      final Font font = graphics.getFont();
      if (!hasAlpha) {
        graphics.setColor(Color.white);
        graphics.fillRect(0, 0, 1000, 1000);
      }
      if (isColourful) {
        RandomObjectGenerator generator = new RandomObjectGenerator(4712, () -> false);
        for (int i = 0; i < 10; i++) {
          for (int j = 0; j < 10; j++) {
            graphics.setColor(generator.generateColor(hasAlpha));
            graphics.fillRect(i * 100, j * 100, 100, 100);
          }
        }
      }
      graphics.setColor(Color.black);
      for (int i = 1; i < 10; i++) {
        graphics.drawLine(0, i * 100, 1000, i * 100);
        graphics.drawLine(i * 100, 0, i * 100, 1000);
      }
      for (int i = 1; i < 11; i++) {
        for (int j = 1; j < 11; j++) {
          String string = i + "," + j;
          final Rectangle2D bounds = font.getStringBounds(string, fontRenderContext);
          graphics.drawString(string,
              (float) (i * 100 - 50 - bounds.getWidth() / 2),
              (float) (j * 100 - 50 + bounds.getHeight() / 2));
        }
      }
      graphics.dispose();
    } finally {
      graphics.dispose();
    }
    return image;
  }
}
