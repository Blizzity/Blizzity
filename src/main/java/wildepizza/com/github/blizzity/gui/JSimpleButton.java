package wildepizza.com.github.blizzity.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class JSimpleButton extends JHoverButton {
    boolean a;
    public JSimpleButton(String text) {
        super();
        setText(text);
        setOpaque(false);
    }
    @Override
    protected void paintComponent(Graphics g, boolean color) {
        int width = getWidth();
        int height = getHeight();
        String text = getText();

        // Calculate baseline for text drawing to align text properly
        int baseline = getBaseline(width, height);
        if (color)
            g.setColor(hoverBoxColor);
        else
            g.setColor(getBackground());
        g.fillRect(0, 0, getWidth(), getHeight());
        if (color)
            g.setColor(hoverTextColor);
        else
            g.setColor(getForeground());
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
    }
}
