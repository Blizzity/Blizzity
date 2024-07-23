package com.github.WildePizza.gui.javafx.mapped;

import com.sun.javafx.geom.Rectangle;
import com.sun.prism.Graphics;

public interface MappedReadbackGraphics extends Graphics {
    public boolean canReadBack();
    public MappedRTTexture readBack(Rectangle view);
    public void releaseReadBackBuffer(MappedRTTexture view);
}
