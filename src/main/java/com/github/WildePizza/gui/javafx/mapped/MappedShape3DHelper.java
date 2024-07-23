package com.github.WildePizza.gui.javafx.mapped;

import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.scene.MappedNodeHelper;
import com.sun.javafx.util.Utils;
import javafx.scene.shape.Shape3D;


public abstract class MappedShape3DHelper extends MappedNodeHelper {

    private static Shape3DAccessor shape3DAccessor;

    static {
        Utils.forceInit(Shape3D.class);
    }

    @Override
    protected void updatePeerImpl(MappedNode node) {
        super.updatePeerImpl(node);
        shape3DAccessor.doUpdatePeer(node);
    }

    @Override
    protected BaseBounds computeGeomBoundsImpl(MappedNode node, BaseBounds bounds,
                                               BaseTransform tx) {
        return shape3DAccessor.doComputeGeomBounds(node, bounds, tx);
    }

    @Override
    protected boolean computeContainsImpl(MappedNode node, double localX, double localY) {
        return shape3DAccessor.doComputeContains(node, localX, localY);
    }

    public static void setShape3DAccessor(final Shape3DAccessor newAccessor) {
        if (shape3DAccessor != null) {
            throw new IllegalStateException();
        }

        shape3DAccessor = newAccessor;
    }

    public interface Shape3DAccessor {
        void doUpdatePeer(MappedNode node);
        BaseBounds doComputeGeomBounds(MappedNode node, BaseBounds bounds, BaseTransform tx);
        boolean doComputeContains(MappedNode node, double localX, double localY);
    }

}
