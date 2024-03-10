package wildepizza.com.github.blizzity.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.UUID;

public class JHoverButton extends JButton implements MouseMotionListener {
    Color hoverTextColor;
    Color hoverBoxColor;
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
    @Override
    protected void paintComponent(Graphics g) {
        try {
            paintComponent(g, /*!MouseListeners.change && */MouseListeners.isSelected(this));
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
        MouseListeners.change = false;
        Point point = e.getPoint();
        point.x += getX();
        point.y += getY();
        MouseListeners.mouse = point;
    }
}
