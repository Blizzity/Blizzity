package wildepizza.com.github.blizzity.gui;

import javafx.embed.swing.JFXPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

public class MouseListeners {
    public static Point mouse;
    public static boolean change = true;
    public static MouseMotionListener getMouseListener(int x, int y) {
        return new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
                mouse = e.getPoint();
                mouse.x += x;
                mouse.y += y;
                change = true;
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                mouse = e.getPoint();
                mouse.x += x;
                mouse.y += y;
                change = true;
            }
        };
    }

    public static void addMouseClickListener(JPanel panel, int x, int y) {
        panel.addMouseMotionListener(getMouseListener(x, y));
    }
    public static void addMouseClickListener(JFXPanel panel, int x, int y) {
        panel.addMouseMotionListener(getMouseListener(x, y));
    }
    public static boolean isSelected(JButton button) {
        if (mouse == null)
            return false;
        return button.getX() <= mouse.x && button.getX()+button.getWidth() > mouse.x && button.getY() < mouse.y && button.getY()+button.getHeight() > mouse.y;
    }
}
