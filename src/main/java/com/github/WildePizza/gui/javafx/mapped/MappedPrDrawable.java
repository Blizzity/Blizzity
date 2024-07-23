package com.github.WildePizza.gui.javafx.mapped;

import com.sun.scenario.effect.FilterContext;
import com.sun.scenario.effect.impl.ImagePool;
import com.sun.scenario.effect.impl.PoolFilterable;

import java.lang.ref.WeakReference;

public abstract class MappedPrDrawable extends MappedPrTexture<MappedRTTexture> implements PoolFilterable {
    private WeakReference<ImagePool> pool;

    public static MappedPrDrawable create(FilterContext fctx, MappedRTTexture rtt) {
        return ((MappedPrRenderer) MappedRenderer.getRenderer(fctx)).createDrawable(rtt);
    }

    protected MappedPrDrawable(MappedRTTexture rtt) {
        super(rtt);
    }

    @Override
    public void setImagePool(ImagePool pool) {
        this.pool = new WeakReference<>(pool);
    }

    @Override
    public ImagePool getImagePool() {
        return pool == null ? null : pool.get();
    }

    @Override public float getPixelScale() {
        return 1.0f;
    }

    @Override public int getMaxContentWidth() {
        return getTextureObject().getMaxContentWidth();
    }

    @Override public int getMaxContentHeight() {
        return getTextureObject().getMaxContentHeight();
    }

    @Override public void setContentWidth(int contentW) {
        getTextureObject().setContentWidth(contentW);
    }

    @Override public void setContentHeight(int contentH) {
        getTextureObject().setContentHeight(contentH);
    }

    public abstract MappedGraphics createGraphics();

    public void clear() {
        MappedGraphics g = createGraphics();
        g.clear();
    }
}
