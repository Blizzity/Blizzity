package wildepizza.com.github.blizzity.gui;

import javax.swing.*;
import java.awt.*;

public class JDarkenedPanel extends JPanel {
    boolean isDarkened;
    public void setDarkened(boolean darkened) {
        isDarkened = darkened;
        repaint();
    }
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int componentCount = getComponentCount();
        for (int i = 0; i < componentCount; i++) {
            getComponent(i);
        }
        if (isDarkened) {
            g.setColor(new Color(0, 0, 0, 128));
            g.fillRect(0, 0, getWidth(), getHeight()); // Draw rectangle (x, y, width, height)
        }
    }
}
