package wildepizza.com.github.blizzity.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class JHoverButton extends JButton implements ActionListener, MouseMotionListener {
    boolean pressed;
    Color hoverTextColor;
    Color hoverBoxColor;
    boolean hovering;
    boolean a;
    public JHoverButton(Color pressedText, Color pressedBox) {
        super();
        this.hoverTextColor = pressedText;
        this.hoverBoxColor = pressedBox;
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
    }
    @Override
    protected void paintComponent(Graphics g) {
        try {
            boolean colored;

            if (pressed) {
                colored = true;
            } else {
                colored = hovering && a;
            }
            if (!pressed)
                hovering = !hovering;
            paintComponent(g, colored);
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
