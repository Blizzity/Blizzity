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
    private Point dragStart;
    private int l;
    private int w;

    public JRoundedTextField(int arcWidth, int arcHeight, int columns, Color backround) {
        this.arcWidth = arcWidth;
        this.arcHeight = arcHeight;
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
                dragStart = point;
                adjustCaretPosition(e, clickOffset);
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                Point point = e.getPoint();
                point.x -= alignmentOffset;
                int dragEnd = viewToModel2D(point);
                if (viewToModel2D(dragStart) != -1 && dragEnd != -1) {
                    select(Math.min(viewToModel2D(dragStart), dragEnd), Math.max(viewToModel2D(dragStart), dragEnd));
                    String selected = getSelectedText();
                    if (selected != null) {
                        int x = getFontMetrics(getFont()).stringWidth(getText().substring(0, Math.min(viewToModel2D(dragStart), viewToModel2D(e.getPoint())))) + alignmentOffset;
                        int x2 = getFontMetrics(getFont()).stringWidth(selected);

                        l = x;
                        w = x2;

                        repaint();
                    }
                }
            }
            /*@Override
            public void mouseReleased(MouseEvent e) {
                Point point = e.getPoint();
                point.x -= alignmentOffset;
                int dragEnd = viewToModel2D(point);
                adjustCaretPosition(e, dragEnd);
                if (viewToModel2D(dragStart) != -1 && dragEnd != -1) {
                    select(Math.min(viewToModel2D(dragStart), dragEnd), Math.max(viewToModel2D(dragStart), dragEnd));
                    if (dragPrevious != null) {
                        repaint(new Rectangle(Math.min(dragStart.x, dragPrevious.x), 0, Math.max(dragPrevious.x-dragStart.x, dragStart.x-dragPrevious.x), getHeight()));
                        dragPrevious = null;
                    }
                    repaint(new Rectangle(Math.min(dragStart.x, point.x) + alignmentOffset, 0, Math.max(point.x-dragStart.x, dragStart.x-point.x), getHeight()));
                    dragPrevious = point;
                }
            }*/
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
    @Override
    protected void paintComponent(Graphics g) {
        try {
            Insets insets = getInsets();

            int width = getWidth();
            int height = getHeight();
            String text = getText();

            // Calculate baseline for text drawing to align text properly
            int baseline = getBaseline(width, height);

            // Set the color for drawing text, considering selection
            if (getSelectedText() != null) {
                g.setColor(getBackground());
                g.fillRoundRect(insets.left, insets.top, getWidth() - insets.left - insets.right, getHeight() - insets.top - insets.bottom, arcWidth, arcHeight);
                g.setColor(getSelectionColor());
                g.fillRect(l, 0, w, getHeight());
                g.setColor(getForeground());
                // Draw the text with the custom alignment
                g.drawString(text, insets.left + alignmentOffset, baseline);
                g.setColor(getSelectedTextColor());
                g.drawString(getSelectedText(), l, baseline);
            } else {
                g.setColor(getBackground());
                g.fillRoundRect(insets.left, insets.top, getWidth() - insets.left - insets.right, getHeight() - insets.top - insets.bottom, arcWidth, arcHeight);
                g.setColor(getForeground());
                // Draw the text with the custom alignment
                g.drawString(text, insets.left + alignmentOffset, baseline);
            }
        } finally {
            g.dispose();
        }
    }
}