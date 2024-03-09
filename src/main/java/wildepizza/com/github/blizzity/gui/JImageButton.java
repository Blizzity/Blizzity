package wildepizza.com.github.blizzity.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class JImageButton extends JButton implements ActionListener, MouseMotionListener {
    boolean pressed;
    Color pressedColor;
    Color pressedBoxColor;
    boolean hovering;
    boolean a;
    private final int arcWidth;
    private final int arcHeight;
    public JImageButton(int arcWidth, int arcHeight, ImageIcon icon) {
        super(icon);
        this.arcWidth = arcWidth;
        this.arcHeight = arcHeight;
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
            int size = Math.max(getIcon().getIconWidth(), getIcon().getIconHeight());
            g.fillRoundRect(width/2-size/2 - 5, height/2-size/2 - 5, size+10, size+10, arcWidth, arcHeight);
            if (pressed) {
                g.setColor(pressedColor);
            } else {
                g.setColor(getForeground());
                hovering = !hovering;
            }
            if (pressed) {
                g.setColor(pressedColor);
            } else
                g.setColor(getForeground());
            Image image = ((ImageIcon) getIcon()).getImage();
            g.drawImage(image, width/2-getIcon().getIconWidth()/2, height/2-getIcon().getIconHeight()/2, this);
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
