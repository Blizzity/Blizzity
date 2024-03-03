package wildepizza.com.github.blizzity.gui;

import wildepizza.com.github.blizzity.GUI;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class MouseListeners implements MouseListener {
    @Override
    public void mouseClicked(MouseEvent e) {
        // Check if the clicked point is outside the text field bounds
        if (!GUI.userText.getBounds().contains(e.getPoint())) {
            GUI.userText.transferFocus(); // Transfers focus away from the field
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
