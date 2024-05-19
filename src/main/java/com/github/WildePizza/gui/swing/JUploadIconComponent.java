package com.github.WildePizza.gui.swing;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.GeneralPath;

public class JUploadIconComponent extends JComponent {
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Set the stroke and color
        g2d.setStroke(new BasicStroke(3));
        g2d.setColor(Color.BLACK);

        // Draw the arrow
        GeneralPath arrow = new GeneralPath();
        arrow.moveTo(50, 20);
        arrow.lineTo(30, 40);
        arrow.lineTo(40, 40);
        arrow.lineTo(40, 70);
        arrow.lineTo(60, 70);
        arrow.lineTo(60, 40);
        arrow.lineTo(70, 40);
        arrow.closePath();

        // Draw the arrow path
        g2d.draw(arrow);

        // Draw the bottom rectangle
        g2d.drawRect(30, 70, 40, 20);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(100, 100);
    }
}
