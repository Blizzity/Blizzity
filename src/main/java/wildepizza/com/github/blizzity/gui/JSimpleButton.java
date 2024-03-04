package wildepizza.com.github.blizzity.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class JSimpleButton extends JButton implements ActionListener, MouseMotionListener {
    boolean pressed;
    boolean hovering;
    boolean a;
    Color pressedColor;
    Color pressedBoxColor;
    public JSimpleButton(String text) {
        super(text);
        setOpaque(false);
        addMouseMotionListener(this);
        MouseAdapter mouseHandler = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                pressed = true;
                repaint();
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                pressed = false;
                repaint();
            }
        };
        addMouseListener(mouseHandler);
        addMouseMotionListener(mouseHandler);
    }
    public void setPressedColor(Color pressed, Color pressedBox) {
        this.pressedColor = pressed;
        this.pressedBoxColor = pressedBox;
    }
    @Override
    protected void paintComponent(Graphics g) {
        try {
            int width = getWidth();
            int height = getHeight();
            String text = getText();

            // Calculate baseline for text drawing to align text properly
            int baseline = getBaseline(width, height);
            if (pressed) {
                g.setColor(pressedBoxColor);
            } else {
                if (hovering && a)
                    g.setColor(pressedBoxColor);
                else {
                    g.setColor(getBackground());
                }
            }
            g.fillRect(0, 0, getWidth(), getHeight());
            if (pressed) {
                g.setColor(pressedColor);
            } else {
                g.setColor(getForeground());
                hovering = !hovering;
            }
            int size = 10;
            if (text.equals("X")) {
                // Draw the "X" lines
                g.drawLine(width / 2 - size / 2, height / 2 - size / 2, width / 2 + size / 2, height / 2 + size / 2);
                g.drawLine(width / 2 - size / 2, height / 2 + size / 2, width / 2 + size / 2, height / 2 - size / 2);
            } else if (text.equals("-")) {
                // Draw the "-" lines
                g.drawLine(width / 2 - size / 2, height / 2, width / 2 + size / 2, height / 2);
            } else {
                g.drawString(text, getWidth()/2-getFontMetrics(getFont()).stringWidth(getText())/2, baseline);
            }
        } finally {
            g.dispose();
        }
    }
    @Override
    public void mouseDragged(MouseEvent e) {

    }
    @Override
    public void mouseMoved(MouseEvent e) {
        if (!a)
            hovering = true;
        a = true;
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        a = false;
        hovering = false;
    }
}
