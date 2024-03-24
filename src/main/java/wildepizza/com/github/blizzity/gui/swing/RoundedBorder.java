package wildepizza.com.github.blizzity.gui.swing;

import java.awt.*;
import javax.swing.border.Border;

public class RoundedBorder implements Border {
    private int arcWidth;
    private int arcHeight;
    private Color borderColor;

    public RoundedBorder(int arcWidth, int arcHeight, Color borderColor) {
        this.arcWidth = arcWidth;
        this.arcHeight = arcHeight;
        this.borderColor = borderColor;
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(borderColor);
        g2d.fillRoundRect(x + arcWidth/2, y + arcHeight/2, width - 1 - arcWidth, height - 1- arcHeight, arcWidth, arcHeight);
    }

    @Override
    public Insets getBorderInsets(Component c) {
        return new Insets(arcHeight, arcWidth, arcHeight, arcWidth);
    }

    @Override
    public boolean isBorderOpaque() {
        return false;
    }
}