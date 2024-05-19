package com.github.WildePizza.gui.swing;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Path2D;

public class UploadIcon implements Icon {
    private final int width;
    private final int height;
    private final Path2D path;

    public UploadIcon(int width, int height) {
        this.width = width;
        this.height = height;
        this.path = createPath();
    }

    private Path2D createPath() {
        Path2D path = new Path2D.Double();
        // SVG path data translated to Java2D path
        path.moveTo(5.625, 15);
        path.curveTo(5.625, 14.5858, 5.28921, 14.25, 4.875, 14.25);
        path.curveTo(4.46079, 14.25, 4.125, 14.5858, 4.125, 15);
        path.lineTo(5.625, 15);
        path.moveTo(4.875, 16);
        path.lineTo(4.125, 16);
        path.lineTo(4.875, 16);
        path.moveTo(19.275, 15);
        path.curveTo(19.275, 14.5858, 18.9392, 14.25, 18.525, 14.25);
        path.curveTo(18.1108, 14.25, 17.775, 14.5858, 17.775, 15);
        path.lineTo(19.275, 15);
        path.moveTo(12.2914, 5.46127);
        path.curveTo(12.5461, 5.13467, 12.4879, 4.66338, 12.1613, 4.40862);
        path.curveTo(11.8347, 4.15387, 11.3634, 4.21212, 11.1086, 4.53873);
        path.lineTo(12.2914, 5.46127);
        path.moveTo(7.20862, 9.53873);
        path.curveTo(6.95387, 9.86533, 7.01212, 10.3366, 7.33873, 10.5914);
        path.curveTo(7.66533, 10.8461, 8.13662, 10.7879, 8.39138, 10.4613);
        path.lineTo(7.20862, 9.53873);
        path.moveTo(12.2914, 4.53873);
        path.curveTo(12.0366, 4.21212, 11.5653, 4.15387, 11.2387, 4.40862);
        path.curveTo(10.9121, 4.66338, 10.8539, 5.13467, 11.1086, 5.46127);
        path.lineTo(12.2914, 4.53873);
        path.moveTo(15.0086, 10.4613);
        path.curveTo(15.2634, 10.7879, 15.7347, 10.8461, 16.0613, 10.5914);
        path.curveTo(16.3879, 10.3366, 16.4461, 9.86533, 16.1914, 9.53873);
        path.lineTo(15.0086, 10.4613);
        path.moveTo(12.45, 5);
        path.curveTo(12.45, 4.58579, 12.1142, 4.25, 11.7, 4.25);
        path.curveTo(11.2858, 4.25, 10.95, 4.58579, 10.95, 5);
        path.lineTo(12.45, 5);
        path.moveTo(10.95, 16);
        path.curveTo(10.95, 16.4142, 11.2858, 16.75, 11.7, 16.75);
        path.curveTo(12.1142, 16.75, 12.45, 16.4142, 12.45, 16);
        path.lineTo(10.95, 16);
        path.moveTo(4.125, 15);
        path.lineTo(4.125, 16);
        path.lineTo(5.625, 16);
        path.lineTo(5.625, 15);
        path.lineTo(4.125, 15);
        path.moveTo(4.125, 16);
        path.curveTo(4.125, 18.0531, 5.75257, 19.75, 7.8, 19.75);
        path.lineTo(7.8, 18.25);
        path.curveTo(6.61657, 18.25, 5.625, 17.2607, 5.625, 16);
        path.lineTo(4.125, 16);
        path.moveTo(7.8, 19.75);
        path.lineTo(15.6, 19.75);
        path.lineTo(15.6, 18.25);
        path.lineTo(7.8, 18.25);
        path.lineTo(7.8, 19.75);
        path.moveTo(15.6, 19.75);
        path.curveTo(17.6474, 19.75, 19.275, 18.0531, 19.275, 16);
        path.lineTo(17.775, 16);
        path.curveTo(17.775, 17.2607, 16.7834, 18.25, 15.6, 18.25);
        path.lineTo(15.6, 19.75);
        path.moveTo(19.275, 16);
        path.lineTo(19.275, 15);
        path.lineTo(17.775, 15);
        path.lineTo(17.775, 16);
        path.lineTo(19.275, 16);
        path.moveTo(11.1086, 4.53873);
        path.lineTo(7.20862, 9.53873);
        path.lineTo(8.39138, 10.4613);
        path.lineTo(12.2914, 5.46127);
        path.lineTo(11.1086, 4.53873);
        path.moveTo(11.1086, 5.46127);
        path.lineTo(15.0086, 10.4613);
        path.lineTo(16.1914, 9.53873);
        path.lineTo(12.2914, 4.53873);
        path.lineTo(11.1086, 5.46127);
        path.moveTo(10.95, 5);
        path.lineTo(10.95, 16);
        path.lineTo(12.45, 16);
        path.lineTo(12.45, 5);
        path.lineTo(10.95, 5);
        return path;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(Color.WHITE);
        g2d.translate(x, y);
        g2d.scale((double) width / 24, (double) height / 24);
        g2d.fill(path);
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