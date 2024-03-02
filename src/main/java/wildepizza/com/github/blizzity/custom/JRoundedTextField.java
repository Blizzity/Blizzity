package wildepizza.com.github.blizzity.custom;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class JRoundedTextField extends JTextField {
    private final int arcWidth;
    private final int arcHeight;
    private int alignmentOffset = 40;
    private Color backround;
    private int dragStart;

    public JRoundedTextField(int arcWidth, int arcHeight, int columns, Color backround) {
        this.arcWidth = arcWidth;
        this.arcHeight = arcHeight;
        this.backround = backround;
        setColumns(columns);
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder());
        /*addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                replaceSelectedText();
            }
        });*/
        MouseAdapter mouseHandler = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                Point point = e.getPoint();
                point.x -= alignmentOffset;
                int clickOffset = viewToModel2D(point);
                dragStart = clickOffset;
                adjustCaretPosition(e, clickOffset);
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                Point point = e.getPoint();
                point.x -= alignmentOffset;
                int dragEnd = viewToModel2D(point);
                if (dragStart != -1 && dragEnd != -1) {
                    select(Math.min(dragStart, dragEnd), Math.max(dragStart, dragEnd));
                }
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                Point point = e.getPoint();
                point.x -= alignmentOffset;
                int dragEnd = viewToModel2D(point);
                adjustCaretPosition(e, dragEnd);
                if (dragStart != -1 && dragEnd != -1) {
                    select(Math.min(dragStart, dragEnd), Math.max(dragStart, dragEnd));
                }
            }
            private void adjustCaretPosition(MouseEvent e, int clickOffset) {
                int textLength = getText().length();
                int adjustedOffset = Math.min(Math.max(clickOffset, 0), textLength);
                setCaretPosition(adjustedOffset);
            }
        };

        addMouseListener(mouseHandler);
        addMouseMotionListener(mouseHandler);
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
    /*@Override
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
    }*/
    private void init() {
    }
    @Override
    protected void paintComponent(Graphics g) {
        Insets insets = getInsets();
        FontMetrics fm = g.getFontMetrics();

        // Clear the existing drawing
        g.setColor(backround);
        g.fillRect(0, 0, getWidth(), getHeight());

        int width = getWidth();
        int height = getHeight();

        // Set the color for drawing text, considering selection
        System.out.println(getSelectedText());
        if (getSelectedText() != null) {
            g.setColor(getSelectionColor());
            g.fillRect(insets.left + alignmentOffset, insets.top, getWidth() - insets.left - insets.right - alignmentOffset, getHeight() - insets.top - insets.bottom);
            g.setColor(getSelectedTextColor());
        } else {
            g.setColor(getBackground());
            g.fillRoundRect(insets.left, insets.top, getWidth() - insets.left - insets.right, getHeight() - insets.top - insets.bottom, arcWidth, arcHeight);
            g.setColor(getForeground());
        }

        // Calculate baseline for text drawing to align text properly
        int baseline = getBaseline(width, height);

        // Draw the text with the custom alignment
        g.drawString(getText(), insets.left + alignmentOffset, baseline);
    }
}