package com.github.WildePizza.gui.javafx.mapped;

import java.nio.Buffer;

public interface MappedRTTexture extends MappedTexture, MappedRenderTarget {
    public int[] getPixels();
    public boolean readPixels(Buffer pixels);
    public boolean readPixels(Buffer pixels, int x, int y, int width, int height);
    public boolean isVolatile();
}
