package com.github.WildePizza.gui.javafx.mapped;

import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.prism.MappedGraphics;
import com.sun.scenario.effect.Effect;
import com.sun.scenario.effect.impl.prism.MappedPrEffectHelper;


public class MappedEffectFilter {
    private Effect effect;
    private MappedNodeEffectInput nodeInput;

    MappedEffectFilter(Effect effect, MappedNGNode node) {
        this.effect = effect;
        this.nodeInput = new MappedNodeEffectInput(node);
    }

    Effect getEffect() { return effect; }
    MappedNodeEffectInput getNodeInput() { return nodeInput; }

    void dispose() {
        effect = null;
        nodeInput.setNode(null);
        nodeInput = null;
    }

    BaseBounds getBounds(BaseBounds bounds, BaseTransform xform) {
        BaseBounds r = getEffect().getBounds(xform, nodeInput);
        return bounds.deriveWithNewBounds(r);
    }

    void render(MappedGraphics g) {
        MappedNodeEffectInput nodeInput = getNodeInput();
        MappedPrEffectHelper.render(getEffect(), g, 0, 0, nodeInput);
        nodeInput.flush();
    }
}
