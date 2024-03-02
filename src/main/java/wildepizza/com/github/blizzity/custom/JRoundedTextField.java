package wildepizza.com.github.blizzity.custom;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class JRoundedTextField extends JTextField {
    private final int arcWidth;
    private final int arcHeight;
    private int alignmentOffset = 40;

    public JRoundedTextField(int arcWidth, int arcHeight, int columns) {
        this.arcWidth = arcWidth;
        this.arcHeight = arcHeight;
        setColumns(columns);
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder());
    }
    public int getAlignmentOffset() {
        return alignmentOffset;
    }

    public void setAlignmentOffset(int alignmentOffset) {
        this.alignmentOffset = alignmentOffset;
        repaint();
    }
    /*@Override
    public Insets getInsets() {
        Insets insets = super.getInsets();
        insets.right -= 100; // Adjust based on your observed text position shift
        return insets;
    }*/
    @Override
    protected void paintComponent(Graphics g) {
        if (ui != null) {
            Graphics scratchGraphics = (g == null) ? null : g.create();
            try {
                int width = getWidth();
                int height = getHeight();
                scratchGraphics.setColor(getBackground());
                scratchGraphics.fillRoundRect(0, 0, width - 1, height - 1, arcWidth, arcHeight); // Draw rounded outline
                scratchGraphics.drawString(getText(), super.getInsets().left + alignmentOffset, getBaseline(width, height));
                ui.update(scratchGraphics, this);
            }
            finally {
                scratchGraphics.dispose();
            }
        }
    }
    @Override
    protected void paintComponent(Graphics g) {
        if (ui != null) {
            Graphics scratchGraphics = (g == null) ? null : g.create();
            try {
                int width = getWidth();
                int height = getHeight();
                scratchGraphics.setColor(getBackground());
                scratchGraphics.fillRoundRect(0, 0, width - 1, height - 1, arcWidth, arcHeight); // Draw rounded outline
                scratchGraphics.drawString(getText(), super.getInsets().left + alignmentOffset, getBaseline(getWidth(), getHeight()));
                ui.update(scratchGraphics, this);
            }
            finally {
                scratchGraphics.dispose();
            }
        }

        Insets insets = getInsets();
        FontMetrics fm = g.getFontMetrics();

        // Clear the existing drawing
        g.setColor(getBackground());
        g.fillRect(0, 0, getWidth(), getHeight());

        int width = getWidth();
        int height = getHeight();

        // Set the color for drawing text, considering selection
        if (getSelectedText() != null) {
            g.setColor(getSelectionColor());
//            g.fillRoundRect(0, 0, width - 1, height - 1, arcWidth, arcHeight); // Draw rounded outline
            g.setColor(getSelectedTextColor());
        } else {
            g.setColor(getForeground());
        }

        // Calculate baseline for text drawing to align text properly
        int baseline = getBaseline(width, height);

        // Draw the text with the custom alignment
        g.drawString(getText(), insets.left + alignmentOffset, baseline);
    }
    /*@Override
    public void paintComponent(Graphics g) {
        if (ui != null) {
            Graphics scratchGraphics = (g == null) ? null : g.create();
            try {
                int width = getWidth();
                int height = getHeight();
                scratchGraphics.setColor(getBackground());
                scratchGraphics.fillRoundRect(0, 0, width - 1, height - 1, arcWidth, arcHeight); // Draw rounded outline
                scratchGraphics.drawString(getText(), super.getInsets().left + alignmentOffset, getBaseline(getWidth(), getHeight()));
                ui.update(scratchGraphics, this);
            }
            finally {
                scratchGraphics.dispose();
            }
        }

    }*/
    /*@Override
    public void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int width = getWidth();
        int height = getHeight();
        g2d.fillRoundRect(arcWidth/2, arcHeight/2, width - 1 - arcWidth, height - 1- arcHeight, arcWidth, arcHeight);
    }*/
}