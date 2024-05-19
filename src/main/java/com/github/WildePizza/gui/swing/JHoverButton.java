package com.github.WildePizza.gui.swing;

import com.github.WildePizza.Blizzity;
import com.github.WildePizza.GUI;
import com.github.WildePizza.gui.listeners.ScreenListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class JHoverButton extends JButton implements MouseMotionListener {
    Color hoverTextColor;
    Color hoverBoxColor;
    boolean isDarkened;
    public void setDarkened(boolean darkened) {
        isDarkened = darkened;
        repaint();
    }
    public void setHoverBackground(Color color) {
        this.hoverBoxColor = color;
    }
    public void setHoverForeground(Color color) {
        this.hoverTextColor = color;
    }
    public JHoverButton() {
        super();
        addMouseMotionListener(this);
    }
    public JHoverButton(Icon icon) {
        super(icon);
        addMouseMotionListener(this);
    }
    @Override
    protected void paintComponent(Graphics g) {
        try {
            Point mousePoint = MouseInfo.getPointerInfo().getLocation();
            paintComponent(g, !ScreenListener.change && ScreenListener.isSelected(this) && GUI.frame.getBounds().contains(mousePoint));
            if (isDarkened) {
                g.setColor(new Color(0, 0, 0, 128));
                g.fillRect(0, 0, getWidth(), getHeight()); // Draw rectangle (x, y, width, height)
            }
        } finally {
            g.dispose();
        }
    }
    protected void paintComponent(Graphics g, boolean color) {}
    @Override
    public void mouseDragged(MouseEvent e) {
    }
    @Override
    public void mouseMoved(MouseEvent e) {
        ScreenListener.change = false;
        Point point = e.getPoint();
        point.x += getX();
        point.y += getY();
        ScreenListener.mouse = point;
    }
}
