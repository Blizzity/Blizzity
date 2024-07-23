package com.github.WildePizza.gui.javafx.mapped;

import com.sun.glass.ui.Screen;
import com.sun.pisces.Surface;

public interface MappedRenderTarget extends Surface {
    public Screen getAssociatedScreen();
    public MappedGraphics createGraphics();
    public boolean isOpaque();
    public void setOpaque(boolean opaque);
    public boolean isMSAA();
}
