package com.github.WildePizza.gui.swing;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.GeneralPath;

public class UploadIcon implements Icon {
    private final int width;
    private final int height;

    public UploadIcon(int width, int height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Set the stroke and color
        g2d.setStroke(new BasicStroke(3));
        g2d.setColor(Color.RED);

        // Draw the arrow
        GeneralPath arrow = new GeneralPath();
        arrow.moveTo(x + width / 2.0, y + height / 4.0);
        arrow.lineTo(x + width / 4.0, y + height / 2.0);
        arrow.lineTo(x + width / 2.5, y + height / 2.0);
        arrow.lineTo(x + width / 2.5, y + 3 * height / 4.0);
        arrow.lineTo(x + 1.5 * width / 2.5, y + 3 * height / 4.0);
        arrow.lineTo(x + 1.5 * width / 2.5, y + height / 2.0);
        arrow.lineTo(x + 3 * width / 4.0, y + height / 2.0);
        arrow.closePath();

        // Draw the arrow path
        g2d.draw(arrow);

        // Draw the bottom rectangle
        g2d.drawRect(x + width / 4, y + 3 * height / 4, width / 2, height / 8);

        g2d.dispose();
    }

    @Override
    public int getIconWidth() {
        return width;
    }

    @Override
    public int getIconHeight() {
        return height;
    }
}