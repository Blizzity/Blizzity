package com.github.WildePizza.gui.swing;

import com.github.WildePizza.GUI;

import javax.swing.*;
import java.awt.*;

public class JIconButton extends JHoverButton {
    private final double arcWidth;
    private final double arcHeight;
    public JIconButton(double arcWidth, double arcHeight, Icon icon) {
        super(icon);
        this.setIcon(icon);
        this.arcWidth = arcWidth;
        this.arcHeight = arcHeight;
        setOpaque(false);
    }
    @Override
    protected void paintComponent(Graphics g, boolean color) {
        int width = getWidth();
        int height = getHeight();
        double multiplier = (double) Math.min(height, width) /40;
        double size = Math.min(height, width);
        g.setColor(GUI.color2);
        g.fillRect(0, 0, width, height);
        if (color)
            g.setColor(this.hoverBoxColor);
        else
            g.setColor(getBackground());
        g.fillRoundRect((int) (5*multiplier), (int) (5*multiplier), (int) (size-10*multiplier), (int) (size-10*multiplier), (int) arcWidth, (int) arcHeight);
//        Icon icon = getIcon();
//        int iconWidth = getIcon().getIconWidth();
//        int iconHeight = getIcon().getIconHeight();
//        if (multiplier != 1)
//            icon = icon.getScaledInstance((int) (iconWidth*multiplier), (int) (iconHeight*multiplier), Image.SCALE_REPLICATE);
        if (color)
            g.setColor(this.hoverTextColor);
        else
            g.setColor(getForeground());
        if (getIcon() != null) {
            int x = (getWidth() - getIcon().getIconWidth()) / 2;
            int y = (getHeight() - getIcon().getIconHeight()) / 2;
            getIcon().paintIcon(this, g, x, y);
        }
    }
}
