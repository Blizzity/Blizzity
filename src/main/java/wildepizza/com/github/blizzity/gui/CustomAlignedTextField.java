package wildepizza.com.github.blizzity.gui;

import javax.swing.*;
import java.awt.*;

public class CustomAlignedTextField extends JTextField {
    // Alignment offset from the left side
    private int alignmentOffset = 20;

    public CustomAlignedTextField(int columns) {
        super(columns);
    }

    public CustomAlignedTextField() {
        super();
    }

    public int getAlignmentOffset() {
        return alignmentOffset;
    }

    public void setAlignmentOffset(int alignmentOffset) {
        this.alignmentOffset = alignmentOffset;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Insets insets = getInsets();
        FontMetrics fm = g.getFontMetrics();

        // Clear the existing drawing
        g.setColor(getBackground());
        g.fillRect(0, 0, getWidth(), getHeight());

        // Set the color for drawing text, considering selection
        if (getSelectedText() != null) {
            g.setColor(getSelectionColor());
            g.fillRect(insets.left + alignmentOffset, insets.top, getWidth() - insets.left - insets.right - alignmentOffset, getHeight() - insets.top - insets.bottom);
            g.setColor(getSelectedTextColor());
        } else {
            g.setColor(getForeground());
        }

        // Calculate baseline for text drawing to align text properly
        int baseline = getBaseline(getWidth(), getHeight());

        // Draw the text with the custom alignment
        g.drawString(getText(), insets.left + alignmentOffset, baseline);
    }
}