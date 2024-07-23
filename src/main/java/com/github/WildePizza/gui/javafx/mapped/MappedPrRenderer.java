package com.github.WildePizza.gui.javafx.mapped;

import java.lang.reflect.Method;
import java.util.Set;
import com.sun.glass.ui.Screen;
import com.sun.prism.GraphicsPipeline;
import com.sun.prism.GraphicsPipeline.ShaderModel;
import com.sun.prism.MappedRenderTarget;
import com.sun.scenario.effect.FilterContext;
import com.sun.scenario.effect.impl.prism.PrFilterContext;

public abstract class MappedPrRenderer extends MappedRenderer {

    
    private static final Set<String> INTRINSIC_PEER_NAMES = Set.of(
            "Crop",
            "Flood",
            "Merge",
            "Reflection");

    
    protected MappedPrRenderer() {
    }

    public abstract MappedPrDrawable createDrawable(MappedRenderTarget rtt);

    public static MappedRenderer createRenderer(FilterContext fctx) {
        Object ref = fctx.getReferent();
        if (!(ref instanceof Screen)) {
            return null;
        }
        boolean isHW;
        if (((PrFilterContext) fctx).isForceSoftware()) {
            isHW = false;
        } else {
            GraphicsPipeline pipe = GraphicsPipeline.getPipeline();
            if (pipe == null) {
                return null;
            }
            isHW = pipe.supportsShaderModel(ShaderModel.SM3);
        }
        return createRenderer(fctx, isHW);
    }

    private static MappedPrRenderer createRenderer(FilterContext fctx, boolean isHW) {
        String klassName = isHW ?
                MappedRenderer.rootPkg + ".impl.prism.ps.PPSRenderer" :
                MappedRenderer.rootPkg + ".impl.prism.sw.PSWRenderer";
        try {
            Class klass = Class.forName(klassName);
            Method m = klass.getMethod("createRenderer", new Class[] { FilterContext.class });
            return (MappedPrRenderer)m.invoke(null, new Object[] { fctx });
        } catch (Throwable e) {}
        return null;
    }

    public static boolean isIntrinsicPeer(String name) {
        return INTRINSIC_PEER_NAMES.contains(name);
    }
}
