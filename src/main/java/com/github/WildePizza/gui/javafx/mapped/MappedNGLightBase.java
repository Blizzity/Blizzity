package com.github.WildePizza.gui.javafx.mapped;

import java.util.List;

import com.sun.javafx.geom.transform.Affine3D;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.prism.MappedGraphics;
import com.sun.prism.paint.Color;

public class MappedNGLightBase extends MappedNGNode {

    // The default color is Color.WHITE
    private Color color = Color.WHITE;
    private boolean lightOn = true;
    private Affine3D worldTransform;

    protected MappedNGLightBase() {
    }

    @Override
    public void setTransformMatrix(BaseTransform tx) {
        super.setTransformMatrix(tx);
    }

    @Override
    protected void doRender(MappedGraphics g) {}

    @Override protected void renderContent(MappedGraphics g) {}

    @Override protected boolean hasOverlappingContents() {
        return false;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Object value) {
        // Null check is done on the scenegraph side,
        // by design value can never be null.
        if (!this.color.equals(value)) {
            this.color = (Color)value;
            visualsChanged();
        }
    }

    public boolean isLightOn() {
        return lightOn;
    }

    public void setLightOn(boolean value) {
        if (lightOn != value) {
            visualsChanged();
            lightOn = value;
        }
    }

    public Affine3D getWorldTransform() {
        return worldTransform;
    }

    public void setWorldTransform(Affine3D localToSceneTx) {
        // TODO: 3D worldTransform is reference to the FX light transform,
        // which is incorrect. Uncomment below to fix problem. Requires sync
        // to be called at the correct time by FX light
//        if (this.worldTransform == null ||
//                !this.worldTransform.equals(localToSceneTx)) {
//        this.worldTransform.setTransform(localToSceneTx);
//            visualsChanged();
//        }
        this.worldTransform = localToSceneTx;
    }

    List<MappedNGNode> scopedNodes = List.of();

    public void setScope(List<MappedNGNode> scopedNodes) {
        if (!this.scopedNodes.equals(scopedNodes)) {
            this.scopedNodes = scopedNodes;
            visualsChanged();
        }
    }

    List<MappedNGNode> excludedNodes = List.of();

    public void setExclusionScope(List<MappedNGNode> excludedNodes) {
        if (!this.excludedNodes.equals(excludedNodes)) {
            this.excludedNodes = excludedNodes;
            visualsChanged();
        }
    }

    final boolean affects(MappedNGShape3D n3d) {
        if (!lightOn) {
            return false;
        }

        // shortcut to avoid traversing the hierarchy
        if (scopedNodes.isEmpty() && excludedNodes.isEmpty()) {
            return true;
        }
        if (scopedNodes.contains(n3d)) {
            return true;
        }
        if (excludedNodes.contains(n3d)) {
            return false;
        }
        MappedNGNode parent = n3d.getParent();
        while (parent != null) {
            if (scopedNodes.contains(parent)) {
                return true;
            }
            if (excludedNodes.contains(parent)) {
                return false;
            }
            parent = parent.getParent();
        }
        // if the node's state is not decided by either list,
        // it comes down to if the light has universal scope or not
        return scopedNodes.isEmpty();
    }

    @Override
    public void release() {
        // TODO: 3D - Need to release native resources
    }
}
