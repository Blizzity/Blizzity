package com.github.WildePizza.gui.javafx.mapped;

import com.sun.javafx.geom.Rectangle;
import com.sun.scenario.effect.LockableResource;

public class MappedPrTexture<T extends MappedTexture> implements LockableResource {

    private final T tex;
    private final Rectangle bounds;

    public MappedPrTexture(T tex) {
        if (tex == null) {
            throw new IllegalArgumentException("MappedTexture must be non-null");
        }
        this.tex = tex;
        this.bounds = new Rectangle(tex.getPhysicalWidth(), tex.getPhysicalHeight());
    }

    public void lock() {
        if (tex != null) tex.lock();
    }

    public void unlock() {
        if (tex != null) tex.unlock();
    }

    public boolean isLost() {
        return tex.isSurfaceLost();
    }

    public Rectangle getNativeBounds() {
        return bounds;
    }

    public T getTextureObject() {
        return tex;
    }
}
