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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import de.jdemo.annotation.Demo;
import de.jdemo.extensions.SwingDemoCase;
import net.anwiba.commons.image.graphic.ClosableGraphicsBuider;
import net.anwiba.commons.image.graphic.IClosableGraphics;
import net.anwiba.commons.image.graphic.RenderingHintsBuider;
import net.anwiba.commons.lang.optional.Optional;
import net.anwiba.commons.model.IChangeableObjectListener;
import net.anwiba.commons.model.IObjectModel;
import net.anwiba.commons.model.ObjectModel;
import net.anwiba.commons.swing.object.DoubleFieldBuilder;
import net.anwiba.commons.swing.utilities.GuiUtilities;

@SuppressWarnings("nls")
public class StringPainterDemo extends SwingDemoCase {

  @Demo
  public void showArial() throws Exception {
    show("Arial", 262);
  }

  @Demo
  public void showMonospaced() throws Exception {
    show(Font.MONOSPACED, 353);
  }

  public void show(final String fontName, final int distance) {
    final RenderingHints hints = new RenderingHintsBuider()
        .setColorRenderQuality()
        .setStrokeControlNormalize()
        .setAntiAliasingOn()
        .setAlphaInterpolationQuality()
        .setTextAntiAliasing(true)
        .setDitheringEnabled()
        .setRenderingQuality()
        .build();

    final Dimension size = new Dimension(600, 400);
    final IObjectModel<Double> factorModel = new ObjectModel<>(1.0);
    final IObjectModel<Dimension> imageSizeModel = new ObjectModel<>(size);
    final IObjectModel<BufferedImage> imageModel = new ObjectModel<>(
        createImage(factorModel.get(), imageSizeModel.get(), hints, fontName, distance));

    final IChangeableObjectListener listener = () -> Optional.of(factorModel.get()).consume(
        f -> Optional.of(imageSizeModel.get()).consume(
            d -> imageModel.set(createImage(factorModel.get(), d, hints, fontName, distance))));

    factorModel.addChangeListener(listener);
    imageSizeModel.addChangeListener(listener);

    final JPanel toolPanel = new JPanel();
    toolPanel.setMinimumSize(new Dimension(10, 30));
    toolPanel.add(
        new DoubleFieldBuilder()
            .setModel(factorModel)
            .setToStringConverter(input -> String.format("%5.2f", input))
            .addSpinnerActions(0.5, 4., 0.01)
            .build()
            .getComponent());

    @SuppressWarnings("serial")
    final JPanel imagePanel = new JPanel() {

      @Override
      public void paint(final Graphics g) {
        super.paint(g);
        final Graphics2D graphics = (Graphics2D) g.create();
        try (IClosableGraphics graphic = new ClosableGraphicsBuider(graphics, hints).build()) {
          graphic.drawImage(imageModel.get(), 0, 0, null);
        }
      }

    };

    imagePanel.addComponentListener(new ComponentAdapter() {

      @Override
      public void componentResized(final ComponentEvent e) {
        final Dimension dimension = e.getComponent().getSize();
        if (dimension == null) {
          return;
        }
        imageSizeModel.set(dimension);
      }

    });
    imagePanel.setMinimumSize(size);
    imagePanel.setPreferredSize(size);

    imageModel.addChangeListener(
        () -> Optional.of(imageModel.get()).consume(i -> GuiUtilities.invokeLater(() -> imagePanel.repaint())));

    final JPanel panel = new JPanel(new BorderLayout());
    panel.add(BorderLayout.NORTH, toolPanel);
    panel.add(BorderLayout.CENTER, imagePanel);
    show(panel);
  }

  public BufferedImage createImage(
      final double factor,
      final Dimension size,
      final RenderingHints hints,
      final String fontName,
      final int distance) {
    final BufferedImage image = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_ARGB);
    final AffineTransform transform = new AffineTransform();
    transform.translate(10, 10);
    transform.scale(factor, factor);
    try (IClosableGraphics graphic = new ClosableGraphicsBuider((Graphics2D) image.getGraphics(), hints).build()) {
      graphic.setClip(0, 0, size.width, size.height);
      graphic.setTransform(transform);
      graphic.setColor(Color.BLACK);
      graphic.setFont(new Font(fontName, Font.PLAIN, 16));
      graphic.drawString("Schienenverkehrslärm 24 Stunden - L", 3, 18);
      graphic.setFont(new Font(fontName, Font.BOLD, 12));
      graphic.drawString("DEM", distance, 18);
    }
    return image;
  }

}