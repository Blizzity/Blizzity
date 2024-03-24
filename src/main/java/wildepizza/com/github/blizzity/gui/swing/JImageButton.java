package wildepizza.com.github.blizzity.gui.swing;

import javax.swing.*;
import java.awt.*;
import wildepizza.com.github.blizzity.GUI;

public class JImageButton extends JHoverButton {
    private final int arcWidth;
    private final int arcHeight;
    public JImageButton(int arcWidth, int arcHeight, ImageIcon icon) {
        super();
        this.setIcon(icon);
        this.arcWidth = arcWidth;
        this.arcHeight = arcHeight;
        setOpaque(false);
    }
    @Override
    protected void paintComponent(Graphics g, boolean color) {
        int width = getWidth();
        int height = getHeight();
        int size = Math.max(getIcon().getIconWidth(), getIcon().getIconHeight());
        g.setColor(GUI.color2);
        g.fillRect(0, 0, width, height);
        if (color)
            g.setColor(this.hoverBoxColor);
        else
            g.setColor(getBackground());
        g.fillRoundRect(width/2-size/2 - 5, height/2-size/2 - 5, size+10, size+10, arcWidth, arcHeight);
        Image image = ((ImageIcon) getIcon()).getImage();
        if (color)
            g.setColor(this.hoverTextColor);
        else
            g.setColor(getForeground());
        g.drawImage(image, width/2-getIcon().getIconWidth()/2, height/2-getIcon().getIconHeight()/2, this);
    }
}
