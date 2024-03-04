package wildepizza.com.github.blizzity.gui;

import wildepizza.com.github.blizzity.GUI;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MouseListeners extends MouseAdapter {
    @Override
    public void mouseClicked(MouseEvent e) {
        // Check if the clicked point is outside the text field bounds
        if (!GUI.userText.getBounds().contains(e.getPoint())) {
            GUI.userText.transferFocus(); // Transfers focus away from the field
        }
        GUI.minimizeButton.a = false;
    }
}
