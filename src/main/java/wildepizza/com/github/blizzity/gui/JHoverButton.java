package wildepizza.com.github.blizzity.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class JHoverButton extends JButton implements ActionListener, MouseMotionListener {
    int pressed = 0;
    boolean a;
    Color hoverTextColor;
    Color hoverBoxColor;
    boolean colored;
    boolean acolor;
    boolean close;
    public void setFrame(JFrame frame) {
        /*MouseAdapter mouseHandler = new MouseAdapter() { //TODO
            @Override
            public void mousePressed(MouseEvent e) {
                pressed = 1;
                System.out.println("a");
                acolor = true;
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                System.out.println("b");
                if (pressed == 1)
                    pressed = 4;
                else if (pressed == 11)
                    pressed = 3;
            }
        };
        frame.addMouseListener(mouseHandler);*/
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
        MouseAdapter mouseHandler = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                pressed = 1;
                System.out.println("a");
                acolor = true;
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                System.out.println("b");
                if (pressed == 1)
                    pressed = 4;
                else if (pressed == 11)
                    pressed = 3;
            }
        };
        addMouseListener(mouseHandler);
    }
    public void setCloseClick(boolean close) {
        this.close = close;
    }
    @Override
    protected void paintComponent(Graphics g) {
        System.out.println("c " + pressed);
        try {
            if (pressed == 0) //nothing
                colored = false;
            else if (pressed == 1) { //press inside
                colored = true;
                pressed = 11;
            } else if (pressed == 11) { //press outside
                colored = true;
                pressed = 1;
            } else if (pressed == 2) { //delay 2
                pressed = 4;
                a = true;
            } else if (pressed == 3) { //swap
                colored = !colored;
            } else if (pressed == 4) { //delay 1
                pressed = 3;
            }
            if (acolor) {
                acolor = false;
                colored = true;
            }
            System.out.println("d " + pressed);
            paintComponent(g, colored);
        } finally {
            g.dispose();
        }
    }
    protected void paintComponent(Graphics g, boolean color) {}
    @Override
    public void mouseDragged(MouseEvent e) {
        if (pressed == 11 && e.getPoint().x <= getWidth() && e.getPoint().x >= 0 && e.getPoint().y <= getHeight() && e.getPoint().y >= 0) {
            pressed = 1;
            acolor = true;
            System.out.println("f");
        }
    }
    @Override
    public void mouseMoved(MouseEvent e) {
        if (pressed == 0)
            pressed = 3;
//        acolor = true;
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        acolor = true;
        /*if (close) {
            a = false;
            hovering = false;
        }*/
    }
}
