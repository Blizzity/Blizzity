package wildepizza.com.github.blizzity.gui;

import javafx.embed.swing.JFXPanel;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class MouseListeners {
    private static final MouseListener mouseListener = new MouseListener() {

        @Override
        public void mouseClicked(MouseEvent e) {
            MouseListeners.mouseClicked(e);
        }

        @Override
        public void mousePressed(MouseEvent e) {
            MouseListeners.mousePressed(e);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            MouseListeners.mouseReleased(e);
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            MouseListeners.mouseEntered(e);
        }

        @Override
        public void mouseExited(MouseEvent e) {
            MouseListeners.mouseExited(e);
        }
    };
    public static void addMouseClickListener(JPanel panel) {
        panel.addMouseListener(mouseListener);
    }
    public static void addMouseClickListener(JFXPanel panel) {
        panel.addMouseListener(mouseListener);
    }
    public static boolean click;
    public static void mouseClicked(MouseEvent e) {
    }
    public static void mousePressed(MouseEvent e) {
        click = true;
    }
    public static void mouseReleased(MouseEvent e) {
        click = false;
    }

    public static void mouseEntered(MouseEvent e) {

    }

    public static void mouseExited(MouseEvent e) {

    }
}
