package com.github.WildePizza.gui.javafx.mapped;

import com.sun.javafx.geom.Rectangle;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.prism.MappedTexture;
import com.sun.scenario.effect.FilterContext;
import com.sun.scenario.effect.ImageData;
import com.sun.scenario.effect.ImageDataRenderer;
import com.sun.scenario.effect.impl.prism.MappedPrTexture;

public class MappedPrRenderInfo implements ImageDataRenderer {
    private MappedGraphics g;

    public MappedPrRenderInfo(MappedGraphics g) {
        this.g = g;
    }

    public MappedGraphics getGraphics() {
        return g;
    }

    // RT-27390
    // TODO: Have MappedGraphics implement ImageRenderer directly to avoid
    // needing a wrapper object...
    public void renderImage(ImageData image,
                            BaseTransform transform,
                            FilterContext fctx)
    {
        if (image.validate(fctx)) {
            Rectangle r = image.getUntransformedBounds();
            // the actual image may be much larger than the region
            // of interest ("r"), so to improve performance we render
            // only that subregion here
            MappedTexture tex = ((MappedPrTexture)image.getUntransformedImage()).getTextureObject();
            BaseTransform savedTx = null;
            if (!transform.isIdentity()) {
                savedTx = g.getTransformNoClone().copy();
                g.transform(transform);
            }
            BaseTransform idtx = image.getTransform();
            if (!idtx.isIdentity()) {
                if (savedTx == null) savedTx = g.getTransformNoClone().copy();
                g.transform(idtx);
            }
            g.drawTexture(tex, r.x, r.y, r.width, r.height);
            if (savedTx != null) {
                g.setTransform(savedTx);
            }
        }
    }
}
